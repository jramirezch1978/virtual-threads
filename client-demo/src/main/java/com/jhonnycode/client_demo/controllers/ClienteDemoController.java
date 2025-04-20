package com.jhonnycode.client_demo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ClienteDemoController {

    private final RestClient restClient;

    private final WebClient webClient;

    public ClienteDemoController(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8081")
                .build();

        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8081")
                .build();
    }

    @GetMapping("/client")
    public String client() {
        log.info("Request Thread {} - {}",
                    Thread.currentThread().threadId(),
                    Thread.currentThread().getName());

        long startTimeNano = System.nanoTime();

        this.restClient.get()
                .uri("demo-service")
                .retrieve()
                .toBodilessEntity();

        long endTimeNano = System.nanoTime();
        double elapsedTimeInSeconds = (endTimeNano - startTimeNano) / 1_000_000_000.0;
        String formattedTime = String.format("%.6f", elapsedTimeInSeconds);

        log.info("Response Thread {} - {} - Tiempo transcurrido: {} segundos",
                    Thread.currentThread().threadId(),
                    Thread.currentThread().getName(),
                    formattedTime);

        return Thread.currentThread().toString();
    }

    @GetMapping("/client2")
    public String client2() {
        log.info("Request Thread {} - {}",
                Thread.currentThread().threadId(),
                Thread.currentThread().getName());

        Mono<Void> result = this.webClient.get()
                                .uri("/demo-service")
                                        .exchangeToMono(clientResponse -> {
                                            log.info("Response Thread {} - {}",
                                                    Thread.currentThread().threadId(),
                                                    Thread.currentThread().getName());

                                            return Mono.empty();
                                        });


        result.subscribe();

        return Thread.currentThread().toString();
    }

}
