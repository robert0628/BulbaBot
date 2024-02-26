package com.belka.users.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.handler.subscribes.subscibers.SubscribersHandler;
import com.belka.users.handler.subscribes.subscriptions.SubscriptionsHandler;
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

/**
 * shows main subscribes opportunities
 */
@Component
@AllArgsConstructor
public class SubscribesHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Subscribes";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String HEADER = "and what do you want?";
    private final static String BUTTON_SUBSCRIPTIONS = "subscriptions";
    private final static String BUTTON_SUBSCRIBERS = "subscribers";
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event)) {
                Long chatId = event.getChatId();
                savePreviousStep(chatId);
                recordStats(chatId);
                return Flux.just(getButtons(event.getChatId()));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = sendMessage(chatId, HEADER);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> rowInlineSubscriptions = getRowInlineWithOneButton(BUTTON_SUBSCRIPTIONS, SubscriptionsHandler.CODE);
        List<InlineKeyboardButton> rowInlineSubscribers = getRowInlineWithOneButton(BUTTON_SUBSCRIBERS, SubscribersHandler.CODE);

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>(
                List.of(
                        rowInlineSubscriptions,
                        rowInlineSubscribers
                )
        );

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }

    private boolean isSubscribeCommand(BelkaEvent event) {
        return event.isHasText() && event.getText().equalsIgnoreCase(CODE) ||
                event.isHasCallbackQuery() && event.getData().equals(CODE);
    }

    private void savePreviousStep(Long chatId) {
        previousService.save(PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                .data("")
                .build());
    }

    private void recordStats(Long chatId) {
        statsService.save(StatsDto.builder()
                .userId(chatId)
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build());
    }
}