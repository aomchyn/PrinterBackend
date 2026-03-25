package com.printer.myprinter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@RestController
@ComponentScan(basePackages = "com.printer.myprinter")
public class MyprinterApplication {

	public static void main(String[] args) {
		// โหลด .env แล้ว set เป็น system properties ก่อน Spring Boot start
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(System.getProperty("user.dir"))
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				if (System.getProperty(entry.getKey()) == null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			});
		} catch (Exception e) {
			System.err.println("Warning: Could not load .env file: " + e.getMessage());
		}

		SpringApplication.run(MyprinterApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello world SpringBoot Printer 555!";
	}

}
