package com.richfit.data.cache.core.memory.journal;


import com.richfit.common_lib.exception.NullException;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.data.cache.core.CacheEntry;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * 缓存日志-基类
 *
 * @version monday 2016-07
 */
public abstract class BasicMemoryJournal implements IMemoryJournal {

    private final LinkedHashMap<String, CacheEntry> mKeyValues;

    public BasicMemoryJournal() {
        this.mKeyValues = new LinkedHashMap<>(0, 0.75f, true);
    }

    final LinkedHashMap<String, CacheEntry> getKeyValues() {
        return mKeyValues;
    }

    @Override
    public CacheEntry get(String key) {
        if (CommonUtil.isEmpty(key)) {
            throw new NullException("key == null");
        }

        CacheEntry entry = mKeyValues.get(key);
        if (entry != null) {
            // 有效期内，才记录最后使用时间
            if (!entry.isExpiry()) {
                entry.setLastUseTime(System.currentTimeMillis());
                entry.setUseCount(entry.getUseCount() + 1);
            }
            //返回信息缓存日志
            return CacheEntry.cloneCacheEntry(entry);
        } else {
            return null;
        }
    }

    @Override
    public boolean put(String key, CacheEntry entry) {
        if (CommonUtil.isEmpty(key) || entry == null) {
            throw new NullException("key == null || value == null");
        }

        if (!entry.isExpiry()) {
            entry.setLastUseTime(System.currentTimeMillis());
            entry.setUseCount(1);
            return mKeyValues.put(key, entry) != null;
        } else {
            return remove(key);
        }
    }

    @Override
    public boolean containsKey(String key) {
        if (CommonUtil.isEmpty(key)) {
            throw new NullException("key == null");
        }

        CacheEntry entry = mKeyValues.get(key);
        return entry != null;
    }

    @Override
    public abstract String getLoseKey();

    @Override
    public boolean remove(String key) {
        if (CommonUtil.isEmpty(key)) {
            throw new NullException("key == null");
        }

        return mKeyValues.remove(key) != null;
    }

    @Override
    public boolean clear() {
        mKeyValues.clear();
        return true;
    }

    @Override
    public Collection<CacheEntry> snapshot() {
        return mKeyValues.values();
    }

    @Override
    public void close() throws IOException {
        // TODO Nothing
    }

}
