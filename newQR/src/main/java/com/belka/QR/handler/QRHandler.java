package com.belka.QR.handler;

import com.belka.QR.service.QRService;
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
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * the handler that processes the user's request to create a QR code
 */
@Component
@AllArgsConstructor
public class QRHandler extends AbstractBelkaHandler {
    private final static String CODE = "/QR";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String EXIT_CODE = "/send QR";
    private final static String HEADER_1 = "write your text";
    private final PreviousService previousService;
    private final QRService qrService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasMessage() && event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
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
                return Flux.just(sendMessage(chatId, HEADER_1));
            }
            if (event.isHasMessage() && event.isHasText() && event.getPrevious_step().equals(CODE)) {
                Long chatId = event.getChatId();
                previousService.save(PreviousStepDto.builder()
                        .previousStep(EXIT_CODE)
                        .userId(chatId)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(event.getChatId())
                        .handlerCode(CODE)
                        .requestTime(OffsetDateTime.now())
                        .build());
                return Flux.just(sendImageFromUrl(qrService.getQRLink(event.getText()), chatId));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }
}
