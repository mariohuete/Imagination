package com.mariohuete.imagination.utils;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 * Created by Mario Huete Jiménez on 08/05/15.
 */
public class MemoryCache {
    // Last argument true for LRU ordering
    private Map<String, Bitmap> cache = Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
    // Current allocated size
    private long size = 0;
    // Max memory cache folder used to download images in bytes
    private long limit = 1000000;

    public MemoryCache() {
        //Use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void setLimit(long new_limit){
        limit = new_limit;
    }

    public Bitmap get(String id) {
        try {
            if(!cache.containsKey(id))
                return null;
            return cache.get(id);
        } catch(NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if(cache.containsKey(id))
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch(Throwable th) {
            th.printStackTrace();
        }
    }

    private void checkSize() {
        if(size > limit) {
            // Least recently accessed item will be the first one iterated
            Iterator<Map.Entry<String, Bitmap>> iter = cache.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if(size <= limit)
                    break;
            }
        }
    }

    public void clear() {
        try {
            // Clear cache
            cache.clear();
            size = 0;
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public long getSizeInBytes(Bitmap bitmap) {
        if(bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

}