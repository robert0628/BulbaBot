package com.belka.BulbaBot.handler;

import com.belka.core.converter.ConverterService;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Data
public class HandlerService {
    private final Collection<BelkaHandler> collection;
    private final ConverterService converterService;

    public Collection<PartialBotApiMethod<?>> handle(Update update) {
        BelkaEvent event = converterService.ConvertTo(BelkaEvent.class, update);
        return collection.stream().map(col -> col.handle(event)).collect(Collectors.toList());
    }


}
