package com.hz.yian.circuit.breaker.listener;

import com.hz.yian.circuit.breaker.CircuitBreaker;
import com.hz.yian.circuit.breaker.listener.event.CircuitBreakerEvent;

import java.util.*;

/**
 * 默认事件通知
 */
public abstract class DefaultCircuitBreakerListener implements EventListener {

    public void click(Map<String, CircuitBreaker> map) {
        if (map == null || map.size() <= 0) {
            return;
        }
        Map<String, CircuitBreakerEvent.CircuitBreaker> circuitBreakerMap = new HashMap<String, CircuitBreakerEvent.CircuitBreaker>(map.size());
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            CircuitBreaker circuitBreaker = map.get(key);
            CircuitBreakerEvent.CircuitBreaker breaker = new CircuitBreakerEvent.CircuitBreaker();
            breaker.setFailCount(circuitBreaker.failCount().get());
            breaker.setMaxFailCount(circuitBreaker.circuitBreakerConfig().maxFailCount());
            breaker.setStatisticalWindow(circuitBreaker.circuitBreakerConfig().statisticalWindow());
            breaker.setStatus(circuitBreaker.status());
            breaker.setCircuitBreakerCount(circuitBreaker.circuitBreakerCount());
            circuitBreakerMap.put(key, breaker);
        }
        CircuitBreakerEvent event = new CircuitBreakerEvent(circuitBreakerMap);
        listener(event);
    }

    protected abstract void listener(CircuitBreakerEvent event);
}
