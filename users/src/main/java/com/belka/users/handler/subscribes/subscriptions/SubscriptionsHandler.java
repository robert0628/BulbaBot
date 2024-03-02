package com.belka.users.handler.subscribes.subscriptions;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.handler.subscribes.subscriptions.subscribe.SubscribeHandler;
import com.belka.users.handler.subscribes.subscriptions.unsubscribe.UnsubscribeHandler;
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

/**
 * shows all commands that user can do with his subscriptions
 */
@Component
@AllArgsConstructor
@Slf4j
public class SubscriptionsHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Subscriptions";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = SubscriptionsHandler.class.getSimpleName();
    private final static String HEADER = "that's your subscriptions";
    private final static String BUTTON_SHOW_SUBSCRIPTIONS = "show all subscriptions";
    private final static String BUTTON_SUBSCRIBE_TO = "subscribe to someone";
    private final static String BUTTON_UNSUBSCRIBE = "unsubscribe from anyone";
    private final ExecutorService executorService;
    private final StatsService statsService;


    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event, CODE)) {
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

        List<InlineKeyboardButton> rowInlineOneShowSubscriptions = getRowInlineWithOneButton(BUTTON_SHOW_SUBSCRIPTIONS, GetSubscriptionsHandler.CODE);
        List<InlineKeyboardButton> rowInlineTwoSubscribe = getRowInlineWithOneButton(BUTTON_SUBSCRIBE_TO, SubscribeHandler.CODE);
        List<InlineKeyboardButton> rowInlineThreeUnsubscribe = getRowInlineWithOneButton(BUTTON_UNSUBSCRIBE, UnsubscribeHandler.CODE);

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>(List.of(
                rowInlineOneShowSubscriptions,
                rowInlineTwoSubscribe,
                rowInlineThreeUnsubscribe)
        );

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