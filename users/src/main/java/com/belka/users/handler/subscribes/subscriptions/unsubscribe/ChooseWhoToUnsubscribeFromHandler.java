package com.belka.users.handler.subscribes.subscriptions.unsubscribe;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.belka.users.handler.subscribes.subscriptions.unsubscribe.UnsubscribeHandler.PREFIX_FOR_UNSUBSCRIBE_CALLBACK;

@Component
@AllArgsConstructor
public class ChooseWhoToUnsubscribeFromHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Choose Who To Unsubscribe From";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = UnsubscribeHandler.CODE;
    private final static String HEADER = "are you sure?";
    public final static String YES_BUTTON = "Yep, that's right";
    public final static String NO_BUTTON = "nope";
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getPrevious_step().equals(PREVIOUS_HANDLER) && event.isHasCallbackQuery() &&
                    event.getData().startsWith(PREFIX_FOR_UNSUBSCRIBE_CALLBACK)) {

                Long chatId = event.getChatId();
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .nextStep(NEXT_HANDLER)
                        .userId(chatId)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(chatId)
                        .handlerCode(CODE)
                        .requestTime(OffsetDateTime.now())
                        .build());

                return Flux.just(editMessage(getButtons(chatId, event), HEADER));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private SendMessage getButtons(Long chatId, BelkaEvent event) {
        SendMessage message = sendMessage(chatId, HEADER);
        String producerId = event.getData().substring(PREFIX_FOR_UNSUBSCRIBE_CALLBACK.length());
        message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineOne = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineTwo = new ArrayList<>();

        InlineKeyboardButton yesButton = getButton(YES_BUTTON, PREFIX_FOR_UNSUBSCRIBE_CALLBACK + YES_BUTTON + producerId);
        rowInlineOne.add(yesButton);

        InlineKeyboardButton noButton = getButton(NO_BUTTON, PREFIX_FOR_UNSUBSCRIBE_CALLBACK + NO_BUTTON);
        rowInlineTwo.add(noButton);

        rowsInLine.add(rowInlineOne);
        rowsInLine.add(rowInlineTwo);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }
}
