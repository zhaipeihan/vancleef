package com.peihan.vancleef.util;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static volatile ExecutorService executorService = null;

    public static ExecutorService getExecutors() {
        if (Objects.isNull(executorService)) {
            synchronized (ThreadUtil.class) {
                if (Objects.isNull(executorService)) {
                    executorService = Executors.newFixedThreadPool(10);
                }
            }
        }
        return executorService;
    }








}
