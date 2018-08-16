package com.hz.yian.circuit.breaker.aspectj;

import com.hz.yian.circuit.breaker.CircuitBreaker;
import com.hz.yian.circuit.breaker.anno.CircuitBreakerAnno;
import com.hz.yian.circuit.breaker.CircuitBreakerBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class CircuitBreakerAspectj {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerAspectj.class);
    private static final Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();

    @Around("execution(* *(..)) && @annotation(circuitBreakerAnno)")
    public Object around(ProceedingJoinPoint point, CircuitBreakerAnno circuitBreakerAnno) throws Throwable {
        //匹配获取熔断器
        CircuitBreaker circuitBreaker = CircuitBreakerBuilder.match(circuitBreakerAnno.commandKey());
        //获取fallback方法
        Class clazz = point.getTarget().getClass();
        Object[] params = point.getArgs();
        Method method = getMethod(params, circuitBreakerAnno.fallback(), clazz);

        //如果熔断器打开
        if (circuitBreaker.status()) {
            Object object = circuitBreaker.retry(point);
            if (object != null) {
                return object;
            }
            //调用fallback方法
            if (params.length == 0) {
                return method.invoke(point.getTarget());
            } else {
                return method.invoke(point.getTarget(), params);
            }
        }
        try {
            return point.proceed();
        } catch (Throwable throwable) {
            LOGGER.debug("throwable:{}", throwable);
            Class[] classes = circuitBreakerAnno.ignoreExecption();
            //过滤异常
            for (int i = 0; i < classes.length; i++) {
                if (classes[i] == throwable.getClass()) {
                    throw throwable;
                }
            }
            circuitBreaker.fail();
            //调用fallback方法
            if (params.length == 0) {
                return method.invoke(point.getTarget());
            } else {
                return method.invoke(point.getTarget(), params);
            }
        }
    }


    private static Method getMethod(Object[] params, String fallbackName, Class clazz) throws NoSuchMethodException {
        String methodKey = clazz.getName() + fallbackName;
        Method method = methodMap.get(methodKey);
        if (method == null) {
            Class[] paramClasss = new Class[params.length];
            if (params.length == 0) {
                method = clazz.getMethod(fallbackName);
            } else {
                for (int i = 0; i < params.length; i++) {
                    paramClasss[i] = params[i].getClass();
                }
                method = clazz.getMethod(fallbackName, paramClasss);
                if (method == null) {
                    Method[] methods = clazz.getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Method method1 = methods[i];
                        //如果方法名称和方法参数个数相同
                        if (method1.getName().equals(fallbackName)
                                && method1.getParameterTypes().length == paramClasss.length) {
                            method = method1;
                        }
                    }
                }
            }
            methodMap.put(methodKey, method);
        }
        return method;
    }

}
