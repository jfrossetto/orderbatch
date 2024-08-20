package br.com.mglu.orderbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderbatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderbatchApplication.class, args);
	}

}
