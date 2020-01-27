package com.github.romulomf.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SpringBootConfiguration
@ComponentScan(basePackages = { "com.github.romulomf.test", "com.github.romulomf.test.config", "com.github.romulomf.test.service" })
public class TestApplication {

	public TestApplication() {
		// Default constructor
	}

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@Bean
	public Logger createLogger(InjectionPoint ip) {
		return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
	}

	@Bean(name = "inputDirectory")
	public Path createInputDirectoryPath() {
		return Paths.get(getHomePath(), "data", "in");
	}

	@Bean(name = "outputDirectory")
	public Path creatOutputDirectoryPath() {
		return Paths.get(getHomePath(), "data", "out");
	}

	private String getHomePath() {
		return System.getProperty("user.home");
	}
}