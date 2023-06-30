package com.belka.BulbaBot.handler;

import com.belka.core.converter.ConverterService;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@Service
@Data
public class HandlerService {
    private final Collection<BelkaHandler> handlers;
    private final ConverterService converterService;

    public Flux<PartialBotApiMethod<?>> handle(Update update) {
        BelkaEvent event = converterService.ConvertTo(BelkaEvent.class, update);
        return Flux.fromStream(handlers.stream()
                        .map(handler -> handler.handle(event))
                        .filter(Objects::nonNull))
                .flatMap(Function.identity());
    }


}
