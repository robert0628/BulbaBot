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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class SaveAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "save audio handler";
    private final static String NEXT_HANDLER = ShareAudioHandler.CODE;
    private final static String PREVIOUS_HANDLER = RecordAudioHandler.CODE;
    final static String BUTTON_SHARE = "share";
    final static String BUTTON_PRIVATE = "let's keep it private";
    private final static String HEADER = "do you want to share this";
    private final AudioService audioService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getPrevious_step().equals(PREVIOUS_HANDLER) && event.getCode().equals(CODE) && event.isHasCallbackQuery()) {
                Long chatId = event.getChatId();
                if (event.getData().equals(RecordAudioHandler.BUTTON_SAVE)) {
                    previousService.save(PreviousStepDto.builder()
                            .previousStep(CODE)
                            .nextStep(NEXT_HANDLER)
                            .userId(chatId)
                            // put the ID of the file to work with it at the stage where we will share the voice
                            .data(previousService.getData(chatId))
                            .build());
                    statsService.save(StatsDto.builder()
                            .userId(event.getChatId())
                            .handlerCode(CODE)
                            .requestTime(OffsetDateTime.now())
                            .build());
                    return Flux.just(getButtons(event.getChatId()));
                } else {
                    audioService.deleteVoice(previousService.getData(chatId));
                    previousService.save(PreviousStepDto.builder()
                            .previousStep(CODE)
                            .nextStep(NEXT_HANDLER)
                            .userId(chatId)
                            .data("")
                            .build());
                    statsService.save(StatsDto.builder()
                            .userId(event.getChatId())
                            .handlerCode(CODE)
                            .requestTime(OffsetDateTime.now())
                            .build());
                    return Flux.just(sendMessage(chatId, "message has been deleted"));
                }
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }


    private SendMessage getButtons(Long chatId) {
        SendMessage message = sendMessage(chatId, HEADER);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton shareButton = getButton(BUTTON_SHARE, BUTTON_SHARE);
        InlineKeyboardButton privateButton = getButton(BUTTON_PRIVATE, BUTTON_PRIVATE);

        rowInline.add(shareButton);
        rowInline.add(privateButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        return message;
    }
}
