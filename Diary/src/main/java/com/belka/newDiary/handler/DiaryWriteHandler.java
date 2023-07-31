package com.belka.newDiary.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.DiaryService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class DiaryWriteHandler implements BelkaHandler {
    static String CODE = "WRITE_DIARY";
    private final static String NEXT_HANDLER = DiaryShareHandler.CODE;
    private final static String PREVIOUS_HANDLER = DiaryGetHeaderWriteHandler.CODE;
    final static String YES_BUTTON = "yes";
    final static String NO_BUTTON = "no";
    private final static String ANSWER = "got it";
    private final static String HEADER = "do you want to share this";
    private final PreviousService previousService;
    private final DiaryService diaryService;
    private final StatsService statsService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getPrevious_step().equals(PREVIOUS_HANDLER)) {
            Long chatId = event.getChatId();
            diaryService.addNote(chatId, event.getText());
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .nextStep(NEXT_HANDLER)
                    .userId(chatId)
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(chatId)
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(belkaSendMessage.sendMessage(chatId, ANSWER), getButtons(chatId));
        }
        return Flux.empty();
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = belkaSendMessage.sendMessage(chatId, HEADER);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton getButton = new InlineKeyboardButton();
        getButton.setText(YES_BUTTON);
        getButton.setCallbackData(CODE + YES_BUTTON);

        InlineKeyboardButton writeButton = new InlineKeyboardButton();
        writeButton.setText(NO_BUTTON);
        writeButton.setCallbackData(CODE + NO_BUTTON);

        rowInline.add(getButton);
        rowInline.add(writeButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        return message;
    }
}
