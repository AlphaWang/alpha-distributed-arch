package com.alphawang.distributed.hystrix.requestmerge;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Batch Command 实现
 */
public class BatchStockCommand extends HystrixCommand<List<Integer>> {

	private StockService stockService;
	private Collection<HystrixCollapser.CollapsedRequest<Integer, Long>> requests;

	public BatchStockCommand(StockService stockService, Collection<HystrixCollapser.CollapsedRequest<Integer, Long>> requests) {
		super(setter());
		this.stockService = stockService;
		this.requests = requests;
	}

	private static Setter setter() {
		return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("stockService_batch"));
	}

	@Override
	protected List<Integer> run() throws Exception {
		List<Long> ids = requests.stream()
			.map(req -> req.getArgument())
			.collect(Collectors.toList());
		return stockService.getStocks(ids);
	}
}
