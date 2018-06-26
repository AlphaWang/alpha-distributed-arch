package com.alphawang.distributed.concurrency;

import com.alphawang.distributed.util.Printer;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by Alpha on 1/8/18.
 *
 * Java 8 CompletableFuture 内部使用ForkJoinPool实现异步处理。
 * 可以对多个异步处理进行编排，实现更复杂的异步处理。
 *
 * 使用 CompletableFuture 可以把 <回调方式> 的实现转变为 <同步调用> 实现
 * ﻿CompletableFuture会在全局的 ForkJoinPool.commonPool() 中获取线程，并执行这些任务
 *
 */
public class C03_CompletableFutureTest {


	private static Service service = new Service();

	/**
	 * Case 1: allOf, thenApplyAsync
	 * 三个服务异步并发调用，然后对结果合并处理：异步处理，不阻塞主线程
	 *
	 * ******* START
	 * [63] [main] Started 3 calls.
	 * [71] [main] >>>>>> Try to get Result
	 *
	 * [1067] [ForkJoinPool.commonPool-worker-2] [Service][Mock Data with 1000ms delay]http://wwww.taobao.com
	 * [1067] [ForkJoinPool.commonPool-worker-3] [Service][Mock Data with 1000ms delay]http://wwww.baidu.com
	 * [1067] [ForkJoinPool.commonPool-worker-1] [Service][Mock Data with 1000ms delay]http://wwww.jd.com
	 *
	 * [1067] [ForkJoinPool.commonPool-worker-3] allOf().applyAsync()2 start.
	 * [1067] [ForkJoinPool.commonPool-worker-1] allOf().applyAsync()1 start. null
	 * 
	 * [2595] [ForkJoinPool.commonPool-worker-3] allOf().applyAsync()2 join (+1500ms delay). [[Mock Data with 1000ms delay]http://wwww.jd.com, [Mock Data with 1000ms delay]http://wwww.taobao.com, [Mock Data with 1000ms delay]http://wwww.baidu.com]
	 *
	 * [2595] [main] >>>>>> Got result : allOf().applyAsync()2: [[Mock Data with 1000ms delay]http://wwww.jd.com, [Mock Data with 1000ms delay]http://wwww.taobao.com, [Mock Data with 1000ms delay]http://wwww.baidu.com]
	 * [2595] [main] >>>>>> Got result : allOf().applyAsync()1: null-TEST
	 * ******* END
	 */
	private static void applyAsync() throws ExecutionException, InterruptedException {

		Stopwatch stopwatch = Stopwatch.createStarted();

		CompletableFuture<String> future1 = service.getHttpData(stopwatch, "http://wwww.jd.com");
		CompletableFuture<String> future2 = service.getHttpData(stopwatch,"http://wwww.taobao.com");
		CompletableFuture<String> future3 = service.getHttpData(stopwatch,"http://wwww.baidu.com");

		Printer.printLatency(stopwatch, "Started 3 calls.");

		CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);

		CompletableFuture<String> futureTest = allFutures
			.thenApplyAsync(response -> {
				Printer.printLatency(stopwatch, "allOf().applyAsync()1 start. " + response);  //TODO why it's null?  因为allOf返回的是Void.
				return response + "-TEST";
			}).exceptionally(e -> {
				Printer.printLatency(stopwatch, "allOf().applyAsync()1 Exception.");
				e.printStackTrace();
				return "Exception...";
			});

		CompletableFuture<List<String>> allFutures2 = allFutures.thenApplyAsync(v -> {
			Printer.printLatency(stopwatch, "allOf().applyAsync()2 start.");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<String> response = Lists.newArrayList(future1, future2, future3).stream()
				.map(future -> future.join())
				.collect(Collectors.toList());

			Printer.printLatency(stopwatch, "allOf().applyAsync()2 join (+1500ms delay). " + response);
			return response;
		});

		// 这一句会先于thenApplyAsync打印！因为不阻塞主线程
		Printer.printLatency(stopwatch,">>>>>> Try to get Result ");

