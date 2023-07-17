package com.belka.stats.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * get buttons to choose which stats you want to get
 */
@Component
@AllArgsConstructor
public class StatsStartHandler implements BelkaHandler {
    private final static String CODE = "/stats";
    private final static String HEADER = "what stats do you want?";
    final static String BUTTON_1 = "get total requests";
    final static String BUTTON_2 = "get total requests by user";
    final static String BUTTON_3 = "get total Requests by code";
    final static String BUTTON_4 = "get most popular request";
    final static String BUTTON_5 = "get most popular request by user";
    private final PreviousService previousService;
    private final StatsService statsService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(chatId)
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(getButtons(chatId));
        }
        return null;
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = belkaSendMessage.sendMessage(chatId, HEADER);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        //collection of all buttons
        Collection<String> buttons = List.of(BUTTON_1, BUTTON_2, BUTTON_3, BUTTON_4, BUTTON_5);

        buttons.forEach(button -> rowsInLine.add(getRowInline(button)));

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }

    private List<InlineKeyboardButton> getRowInline(String button) {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton first = new InlineKeyboardButton();
        first.setText(button);
        first.setCallbackData(button);
        rowInline.add(first);
        return rowInline;
    }
}