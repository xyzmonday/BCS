package com.richfit.barcodesystemproduct.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载,先只考虑加载本地图片。采用内存Lru缓存技术。
 * 需要改进的地方:
 * 1. 考虑网络加载
 * 2. 考虑磁盘缓存（前提是如果考虑了1）
 * 3. 考虑bitmap解析时的内存优化
 * Created by monday on 2016/1/4.
 */
public class NativeImageLoader {

    /**
     * 内存缓存
     */
    private LruCache<String, Bitmap> mMemoryCache;
    /**
     * 子线程Handler，处理图片加载任务
     */
    private static Handler mSubThreadHandler;
    /**
     * 处理子线程和主线程并发访问mSubThreadHandler的同步工具
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * UI线程Handler,用来处理UI更新，比如设置imageview的背景
     */
    private static Handler mHandler;
    /**
     * 处理线程池内部任务队列并发同步工具(这里仅仅是为了体现LIFO,FIFO的效果，所以必须对线程池内的线程进行阻塞)
     */
    private volatile Semaphore mThreadPoolSemaphore;
    /**
     * 线程池线程的个数，也是同事处理任务的个数
     */
    private int mThreadCount;
    /**
     * 图片加载任务结合
     */
    private LinkedList<Runnable> mTasks;
    private ExecutorService mThreadPool;

    /**
     * 加载图片任务的类型
     */
    public enum TaskType {
        LIFO, FIFO
    }


    private TaskType mType;

    private static NativeImageLoader instance;

    //单例模式
    private NativeImageLoader(int threadCount, TaskType taskType) {
        this.mThreadCount = threadCount;
        this.mType = taskType;
        initImageLoader();

    }


    private NativeImageLoader() {
    }

    //懒汉模式(双重检验)
    public static NativeImageLoader getInstance(int threadCount, TaskType taskType) {
        if (instance == null) {
            synchronized (NativeImageLoader.class) {
                if (instance == null) {
                    instance = new NativeImageLoader(threadCount, taskType);
                }
            }
        }
        return instance;
    }

    //缺省的情况
    public static NativeImageLoader getInstance() {
        if (instance == null) {
            synchronized (NativeImageLoader.class) {
                if (instance == null) {
                    instance = new NativeImageLoader(1, TaskType.LIFO);
                }
            }
        }
        return instance;
    }


