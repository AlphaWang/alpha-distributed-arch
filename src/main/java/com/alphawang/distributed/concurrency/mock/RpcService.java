package com.alphawang.distributed.concurrency.mock;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by Alpha on 1/4/18.
 */
@Slf4j
public class RpcService {
	public String getRpcResult(Stopwatch stopwatch) throws Exception {
		Thread.sleep(1000);
		log.info("[{}] {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), "[RpcService] delay 1000ms.");
		return "Rpc Result with 1000ms delay";
	}
}
