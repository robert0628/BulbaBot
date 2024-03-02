package com.belka.stats.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * get buttons to choose which stats you want to get
 */
@Component
@AllArgsConstructor
@Slf4j
public class StatsStartHandler extends AbstractBelkaHandler {
    final static String CODE = "/stats";
    private final static String NEXT_HANDLER = GetStatsHandler.CODE;
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = StatsStartHandler.class.getSimpleName();
    private final static String HEADER = "what stats do you want?";
    final static String BUTTON_1 = "get total requests";
    final static String BUTTON_2 = "get total requests by user";
    final static String BUTTON_3 = "get total Requests by code";
    final static String BUTTON_4 = "get most popular request";
    final static String BUTTON_5 = "get most popular request by user";
    private final ExecutorService executorService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event, CODE)) {
                Long chatId = event.getChatId();
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                return Flux.just(getButtons(chatId));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private SendMessage getButtons(Long chatId) {
        SendMessage message = sendMessage(chatId, HEADER);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        //collection of all buttons
        Collection<String> buttons = List.of(BUTTON_1, BUTTON_2, BUTTON_3, BUTTON_4, BUTTON_5);

        buttons.forEach(button -> rowsInLine.add(getRowInline(button)));

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        return message;
    }

    private List<InlineKeyboardButton> getRowInline(String buttonText) {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = getButton(buttonText, buttonText);
        rowInline.add(button);
        return rowInline;
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