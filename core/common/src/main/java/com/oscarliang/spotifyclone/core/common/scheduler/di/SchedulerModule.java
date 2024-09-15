package com.oscarliang.spotifyclone.core.common.scheduler.di;

import com.oscarliang.spotifyclone.core.common.scheduler.Scheduled;
import com.oscarliang.spotifyclone.core.common.scheduler.SchedulerType;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Module
public class SchedulerModule {

    @Scheduled(SchedulerType.IO)
    @Provides
    public Scheduler provideIOScheduler() {
        return Schedulers.io();
    }

    @Scheduled(SchedulerType.COMPUTATION)
    @Provides
    public Scheduler provideComputationScheduler() {
        return Schedulers.computation();
    }

}