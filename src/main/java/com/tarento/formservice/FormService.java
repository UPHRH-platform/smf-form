package com.tarento.formservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tarento.formservice.utils.Constants;

/**
 * 
 * @author Darshan Nagesh
 *
 */
@SpringBootApplication
public class FormService {
	public static void main(String[] args) {
		SpringApplication.run(FormService.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
