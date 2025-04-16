package edu.bhcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The `FitnessApplication` class is the main entry point for the Spring Boot application.
 * It serves as the root configuration class, enabling auto-configuration, component scanning,
 * and web application features.
 */
@SpringBootApplication
public class FitnessApplication {
	/**
	 * Initialize Spring Application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(FitnessApplication.class, args);
	}
}
