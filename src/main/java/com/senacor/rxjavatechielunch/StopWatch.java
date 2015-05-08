package com.senacor.rxjavatechielunch;

public class StopWatch {
    public static void measureAndPrintTime(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start));
    }
}
