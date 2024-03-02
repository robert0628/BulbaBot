package com.belka.BulbaBot.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.dto.UserDto;
import com.belka.users.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * а handler that initializes the user in the system and starts the interaction
 */
@Component
@AllArgsConstructor
@Slf4j
public class StartHandler extends AbstractBelkaHandler {

    private final static String CODE = "/start";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = StartHandler.class.getSimpleName();
    private final ExecutorService executorService;
    private final UserService userService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event, CODE)) {
                Long chatId = event.getChatId();
                String answer = EmojiParser.parseToUnicode("Hi, " + event.getUpdate().getMessage().getChat().getFirstName() + " nice to meet you" + " :blush:");
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                registerUser(event.getUpdate().getMessage());
                recordStats(getStats(chatId));
                return Flux.just(sendMessage(chatId, answer));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private void registerUser(Message message) {
        if (!userService.existsById(message.getChatId())) {
            Chat chat = message.getChat();
            UserDto userDto = UserDto.builder()
                    .id(chat.getId())
                    .firstname(chat.getFirstName())
                    .lastname(chat.getLastName())
                    .username(chat.getUserName())
                    .registeredAt(OffsetDateTime.now())
                    .build();
            userService.save(userDto);
        }
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
