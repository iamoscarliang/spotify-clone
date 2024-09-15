package com.oscarliang.spotifyclone.core.common.scheduler;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Scheduled {

    SchedulerType value();

}