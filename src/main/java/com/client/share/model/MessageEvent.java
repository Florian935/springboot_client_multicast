package com.client.share.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MessageEvent {

    private Long timer;
    private String message;
    private Instant instant;
    private LocalDateTime dateTime;
}
