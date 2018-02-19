package com.amdocs.beermanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class BottleMaker {

	public static void main(String[] args) {
		SpringApplication.run(BottleMaker.class, args);
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}


@RestController
@Slf4j
class BeerGreeting {

	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/make-bottle")
	@HystrixCommand
	String makeBottle(){
		log.info("In method make-bottle");
		restTemplate.getForEntity("http://label-maker/make-label", String.class);
		restTemplate.getForEntity("http://beer-filler/fill-beer", String.class);
		restTemplate.getForEntity("http://cap-handler/close-bottle", String.class);
		return "ok";
	}
}
