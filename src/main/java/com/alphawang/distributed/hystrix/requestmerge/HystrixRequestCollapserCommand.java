package com.alphawang.distributed.hystrix.requestmerge;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HystrixRequestCollapserCommand extends HystrixCollapser<List<Integer>, Integer, Long> {

	private StockService stockService;
	private Long id;

	public HystrixRequestCollapserCommand(StockService stockService, Long id) {
		super(setter());
		this.stockService = stockService;
		this.id = id;
	}

	private static Setter setter() {
		return Setter
			.withCollapserKey(HystrixCollapserKey.Factory.asKey("stockService_collapser"))
			.andCollapserPropertiesDefaults(
				HystrixCollapserProperties
					.Setter()
					// 每个请求合并 允许的最大请求书
					.withMaxRequestsInBatch(2)
					// 在批处理执行之前的等待超时时间
					.withTimerDelayInMilliseconds(5)
					// 消除重复请求
					.withRequestCacheEnabled(true)
			)
			// 请求合并的范围
			.andScope(Scope.REQUEST);
	}

	/**
	 * 返回请求参数
	 */
	@Override
	public Long getRequestArgument() {
		return id;
	}

	/**
	 * 创建可批处理的Command.
	 * 当合并之后数量达到 MaxRequestsInBatch， 或者 TimerDelayInMilliseconds 超时后就会创建此Command.
	 */
	@Override
	protected HystrixCommand<List<Integer>> createCommand(Collection<CollapsedRequest<Integer, Long>> collapsedRequests) {
		return new BatchStockCommand(stockService, collapsedRequests);
	}

	/**
	 * 将执行结果映射到请求中。
	 */
	@Override
	protected void mapResponseToRequests(List<Integer> batchResponse, Collection<CollapsedRequest<Integer, Long>> collapsedRequests) {
		final AtomicInteger count = new AtomicInteger(0);
		collapsedRequests.forEach( request -> {
			request.setResponse(batchResponse.get(count.getAndIncrement()));
		});
	}
}
