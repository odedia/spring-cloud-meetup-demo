package com.amdocs.beermanager;

import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableDiscoveryClient
public class BeermanagerApplication {

	@Bean
	CommandLineRunner runner(BeerRepository br){
		return args -> {
			br.deleteAll();

			Arrays.asList("Heiniken", "Carlsberg", "Stella Artoir")
			.forEach(x -> br.save(new Beer(null, x, 10.0)));

			br.findAll().forEach(System.out::println);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BeermanagerApplication.class, args);
	}
}


@RestController
@RefreshScope
class BeerGreeting {
	@Value("${greeting}")
	private String greeting;
	
	@GetMapping("/greeting")
	String greeting(){
		return greeting;
	}
}

@RepositoryRestResource(collectionResourceRel = "beers", path = "beers")
interface BeerRepository extends JpaRepository<Beer, Long> {
	
}


@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
class Beer {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private Double price;
}
