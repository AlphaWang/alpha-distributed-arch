package com.alphawang.distributed.hystrix.requestcache;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class HystrixRequestCacheCommand extends HystrixCommand<Integer> {

	private StockService stockService;
	private Long id;

	protected HystrixRequestCacheCommand(StockService stockService, Long id) {
		super(setter());
		this.id = id;
		this.stockService = stockService;
	}

	private static Setter setter() {
		HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("stock.cache");

		HystrixCommandProperties.Setter commandPropertiesSetter = HystrixCommandProperties.Setter()
			.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
			.withRequestCacheEnabled(true);

		return Setter
			.withGroupKey(groupKey)
			.andCommandPropertiesDefaults(commandPropertiesSetter);
	}

	@Override
	protected Integer run() throws Exception {
		return stockService.getStock(id);
	}

	@Override
	public String getCacheKey() {
		return "product_" + id;
	}

}
