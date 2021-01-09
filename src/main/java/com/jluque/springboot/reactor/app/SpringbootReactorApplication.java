package com.jluque.springboot.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux<String> nombres = Flux.just("Julio", "Alfredo", "Luque", "Ticona", "aelfraed").doOnNext(e -> {
			if (e.isEmpty()) {
				throw new RuntimeException("No puede ser vacio");
			}
			System.out.println(e);
		});
		nombres.subscribe(e -> log.info(e), error -> log.error(error.getMessage()), new Runnable() {
			@Override
			public void run() {
				log.info("Flux finalizado correctamente..!!!");
			}
			
		});
	}

}
