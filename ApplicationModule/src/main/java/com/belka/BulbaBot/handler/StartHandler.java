package com.belka.BulbaBot.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.dto.UserDto;
import com.belka.users.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * а handler that initializes the user in the system and starts the interaction
 */
@Component
@AllArgsConstructor
public class StartHandler implements BelkaHandler {

    private final static String CODE = "/start";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final PreviousService previousService;
    private final UserService userService;
    private final BelkaSendMessage belkaSendMessage;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
            Long chatId = event.getChatId();
            String answer = EmojiParser.parseToUnicode("Hi, " + event.getUpdate().getMessage().getChat().getFirstName() + " nice to meet you" + " :blush:");
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .nextStep(NEXT_HANDLER)
                    .build());
            registerUser(event.getUpdate().getMessage());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(belkaSendMessage.sendMessage(chatId, answer));
        }
        return null;
    }

    private void registerUser(Message message) {
        if (!userService.existsById(message.getChatId())) {
            Chat chat = message.getChat();
            UserDto userDto = UserDto.builder()
                    .id(chat.getId())
                    .firstname(chat.getFirstName())
                    .lastname(chat.getLastName())
                    .username(chat.getUserName())
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            userService.save(userDto);
        }
    }
}
