package com.alphawang.distributed.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;

import java.util.concurrent.CompletableFuture;

                     
/**
 * Created by Alpha on 1/4/18.
 *
 * 异步Callback
 *
 *﻿ 实现方式：通过回调机制。首先发出网络请求，当返回时回调相关方法。
 * 性能：并不能提升性能，而是为了提升吞吐量。
 *
 *
 * 例如HttpAsyncClient使用基于NIO的异步IO模型实现，它实现了Reactor模式。
 * 这种机制不能提升性能，而是为了支撑大量并发连接，或提升吞吐量
 *
 *
 * 	 RESULT:
 *
 * [0] [main] ---- HttpAsyncClient START.
 * [12] [main] ---- HttpAsyncClient Executing.
 * [56] [main] ---- HttpAsyncClient END.             //主线程输出！不阻塞主线程
 * **** Getting Future.
 * [161] [I/O dispatcher 1] ==== Callback Completed.
 * **** Got Future.
 */
@Slf4j
public class C02_HttpAsyncClient_FutureCallbackTest {
	
	private static CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault();

	public static CompletableFuture<HttpResponse> getHttpData(String url) {

		httpAsyncClient.start();
		log.info("---- HttpAsyncClient START." + url);
		CompletableFuture asyncFuture = new CompletableFuture();

		HttpAsyncRequestProducer producer = HttpAsyncMethods.create(new HttpPost(url));
		BasicAsyncResponseConsumer consumer = new BasicAsyncResponseConsumer();

		FutureCallback callback = new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse response) {
				log.info("==== Callback Completed. " + url);
				log.info("response headers: {}", response.getAllHeaders());
				asyncFuture.complete(response);
			}

			@Override
			public void failed(Exception ex) {
				log.error("==== Callback Failed. " + url);
				asyncFuture.completeExceptionally(ex);
			}

			@Override
			public void cancelled() {
				log.warn("==== Call backCanceled. " + url);
				asyncFuture.cancel(true);
			}
		};

		log.info("---- HttpAsyncClient Executing. " + url);
		httpAsyncClient.execute(producer, consumer, callback);

		/**
		 * 不阻塞主线程
		 * End 会先于 Completed 输出
		 */
		log.info("---- HttpAsyncClient END. " + url);
		return asyncFuture;
	}

	public static void main(String[] args) {
		CompletableFuture<HttpResponse> completableFuture = C02_HttpAsyncClient_FutureCallbackTest.getHttpData("http://www.jd.com");
		try {
			log.warn("**** Getting Future.");
			Object result = completableFuture.get();
			log.warn("**** Got Future.");

			System.out.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
