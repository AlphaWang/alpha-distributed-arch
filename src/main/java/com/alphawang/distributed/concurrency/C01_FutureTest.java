package com.alphawang.distributed.concurrency;

import com.alphawang.distributed.concurrency.mock.HttpService;
import com.alphawang.distributed.concurrency.mock.RpcService;
import com.alphawang.distributed.util.Printer;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alpha on 1/4/18.
 *
 * ﻿实现方式：使用线程池配合Future实现，可以并发发出N个请求。
 * 性能：响应时间为最慢的那个请求的用时。
 * 缺点：但是要阻塞主请求线程；高并发时依然会造成线程数过多、CPU上下文切换。
 *
 *
 * 实际耗时为最长的call, 2000ms
 *
 * RESULT:
 *
 * [0] [main] Main Thread START
 * [67] [pool-1-thread-1] submit callable 1.
 * [67] [main] Main Thread getting results.
 * [67] [pool-1-thread-2] submit callable 2.
 *
 * [1067] [pool-1-thread-1] [RpcService] delay 1000ms.
 * [1068] [pool-1-thread-1] callable 1 return: Rpc Result with 1000ms delay
 * [1068] [main] Result 1 : Rpc Result with 1000ms delay
 *
 * [2070] [pool-1-thread-2] [HttpService] delay 2000ms.
 * [2070] [pool-1-thread-2] callable 2 return: HTTP Result with 2000ms delay
 * [2071] [main] Result 2 : HTTP Result with 2000ms delay
 * 
 * [2071] [main] Main Thread END.
 *
 */
@Slf4j
public class C01_FutureTest {

	private final static ExecutorService executor = Executors.newFixedThreadPool(2);

	public static void main(String[] args) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		log.warn("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "Main Thread START");

		RpcService rpcService = new RpcService();
		HttpService httpService = new HttpService();

		Future<String> future1 = executor.submit(() -> {
			log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "submit callable 1.");
			String result = rpcService.getRpcResult();
			log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "callable 1 return: " + result);

			return result;
		});
		Future<String> future2 = executor.submit(() -> {
			log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "submit callable 2.");
			String result = httpService.getHttpResult();
			log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "callable 2 return: " + result);
			
			return result;
		});

		log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "Main Thread getting results.");
		try {
			String result1 = future1.get(5000, TimeUnit.MILLISECONDS);
			log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "Result 1 : " + result1);

			String result2 = future2.get(5000, TimeUnit.MILLISECONDS);
			log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "Result 2 : " + result2);
			
		} catch (Exception e) {
			if (future1 != null) {
				future1.cancel(true);
			}
			if (future2 != null) {
				future2.cancel(true);
			}
			e.printStackTrace();
		}

		/**
		 * 并发发出N个请求，然后等待最慢的一个返回，总响应时间为最慢的一个请求返回的用时；而非累加
		 */
		stopwatch.stop();

		/**
		 * 会阻塞主线程
		 * 所以这一句总是最后打印
		 *
		 * 缺点：高并发时依然会造成线程数过多、CPU上下文切换
		 */
		log.warn("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "Main Thread END. 会阻塞主线程, 所以这一句总是最后打印");
	}


}
