package com.senacor.rxjavatechielunch;

/**
 * Created by tkreylin on 07.05.2015.
 */
public class StopWatch {
    public static void measureAndPrintTime(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start));
    }
}
