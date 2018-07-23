package com.alphawang.distributed.concurrency;

import com.alphawang.distributed.concurrency.mock.HttpService;
import com.alphawang.distributed.util.Printer;
import com.esotericsoftware.minlog.Log;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class C04_ForkJoin {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
//		testSubmit();
		testTaskSet();
	}

	/**
	 * 串行执行
	 *
	 * [1] [main] START
	 *
	 * [16] [main] submit task 0
	 * [2023] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms. 0
	 *
	 * [2024] [main] submit task 1
	 * [4028] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms. 1
	 *
	 * [4029] [main] submit task 2
	 * [6032] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms. 2
	 *
	 * [6032] [main] submit task 3
	 * [8032] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms. 3
	 *
	 * [8033] [main] submit task 4
	 * [10037] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms. 4
	 *
	 * [10038] [main] results: [HTTP Result with 2000ms delay: 0, HTTP Result with 2000ms delay: 1, HTTP Result with 2000ms delay: 2, HTTP Result with 2000ms delay: 3, HTTP Result with 2000ms delay: 4]
	 */
	private static void testSubmit() throws InterruptedException, ExecutionException {
		ForkJoinPool forkJoinPool = new ForkJoinPool(4);
		log.warn("START");

		List<String> results = Lists.newArrayList();

		for (long i = 0; i < 5; i++) {
			HttpService httpService = new HttpService();

			log.info("submit task " + i);
			long finalI = i;
			ForkJoinTask<String> task = forkJoinPool.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					return httpService.getHttpResult(finalI);
				}
			});


			results.add(task.get());
		}

		log.info("results: " + results);
	}

	/**
	 * [1] [main] START
	 * [71] [main] forkJoinPool.submit
	 * [72] [main] Try to get ForkJoinTask result.
	 *
	 * [2081] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms.4
	 * [2081] [ForkJoinPool-1-worker-2] [HttpService] delay 2000ms.0
	 * [2081] [ForkJoinPool-1-worker-3] [HttpService] delay 2000ms.3
	 * [2081] [ForkJoinPool-1-worker-0] [HttpService] delay 2000ms.1
	 *
	 * [4086] [ForkJoinPool-1-worker-1] [HttpService] delay 2000ms.2
	 *
	 * [4086] [main] Got ForkJoinTask result. [HTTP Result with 2000ms delay: 1, HTTP Result with 2000ms delay: 0, HTTP Result with 2000ms delay: 4, HTTP Result with 2000ms delay: 2, HTTP Result with 2000ms delay: 3]
	 */
	private static void testTaskSet() throws ExecutionException, InterruptedException {
		ForkJoinPool forkJoinPool = new ForkJoinPool(4);
		log.warn("START");

		Set<HttpService> services = Sets.newHashSet();
		for (long i = 0; i < 5; i++) {
			long finalI = i;
			
			HttpService httpService = new HttpService() {
				public String getHttpResult() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					log.info("[HttpService] delay 2000ms." + finalI);
					return "HTTP Result with 2000ms delay: " + finalI;
				}
			};
			services.add(httpService);
		}

		Callable<List<String>> task = () -> services.parallelStream().map(new Function<HttpService, String>() {
			@Override public String apply(HttpService httpService) {
				return httpService.getHttpResult();
			}
		}).collect(Collectors.toList());

		log.info("forkJoinPool.submit...");
		ForkJoinTask<List<String>> forkJoinTask = forkJoinPool.submit(task);

		log.info("Try to get ForkJoinTask result.");
		List<String> results = forkJoinTask.get();
		log.info("Got ForkJoinTask result: " + results);
	}
}
