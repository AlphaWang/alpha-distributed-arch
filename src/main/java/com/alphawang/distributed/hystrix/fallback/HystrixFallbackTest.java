package com.alphawang.distributed.hystrix.fallback;

import com.alphawang.distributed.hystrix.StockService;
import com.netflix.hystrix.HystrixEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Alpha on 12/20/17.
 */
@Component
public class HystrixFallbackTest {

	@Autowired
	private StockService stockService; // return fallback response if stockService is null.

	public void testFallback() {
		StockService stockService = new StockService();
		for (long i = 0; i <= 50; i++) {
			HystrixFallbackCommand command = new HystrixFallbackCommand(stockService, i);

			Integer stock = command.execute();
			Boolean fallBack = command.isResponseFromFallback(); //don't call it before execute.

			System.out.println("isResponseFromFallback(wrong) : " + fallBack);

			System.out.println("result : " + stock);
			System.out.println("commandGroup : " + command.getCommandGroup());
			System.out.println("isResponseFromFallback : " + command.isResponseFromFallback());
			System.out.println("isResponseShortCircuited : " + command.isResponseShortCircuited());
		}
	}


	public void testCircuitBreaker() {
		stockService = new StockService() {
			@Override
			public Integer getStock(Long id) {
				System.err.println("CIRCUIT ERROR");
				throw new RuntimeException("Mock Error");
			}
		};


		for (long i = 0; i <= 50; i++) {
			HystrixFallbackCommand command = new HystrixFallbackCommand(stockService, i);
			Integer stock = command.execute();
			System.out.println("result : " + stock);
			System.out.println("commandGroup : " + command.getCommandGroup());
			System.out.println("isResponseFromFallback : " + command.isResponseFromFallback());
			System.out.println("isResponseShortCircuited : " + command.isResponseShortCircuited());

			System.out.println("[Metrics] executionTimeMean : " + command.getMetrics().getExecutionTimeMean());
			System.out.println("[Metrics] fallback success cumulative count : " + command.getMetrics().getCumulativeCount(HystrixEventType.FALLBACK_SUCCESS));

			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		HystrixFallbackTest test = new HystrixFallbackTest();

		test.testFallback();
//		test.testCircuitBreaker();
	}
}
