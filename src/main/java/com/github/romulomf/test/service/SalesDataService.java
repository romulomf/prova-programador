package com.github.romulomf.test.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
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
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.romulomf.test.model.DataType;
import com.github.romulomf.test.model.Sale;
import com.github.romulomf.test.model.SaleData;

@Component
public class SalesDataService {

	@Autowired
	private Logger logger;

	@Autowired
	private Path inputDirectory;

	@Autowired
	private Path outputDirectory;

	@Autowired
	private SaleDataFactory factory;

	private ForkJoinPool pool;

	public SalesDataService() {
		pool = new ForkJoinPool();
	}

	public void processBatch() throws InterruptedException {
		try (Stream<Path> paths = Files.list(inputDirectory)) {
			paths.forEach(this::processFile);
			registerDirectoryChangeListener();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void registerDirectoryChangeListener() throws IOException, InterruptedException {
		try (WatchService service = FileSystems.getDefault().newWatchService()) {
			// Trata apenas os eventos de arquivos novos. Arquivos renomeados dentro do diretório não são reprocessados.
			inputDirectory.register(service, ENTRY_CREATE);
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
		if (file != null) {
			logger.info("Encontrado o arquivo {} para ser processado.", file.getFileName());
			Map<DataType, Set<SaleData>> translatedData = new EnumMap<>(DataType.class);
			translatedData.put(DataType.SALESMAN, new HashSet<>());
			translatedData.put(DataType.CUSTOMER, new HashSet<>());
			translatedData.put(DataType.SALE, new TreeSet<>());
			List<String> content = null;
			try {
				content = Files.readAllLines(file);
				List<SaleData> salesData = factory.getSalesData(content);
				for (SaleData saleData : salesData) {
					translatedData.get(saleData.getDataType()).add(saleData);
				}
				int salesmanCount = translatedData.get(DataType.SALESMAN).size();
				int customerCount = translatedData.get(DataType.CUSTOMER).size();
				SortedSet<Sale> sales = translatedData.get(DataType.SALE).stream().map(s -> (Sale) s).collect(Collectors.toCollection(TreeSet::new));
				Optional<Sale> bestSale = null;
				Optional<Sale> worstSale = null;
				if (CollectionUtils.isNotEmpty(sales)) {
					bestSale = Optional.of(sales.first());
					worstSale = Optional.of(sales.last());
				} else {
					bestSale = Optional.empty();
					worstSale = Optional.empty();
				}
				generateReportFile(file, salesmanCount, customerCount, bestSale, worstSale);
			} catch (IOException e) {
				logger.warn("Ocorreu um erro que impediu que o arquivo {} pudesse ser processado corretamente.", file.getFileName());
			}
		}
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
			logger.info("Gerado o arquivo de relatório {}", reportFile.getFileName());
		} catch (IOException e) {
			logger.error(String.format("Ocorreu um erro e por isso não foi possível salvar o arquivo com os dados do relatório originário de %s.", file.getFileName()));
		}
	}
}