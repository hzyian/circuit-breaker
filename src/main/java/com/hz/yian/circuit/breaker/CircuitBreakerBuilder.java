package com.hz.yian.circuit.breaker;

import com.hz.yian.circuit.breaker.config.CircuitBreakerConfig;
import com.hz.yian.circuit.breaker.exception.CircuitBreakerException;
import com.hz.yian.circuit.breaker.listener.DefaultCircuitBreakerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 熔断器执行器
 */
public class CircuitBreakerBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerBuilder.class);
    private static CircuitBreakerConfiguration circuitBreakerConfiguration;

    private static DefaultCircuitBreakerListener listener;

    private static final Object object = new Object();

    private static final List<CircuitBreakerConfig> circuitBreakerConfigs = new ArrayList<CircuitBreakerConfig>();

    /**
     * 初始化
     *
     * @throws IOException
     */
    public static void build() throws IOException {
        if (circuitBreakerConfiguration == null) {
            synchronized (object) {
                if (circuitBreakerConfiguration == null) {
                    circuitBreakerConfiguration = CircuitBreakerConfiguration.builder();
                    circuitBreakerConfiguration.build(circuitBreakerConfigs);
                    listenerStart();
                }
            }
        }
    }

    /**
     * 匹配熔断器
     *
     * @param commonKey
     * @return
     */
    public static CircuitBreaker match(String commonKey) {
        if (circuitBreakerConfiguration == null) {
            throw new CircuitBreakerException("circuitBreaker not init, Please init circuitBreaker！！！！！");
        }
        return circuitBreakerConfiguration.match(commonKey);
    }

    /**
     * 判断熔断器的状态
     *
     * @param commonKey
     * @return
     */
    public static boolean status(String commonKey) {
        return match(commonKey).status();
    }


    /**
     * 添加监听器
     *
     * @param listener
     */
    public static void listener(DefaultCircuitBreakerListener listener) {
        CircuitBreakerBuilder.listener = listener;
    }

    /**
     * 启动监听器
     */
    private static void listenerStart() {
        /**
         * 定时器、上报熔断器的监控状态
         *
         * 每30秒执行一次
         */
        if (listener != null) {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.click(circuitBreakerConfiguration.circuitBreakers());
                                                    }
                                                },
                    30,
                    30,
                    TimeUnit.SECONDS);
            return;
        }
        LOGGER.warn("not find CircuitBreaker listener!!!!!!!!!!!!!!!!!!!");
    }
}
