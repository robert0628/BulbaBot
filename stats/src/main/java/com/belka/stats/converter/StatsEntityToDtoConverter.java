package com.belka.stats.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.stats.StatsDto;
import com.belka.stats.StatsEntity;
import org.springframework.stereotype.Component;

@Component
public class StatsEntityToDtoConverter implements BelkaConverter<StatsEntity, StatsDto> {
    @Override
    public StatsDto convert(StatsEntity value) {
        return StatsDto.builder()
                .requestTime(value.getRequestTime())
                .handlerCode(value.getHandlerCode())
                .userId(value.getUserId())
                .build();
    }

    @Override
    public Class<StatsDto> getOutputType() {
        return StatsDto.class;
    }

    @Override
    public Class<StatsEntity> getInputType() {
        return StatsEntity.class;
    }
}
