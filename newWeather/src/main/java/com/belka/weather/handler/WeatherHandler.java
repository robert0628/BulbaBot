package com.belka.weather.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.weather.service.weather.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

/**
 * the handler that processes the weather request
 */
@Component
@AllArgsConstructor
public class WeatherHandler implements BelkaHandler {

    private final static String CODE = "/weather";
    private final PreviousService previousService;
    private final WeatherService weatherService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            return Flux.just(sendMessage(chatId, weatherService.getWeatherResponse(weatherService.findCity())));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId, String weather) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(weather);
        return message;
    }
}
