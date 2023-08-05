package com.belka.BulbaBot.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.converter.ConverterService;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class HandlerServiceImpl implements HandlerService {
    private final Collection<BelkaHandler> handlers;
    private final ConverterService converterService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(Update update) {
        BelkaEvent event = converterService.ConvertTo(BelkaEvent.class, update);
        if (handlers.stream()
                .map(handler -> handler.handle(event))
                .filter(flux -> flux.hasElements().block())
                .findAny()
                .isEmpty()) {
            return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), "asd"));
        }
        return Flux.fromStream(handlers.stream()
                        .map(handler -> handler.handle(event)))
                .flatMap(Function.identity());
    }

}
