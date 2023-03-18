package com.belka.wearther.models;

import lombok.Data;

/**
 * DTO for extract user's IP with external API
 */
@Data
public class IP {
    /**
     * user's IP
     */
    private String ip;
}
