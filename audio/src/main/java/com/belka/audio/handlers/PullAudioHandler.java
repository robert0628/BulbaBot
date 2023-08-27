package com.belka.audio.handlers;

import com.belka.audio.services.AudioService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class PullAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/pull audio";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final AudioService audioService;
    private final UserService userService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasCallbackQuery() && event.getData().equals(EntranceAudioHandler.BUTTON_PULL)) {
                Long chatId = event.getChatId();
                Collection<Long> producersId = userService.getProducersId(chatId);
                Collection<String> audiosId = getAudiosId(producersId, chatId);

                Collection<PartialBotApiMethod<?>> methods = new ArrayList<>();
                audiosId.forEach(id -> methods.add(sendAudioFromLocalStorage(audioService.getPathToAudio(id), chatId)));

                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .nextStep(NEXT_HANDLER)
                        .userId(chatId)
                        .data("")
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(event.getChatId())
                        .handlerCode(CODE)
                        .requestTime(LocalDateTime.now())
                        .build());

                return Flux.fromIterable(methods);
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    @Transactional
    public Collection<String> getAudiosId(Collection<Long> producersId, Long chatId) {
        LocalDate today = LocalDate.now();
        Collection<String> audiosId = new ArrayList<>();
        for (Long id : producersId) {
            audiosId.addAll(audioService.getAudiosIDbyUser(id));
            userService.setPullDate(today, id, chatId);
        }
        return audiosId;
    }
}
