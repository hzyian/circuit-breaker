package com.hz.yian.circuit.breaker;

import com.hz.yian.circuit.breaker.config.CircuitBreakerConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);
    //熔断器状态
    private AtomicBoolean status;
    //初始化时间
    private volatile long initTime;

    private AtomicInteger failCount;

    private CircuitBreakerConfig circuitBreakerConfig;

    private volatile long lastRetryTime;

    private AtomicLong circuitBreakerCount;

    private AtomicBoolean retryStatus = new AtomicBoolean(false);


    public static CircuitBreaker builder() {
        return new CircuitBreaker();
    }

    public CircuitBreaker circuitBreakerConfig(CircuitBreakerConfig circuitBreakerConfig) {
        this.circuitBreakerConfig = circuitBreakerConfig;
        return this;
    }

    protected void close() {
        if (status.compareAndSet(true, false)) {
            this.failCount.set(0);
            this.initTime = System.currentTimeMillis();
            LOGGER.warn("commandKey " + circuitBreakerConfig.commandKey() + " ,Circuit Breaker is close!!!!!!!!!!!!!!!!!!!");
        }
    }

    protected void refresh(int value) {
        if (!status()) {
            long now = System.currentTimeMillis();
            this.failCount.set(value);
            this.initTime = now;
            this.lastRetryTime = now;
        }
    }

    /**
     * 熔断器计数
     *
     * @return
     */
    public void fail() {
        //判断是否在统计时间窗口内
        if (System.currentTimeMillis() - initTime <= circuitBreakerConfig.statisticalWindow()) {
            if (status.get()) {
                return;
            }
            //如果次数达到最大值，则进行熔断，并且报警
            if (failCount.get() >= circuitBreakerConfig.maxFailCount()) {
                if (status.compareAndSet(false, true)) {
                    circuitBreakerCount.incrementAndGet();
                    LOGGER.warn("commandKey " + circuitBreakerConfig.commandKey() + " ,Circuit Breaker is open!!!!!!!!!!!!!!!!!!!");
                }
                return;
            }
            failCount.incrementAndGet();
            return;
        }
        refresh(1);
    }

    //判断是否需要重试
    public Object retry(final ProceedingJoinPoint point) {
        if (!circuitBreakerConfig.autoRecovery()) {
            return null;
        }
        long now = System.currentTimeMillis();

        if (now - lastRetryTime >= circuitBreakerConfig.autoRecoveryStatisticalWindow() && retryStatus.compareAndSet(false, true)) {
            if (now - lastRetryTime >= circuitBreakerConfig.autoRecoveryStatisticalWindow()) {
                if (status()) {
                    try {
                        Object object = point.proceed();
                        close();
                        return object;
                    } catch (Throwable e) {
                        return null;
                    } finally {
                        lastRetryTime = now;
                        retryStatus.set(false);
                    }
                }
            }
        }
        return null;
    }

    private CircuitBreaker() {
        long now = System.currentTimeMillis();
        this.status = new AtomicBoolean(false);
        this.initTime = now;
        this.failCount = new AtomicInteger(0);
        this.lastRetryTime = now;
        this.circuitBreakerCount = new AtomicLong(0);
    }

    public boolean status() {
        return status.get();
    }

    public long circuitBreakerCount() {
        return circuitBreakerCount.get();
    }

    public AtomicInteger failCount() {
        return failCount;
    }

    public CircuitBreakerConfig circuitBreakerConfig() {
        return circuitBreakerConfig;
    }

}
