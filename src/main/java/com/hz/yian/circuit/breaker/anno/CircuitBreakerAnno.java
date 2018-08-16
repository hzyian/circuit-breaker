package com.hz.yian.circuit.breaker.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreakerAnno {
    String commandKey();

    String fallback();

    //忽略的异常类型
    Class[] ignoreExecption() default {};
}
