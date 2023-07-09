package com.belka.stats.service;

import com.belka.core.converter.ConverterService;
import com.belka.stats.StatsDto;
import com.belka.stats.StatsEntity;
import com.belka.stats.StatsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final ConverterService converterService;

    @Override
    public void save(StatsDto dto) {
        statsRepository.save(converterService.ConvertTo(StatsEntity.class, dto));
    }

    public long getTotalRequests(){
        return statsRepository.count();
    }

    @Override
    public long getTotalRequestsByUser(Long chatId) {
        return statsRepository.countByUserId(chatId);
    }

    @Override
    public long getTotalRequestsByCode(String code) {
        return statsRepository.countByHandlerCode(code);
    }

    @Override
    public String getMostPopularRequest() {
        return statsRepository.getMostPopularRequest();
    }

    @Override
    public String getMostPopularRequestByUser(Long chatId) {
        return statsRepository.getMostPopularRequestByUser(chatId);
    }

}
