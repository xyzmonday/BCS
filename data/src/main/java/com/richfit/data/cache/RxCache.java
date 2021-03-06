package com.richfit.data.cache;

import android.content.Context;
import android.os.StatFs;

import com.esotericsoftware.kryo.Kryo;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.data.cache.core.CacheCore;
import com.richfit.data.cache.core.disk.converter.IDiskConverter;
import com.richfit.data.cache.core.disk.converter.KryoDiskConverter;
import com.richfit.data.cache.core.disk.journal.IDiskJournal;
import com.richfit.data.cache.core.disk.journal.LRUDiskJournal;
import com.richfit.data.cache.core.disk.storage.IDiskStorage;
import com.richfit.data.cache.core.disk.storage.LRUDiskStorage;
import com.richfit.data.cache.core.memory.journal.IMemoryJournal;
import com.richfit.data.cache.core.memory.journal.LRUMemoryJournal;
import com.richfit.data.cache.core.memory.storage.IMemoryStorage;
import com.richfit.data.cache.core.memory.storage.LRUMemoryStorage;
import com.richfit.data.cache.netcache.ResultData;
import com.richfit.data.cache.netcache.retrofit.RxCallAdapterFactory;
import com.richfit.data.cache.netcache.strategy.CacheStrategy;

import org.reactivestreams.Publisher;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by monday on 2016/12/30.
 */

public class RxCache {
    private static final String DEFUALT_CACHE_DB_NAME = "RxCache.db";

    private RxCache() {
    }

    private static LiteOrm mLiteOrm;
    private static CacheCore mCacheCore;
    private static RxCacheManager mRxCacheManager;
    private static int mExpires;

    public static void init(Context context) {
        initLiteOrm(context,DEFUALT_CACHE_DB_NAME);
    }

    private static void initLiteOrm(Context context,String cacheDbName) {
        if (mLiteOrm == null) {
            try {

                DataBaseConfig config = new DataBaseConfig(context, cacheDbName);
                config.dbVersion = 1;
                config.onUpdateListener = null;
                mLiteOrm = LiteOrm.newSingleInstance(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mLiteOrm.setDebugged(true);
    }

    private RxCache(long memoryMaxSize, long memoryMaxQuantity, long diskMaxSize, long diskMaxQuantity,
                    int expires, IMemoryStorage memoryStorage, IMemoryJournal memoryJournal,
                    IDiskStorage diskStorage, IDiskJournal diskJournal) {
        if(mLiteOrm == null) {
            throw new RuntimeException("请先调用init方法初始化RxCache");
        }
        mExpires = expires;
        CacheCore.Builder coreBuilder = new CacheCore.Builder();
        coreBuilder.memory(memoryStorage);
        coreBuilder.memoryJournal(memoryJournal);
        coreBuilder.memoryMax(memoryMaxSize, memoryMaxQuantity);
        coreBuilder.disk(diskStorage);
        coreBuilder.diskJournal(diskJournal);
        coreBuilder.diskMax(diskMaxSize, diskMaxQuantity);
        mCacheCore = coreBuilder.create();
        mRxCacheManager = new RxCacheManager(mCacheCore, mExpires);
    }

    public static RxCacheManager manager() {
        if (mLiteOrm == null || mRxCacheManager == null) {
            new Throwable("RxCache is not initiated");
        }
        return mRxCacheManager;
    }


    public static <T> FlowableTransformer<T, ResultData<T>> transformer(String key, CacheStrategy strategy) {
        return new RxCache.CacheTransformer(key, strategy);
    }

    public static GsonBuilder gson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResultData.class, new RxCache.ResultDataAdapter());
        return builder;
    }

    public static Converter.Factory gsonConverter(GsonBuilder builder) {
        builder.registerTypeAdapter(ResultData.class, new RxCache.ResultDataAdapter());
        return GsonConverterFactory.create(builder.create());
    }

    public static Converter.Factory gsonConverter() {
        return gsonConverter(RxCache.gson());
    }

    public static CallAdapter.Factory rxCallAdapter() {
        return RxCallAdapterFactory.create();
    }

//    public static class CacheObservalbeTransformer<T> implements ObservableTransformer<T, ResultData<T>> {
//
//        private String key;
//        private CacheStrategy cacheStrategy;
//
//        public CacheObservalbeTransformer(String key, CacheStrategy cacheStrategy) {
//            this.key = CommonUtil.hashKeyForDisk(key);
//            this.cacheStrategy = cacheStrategy;
//        }
//
//        @Override
//        public ObservableSource<ResultData<T>> apply(Observable<T> upstream) {
//            return cacheStrategy.execute(key,upstream.toFlowable(BackpressureStrategy.LATEST))
//                    .toObservable();
//        }
//    }

    /**
     * 将数据T转换成ResultData<T>，在这过程中可以根据不同的缓存策略,
     * 将数据T缓存起来。
     *
     * @param <T>
     */
    public static class CacheTransformer<T> implements FlowableTransformer<T, ResultData<T>> {
        private String key;
        private CacheStrategy cacheStrategy;

        public CacheTransformer(String key, CacheStrategy cacheStrategy) {
            this.key = CommonUtil.hashKeyForDisk(key);
            this.cacheStrategy = cacheStrategy;
        }

        @Override
        public Publisher<ResultData<T>> apply(Flowable<T> upstream) {
            return cacheStrategy.execute(key, upstream);
        }
    }

    private static class ResultDataAdapter<T> implements JsonSerializer<ResultData<T>>, JsonDeserializer<ResultData<T>> {
        @Override
        public ResultData<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new ResultData(null, null, context.deserialize(json, getWrapType(typeOfT)));
        }

        @Override
        public JsonElement serialize(ResultData<T> src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.data, getWrapType(typeOfSrc));
        }

