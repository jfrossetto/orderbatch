package br.com.mglu.orderbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
//@EnableJdbcRepositories(basePackages = "br.com.mglu.order")
public class OrderbatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderbatchApplication.class, args);
	}

}
