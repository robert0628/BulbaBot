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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DiaryShareHandler extends AbstractBelkaHandler {
    final static String CODE = "WRITE_DIARY";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = DiaryWriteHandler.CODE;
    private final static String PREVIOUS_DATA = DiaryWriteHandler.CODE + DiaryWriteHandler.YES_BUTTON;
    private final static String ANSWER = "the note has been sent";
    private final static String PREFIX_FOR_NOTE = "note from ";
    private final PreviousService previousService;
    private final DiaryService diaryService;
    private final StatsService statsService;
    private final UserService userService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasCallbackQuery() && event.getData().equals(PREVIOUS_DATA)) {
            Long chatId = event.getChatId();
            String note = PREFIX_FOR_NOTE + userService.getName(chatId) + "/n" + diaryService.getNote(LocalDate.now(), chatId);
            Collection<Long> followersId = userService.getFollowersId(chatId);
            Collection<SendMessage> messages = followersId.stream().map(id -> sendMessage(id, note)).collect(Collectors.toList());

            messages.add(sendMessage(chatId, ANSWER));

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
            return Flux.fromIterable(messages);
        }
        return Flux.empty();
    }
}