    /**
     * 初始化imageLoder
     */
    private void initImageLoader() {
        mThreadPool = Executors.newFixedThreadPool(mThreadCount);
        mThreadPoolSemaphore = new Semaphore(mThreadCount);
        mTasks = new LinkedList<>();
        //开辟子线程处理加载图片任务
        new Thread(() -> {
            Looper.prepare();//创建sThreadLocal变量保存Looper实例对象，在创建Looper对象的时候生成消息队列
            mSubThreadHandler = new Handler()//将handler与该线程绑定，关联Looper实例对象中的消息队列
            {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        //必须保证线程池里面的任务全部执行完毕之后，后续的任务才能执行
                        /**
                         * 比方说，现在有10个图片加载加载的任务（调用10次addTask方法），线程池的线程个数为3.我们先不考虑线程池是怎样优化调度这个三个线程的。
                         * 此时根据任务调度方式取出任务，而且线程池只能够同时去完成这个3个任务，其他的任务必须等待。
                         */
                        mThreadPoolSemaphore.acquire();
                        //线程池处理图片加载任务,任务完成后向主线程发送消息。
                        Runnable task = getTask();
                        if(task != null)
                        mThreadPool.execute(task);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };

            //消息循环，不断从消息队列里面获取要处理的消息
            Looper.loop();
            //释放信息号，说明子线程已经将mSubThreadHandler初始化完毕
            mSemaphore.release();
        }).start();


        final int maxSize = (int) Runtime.getRuntime().maxMemory();
        // 设置为可用内存的1/8（按Byte计算）
        final int cacheSize = maxSize / 8;
        //初始化内存缓存
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                if (bitmap != null) {
                    // 计算存储bitmap所占用的字节数
                    return bitmap.getRowBytes() * bitmap.getHeight();//getRowBytes：每一行所占用的字节数,getByteCount一个位图所占用的字节数
                } else {
                    return 0;
                }
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (evicted && oldValue != null) {
                    oldValue.recycle();
                }
            }
        };
    }


    private synchronized void addTask(Runnable task) {
        if (mSubThreadHandler == null) {
            //主线程阻塞，必须等待子线程将mSubThreadHander初始化完毕
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mTasks.add(task);
        mSubThreadHandler.sendEmptyMessage(0);
    }

    private synchronized Runnable getTask() {
        if (mType == TaskType.FIFO) {
            //先进先出(队列)
            return mTasks.removeFirst();
        }
        if (mType == TaskType.LIFO) {
            //后进先出（栈）
            return mTasks.removeLast();
        }
        return null;
    }


    /**
     * 图片加载
     *
     * @param imageview
     * @param imageUrl
     */
    public void loadImage(final ImageView imageview, final String imageUrl) {
        imageview.setTag(imageUrl);
        Bitmap bm = null;
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ViewHolder holder = (ViewHolder) msg.obj;
                    ImageView imageview = holder.mImageview;
                    Bitmap bitmap = holder.bitmap;
                    String path = holder.path;
                    if (imageview != null && path.equals(imageview.getTag()) && bitmap != null) {
                        imageview.setImageBitmap(bitmap);
                    }
                }
            };
        }

        bm = getBitmapFromMemoryCache((imageUrl));

        if (bm != null) {
            ViewHolder holder = new ViewHolder();
            holder.mImageview = imageview;
            holder.bitmap = bm;
            holder.path = imageUrl;
            Message msg = Message.obtain();
            msg.obj = holder;
            mHandler.sendMessage(msg);
        } else {
            addTask(() -> {
                //获取imageview的宽高
                Point point = getImageViewWidth(imageview);
                Bitmap bm1 = decodeBitmapFromFile(imageUrl, point.x, point.y);
                addBitmapToMemoryCache(imageUrl, bm1);
                ViewHolder holder = new ViewHolder();
                holder.path = imageUrl;
                holder.mImageview = imageview;
                holder.bitmap = bm1;
                Message msg = Message.obtain();
                msg.obj = holder;
                mHandler.sendMessage(msg);
                //释放信号，让其他的任务执行
                mThreadPoolSemaphore.release();
            });
        }
    }


    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            if (bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        if (mMemoryCache != null) {
            return mMemoryCache.get(key);
        } else {
            return null;
        }
    }


    /**
     * 封装decodeBitmapFromFile
     */
    private Bitmap decodeBitmapFromFile(String filePahth, int targetWidth, int targetHeight) {
        Bitmap bm = null;
        //获得BitmapFactory.Options
        BitmapFactory.Options options = new BitmapFactory.Options();
        //注意inJustDecodeBounds = true，那么解析返回的图片为null
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePahth, options);
        //获得图片新的压缩比例
        int inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);//这里我们的目标是ImageView的大小
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(filePahth, options);
        return bm;
    }


    /**
     * 获得图片的压缩比例
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int targetWith, int targetHeight) {
        int inSampleSize = 1;

        //获得解析出来原始图片的大小
        int picWidth = options.outWidth;
        int picHeight = options.outHeight;
        if (picWidth > targetWith || picHeight > targetHeight) {
            int scaleWidth = (int) Math.round(picWidth * 1.0 / (targetWith * 1.0));
            int scaleHeight = (int) Math.round(picHeight * 1.0 / (targetHeight * 1.0));
            inSampleSize = Math.max(scaleWidth, scaleHeight);
        }
        return inSampleSize;
    }


    /**
     * 根据ImageView获得适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    private Point getImageViewWidth(ImageView imageView) {
        Point point = new Point();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getWidth(); // Get actual image width
        if (width <= 0)
            width = params.width; // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels;
        int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = params.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;
        point.x = width;
        point.y = height;
        return point;

    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    class ViewHolder {
        ImageView mImageview;
        Bitmap bitmap;
        String path;
    }

    /**
     * 清除内存缓存，取消所有的图片加载任务
     */
    public void clearMemoryCacheAndCancelTask() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
    }
}
