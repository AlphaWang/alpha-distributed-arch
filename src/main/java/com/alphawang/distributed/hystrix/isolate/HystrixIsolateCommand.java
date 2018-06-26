package com.alphawang.distributed.hystrix.isolate;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * Created by Alpha on 12/19/17.
 */
public class HystrixIsolateCommand extends HystrixCommand<Integer> {

	private StockService stockService;
	private Long id;

	public HystrixIsolateCommand(StockService stockService, Long id) {
		super(setter());
		this.stockService = stockService;
		this.id = id;
	}

	private static Setter setter() {
		HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("stock.isolate.group");
		HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("stock.isolate.command");
		HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("stock.isolate.threadpool");

		HystrixThreadPoolProperties.Setter threadpoolProperties = HystrixThreadPoolProperties.Setter()
			.withCoreSize(10)                      // 配置核心线程池大小
			.withMaximumSize(20)                   // 配置线程池最大大小
			.withKeepAliveTimeMinutes(5)           // 配置线程池中空闲线程生存时间
			.withMaxQueueSize(Integer.MAX_VALUE)   // 配置线程池队列最大大小
			.withQueueSizeRejectionThreshold(10000);


		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()

			// isolation settings.
			.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
			.withExecutionIsolationThreadInterruptOnFutureCancel(true)  // 隔离策略为Thread时，若线程执行超时，是否进行中断处理
			.withExecutionIsolationThreadInterruptOnTimeout(true)
			.withExecutionTimeoutEnabled(true)
			.withExecutionTimeoutInMilliseconds(1000);

		return Setter
			.withGroupKey(groupKey)
			.andCommandKey(commandKey)
			.andThreadPoolKey(threadPoolKey)
			.andThreadPoolPropertiesDefaults(threadpoolProperties)
			.andCommandPropertiesDefaults(commandProperties);
	}

	@Override
	protected Integer run() throws Exception {
		return stockService.getStock(id);
	}
	
}
