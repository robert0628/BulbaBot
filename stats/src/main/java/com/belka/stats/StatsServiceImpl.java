package com.belka.stats;

import com.belka.core.converter.ConverterService;
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
}
