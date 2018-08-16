package com.hz.yian.circuit.breaker;

import com.hz.yian.circuit.breaker.config.CircuitBreakerConfig;
import com.hz.yian.circuit.breaker.exception.CircuitBreakerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 熔断器
 */
public class CircuitBreakerConfiguration {

    private Map<String, CircuitBreaker> circuitBreakers;

    public CircuitBreakerConfiguration build(List<CircuitBreakerConfig> circuitBreakerConfigs) throws IOException {
        if (circuitBreakerConfigs.isEmpty()) {
            circuitBreakerConfigs = properties();
        }
        circuitBreakers = new HashMap<String, CircuitBreaker>(circuitBreakerConfigs.size());
        for (int i = 0; i < circuitBreakerConfigs.size(); i++) {
            CircuitBreakerConfig circuitBreakerConfig = circuitBreakerConfigs.get(i);
            circuitBreakers.put(circuitBreakerConfig.commandKey(), CircuitBreaker.builder()
                    .circuitBreakerConfig(circuitBreakerConfig));
        }
        return this;
    }


    private List<CircuitBreakerConfig> properties() throws IOException {
        Properties properties = new Properties();
        InputStream in = CircuitBreakerBuilder.class.getClassLoader().getResourceAsStream("circuit_breaker.properties");
        properties.load(in);
        String commonKeys = properties.getProperty("circuit_breaker_common_keys");
        if (commonKeys == null || commonKeys.isEmpty()) {
            throw new CircuitBreakerException("circuit_breaker_common_keys is empty");
        }
        String[] keys = commonKeys.split(",");
        List<CircuitBreakerConfig> list = new ArrayList<CircuitBreakerConfig>();

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            list.add(new CircuitBreakerConfig().maxFailCount(Integer.parseInt(properties.getProperty(key + "_max_fail_count")))
                    .statisticalWindow(Long.parseLong(properties.getProperty(key + "_statistical_window")))
                    .commandKey(key)
                    .autoRecovery(Boolean.parseBoolean(properties.getProperty(key + "_auto_recovery", "false")))
                    .autoRecoveryStatisticalWindow(Long.parseLong(properties.getProperty(key + "_auto_recovery_statistical_window"))));
        }
        return list;
    }


    /**
     * 获取当前熔断器
     *
     * @return true:表示熔断器打开
     * false表示熔断器关闭
     */
    public CircuitBreaker match(String commonKey) {
        if (!circuitBreakers.containsKey(commonKey)) {
            throw new RuntimeException("commonkey is empty");
        }
        return circuitBreakers.get(commonKey);

    }

    public static CircuitBreakerConfiguration builder() {
        return new CircuitBreakerConfiguration();
    }

    public Map<String, CircuitBreaker> circuitBreakers() {
        return circuitBreakers;
    }

    private CircuitBreakerConfiguration() {
    }
}
