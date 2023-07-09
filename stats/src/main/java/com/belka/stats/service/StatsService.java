package com.belka.stats.service;

import com.belka.stats.StatsDto;

/**
 * collects and processes stats
 */
public interface StatsService {
    /**
     * save stats
     *
     * @param dto {@link StatsDto}
     */
    void save(StatsDto dto);

    /**
     * get number of requests
     */
    long getTotalRequests();

    /**
     * get number of requests by user's id
     *
     * @param chatId {@link com.belka.core.handlers.BelkaEvent's chatId}
     */
    long getTotalRequestsByUser(Long chatId);

    /**
     * get number of requests by code
     *
     * @param code {@link com.belka.core.handlers.BelkaHandler's code}
     */
    long getTotalRequestsByCode(String code);

    /**
     * get {@link com.belka.core.handlers.BelkaHandler's code} with the maximum number of requests
     */
    String getMostPopularRequest();

    /**
     * get {@link com.belka.core.handlers.BelkaHandler's code} with the maximum number of requests from user
     *
     * @param chatId {@link com.belka.core.handlers.BelkaEvent's chatId}
     */
    String getMostPopularRequestByUser(Long chatId);

}
