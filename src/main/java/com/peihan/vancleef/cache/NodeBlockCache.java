package com.peihan.vancleef.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeBlockCache {
    private static volatile NodeBlockCache INSTANCE;

    private Map<String,Object> cache;

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

    public void put(String key, Object object){
        cache.put(key,object);
    }

    public Map getAll(){
        return this.cache;
    }
}
