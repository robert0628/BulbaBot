package com.belka.audio.handlers;

import com.belka.audio.models.NotListened;
import com.belka.audio.services.AudioCalendarService;
import com.belka.audio.services.AudioService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class PullAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/pull audio";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String NO_AUDIO_ANSWER = "there are no new audios for you";
    private final AudioService audioService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasCallbackQuery() && event.getData().equals(CODE)) {
                Long chatId = event.getChatId();
                if (!audioService.existAudioForUser(chatId)) {
                    return Flux.just(sendMessage(chatId, "there are no new audios for you"));
                }
                NotListened notListenedAudio = audioService.getMetaDataAudioForPull();
                String fileId = notListenedAudio.getAudioId();
                audioService.removeAudioFromListening(notListenedAudio.getSubscriber(), fileId);

                savePreviousAndStats(chatId);

                return Flux.just(sendAudioFromLocalStorage(audioService.getPathToAudio(fileId), chatId));
            }
            if (event.isHasCallbackQuery() && event.getData().startsWith(AudioCalendarService.DATA_AUDIO_CODE)) {
                Long chatId = event.getChatId();
                String[] data = event.getData().split("\\.");
                LocalDate date = LocalDate.of(Integer.parseInt(data[1]), Integer.parseInt(data[2]) + 1, Integer.parseInt(data[3]));
                String fileId = audioService.getFileId(chatId, date);

                savePreviousAndStats(chatId);

                if (fileId == null) {
                    SendMessage message = sendMessage(chatId, NO_AUDIO_ANSWER);
                    message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());
                    return Flux.just(editMessage(message, NO_AUDIO_ANSWER));
                }
                return Flux.just(sendAudioFromLocalStorage(audioService.getPathToAudio(fileId), chatId));

            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private void savePreviousAndStats(Long chatId) {
        previousService.save(PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                .data("")
                .build());
        statsService.save(StatsDto.builder()
                .userId(chatId)
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build());
    }
}
