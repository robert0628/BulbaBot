package com.belka.audio.handlers;

import com.belka.audio.services.AudioService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class RecordAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/record audio";
    private final static String NEXT_HANDLER = "save audio handler";
    private final static String PREVIOUS_HANDLER = "";
    final static String BUTTON_SAVE = "SAVE";
    final static String BUTTON_DELETE = "DELETE";
    private final static String HEADER_1 = "do we save this record?";
    private final AudioService audioService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getUpdate().getMessage().hasVoice()) {
                Long chatId = event.getChatId();
                Voice voice = event.getUpdate().getMessage().getVoice();
                audioService.saveVoice(voice, chatId);

                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .nextStep(NEXT_HANDLER)
                        .userId(chatId)
                        .data(voice.getFileId())
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(event.getChatId())
                        .handlerCode(CODE)
                        .requestTime(LocalDateTime.now())
                        .build());
                return Flux.just(getButtons(event.getChatId()));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = sendMessage(chatId, HEADER_1);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton getButton = new InlineKeyboardButton();
        getButton.setText(BUTTON_SAVE);
        getButton.setCallbackData(CODE + BUTTON_SAVE);

        InlineKeyboardButton writeButton = new InlineKeyboardButton();
        writeButton.setText(BUTTON_DELETE);
        writeButton.setCallbackData(CODE + BUTTON_DELETE);

        rowInline.add(getButton);
        rowInline.add(writeButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        return message;
    }
}