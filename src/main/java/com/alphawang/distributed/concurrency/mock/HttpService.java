package com.alphawang.distributed.concurrency.mock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alpha on 1/4/18.
 */
@Slf4j
public class HttpService {

	public String getHttpResult() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("[HttpService] delay 2000ms.");
		return "HTTP Result with 2000ms delay";
	}

	public String getHttpResult(Long id) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("[HttpService] delay 2000ms. " + id);
		return "HTTP Result with 2000ms delay: " + id;
	}
}
