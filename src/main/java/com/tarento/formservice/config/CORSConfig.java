package com.tarento.formservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tarento.formservice.utils.Constants;

@Configuration
public class CORSConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods(Constants.RequestMethods.GET, Constants.RequestMethods.POST,
						Constants.RequestMethods.PUT, Constants.RequestMethods.DELETE, Constants.RequestMethods.OPTIONS)
						.allowedOrigins("*").allowedHeaders("*");
			}
		};
	}
}