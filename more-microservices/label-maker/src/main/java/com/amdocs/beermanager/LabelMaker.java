package com.amdocs.beermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class LabelMaker {

	public static void main(String[] args) {
		SpringApplication.run(LabelMaker.class, args);
	}
}


@RestController
@Slf4j
class BeerGreeting {

	@GetMapping("/make-label")
	@HystrixCommand
	String makeLabel(){
		log.info("In method make-label");
		return "ok";
	}
}
