package com.hudq.visitor.intern;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hudq on 2016/12/15.
 */

public class $$ {

    private static volatile ExecutorService $;

    public static void execute(Runnable runnable) {
        if ($ == null) {
            $ = Executors.newCachedThreadPool();
        }
        $.execute(runnable);
    }

    public static void shutdown() {
        if ($ != null) {
            $.shutdown();
        }
    }

    public static void shutdownNow() {
        if ($ != null) {
            $$.shutdownNow();
        }
    }
}
