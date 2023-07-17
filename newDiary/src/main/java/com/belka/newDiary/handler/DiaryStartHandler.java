package com.belka.newDiary.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class DiaryStartHandler implements BelkaHandler {
    final static String CODE = "/diary";
    final static String BUTTON_1 = "get diary";
    final static String BUTTON_2 = "write diary";
    private final static String HEADER_1 = "what do you want?";
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
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(getButtons(chatId));
        }
        return null;
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = belkaSendMessage.sendMessage(chatId, HEADER_1);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton getButton = new InlineKeyboardButton();
        getButton.setText(BUTTON_1);
        getButton.setCallbackData(CODE + BUTTON_1);

        InlineKeyboardButton writeButton = new InlineKeyboardButton();
        writeButton.setText(BUTTON_2);
        writeButton.setCallbackData(CODE + BUTTON_2);

        rowInline.add(getButton);
        rowInline.add(writeButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        return message;
    }
}
