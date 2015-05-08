package com.senacor.rxjavatechielunch;

public class ThreadTracer {
    public static void printThreadName() {
        Thread currentThread = Thread.currentThread();
        StackTraceElement[] stackTrace = currentThread.getStackTrace();
        String methodName = stackTrace[2].getMethodName();
        System.err.println(methodName +  " executed in " + currentThread.getName());
    }
}
