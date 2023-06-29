package com.belka.BulbaBot.service;

import com.belka.BulbaBot.config.BotConfig;
import com.belka.BulbaBot.handler.HandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * building and sending messages, receiving and processing {@link Update updates}
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final HandlerService handlerService;

    @Autowired
    public TelegramBot(BotConfig botConfig, HandlerService handlerService) {
        this.botConfig = botConfig;
        this.handlerService = handlerService;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        listOfCommands.add(new BotCommand("/weather", "get weather"));
        listOfCommands.add(new BotCommand("/QR", "get QR code for your text"));
        listOfCommands.add(new BotCommand("/diary", "make your own diary"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    /**
     * process user {@link Update telegram update} and send receive
     *
     * @param update {@link Update telegram update}
     */
    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            CompletableFuture.runAsync(
                    () -> handlerService.handle(update)
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(this::executeMessage)
            );
        }
    }

    /**
     * send message
     *
     * @param message {@link SendMessage message for user}
     */
    private void executeMessage(PartialBotApiMethod<?> message) {
        try {
            if (message instanceof BotApiMethod) {
                execute((BotApiMethod<?>) message);
            } else if (message instanceof SendPhoto sendPhoto) {
                execute(sendPhoto);
            } else if (message instanceof SendDocument sendDocument) {
                execute(sendDocument);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
