package com.belka.wearther.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for extract user's geolocation from external API
 */
@Data
public class Geo {

    @JsonProperty("query")
    private String ip;
    private String country;
    private String countryCode;
    private String city;
    private Double lat;
    private Double lon;

}
