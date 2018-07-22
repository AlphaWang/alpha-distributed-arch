package com.alphawang.distributed.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockService {

	public Integer getStock(Long id) {
		log.info("-- Running StockService.getStock() for {}", id);
		if (id % 2 == 0) {
			System.err.println("ERROR in service.");
			throw new RuntimeException("Random Exception.");
		}

		return Math.toIntExact(1000 + id);
	}

	public List<Integer> getStocks(List<Long> ids) {
		log.info("-- Running StockService.getStocks() for {}", ids);
		return ids.stream()
			.map(Long::intValue)
			.collect(Collectors.toList());
	}
}
