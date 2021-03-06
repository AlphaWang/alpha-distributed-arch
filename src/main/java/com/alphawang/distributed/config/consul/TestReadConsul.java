package com.alphawang.distributed.config.consul;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.ConsulException;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.QueryOptions;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * ﻿consul 启动：
 * $ consul agent -server -bootstrap-expect 1 -data-dir /pang/data/consul -bind 127.0.0.1 -client 0.0.0.0 -ui-dir ./ui/
 *
 * consul 配置开关:
 *
 * $ curl -X PUT -d 'true' http://127.0.0.1:8500/v1/kv/item_tomcat/user.not.call.backend
 *
 * consul 查询开关：
 * $ curl http://127.0.0.1:8500/v1/kv/item_tomcat/user.not.call.backend
 * [{"LockIndex":0,"Key":"item_tomcat/user.not.call.backend","Flags":0,"Value":"dHJ1ZQ==","CreateIndex":24,"ModifyIndex":24}]
 */
@Slf4j
public class TestReadConsul {

	private static transient Properties properties;
	private static transient String system = "item_tomcat";

	public static void main(String[] args) {
		
		Consul consul = Consul.builder()
			.withHostAndPort(HostAndPort.fromString("127.0.0.1:8500"))
			.withConnectTimeoutMillis(1000)
			.withReadTimeoutMillis(30 * 1000)
			.withWriteTimeoutMillis(5000)
			.build();

		KeyValueClient keyValueClient = consul.keyValueClient();
		AtomicBoolean needBreak = new AtomicBoolean(true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				BigInteger index = BigInteger.ZERO;

				while (true) {
					try {
						Properties _pro = new Properties();
						List<Value> values = keyValueClient.getValues(system, QueryOptions.blockSeconds(30, index).build());
						for (Value value : values) {
							log.info("get value:" + value);
							_pro.put(value.getKey(), value.getValueAsString().orNull());
							index = index.max(BigInteger.valueOf(value.getModifyIndex()));
						}
						properties = _pro;
						log.info("read properties: {}", properties);
					} catch (ConsulException e) {
						e.printStackTrace();
						if (e.getCode() == 404) {
							try {
								Thread.sleep(5000L);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}

					if (needBreak.get() == true) {
						break;
					}

				}
			}
		});

		thread.run();
		needBreak.set(false);
		thread.start();
	}
}