        private static Type getWrapType(Type typeOf) {
            ParameterizedType type = (ParameterizedType) typeOf;
            return Arrays.asList(type.getActualTypeArguments()).get(0);
        }
    }

    /**
     * 构造器
     */
    public static final class Builder {
        // 缓存默认有效期
        private static final int DEFAULT_EXPIRES = 12 * 60 * 60 * 1000;
        private static final long DEFAULT_MEMORY_MAX_QUANTITY = 1000;
        private static final long DEFAULT_DISK_MAX_QUANTITY = 1000;
        private static final long MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
        private static final long MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
        private static final long DEFAULT_MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);//运行内存的8分之1
        private int expires;
        private File diskCacheDir;
        private long memoryMaxSize;
        private long memoryMaxQuantity;
        private int appVersion;
        private long diskMaxSize;
        private long diskMaxQuantity;
        private IMemoryStorage memoryStorage;
        private IMemoryJournal memoryJournal;
        private IDiskStorage diskStorage;
        private IDiskJournal diskJournal;
        private IDiskConverter diskConverter;

        public Builder() {
        }

        public RxCache.Builder memoryMax(int maxSize) {
            this.memoryMaxSize = maxSize;
            return this;
        }

        public RxCache.Builder appVersion(int appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public RxCache.Builder diskCache(File diskCacheDir) {
            this.diskCacheDir = diskCacheDir;
            return this;
        }

        public RxCache.Builder expires(int expires) {
            this.expires = expires;
            return this;
        }

        public RxCache.Builder diskConverter(IDiskConverter converter) {
            this.diskConverter = converter;
            return this;
        }

        public RxCache.Builder diskMax(long maxSize) {
            this.diskMaxSize = maxSize;
            return this;
        }

        public RxCache.Builder memoryStorage(IMemoryStorage memoryStorage) {
            this.memoryStorage = memoryStorage;
            return this;
        }

        public RxCache.Builder memroyJournal(IMemoryJournal memoryJournal) {
            this.memoryJournal = memoryJournal;
            return this;
        }

        public RxCache.Builder diskStorage(IDiskStorage diskStorage) {
            this.diskStorage = diskStorage;
            return this;
        }

        public RxCache.Builder diskJournal(IDiskJournal diskJournal) {
            this.diskJournal = diskJournal;
            return this;
        }

        public RxCache build() {
            if (this.diskCacheDir == null) {
                throw new NullPointerException("DiskDir can not be null.");
            }
            if (!this.diskCacheDir.exists()) {
                this.diskCacheDir.mkdirs();
            }

            if (expires <= 0) {
                expires = DEFAULT_EXPIRES;
            }

            if (memoryMaxSize <= 0) {
                memoryMaxSize = DEFAULT_MEMORY_CACHE_SIZE;
            }

            if (memoryMaxQuantity <= 0) {
                memoryMaxQuantity = DEFAULT_MEMORY_MAX_QUANTITY;
            }

            if (diskMaxSize <= 0) {
                diskMaxSize = calculateDiskCacheSize(diskCacheDir);
            }

            if (diskMaxQuantity <= 0) {
                diskMaxQuantity = DEFAULT_DISK_MAX_QUANTITY;
            }

            if (memoryStorage == null) {
                memoryStorage = new LRUMemoryStorage((int) memoryMaxSize);
            }

            if (memoryJournal == null) {
                memoryJournal = new LRUMemoryJournal();
            }

            if (diskConverter == null) {
                diskConverter = new KryoDiskConverter(new Kryo());
            }

            appVersion = Math.max(1, this.appVersion);
            if (diskStorage == null) {
                diskStorage = new LRUDiskStorage(diskConverter, diskCacheDir, appVersion, diskMaxSize);
            }

            if (diskJournal == null) {
                diskJournal = new LRUDiskJournal(mLiteOrm);
            }
            return new RxCache(memoryMaxSize, memoryMaxQuantity, diskMaxSize, diskMaxQuantity,
                    expires, memoryStorage, memoryJournal, diskStorage, diskJournal);
        }

        /**
         * 计算缓存目录下的可用空间
         *
         * @param dir
         * @return
         */
        private static long calculateDiskCacheSize(File dir) {
            long size = 0;

            try {
                StatFs statFs = new StatFs(dir.getAbsolutePath());
                long available = statFs.getBlockCount() * statFs.getAvailableBlocks();
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }
            return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
        }
    }

}
