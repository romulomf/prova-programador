package com.github.romulomf.test.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.romulomf.test.service.SalesDataService;

@Component
public class StartupConfiguration implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private Logger logger;

	@Autowired
	private Path inputDirectory;

	@Autowired
	private Path outputDirectory;

	@Autowired
	private SalesDataService salesDataService;

	public StartupConfiguration() {
		// Default constructor
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info("Inicializando o serviço de processamento de arquivos em lote.");
		try {
			if (Files.notExists(inputDirectory)) {
				Files.createDirectories(inputDirectory);
				logger.info("Criado o diretório de leitura {}.", inputDirectory);
			}
			if (Files.notExists(outputDirectory)) {
				Files.createDirectories(outputDirectory);
				logger.info("Criado o diretório de escrita {}.", outputDirectory);
				DosFileAttributeView dosAttrsView;
				dosAttrsView = Files.getFileAttributeView(outputDirectory, DosFileAttributeView.class);
				if (dosAttrsView != null) {
					dosAttrsView.setReadOnly(false);
				}
				// pode ser implementada a garantia em sistemas unix-like que o diretório 'out' tenha permissão de escrita também
			}
			salesDataService.processBatch();
		} catch (IOException e) {
			logger.error("Não foi possível criar os diretórios necessários para aplicação.");
		} catch (InterruptedException e) {
			logger.warn("Houve uma interrupção na execução do serviço.");
			Thread.currentThread().interrupt();
		}
	}
}