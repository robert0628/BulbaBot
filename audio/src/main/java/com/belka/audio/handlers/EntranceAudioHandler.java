package com.belka.audio.handlers;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.handler.subscribes.subscibers.GetSubscribersHandler;
import com.belka.users.handler.subscribes.subscriptions.SubscriptionsHandler;
import com.belka.users.handler.subscribes.subscriptions.subscribe.SubscribeHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
@Slf4j
public class EntranceAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/audio";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = EntranceAudioHandler.class.getSimpleName();
    final static String BUTTON_PULL = "pull new ones";
    final static String BUTTON_SUBSCRIBE = "subscribe to new people";
    final static String BUTTON_CALENDAR = "calendar";
    final static String BUTTON_SUBSCRIBERS = "subscribers";
    final static String BUTTON_SUBSCRIPTIONS = "subscriptions";


    private final static String HEADER = "what would you like to do?";
    private final ExecutorService executorService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
                Long chatId = event.getChatId();

                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                return Flux.just(getButtons(event.getChatId()));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = sendMessage(chatId, HEADER);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> rowInlinePull = getRowInlineWithOneButton(BUTTON_PULL, PullAudioHandler.CODE);
        List<InlineKeyboardButton> rowInlineSubscribe = getRowInlineWithOneButton(BUTTON_SUBSCRIBE, SubscribeHandler.CODE);
        List<InlineKeyboardButton> rowInlineCalendar = getRowInlineWithOneButton(BUTTON_CALENDAR, CalendarAudioHandler.CODE);
        List<InlineKeyboardButton> rowInlineSubscriptions = getRowInlineWithOneButton(BUTTON_SUBSCRIPTIONS, SubscriptionsHandler.CODE);
        List<InlineKeyboardButton> rowInlineSubscribers = getRowInlineWithOneButton(BUTTON_SUBSCRIBERS, GetSubscribersHandler.CODE);

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>(List.of(
                rowInlinePull,
                rowInlineSubscribe,
                rowInlineCalendar,
                rowInlineSubscriptions,
                rowInlineSubscribers));

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }

    private PreviousStepDto getPreviousStep(Long chatId) {
        return PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                .data("")
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
