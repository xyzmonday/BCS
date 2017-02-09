package com.richfit.data.cache.core.memory.journal;


import com.richfit.data.cache.core.CacheEntry;

/**
 * FIFO缓存日志
 * @version monday 2016-07
 */
public class FIFOMemoryJournal extends BasicMemoryJournal {

    @Override
    public String getLoseKey() {
        CacheEntry entry = null;
        for (CacheEntry item : getKeyValues().values()) {
            if (entry == null || entry.getCreateTime() > item.getCreateTime()) {
                entry = item;
            }
        }
        if (entry != null) {
            return entry.getKey();
        } else {
            return null;
        }
    }

}
