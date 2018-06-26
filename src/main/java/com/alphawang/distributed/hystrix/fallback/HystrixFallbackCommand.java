package com.alphawang.distributed.hystrix.fallback;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * Created by Alpha on 12/19/17.
 */
public class HystrixFallbackCommand extends HystrixCommand<Integer> {

	private StockService stockService;
	private Long id;

	public HystrixFallbackCommand(StockService stockService, Long id) {
		super(setter());
		this.stockService = stockService;
		this.id = id;
	}

	private static Setter setter() {
		HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("stock.fallback");

		HystrixCommandProperties.Setter commandPropertiesSetter = HystrixCommandProperties.Setter()

			// isolation settings.
			.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
			.withExecutionIsolationThreadInterruptOnFutureCancel(true)  // 隔离策略为Thread时，若线程执行超时，是否进行中断处理
			.withExecutionIsolationThreadInterruptOnTimeout(true)
			.withExecutionTimeoutEnabled(true)
			.withExecutionTimeoutInMilliseconds(1000)

			// fallback settings.
			.withFallbackEnabled(true)
			.withFallbackIsolationSemaphoreMaxConcurrentRequests(100)  // 如果请求超过并发量，则不再尝试调用getFallback()，而是快速失败 抛出异常

			// circuit breaker settings.
			.withCircuitBreakerEnabled(true)
			.withCircuitBreakerForceClosed(false)
			.withCircuitBreakerForceOpen(false)
			.withCircuitBreakerErrorThresholdPercentage(50)  // 错误比例
			.withCircuitBreakerRequestVolumeThreshold(5)     // 判断失败率之前，一个采样周期内必须至少进行 N 个请求
			.withCircuitBreakerSleepWindowInMilliseconds(5000) // 熔断后的重试时间窗口

			// metrics settings.
			.withMetricsRollingPercentileWindowInMilliseconds(1000)  // 采样滚转时间
			.withMetricsRollingStatisticalWindowBuckets(10)          // 滚转时间内的 桶 的总数量. --一个桶就是一次采样？
			.withMetricsHealthSnapshotIntervalInMilliseconds(500)    // 健康采样时间的快照频率
			;

		return Setter
			.withGroupKey(groupKey)
			.andCommandPropertiesDefaults(commandPropertiesSetter);
	}

	@Override
	protected Integer run() throws Exception {
		return stockService.getStock(id);
	}

	@Override
	protected Integer getFallback() {
		return 0;
	}
}
