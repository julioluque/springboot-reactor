package com.jluque.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jluque.springboot.reactor.app.model.Comentarios;
import com.jluque.springboot.reactor.app.model.Usuario;
import com.jluque.springboot.reactor.app.model.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	public void iterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Julio Luque");
		usuariosList.add("Cecilia Ortiz");
		usuariosList.add("Delfina Luque");
		usuariosList.add("Alfredo Luque");
		usuariosList.add("Delifina Ortiz");
		usuariosList.add("Julio Ticona");
		usuariosList.add("Alfredo Ticona");
		usuariosList.add("Griselda Ortiz");

		Flux<String> nombres = Flux.fromIterable(usuariosList);

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getApellido().equalsIgnoreCase("ortiz")).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("No puede ser vacio");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {
			@Override
			public void run() {
				log.info("Flux finalizado correctamente!!!");
			}

		});
	}

	public void flatMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Julio Luque");
		usuariosList.add("Cecilia Ortiz");
		usuariosList.add("Delfina Luque");
		usuariosList.add("Alfredo Luque");
		usuariosList.add("Delifina Ortiz");
		usuariosList.add("Julio Ticona");
		usuariosList.add("Alfredo Ticona");
		usuariosList.add("Griselda Ortiz");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("Julio")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Julio", "Luque"));
		usuariosList.add(new Usuario("Cecilia", "Ortiz"));
		usuariosList.add(new Usuario("Delfina", "Luque"));
		usuariosList.add(new Usuario("Alfredo", "Luque"));
		usuariosList.add(new Usuario("Delifina", "Ortiz"));
		usuariosList.add(new Usuario("Julio", "Ticona"));
		usuariosList.add(new Usuario("Alfredo", "Ticona"));
		usuariosList.add(new Usuario("Griselda", "Ortiz"));

		Flux.fromIterable(usuariosList).map(
				usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("Julio".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				}).map(nombre -> nombre.toLowerCase()).subscribe(u -> log.info(u.toString()));

	}

	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Julio", "Luque"));
		usuariosList.add(new Usuario("Cecilia", "Ortiz"));
		usuariosList.add(new Usuario("Delfina", "Luque"));
		usuariosList.add(new Usuario("Alfredo", "Luque"));
		usuariosList.add(new Usuario("Delifina", "Ortiz"));
		usuariosList.add(new Usuario("Julio", "Ticona"));
		usuariosList.add(new Usuario("Alfredo", "Ticona"));
		usuariosList.add(new Usuario("Griselda", "Ortiz"));

		Flux.fromIterable(usuariosList).collectList()
				.subscribe(lista -> lista.forEach(item -> log.info(item.toString())));
	}

	public Comentarios crearComentarios() {
		return new Comentarios();
	}

	public void ejemploUsuarioComentariosFlatMap() throws Exception {

		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Juan", "Perez"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe que tal");
			comentarios.addComentarios("Hola, este es un comentario reactivo");
			comentarios.addComentarios("Hola estoy combinando con flatMap");
			return comentarios;
		});

		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u, c)))
				.subscribe(uc -> log.info(uc.toString()));

	}

	public void ejemploUsuarioComentariosZipWith() throws Exception {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Juan", "Perez"));
		Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe que tal");
			comentarios.addComentarios("Hola, este es un comentario reactivo");
			comentarios.addComentarios("Hola estoy combinando con zipWith");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioComentarios = usuarioMono.zipWith(comentariosMono, (u, c) -> new UsuarioComentarios(u, c));
		usuarioComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	public void zipWithUsuarioComentarios2() throws Exception {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Juan", "Perez"));
		Mono<Comentarios> comentarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe que tal");
			comentarios.addComentarios("Hola, este es un comentario reactivo");
			comentarios.addComentarios("Hola estoy combinando con zipWith");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono.zipWith(comentarioMono).map(tuple -> {
			Usuario u = tuple.getT1();
			Comentarios c = tuple.getT2();
			return new UsuarioComentarios(u, c);
		});
		usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	// ========================== MAIN ==========================
	@Override
	public void run(String... args) throws Exception {
//		iterable();
//		flatMap();
//		ejemploToString();
//		ejemploCollectList();
//		ejemploUsuarioComentariosFlatMap();
//		ejemploUsuarioComentariosZipWith();
		zipWithUsuarioComentarios2();

	}

}
