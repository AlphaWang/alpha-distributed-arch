package com.alphawang.distributed.concurrency.mock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alpha on 1/4/18.
 */
@Slf4j
public class RpcService {
	public String getRpcResult() throws Exception {
		Thread.sleep(1000);
		log.info("[RpcService] delay 1000ms.");
		return "Rpc Result with 1000ms delay";
	}
}
