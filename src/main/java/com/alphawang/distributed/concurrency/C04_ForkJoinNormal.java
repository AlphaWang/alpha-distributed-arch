package com.alphawang.distributed.concurrency;

import com.alphawang.distributed.util.Printer;
import com.google.common.base.Stopwatch;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * [1] [main] START
 * [9] [main] forkJoinPool.submit()
 *
 * [11] [ForkJoinPool-1-worker-3] computing from 51 to 52
 * [11] [ForkJoinPool-1-worker-2] computing from 14 to 16
 * [11] [ForkJoinPool-1-worker-1] computing from 1 to 2
 * [11] [ForkJoinPool-1-worker-0] computing from 26 to 27
 * [11] [ForkJoinPool-1-worker-3] computing from 53 to 54
 * [11] [ForkJoinPool-1-worker-1] computing from 3 to 4
 * [11] [ForkJoinPool-1-worker-2] computing from 17 to 19
 * [11] [ForkJoinPool-1-worker-0] computing from 28 to 29
 * [11] [ForkJoinPool-1-worker-3] computing from 55 to 57
 * [11] [ForkJoinPool-1-worker-1] computing from 5 to 7
 * [11] [ForkJoinPool-1-worker-2] computing from 20 to 22
 * [11] [ForkJoinPool-1-worker-0] computing from 30 to 32
 * [11] [ForkJoinPool-1-worker-3] computing from 58 to 60
 * [11] [ForkJoinPool-1-worker-1] computing from 8 to 10
 * [11] [ForkJoinPool-1-worker-0] computing from 33 to 35
 * [11] [ForkJoinPool-1-worker-3] computing from 61 to 63
 * [11] [ForkJoinPool-1-worker-1] computing from 11 to 13
 * [11] [ForkJoinPool-1-worker-0] computing from 36 to 38
 * [12] [ForkJoinPool-1-worker-3] computing from 64 to 66
 * [12] [ForkJoinPool-1-worker-3] computing from 67 to 69
 * [12] [ForkJoinPool-1-worker-3] computing from 70 to 72
 * [12] [ForkJoinPool-1-worker-3] computing from 73 to 75
 * [12] [ForkJoinPool-1-worker-3] computing from 76 to 77
 * [12] [ForkJoinPool-1-worker-3] computing from 78 to 79
 * [12] [ForkJoinPool-1-worker-3] computing from 80 to 82
 * [13] [ForkJoinPool-1-worker-3] computing from 83 to 85
 * [13] [ForkJoinPool-1-worker-3] computing from 86 to 88
 * [13] [ForkJoinPool-1-worker-3] computing from 89 to 91
 * [13] [ForkJoinPool-1-worker-3] computing from 92 to 94
 * [13] [ForkJoinPool-1-worker-3] computing from 95 to 97
 * [13] [ForkJoinPool-1-worker-3] computing from 98 to 100
 * [14] [ForkJoinPool-1-worker-3] computing from 45 to 47
 * [14] [ForkJoinPool-1-worker-3] computing from 48 to 50
 * [14] [ForkJoinPool-1-worker-3] computing from 39 to 41
 * [14] [ForkJoinPool-1-worker-3] computing from 42 to 44
 * [15] [ForkJoinPool-1-worker-2] computing from 23 to 25
 *
 * [16] [main] Get result: 5050
 */
public class C04_ForkJoinNormal {

	static Stopwatch stopwatch = Stopwatch.createStarted();

	public static void main(String[] args) {

		Printer.printLatency(stopwatch, "START");

		ForkJoinPool pool = new ForkJoinPool(4);
		CountTask task = new CountTask(1, 100);

		Printer.printLatency(stopwatch, "forkJoinPool.submit()");
		Future<Integer> future = pool.submit(task);
		try {
			int sum = future.get();
			Printer.printLatency(stopwatch, "Get result: " + sum);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		stopwatch.stop();
	}


	static class CountTask extends RecursiveTask<Integer> {
		private static final int THRESHOLD = 2;
		private int start;
		private int end;

		public CountTask(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		protected Integer compute() {
			int sum = 0;

			// 如果任务足够小，就计算任务
			boolean canCompute = (end - start) <= THRESHOLD;
			if (canCompute) {
				Printer.printLatency(stopwatch, "computing from " + start + " to " + end);
				for (int i = start; i <= end; i++) {
					sum += i;
				}
			}
			// 否则分割成更小的任务
			else {
				// 分解
				int middle = (start + end) / 2;
				CountTask leftTask = new CountTask(start, middle);
				CountTask rightTask = new CountTask(middle + 1, end);
				leftTask.fork();
				rightTask.fork();

				// 合并
				int leftResult = leftTask.join();
				int rightResult = rightTask.join();
				sum = leftResult + rightResult;
			}
			return sum;
		}
	}

}
