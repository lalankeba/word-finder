package com.laan.wordfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WordFinderApplication {

	public static void main(final String[] args) {
		SpringApplication.run(WordFinderApplication.class, args);
	}

}
