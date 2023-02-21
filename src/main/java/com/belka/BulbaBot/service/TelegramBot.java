package com.belka.BulbaBot.service;

import com.belka.BulbaBot.config.BotConfig;
import com.belka.BulbaBot.model.Ads;
import com.belka.BulbaBot.model.User;
import com.belka.BulbaBot.repository.AdsRepository;
import com.belka.BulbaBot.repository.UserRepository;
import com.belka.ServiceStackOverFlow;
import com.belka.StackOverFlow;
import com.vdurmont.emoji.EmojiParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final AdsRepository adsRepository;
    @Setter
    private StackOverFlow serviceStackOverFlow;
    private static final String TEXT_HELP = "This bot was created like demo";
    private static final String YES_BUTTON = "YES_BUTTON";
    private static final String NO_BUTTON = "NO_BUTTON";
    private static final String ERROR_TEXT = "Error occurred: ";


    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepository userRepository, AdsRepository adsRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.adsRepository = adsRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
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

    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && botConfig.getBotOwner().equals(chatId)) {
                String textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                Iterable<User> users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getId(), textToSend);
                }
            } else {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, TEXT_HELP);
                        break;
                    case "/register":
                        register(chatId);
                        break;
                    case "/stack":
                        serviceStackOverFlow.fuu();
                    default:
                        prepareAndSendMessage(chatId, "sorry, but command was not recognized");
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            String callbackData = query.getData();
            String answer;
            long messageId = query.getMessage().getMessageId();
            long chatId = query.getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                answer = "You pressed YES button";
                executeEditMessageText(answer, chatId, messageId);
            } else if (callbackData.equals(NO_BUTTON)) {
                answer = "You pressed NO button";
                executeEditMessageText(answer, chatId, messageId);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + " nice to meet you" + " :blush:");
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(makeReplyKeyboardMarkup());
        executeMessage(message);
    }

    private ReplyKeyboardMarkup makeReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("first button");
        row1.add("next one button");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("register");
        row2.add("check my data");
        row2.add("delete my data");

        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    private void registerUser(Message message) {
        if (!userRepository.existsById(message.getChatId())) {
            User user = new User(message.getChatId(),
                    message.getChat().getFirstName(),
                    message.getChat().getLastName(),
                    message.getChat().getUserName(),
                    new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
        }
    }

    private void register(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("do you want to register?")
                .build();
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, Long chatId, long messageId) {
        EditMessageText message = EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "0 * * * * *")
    protected void send () {
        Iterable<Ads> ads = adsRepository.findAll();
        Iterable<User> users = userRepository.findAll();

        for (Ads ad: ads){
            for(User user: users){
                prepareAndSendMessage(user.getId(), ad.getAd());
            }
        }
    }
}
