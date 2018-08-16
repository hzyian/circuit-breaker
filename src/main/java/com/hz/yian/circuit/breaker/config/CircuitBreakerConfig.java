package com.hz.yian.circuit.breaker.config;

public class CircuitBreakerConfig {
    //统计时间窗口
    private long statisticalWindow;
    //key
    private String commandKey;
    //时间窗口内，最大失败次数
    private int maxFailCount;
    //是否需要自动恢复
    private boolean autoRecovery;
    //自动恢复时间窗口
    private long autoRecoveryStatisticalWindow;

    public long statisticalWindow() {
        return statisticalWindow;
    }

    public CircuitBreakerConfig statisticalWindow(long statisticalWindow) {
        this.statisticalWindow = statisticalWindow;
        return this;
    }

    public int maxFailCount() {
        return maxFailCount;
    }

    public CircuitBreakerConfig maxFailCount(int maxFailCount) {
        this.maxFailCount = maxFailCount;
        return this;
    }

    public boolean autoRecovery() {
        return autoRecovery;
    }

    public CircuitBreakerConfig autoRecovery(boolean autoRecovery) {
        this.autoRecovery = autoRecovery;
        return this;
    }

    public long autoRecoveryStatisticalWindow() {
        return autoRecoveryStatisticalWindow;
    }

    public CircuitBreakerConfig autoRecoveryStatisticalWindow(long autoRecoveryStatisticalWindow) {
        this.autoRecoveryStatisticalWindow = autoRecoveryStatisticalWindow;
        return this;
    }

    public String commandKey() {
        return commandKey;
    }

    public CircuitBreakerConfig commandKey(String commandKey) {
        this.commandKey = commandKey;
        return this;
    }
}
