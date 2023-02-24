package com.belka.wearther.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Main {
    private Integer temp;

    @JsonProperty("feels_like")
    private Integer feelsLike;
}
