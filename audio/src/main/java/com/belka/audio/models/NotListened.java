package com.belka.audio.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotListened {
    private Long subscriber;
    private String audioId;
}