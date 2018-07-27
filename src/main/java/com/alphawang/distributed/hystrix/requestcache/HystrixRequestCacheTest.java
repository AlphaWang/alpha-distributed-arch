package com.alphawang.distributed.hystrix.requestcache;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 原理：
 * Hystrix使用了 ThreadLocal HystrixRequestContext实现，
 * 在异步线程执行之前注入  ThreadLocal HystrixRequestContext，
 * 从而实现请求级别的缓存
 */
@Slf4j
public class HystrixRequestCacheTest {

	public static void main(String[] args) {

		/**
		 * 必须要有 HystrixRequestContext， 否则会报错
		 */
//		19:50:10.236 [main] DEBUG com.netflix.hystrix.AbstractCommand - HystrixRequestCacheCommand failed while executing.
//			java.util.concurrent.ExecutionException: Observable onError
//		at rx.internal.operators.BlockingOperatorToFuture$2.getValue(BlockingOperatorToFuture.java:118)
//		at rx.internal.operators.BlockingOperatorToFuture$2.get(BlockingOperatorToFuture.java:102)
//		at com.netflix.hystrix.HystrixCommand$4.get(HystrixCommand.java:423)
//		at com.netflix.hystrix.HystrixCommand.queue(HystrixCommand.java:436)
//		at com.netflix.hystrix.HystrixCommand.execute(HystrixCommand.java:344)
//		at com.alphawang.microservice.hystrix.requestcache.HystrixRequestCacheTest.main(HystrixRequestCacheTest.java:14)
//		Caused by: java.lang.IllegalStateException: Request caching is not available. Maybe you need to initialize the HystrixRequestContext?

		HystrixRequestContext context = HystrixRequestContext.initializeContext();

		try {
			StockService stockService = new StockService();

			HystrixRequestCacheCommand cacheCommand1 = new HystrixRequestCacheCommand(stockService, 1L);
			HystrixRequestCacheCommand cacheCommand2 = new HystrixRequestCacheCommand(stockService, 1L);

			Integer result1 = cacheCommand1.execute();
			Integer result2 = cacheCommand2.execute();

			log.info("1st request from cache? {}", cacheCommand1.isResponseFromCache());
			log.info("2nd request from cache? {}", cacheCommand2.isResponseFromCache());
		} finally {
			context.shutdown();
		}
	}
}
