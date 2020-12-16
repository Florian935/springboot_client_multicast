package com.client.share.controller;

import com.client.share.model.MessageEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final WebClient webClient;
    private static final String SERVER_BASE_URL = "http://localhost:8081";

    public ClientController() {
        webClient = WebClient.create(SERVER_BASE_URL);
    }

    @GetMapping(path = "/streamSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MessageEvent> streamSSE() {
        return webClient
                .get()
                .uri("/server/stream")
                .retrieve()
                .bodyToFlux(Long.class)
                .flatMap(timer ->
                        Flux.just(
                                MessageEvent.builder()
                                        .message("Hola !")
                                        .timer(timer)
                                        .instant(Instant.now())
                                        .dateTime(LocalDateTime.now())
                                        .build()
                        )
                );
    }

    @GetMapping(path = "/streamSSE/zips/{message}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamSSEZip(@PathVariable String message) {
        Flux<Long> timer = webClient
                .get()
                .uri("/server/stream")
                .retrieve()
                .bodyToFlux(Long.class);

        Flux<String> messageFlux = Flux.generate(
                () -> 0,
                (index, sink) -> {
                    sink.next(message);
                    return ++index;
                });

        return Flux.zip(timer, messageFlux)
                .map(tuple -> String.format("%s %d", tuple.getT2(), tuple.getT1())
                );
    }
}
