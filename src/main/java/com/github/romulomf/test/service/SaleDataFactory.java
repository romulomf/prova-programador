package com.github.romulomf.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.github.romulomf.test.model.Customer;
import com.github.romulomf.test.model.DataType;
import com.github.romulomf.test.model.Sale;
import com.github.romulomf.test.model.SaleData;
import com.github.romulomf.test.model.Salesman;

@Component
@ApplicationScope
public class SaleDataFactory {

	@Autowired
	private Logger logger;

	@Autowired
	private String separator;

	private String[] codes;

	public SaleDataFactory() {
		// construtor padrão
	}

	@PostConstruct
	public void initialize() {
		Set<String> identifiers = Stream.of(DataType.values()).map(DataType::toString).collect(Collectors.toSet());
		codes = identifiers.toArray(new String[DataType.values().length]);
	}

	public List<SaleData> getSalesData(List<String> content) {
		int lines = CollectionUtils.size(content);
		List<SaleData> salesData = new ArrayList<>(lines);
		String text = null;
		for (int i = 0; i < lines; i++) {
			String line = content.get(i);
			if (text != null) {
				if (StringUtils.startsWithAny(line, codes)) {
					String[] data = StringUtils.split(text, separator);
					SaleData saleData = converToSaleData(data);
					salesData.add(saleData);
					text = line;
				} else {
					text = text.concat(line);
				}
			} else {
				if (StringUtils.startsWithAny(line, codes)) {
					text = line;
				}
			}
		}
		return salesData;
	}

	private SaleData converToSaleData(String[] data) {
		SaleData saleData = null;
		try {
			DataType dataType = null;
			dataType = DataType.parse(data[0]);
			switch (dataType) {
			case SALESMAN:
				saleData = Salesman.parse(data);
				break;
			case CUSTOMER:
				saleData = Customer.parse(data);
				break;
			case SALE:
				saleData = Sale.parse(data);
				break;
			}
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage());
		}
		return saleData;
	}
}