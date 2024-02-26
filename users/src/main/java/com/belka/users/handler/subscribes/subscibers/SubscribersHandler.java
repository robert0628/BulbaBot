package com.belka.users.handler.subscribes.subscibers;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.handler.subscribes.subscriptions.unsubscribe.DeleteSubscriptionHandler;
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
 * shows all commands that user can do with his subscribers
 */
@Component
@AllArgsConstructor
public class SubscribersHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Subscribers";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String HEADER = "that's your subscribers";
    private final static String BUTTON_SHOW_SUBSCRIBERS = "show all subscribers";
    private final static String BUTTON_DELETE_SUBSCRIBER = "to block someone"; //todo button for blocking subscriber
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

        List<InlineKeyboardButton> rowInlineOneShowSubscribers = getRowInlineWithOneButton(BUTTON_SHOW_SUBSCRIBERS, GetSubscribersHandler.CODE);
        List<InlineKeyboardButton> rowInlineDeleteSubscriber = getRowInlineWithOneButton(BUTTON_DELETE_SUBSCRIBER, DeleteSubscriptionHandler.CODE);

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>(List.of(
                rowInlineOneShowSubscribers,
                rowInlineDeleteSubscriber)
        );

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }

    private boolean isSubscribeCommand(BelkaEvent event) {
        return event.isHasText() && event.getText().equalsIgnoreCase(CODE) ||
                event.isHasCallbackQuery() && event.getData().equalsIgnoreCase(CODE);
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
