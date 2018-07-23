package com.alphawang.distributed.concurrency;

import com.alphawang.distributed.concurrency.mock.HttpService;
import com.alphawang.distributed.util.Printer;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 利用CompletableFuture, 可以实现客户端多线程批量查询。
 *
 *
 * 默认启动 7 个线程？
 *
 * [1] [main] START
 * [10] [main] >>>>>> Try Future.get()
 *
 * [2014] [ForkJoinPool.commonPool-worker-5] [HttpService] delay 2000ms.
 * [2014] [ForkJoinPool.commonPool-worker-2] [HttpService] delay 2000ms.
 * [2014] [ForkJoinPool.commonPool-worker-4] [HttpService] delay 2000ms.
 * [2014] [ForkJoinPool.commonPool-worker-6] [HttpService] delay 2000ms.
 * [2014] [ForkJoinPool.commonPool-worker-3] [HttpService] delay 2000ms.
 * [2014] [ForkJoinPool.commonPool-worker-1] [HttpService] delay 2000ms.
 * [2014] [ForkJoinPool.commonPool-worker-7] [HttpService] delay 2000ms.
 *
 * [4018] [ForkJoinPool.commonPool-worker-6] [HttpService] delay 2000ms.
 * [4018] [ForkJoinPool.commonPool-worker-5] [HttpService] delay 2000ms.
 * [4018] [ForkJoinPool.commonPool-worker-4] [HttpService] delay 2000ms.
 * [4018] [ForkJoinPool.commonPool-worker-2] [HttpService] delay 2000ms.
 *
 * [4019] [main] >>>>>> Got result : null
 *
 *
 * [4073] [main] >>>>>> Try applyAsync.get()
 * [4073] [ForkJoinPool.commonPool-worker-2] allOf().applyAsync() start.
 * [5590] [ForkJoinPool.commonPool-worker-2] allOf().applyAsync() join (+1500ms delay). [HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay]
 * [5590] [main] >>>>>> Got result : [HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay, HTTP Result with 2000ms delay]
 */
@Slf4j
public class C04_BatchCall {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		List<CompletableFuture<String>> futures = Lists.newArrayList();
		HttpService httpService = new HttpService();

		log.warn("START");

		for (int i = 0; i <= 10; i++) {
			futures.add(CompletableFuture.supplyAsync(new Supplier<String>() {
				@Override
				public String get() {
					return httpService.getHttpResult();
				}
			}));
		}

		CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		log.info(">>>>>> Try Future.get()");
		Void s = allOf.get();
		log.info(">>>>>> Got result, allOf.get() = {}", s);

		CompletableFuture<List<String>> applyAsync = allOf.thenApplyAsync(v -> {
			log.info("allOf().applyAsync() start.");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			List<String> response = futures.stream()
				.map(future -> future.join())
				.collect(Collectors.toList());

			log.info("allOf().applyAsync() join (+1500ms delay), response = {}", response);
			return response;
		});

		log.info(">>>>>> Try applyAsync.get()");
		List<String> result = applyAsync.get();
		log.info(">>>>>> Got result : applyAsync.get() = {}", result);
	}


}
