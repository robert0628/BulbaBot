package com.belka.audio.handlers;

import com.belka.audio.services.AudioService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
@Slf4j
public class RecordAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/record audio";
    private final static String NEXT_HANDLER = "save audio handler";
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = RecordAudioHandler.class.getSimpleName();
    final static String BUTTON_SAVE = "SAVE";
    final static String BUTTON_DELETE = "DELETE";
    private final static String HEADER_1 = "do we save this record?";
    private final AudioService audioService;
    private final ExecutorService executorService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasMessage() && event.getUpdate().getMessage().hasVoice()) {
                Long chatId = event.getChatId();
                Voice voice = event.getUpdate().getMessage().getVoice();
                audioService.saveVoice(voice, chatId);

                savePreviousStep(getPreviousStep(chatId, voice.getFileId()), CLASS_NAME);
                recordStats(getStats(chatId));
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

        InlineKeyboardButton saveButton = getButton(BUTTON_SAVE, BUTTON_SAVE);
        InlineKeyboardButton deleteButton = getButton(BUTTON_DELETE, BUTTON_DELETE);

        rowInline.add(saveButton);
        rowInline.add(deleteButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        return message;
    }

    private PreviousStepDto getPreviousStep(Long chatId, String data) {
        return PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                // put the ID of the file to work with it at the stage where we will share the voice
                .data(data)
                .build();
    }

    private StatsDto getStats(Long chatId) {
        return StatsDto.builder()
                .userId(chatId)
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build();
    }

    private void recordStats(StatsDto statsDto) {
        executorService.execute(() -> {
                    statsService.save(statsDto);
                    log.info("Stats from {} have been recorded", CLASS_NAME);
                }
        );
    }
}