package com.alphawang.distributed.hystrix.requestmerge;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * -- Running StockService.getStocks() [4, 5]
 * -- Running StockService.getStocks() [1]
 * -- Running StockService.getStocks() [2, 3]
 */
@Slf4j
public class HystrixRequestCollasperTest {

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		HystrixRequestContext requestContext = HystrixRequestContext.initializeContext();
		try {

			StockService stockService = new StockService();

			HystrixRequestCollapserCommand command1 = new HystrixRequestCollapserCommand(stockService, 1L);
			HystrixRequestCollapserCommand command2 = new HystrixRequestCollapserCommand(stockService, 2L);
			HystrixRequestCollapserCommand command3 = new HystrixRequestCollapserCommand(stockService, 3L);
			HystrixRequestCollapserCommand command4 = new HystrixRequestCollapserCommand(stockService, 4L);
			HystrixRequestCollapserCommand command5 = new HystrixRequestCollapserCommand(stockService, 4L);

			Future<Integer> future1 = command1.queue();
			Future<Integer> future2 = command2.queue();
			Future<Integer> future3 = command3.queue();
			Future<Integer> future4 = command4.queue();
			Future<Integer> future5 = command5.queue();

			log.info("result 1: " + future1.get());
			log.info("result 2: " + future2.get());
			log.info("result 3: " + future3.get());
			log.info("result 4: " + future4.get());
			log.info("result 5: " + future5.get());
		} finally {
			requestContext.shutdown();
		}
	}
}
