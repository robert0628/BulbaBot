package com.belka.audio.converter;

import com.belka.audio.entityes.NotListenedEntity;
import com.belka.audio.models.NotListened;
import com.belka.core.converter.BelkaConverter;
import org.springframework.stereotype.Component;

@Component
public class NotListenedEntityToModelConverter implements BelkaConverter<NotListenedEntity, NotListened> {
    @Override
    public NotListened convert(NotListenedEntity value) {
        return NotListened.builder()
                .audioId(value.getAudioId())
                .subscriber(value.getSubscriber())
                .build();
    }

    @Override
    public Class<NotListened> getOutputType() {
        return NotListened.class;
    }

    @Override
    public Class<NotListenedEntity> getInputType() {
        return NotListenedEntity.class;
    }
}
