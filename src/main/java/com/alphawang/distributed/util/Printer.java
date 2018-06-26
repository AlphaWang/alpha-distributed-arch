package com.alphawang.distributed.util;

import com.google.common.base.Stopwatch;
import org.apache.http.Header;

import java.util.concurrent.TimeUnit;

/**
 * Created by Alpha on 1/5/18.
 */
public class Printer {
	
	public static void printLatency(Stopwatch stopwatch, Object o) {
		System.out.println(String.format("[%s] [%s] %s", stopwatch.elapsed(TimeUnit.MILLISECONDS), Thread.currentThread().getName(), o));
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void print(String format, Object... args) {
		format = format.replace("{}", "%s");
		System.out.println(String.format(format, args));
	}

	public static void print(Header header) {
		System.out.println(header.getName() + " : " + header.getValue());
	}

	public static void print(Header... headers) {
		for (Header header : headers) {
			print(header);
		}
	}
}
