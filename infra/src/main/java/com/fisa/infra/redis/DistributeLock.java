package com.fisa.infra.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {

    //분산락 key 이름
    String key(); // (1)

    //분산락 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS; // (2)

    //분산락을 얻기 위한 대기 시간,
    //다른 스레드나 프로세스가 해당 락을 풀어줘야 락을 획득할 수 있음
    long waitTime() default 5L; // (3)

    //분삭락 유지 시간으로 락을 획득한 이후에 해당 시간동안 분산락이 유지됨
    //시간이 종료되면 자동으로 락이 해체됨
    long leaseTime() default 3L; // (4)
}
