package com.belka.BulbaBot.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.users.model.UserDto;
import com.belka.users.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;

/**
 * а handler that initializes the user in the system and starts the interaction
 */
@Component
@AllArgsConstructor
public class StartHandler implements BelkaHandler {

    private final static String CODE = "/start";
    private final PreviousService previousService;
    private final UserService userService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .previousId(event.getUpdateId())
                    .build());
            registerUser(event.getUpdate().getMessage());
            return Flux.just(startCommandReceived(chatId, event.getUpdate().getMessage().getChat().getFirstName()));
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

    private SendMessage startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + " nice to meet you" + " :blush:");
        return sendMessage(chatId, answer);
    }

    private SendMessage sendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
