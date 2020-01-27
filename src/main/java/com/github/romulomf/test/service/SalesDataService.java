package com.github.romulomf.test.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.romulomf.test.model.Customer;
import com.github.romulomf.test.model.DataType;
import com.github.romulomf.test.model.Sale;
import com.github.romulomf.test.model.SaleData;
import com.github.romulomf.test.model.SaleItem;
import com.github.romulomf.test.model.Salesman;

@Component
public class SalesDataService {

	@Autowired
	private Logger logger;

	@Autowired
	private Path inputDirectory;

	@Autowired
	private Path outputDirectory;

	private ForkJoinPool pool;

	public SalesDataService() {
		pool = new ForkJoinPool();
	}

	public void processBatch() {
		try {
			List<Path> files = Files.list(inputDirectory).collect(Collectors.toList());
			for (Path file : files) {
				pool.execute(() -> processFile(file));
			}
			registerDirectoryChangeListener();
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage());
		}
	}

	private void registerDirectoryChangeListener() throws IOException, InterruptedException {
		try (WatchService service = FileSystems.getDefault().newWatchService()) {
			inputDirectory.register(service, ENTRY_CREATE, ENTRY_MODIFY);
			WatchKey key = null;
			while ((key = service.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if (kind == OVERFLOW) {
						continue;
					}
					@SuppressWarnings("unchecked")
					WatchEvent<Path> eventPath = (WatchEvent<Path>) event;
					Path file = eventPath.context();
					Path newFile = inputDirectory.resolve(file);
					pool.execute(() -> processFile(newFile));
				}
				key.reset();
			}
		}
	}

	private void processFile(Path file) {
		Map<DataType, Set<SaleData>> translatedData = new EnumMap<>(DataType.class);
		translatedData.put(DataType.SALESMAN, new HashSet<SaleData>());
		translatedData.put(DataType.CUSTOMER, new HashSet<SaleData>());
		translatedData.put(DataType.SALE, new HashSet<SaleData>());
		List<String> content = null;
		try {
			content = Files.readAllLines(file);
			for (String line : content) {
				String[] data = StringUtils.split(line, SaleData.SEPARATOR);
				SaleData saleData = translate(data);
				if (saleData != null) {
					translatedData.get(saleData.getDataType()).add(saleData);
				}
			}
			int salesmanCount = translatedData.get(DataType.SALESMAN).size();
			int customerCount = translatedData.get(DataType.CUSTOMER).size();
			Set<SaleData> sales = translatedData.get(DataType.SALE);
			List<Sale> orderedSales = sales.stream().map(sd -> (Sale) sd).sorted(Comparator.comparingDouble(Sale::getTotal).reversed()).collect(Collectors.toList());
			Optional<Sale> bestSale = Optional.empty();
			Optional<Sale> worstSale = Optional.empty();
			if (!orderedSales.isEmpty()) {
				bestSale = Optional.of(orderedSales.get(0));
				worstSale = Optional.of(orderedSales.get(orderedSales.size() - 1));
			}			
			generateReportFile(file, salesmanCount, customerCount, bestSale, worstSale);
		} catch (IOException e) {
			logger.warn(String.format("Ocorreu um erro que impediu que o arquivo %s pudesse ser processado corretamente.", file.getFileName()));
		}
	}

	private SaleData translate(String[] data) {
		SaleData saleData = null;
		try {
			DataType dataType = null;
			dataType = DataType.parse(data[0]);
			switch (dataType) {
			case SALESMAN:
				saleData = processSalesman(data);
				break;
			case CUSTOMER:
				saleData = processCustomer(data);
				break;
			case SALE:
				saleData = processSale(data);
				break;
			}
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage());
		}
		return saleData;
	}

	private SaleData processSalesman(String[] data) {
		String socialNumber = data[1];
		String name = data[2];
		double salary = Double.parseDouble(data[3]);
		return new Salesman(socialNumber, name, salary);
	}

	private SaleData processCustomer(String[] data) {
		String companyNumber = data[1];
		String name = data[2];
		String businessArea = data[3];
		return new Customer(companyNumber, name, businessArea);
	}

	private SaleData processSale(String[] data) {
		int id = Integer.parseInt(data[1]);
		String salesmanName = data[3];
		List<SaleItem> saleItems = new ArrayList<>();
		String itemData = StringUtils.substringBetween(data[2], "[", "]");
		String[] items = StringUtils.split(itemData, ',');
		for (String item : items) {
			String[] itemInfo = StringUtils.split(item, '-');
			int itemId = Integer.parseInt(itemInfo[0]);
			int quantity = Integer.parseInt(itemInfo[1]);
			double price = Double.parseDouble(itemInfo[2]);
			SaleItem saleItem = new SaleItem(itemId, quantity, price);
			saleItems.add(saleItem);
		}
		return new Sale(id, saleItems, salesmanName);
	}

	private void generateReportFile(Path file, int salesmanCount, int customersCount, Optional<Sale> bestSale, Optional<Sale> worstSale) {
		try {
			Path reportFile = outputDirectory.resolve(file.getFileName());
			if (Files.notExists(reportFile)) {
				Files.createFile(reportFile);
			}
			StringBuilder content = new StringBuilder();
			content.append(String.format("Quantidade de clientes: %d%n", customersCount));
			content.append(String.format("Quantidade de vendedores: %d%n", salesmanCount));
			bestSale.ifPresent(s -> content.append(String.format("ID da venda mais cara: %d%n", s.getId())));
			worstSale.ifPresent(s -> content.append(String.format("O pior vendedor: %s%n", s.getSalesmanName())));
			Files.writeString(reportFile, content.toString(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
			logger.info(String.format("Gerado o arquivo de relatório %s", file));
		} catch (IOException e) {
			logger.error(String.format("Ocorreu um erro e por isso não foi possível salvar o arquivo com os dados do relatório originários de %s.", file.getFileName()));
		}
	}
}