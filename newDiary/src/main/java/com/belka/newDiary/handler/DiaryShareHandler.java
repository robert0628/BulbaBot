package com.belka.newDiary.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.DiaryService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DiaryShareHandler extends AbstractBelkaHandler {
    final static String CODE = "SHARE_DIARY";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = DiaryWriteHandler.CODE;
    private final static String PREVIOUS_DATA_YES = DiaryWriteHandler.CODE + DiaryWriteHandler.YES_BUTTON;
    private final static String PREVIOUS_DATA_NO = DiaryWriteHandler.CODE + DiaryWriteHandler.NO_BUTTON;
    private final static String ANSWER_FOR_SHARING = "the note has been sent";
    private final static String ANSWER_FOR_SAVING = "the note has been saved";
    private final static String PREFIX_FOR_NOTE = "note from ";
    private final PreviousService previousService;
    private final DiaryService diaryService;
    private final StatsService statsService;
    private final UserService userService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasCallbackQuery() && event.getData().equals(PREVIOUS_DATA_YES)) {
                Long chatId = event.getChatId();
                String note = PREFIX_FOR_NOTE + userService.getName(chatId) + "/n" + diaryService.getNote(LocalDate.now(), chatId);
                Collection<Long> followersId = userService.getFollowersId(chatId);
                Collection<SendMessage> messages = followersId.stream().map(id -> sendMessage(id, note)).collect(Collectors.toList());

                messages.add(sendMessage(chatId, ANSWER_FOR_SHARING));

                savePreviousAndStats(event);
                return Flux.fromIterable(messages);
            } else if (event.isHasCallbackQuery() && event.getData().equals(PREVIOUS_DATA_NO)) {
                Long chatId = event.getChatId();
                savePreviousAndStats(event);
                return Flux.just(sendMessage(chatId, ANSWER_FOR_SAVING));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private void savePreviousAndStats(BelkaEvent event) {
        previousService.save(PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(event.getChatId())
                .build());
        statsService.save(StatsDto.builder()
                .userId(event.getChatId())
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build());
    }
}
