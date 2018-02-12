package com.amdocs.beermanager;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding(Sink.class)
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

@Component
@Slf4j
class BeerHandler {
	
	@Autowired 
	BeerRepository repo;
	
	@StreamListener(target=Sink.INPUT)
	public void handleBeerMessage (String message) {
		log.info("Received beer: {}", message);
		this.repo.save(new Beer(null, message, 10.0));
	}
}

@RestController
@RefreshScope
@Slf4j
class BeerGreeting {
	@Value("${greeting}")
	private String greeting;
	
	@GetMapping("/greeting")
	String greeting(){
		log.info("Returning greeting from service...");
		return greeting;
	}
}

@RepositoryRestResource(collectionResourceRel = "beers", path = "beers")
interface BeerRepository extends JpaRepository<Beer, Long> {
	@RestResource(path="by-name")
	List<Beer> findByName(@Param("name") String name);

}


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class Beer {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private Double price;
}
