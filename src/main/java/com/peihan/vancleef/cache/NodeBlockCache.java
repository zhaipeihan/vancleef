package com.peihan.vancleef.cache;

import com.peihan.vancleef.model.Block;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeBlockCache {
    private static volatile NodeBlockCache INSTANCE;

    private Map<String,List<Block>> cache;

    private NodeBlockCache(){
        this.cache = new ConcurrentHashMap<>(128);
    }

    public static NodeBlockCache getInstance() {
        if(INSTANCE == null){
            synchronized (NodeBlockCache.class){
                if(INSTANCE == null){
                    INSTANCE = new NodeBlockCache();
                }
            }
        }
        return INSTANCE;
    }

    public boolean containsKey(String key){
        return cache.containsKey(key);
    }

    public Object get(String key){
        return cache.getOrDefault(key,null);
    }

    public void put(String key, List<Block> blocks){
        cache.put(key,blocks);
    }

    public Map<String,List<Block>> getAll(){
        return this.cache;
    }

    public boolean isEmpty(){
        return MapUtils.isEmpty(cache);
    }
}
