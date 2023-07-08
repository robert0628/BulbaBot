package com.belka.stats;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatsDto {
    private Long id;
    private Long userId;
    private String handlerCode;
    private LocalDateTime requestTime;
}
