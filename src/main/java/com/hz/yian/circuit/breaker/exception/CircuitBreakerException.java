package com.hz.yian.circuit.breaker.exception;

/**
 * 熔断器异常
 */
public class CircuitBreakerException extends RuntimeException {
    public CircuitBreakerException() {
    }

    public CircuitBreakerException(String message) {
        super(message);
    }
}
