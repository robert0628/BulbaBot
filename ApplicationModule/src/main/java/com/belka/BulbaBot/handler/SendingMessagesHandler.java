package com.belka.BulbaBot.handler;

import com.belka.BulbaBot.config.BotConfig;
import com.belka.BulbaBot.service.UserService;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@AllArgsConstructor
public class SendingMessagesHandler implements BelkaHandler {

    private final static String CODE = "/send";
    private final PreviousService previousService;
    private final UserService userService;
    private final BotConfig botConfig;

    @Override
    public PartialBotApiMethod<?> handle(BelkaEvent event) {
        if (event.isHasText()
                && event.getText().equalsIgnoreCase(CODE)
                && botConfig.getBotOwner().equals(event.getChatId())) {

            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());

            String textToSend = EmojiParser.parseToUnicode(event.getText());
            userService.getAll().forEach(userDto -> sendMessage(userDto.getId(), textToSend));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        return message;
    }
}
