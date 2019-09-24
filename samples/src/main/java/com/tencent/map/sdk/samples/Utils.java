package com.tencent.map.sdk.samples;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Utils {

    private static Executor sUIThreadExecutor = Executors.newSingleThreadExecutor();
    private static Executor sHighThreadExecutor = Executors.newFixedThreadPool(3);

    public static Executor obtainUIThreadExecutor() {
        return sUIThreadExecutor;
    }

    public static Executor obtainHighThreadExecutor() {
        return sHighThreadExecutor;
    }
}
