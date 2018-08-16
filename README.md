# circuit-breaker
内存熔断器

在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致级联故障，进而造成整个系统不可用的情况，这种现象被称为服务雪崩效应。
为了应对服务雪崩, 一种常见的做法是通过hystrix对非核心依赖熔断降级。

hystrix提供两种熔断降级的方式：1:基于线程，2:基于信号量；不管哪种方式都需要不断的创建新的对象（用线程模式，会频繁的线程切换），会加快ygc的回收频率，对于时间敏感型接口影响比较大。

所以需要一套更为简洁内存熔断器

# 流程
![Aaron Swartz](https://raw.githubusercontent.com/hzyian/circuit-breaker/master/%E7%86%94%E6%96%AD%E5%99%A8%E9%80%BB%E8%BE%91.png)

# 接入
#### 1:熔断器配置
classpath目录下添加上circuit_breaker.properties配置文件
```
//表面多种配置
circuit_breaker_common_keys=key1,key2
//最大失败数
key1_max_fail_count=10
//时间窗口内，最大失败次数
key1_statistical_window=10000
//是否需要自动恢复
key1_auto_recovery=true
//自动恢复时间窗口
key1_auto_recovery_statistical_window=5000
 
key2_max_fail_count=10
key2_statistical_window=10000
key2_auto_recovery=true
key2_auto_recovery_statistical_window=5000

```
#### 2:使用
```
@CircuitBreakerAnno(commandKey = "key1",fallback = "fallbackMethod",ignoreExecption = {Exception.class,IndexOutOfBoundsException.class})
public boolean method(String key, Object obj, int seconds) {
    执行正常操作
}
public boolean fallbackMethod(String key, Object obj, int seconds) {
    执行fallback内容
}
```
1）commandKey：指明使用何种熔断器

2）fallbackMethodName：指明fallback方法

3）ignoreExecption：忽略的异常类型，即该异常不会用于熔断器计数，会直接抛出

#### 3:接收日志
继承DefaultCircuitBreakerListener类,每30s会打印一次熔断器日志信息，主要包括如下信息：
```
    //状态
    private boolean status;
    //熔断器当前失败数
    private int failCount;
    //熔断器时间统计窗口
    private long statisticalWindow;
    //熔断器失败数
    private int maxFailCount;
    //熔断器熔断次数
    private long circuitBreakerCount;
```


#### 4:启动
```
//如果需要监听熔断器内部状态日志，需要监听熔断器
CircuitBreakerBuilder.listener(new listener日志);
CircuitBreakerBuilder.build();
```