		// 此处阻塞
		List<String> s2 = allFutures2.get();
		Printer.printLatency(stopwatch,">>>>>> Got result : allOf().applyAsync()2: " + s2);

		String s = futureTest.get();
		Printer.printLatency(stopwatch,">>>>>> Got result : allOf().applyAsync()1: " + s);
	}

	/**
	 * Case 2: allOf, thenApply
	 * 三个服务异步并发调用，然后对结果合并处理：同步处理，阻塞主线程
	 *
	 * ******* START
	 * [66] [main] Started 3 calls.
	 * [72] [main] >>>>>> Try Future.get()    -----??????????
	 *
	 * [1068] [ForkJoinPool.commonPool-worker-2] [Service][Mock Data with 1000ms delay]http://wwww.taobao.com
	 * [1068] [ForkJoinPool.commonPool-worker-1] [Service][Mock Data with 1000ms delay]http://wwww.jd.com
	 * [1068] [ForkJoinPool.commonPool-worker-3] [Service][Mock Data with 1000ms delay]http://wwww.baidu.com
	 *
	 * [2573] [ForkJoinPool.commonPool-worker-3] allOf().thenApply() with 1500ms delay.
	 * 
	 * [2587] [main] >>>>>> Got result : [[Mock Data with 1000ms delay]http://wwww.jd.com, [Mock Data with 1000ms delay]http://wwww.taobao.com, [Mock Data with 1000ms delay]http://wwww.baidu.com]
	 * ******* END
	 *
	 */
	public static void applySync() throws ExecutionException, InterruptedException {
		Stopwatch stopwatch = Stopwatch.createStarted();

		CompletableFuture<String> future1 = service.getHttpData(stopwatch,"http://wwww.jd.com");
		CompletableFuture<String> future2 = service.getHttpData(stopwatch,"http://wwww.taobao.com");
		CompletableFuture<String> future3 = service.getHttpData(stopwatch,"http://wwww.baidu.com");

		Printer.printLatency(stopwatch, "Started 3 calls.");

		CompletableFuture<Void> futures = CompletableFuture.allOf(future1, future2, future3);

		CompletableFuture<? extends List<String>> future = futures
			// thenApply
			.thenApply((Void) -> {
				try {
					Thread.sleep(1500);
					Printer.printLatency(stopwatch, "allOf().thenApply() with 1500ms delay.");

					return Lists.newArrayList(future1.get(), future2.get(), future3.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				} catch (ExecutionException e) {
					e.printStackTrace();
					return null;
				}
			}).exceptionally(e -> {
				Printer.printLatency(stopwatch, "allOf().thenApply() Exception.");
				e.printStackTrace();
				return null;
			});

		// TODO 这一句一定在thenApply之后打印，因为阻塞主线程
		Printer.printLatency(stopwatch, ">>>>>> Try Future.get()");

		List s = future.get();
		Printer.printLatency(stopwatch,">>>>>> Got result : " + s);
	}

	/**
	 * Case 3: thenAcceptBothAsync
	 * 两个服务并发调用，然后消费结果，不阻塞主线程
	 *
	 * ******* START
	 * [61] [main] Started 2 calls.
	 * [69] [main] >>>>>> Try to get Result     ----不阻塞主线程
	 *
	 * [1063] [ForkJoinPool.commonPool-worker-1] [Service][Mock Data with 1000ms delay]http://wwww.jd.com
	 * [1063] [ForkJoinPool.commonPool-worker-2] [Service][Mock Data with 1000ms delay]http://wwww.taobao.com
	 *
	 * [2578] [ForkJoinPool.commonPool-worker-2] future1.thenAcceptBothAsync() with 1500ms delay: [[Mock Data with 1000ms delay]http://wwww.jd.com, [Mock Data with 1000ms delay]http://wwww.taobao.com]
	 *
	 *  [2578] [main] >>>>>> result : null
	 * ******* END
	 *
	 */
	private static void acceptBothAsync() throws ExecutionException, InterruptedException {
		Stopwatch stopwatch = Stopwatch.createStarted();

		CompletableFuture<String> future1 = service.getHttpData(stopwatch,"http://wwww.jd.com");
		CompletableFuture<String> future2 = service.getHttpData(stopwatch,"http://wwww.taobao.com");
		Printer.printLatency(stopwatch, "Started 2 calls.");

		CompletableFuture<Void> future = future1.thenAcceptBothAsync(future2, (result1, result2) -> {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List result = Lists.newArrayList(result1, result2);
			Printer.printLatency(stopwatch, "future1.thenAcceptBothAsync() with 1500ms delay: " + result);
//			return  //这是消费结果，不能返回
		}).exceptionally(e -> {
			return null;
		});

		// 提前打印，因为不阻塞主线程
		Printer.printLatency(stopwatch, ">>>>>> Try to get Result ");
		Void s = future.get();
		Printer.printLatency(stopwatch, ">>>>>> result : " + s);
	}

	/**
	 * Case 4: thenCombineAsync
	 * service1执行完后，并发执行2、3， 然后消费结果，不阻塞主线程
	 *
	 * ******* START
	 * [81] [main] Started 1st call.
	 * [86] [main] Started 3rd call.
	 * [88] [main] >>>>>> Try to get Result
	 *
	 * [1081] [ForkJoinPool.commonPool-worker-1] [Service][Mock Data with 1000ms delay]http://wwww.jd.com
	 * [1090] [ForkJoinPool.commonPool-worker-2] [Service][Mock Data with 1000ms delay]http://wwww.baidu.com
	 *
	 * [2585] [ForkJoinPool.commonPool-worker-1] future2 = future1.thenApplyAsync() with 1500ms delay: [Mock Data with 1000ms delay]http://wwww.jd.com
	 *
	 * [5089] [ForkJoinPool.commonPool-worker-1] future2.thenCombineAsync(future3) with 2500ms delay: result from #2[Mock Data with 1000ms delay]http://wwww.jd.com[Mock Data with 1000ms delay]http://wwww.baidu.com
	 * [5089] [main] >>>>>> result : result from #2[Mock Data with 1000ms delay]http://wwww.jd.com[Mock Data with 1000ms delay]http://wwww.baidu.com
	 * ******* END
	 */
	private static void flow() throws ExecutionException, InterruptedException {
		Stopwatch stopwatch = Stopwatch.createStarted();

		CompletableFuture<String> future1 = service.getHttpData(stopwatch,"http://wwww.jd.com");
		Printer.printLatency(stopwatch, "Started 1st call.");

		CompletableFuture<String> future2 = future1.thenApplyAsync((v) -> {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Printer.printLatency(stopwatch, "future2 = future1.thenApplyAsync() with 1500ms delay: " + v);
			return "result from #2" +v;
		});

		CompletableFuture<String> future3 = service.getHttpData(stopwatch,"http://wwww.baidu.com");
		Printer.printLatency(stopwatch, "Started 3rd call.");

		CompletableFuture<String> result = future2.thenCombineAsync(future3, (result2, result3) -> {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Printer.printLatency(stopwatch, "future2.thenCombineAsync(future3) with 2500ms delay: " + result2 + result3);

			return result2 + result3;
		}).exceptionally(e -> {
			return null;
		});

		Printer.printLatency(stopwatch, ">>>>>> Try to get Result ");
		String s = result.get();
		Printer.printLatency(stopwatch, ">>>>>> result : " + s);
	}

	private static class Service {
		public CompletableFuture<String> getHttpData(Stopwatch stopwatch, String url) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Printer.printLatency(stopwatch, "[Service][Mock Data with 1000ms delay]" + url);
				return "[Mock Data with 1000ms delay]" + url;
			});
		}
	}

	public static void main(String[] args) {
		try {
			Printer.print("******* START");

//			applyAsync();
			applySync();
//			acceptBothAsync();
//			flow();

			Printer.print("******* END");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
