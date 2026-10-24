package com.Amogh.url_shortener;

import com.Amogh.url_shortener.service.UrlService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}



}

//docker compose up -d

//docker compose down
//docker compose ps
//docker compose logs -f app

