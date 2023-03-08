package com.example.codearchgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CodeArchGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeArchGeneratorApplication.class, args);
	}

}
