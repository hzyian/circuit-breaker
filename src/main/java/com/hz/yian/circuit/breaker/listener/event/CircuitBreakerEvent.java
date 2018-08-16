package com.hz.yian.circuit.breaker.listener.event;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

/**
 * 熔断器事件
 */
public class CircuitBreakerEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CircuitBreakerEvent(Map<String, CircuitBreaker> source) {
        super(source);
    }

    public static class CircuitBreaker {
        //熔断器状态
        private boolean status;
        //当前失败数
        private int failCount;
        //统计时间窗口
        private long statisticalWindow;
        //时间窗口内，最大失败次数
        private int maxFailCount;
        //熔断器历史熔断次数
        private long circuitBreakerCount;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public int getFailCount() {
            return failCount;
        }

        public void setFailCount(int failCount) {
            this.failCount = failCount;
        }

        public long getStatisticalWindow() {
            return statisticalWindow;
        }

        public void setStatisticalWindow(long statisticalWindow) {
            this.statisticalWindow = statisticalWindow;
        }

        public int getMaxFailCount() {
            return maxFailCount;
        }

        public void setMaxFailCount(int maxFailCount) {
            this.maxFailCount = maxFailCount;
        }

        public long getCircuitBreakerCount() {
            return circuitBreakerCount;
        }

        public void setCircuitBreakerCount(long circuitBreakerCount) {
            this.circuitBreakerCount = circuitBreakerCount;
        }

        @Override
        public String toString() {
            return "CircuitBreaker{" +
                    "status=" + status +
                    ", failCount=" + failCount +
                    ", statisticalWindow=" + statisticalWindow +
                    ", maxFailCount=" + maxFailCount +
                    ", circuitBreakerCount=" + circuitBreakerCount +
                    '}';
        }
    }
}
