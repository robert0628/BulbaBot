package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class DiaryBaseHandler implements BelkaHandler {

    private final static String CODE = "/diary";
    private final static String GET_DIARY = "GET_DIARY";
    private final static String WRITE_DIARY = "WRITE_DIARY";
    private final static String HEADER_1 = "what do you want?";
    private final static String HEADER_2 = "write some words";
    private final static String BUTTON_1 = "get diary";
    private final static String BUTTON_2 = "write diary";
    private final PreviousService previousService;

    @Override
    public PartialBotApiMethod<?> handle(BelkaEvent event) {
        Update update = event.getUpdate();
        if (update.hasMessage() && update.getMessage().hasText() && event.getMessage().equals(CODE)) {
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(update.getMessage().getChatId())
                    .build());
            return getButtons(update.getMessage().getChatId());
        }
        return null;
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(HEADER_1)
                .build();
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton getButton = new InlineKeyboardButton();
        getButton.setText(BUTTON_1);
        getButton.setCallbackData(GET_DIARY);

        InlineKeyboardButton writeButton = new InlineKeyboardButton();
        writeButton.setText(BUTTON_2);
        writeButton.setCallbackData(WRITE_DIARY);

        rowInline.add(getButton);
        rowInline.add(writeButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        return message;
    }
}
