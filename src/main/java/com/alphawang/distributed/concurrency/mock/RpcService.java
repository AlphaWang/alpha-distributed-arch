package com.alphawang.distributed.concurrency.mock;

import com.alphawang.distributed.util.Printer;
import com.google.common.base.Stopwatch;

/**
 * Created by Alpha on 1/4/18.
 */
public class RpcService {
	public String getRpcResult(Stopwatch stopwatch) throws Exception {
		Thread.sleep(1000);
		Printer.printLatency(stopwatch, "[RpcService] delay 1000ms.");
		return "Rpc Result with 1000ms delay";
	}
}
