package com.belka.wearther.models;

import lombok.Data;

@Data
public class Geo {

    private String query;
    private String country;
    private String countryCode;
    private String city;
    private Double lat;
    private Double lon;

}
