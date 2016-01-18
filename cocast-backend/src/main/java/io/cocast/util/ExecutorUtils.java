package io.cocast.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that handles asynchronous executions
 */
public class ExecutorUtils {

    private static ExecutorService executorService;

    private static final Integer THREAD_POOL_SIZE = 5;

    static {
        executorService = Executors.newFixedThreadPool(5);
    }

    /**
     * Executes a service in background
     */
    public static void execute(Runnable command) {
        executorService.execute(command);
    }
}
