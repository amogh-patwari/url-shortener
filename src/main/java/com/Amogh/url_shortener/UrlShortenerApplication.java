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

    @Bean
    public CommandLineRunner testRunner(UrlService urlService) {
        return args -> {
            String code = urlService.shorten("https://www.example.com/some/long/path");
            System.out.println("Generated short code: " + code);
        };

    }

}
