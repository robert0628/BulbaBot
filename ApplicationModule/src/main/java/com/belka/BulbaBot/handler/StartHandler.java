package com.belka.BulbaBot.handler;

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
public class StartHandler implements BelkaHandler {

    private final static String CODE = "/start";
    private final PreviousService previousService;
    @Override
    public PartialBotApiMethod<?> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equals(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            return startCommandReceived(chatId, event.getUpdate().getMessage().getChat().getFirstName());
        }
        return null;
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
