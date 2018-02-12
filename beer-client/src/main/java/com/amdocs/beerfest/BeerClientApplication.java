package com.amdocs.beerfest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableBinding(Source.class)
@EnableCircuitBreaker
@Slf4j
public class BeerClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeerClientApplication.class, args);
	}

	@GetMapping("/beer/{name}")
	public String getBeer(@PathVariable String name){
		log.info("Entered greeting API");
		return "Hi! Have a " + name + "!";
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
@Slf4j
class BeerNamesController {
	@Output(Source.OUTPUT)
	@Autowired
	private MessageChannel messageChannel;

	
	@Autowired
	RestTemplate restTemplate;
	
	@PostMapping("/beer-me-up")
	public void write(@RequestBody BeerDTO beer){
		log.info("Beer is {}", beer);
		this.messageChannel.send(MessageBuilder.withPayload(beer.getName()).build());
	}
	
	
	public List<String> getBeerNamesFallback(){
		return Arrays.asList("Corrona");
	}
	
	@GetMapping("/beer-names")
	@HystrixCommand(fallbackMethod="getBeerNamesFallback")
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
