package com.senacor.rxjavatechielunch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CustomerSchedulers {

    static Scheduler scheduler(String name, int poolSize) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(name + "-%d")
                .setDaemon(true)
                .build();
        return Schedulers.from(Executors.newFixedThreadPool(poolSize, threadFactory));
    }

}
