package com.webrest;

import com.webrest.common.repostiory.BaseRepositoryImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
