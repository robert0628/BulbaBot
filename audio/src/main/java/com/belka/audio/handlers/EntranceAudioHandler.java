package com.belka.audio.handlers;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.handler.GetSubscribersHandler;
import com.belka.users.handler.GetSubscriptionsHandler;
import com.belka.users.handler.SubscribeHandler;
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
public class EntranceAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/audio";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    final static String BUTTON_PULL = "pull new ones";
    final static String BUTTON_SUBSCRIBE = "subscribe to new people";
    final static String BUTTON_CALENDAR = "get calendar";
    final static String BUTTON_SUBSCRIBERS = "get subscribers";
    final static String BUTTON_SUBSCRIPTIONS = "get subscriptions";


    private final static String HEADER = "what would you like to do?";
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
                Long chatId = event.getChatId();

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
                return Flux.just(getButtons(event.getChatId()));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = sendMessage(chatId, HEADER);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineOne = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineTwo = new ArrayList<>();

        InlineKeyboardButton pullButton = getButton(BUTTON_PULL, PullAudioHandler.CODE);
        InlineKeyboardButton subscribeButton = getButton(BUTTON_SUBSCRIBE, SubscribeHandler.CODE);
        InlineKeyboardButton calendarButton = getButton(BUTTON_CALENDAR, CalendarAudioHandler.CODE);
        InlineKeyboardButton subscriptionsButton = getButton(BUTTON_SUBSCRIPTIONS, GetSubscriptionsHandler.CODE);
        InlineKeyboardButton subscribersButton = getButton(BUTTON_SUBSCRIBERS, GetSubscribersHandler.CODE);

        rowInlineOne.add(pullButton);
        rowInlineOne.add(subscribeButton);
        rowInlineOne.add(calendarButton);
        rowsInLine.add(rowInlineOne);

        rowInlineTwo.add(subscriptionsButton);
        rowInlineTwo.add(subscribersButton);
        rowsInLine.add(rowInlineTwo);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }
}
