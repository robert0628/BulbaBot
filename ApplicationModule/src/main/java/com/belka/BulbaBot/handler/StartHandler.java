package com.belka.BulbaBot.handler;

import com.belka.BulbaBot.model.User;
import com.belka.BulbaBot.repository.UserRepository;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

@Component
@AllArgsConstructor
public class StartHandler implements BelkaHandler {

    private final static String CODE = "/start";
    private final PreviousService previousService;
    private final UserRepository userRepository;

    @Override
    public PartialBotApiMethod<?> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equals(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            registerUser(event.getUpdate().getMessage());
            return startCommandReceived(chatId, event.getUpdate().getMessage().getChat().getFirstName());
        }
        return null;
    }

    private void registerUser(Message message) {
        if (!userRepository.existsById(message.getChatId())) {
            Chat chat = message.getChat();
            User user = User.builder()
                    .id(chat.getId())
                    .firstname(chat.getFirstName())
                    .lastname(chat.getLastName())
                    .username(chat.getUserName())
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            userRepository.save(user);
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
