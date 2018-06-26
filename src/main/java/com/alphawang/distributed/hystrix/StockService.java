package com.alphawang.distributed.hystrix;

import com.alphawang.distributed.util.Printer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

	public Integer getStock(Long id) {
		Printer.print("-- Running StockService.getStock() " + id);
		if (id % 2 == 0) {
			System.err.println("ERROR in service.");
			throw new RuntimeException("Random Exception.");
		}

		return Math.toIntExact(1000 + id);
	}

	public List<Integer> getStocks(List<Long> ids) {
		Printer.print("-- Running StockService.getStocks() " + ids);
		return ids.stream()
			.map(Long::intValue)
			.collect(Collectors.toList());
	}
}
