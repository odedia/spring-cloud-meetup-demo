package com.amdocs.beerfest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class BeerClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeerClientApplication.class, args);
	}

	@GetMapping("/beer/{name}")
	public String getBeer(@PathVariable String name){
		return "Hi " + name + "! Have a Heiniken.";
	}
}

@Configuration
class Config{
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}

@RestController
class BeerNamesController {
	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/beer-names")
	public List<String> getBeerNames(){
		return restTemplate
									.exchange("http://beer-service/beers", 
									HttpMethod.GET, null, 
									new ParameterizedTypeReference<Resources<BeerDTO>>() {})
									
									.getBody()
									.getContent()
									.stream()
									.map(BeerDTO::getName)
									.collect(Collectors.toList());

	}
}



@Data
@RequiredArgsConstructor
@AllArgsConstructor
class BeerDTO {
	private Long id;
	private String name;
}
