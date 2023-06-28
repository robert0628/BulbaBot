package com.belka.BulbaBot.service;

import com.belka.BulbaBot.config.BotConfig;
import com.belka.BulbaBot.model.User;
import com.belka.BulbaBot.repository.UserRepository;
import com.belka.QR.Services.QRService;
import com.belka.newDiary.service.DiaryService;
import com.belka.weather.service.weather.WeatherService;
import com.vdurmont.emoji.EmojiParser;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
import java.util.Objects;

/**
 * building and sending messages, receiving and processing {@link Update updates}
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final WeatherService weatherService;
    private final QRService qrService;
    private final DiaryService diaryService;
    private HandlerService handlerService;
    private static final String TEXT_HELP = "This bot was created like demo";
    private static final String YES_BUTTON = "YES_BUTTON";
    private static final String NO_BUTTON = "NO_BUTTON";
    private static final String ERROR_TEXT = "Error occurred: ";

    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepository userRepository, WeatherService weatherService,
                       QRService qrService, DiaryService diaryService, HandlerService handlerService) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.weatherService = weatherService;
        this.qrService = qrService;
        this.diaryService = diaryService;
        this.handlerService = handlerService;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        listOfCommands.add(new BotCommand("/weather", "get weather"));

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
    //todo refactor this
    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
       /* if (update.hasMessage() || update.hasCallbackQuery()) {
            CompletableFuture.runAsync(
                    () -> someService.tryNext(update)
            );
        }*/

        if (update.hasMessage() || update.hasCallbackQuery()) {
          /*  String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();*/

            handlerService.handle(update).stream().filter(Objects::nonNull).forEach(msg -> executeMessage(msg));


/*
            if (messageText.startsWith("/send") && botConfig.getBotOwner().equals(chatId)) {
                String textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                Iterable<User> users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getId(), textToSend);
                }
            } else {
                if (messageText.startsWith("/QR - ")) {
                    sendImageFromUrl(qrService.getQRLink(messageText), chatId);
                    return;
                } else if (messageText.startsWith("/diary - ")) {
                    diaryService.addNote(chatId, messageText);
                    sendMessage(chatId, "got it");
                    return;
                }
                switch (messageText) {
                    case "/start" -> {
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    }
                    case "/help" -> prepareAndSendMessage(chatId, TEXT_HELP);
                    case "/register" -> register(chatId);
                    case "/weather" ->
                            sendMessage(chatId, weatherService.getWeatherResponse(weatherService.findCity()));
                    default -> prepareAndSendMessage(chatId, "sorry, but command was not recognized");
                }
            }
        } else if (update.hasCallbackQuery()) {

            someService.tryNext(update).stream().filter(Objects::nonNull).forEach(msg -> executeMessage((SendMessage) msg));

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
            }*/
        }
    }

    /**
     * get greeting receive
     *
     * @param chatId user's telegram id
     * @param name   user's name
     */
    private void startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + " nice to meet you" + " :blush:");
        sendMessage(chatId, answer);
    }

    /**
     * send message
     *
     * @param chatId     user's telegram id
     * @param textToSend message text
     */
    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(makeReplyKeyboardMarkup());
        executeMessage(message);
    }

    /**
     * make buttons
     *
     * @return buttons
     */
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

    /**
     * register user
     *
     * @param message {@link Message user's message}
     */
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

    /**
     * make buttons for register
     *
     * @param chatId user's telegram id
     */
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

    /**
     * @param text      message text
     * @param chatId    user's telegram id
     * @param messageId message id
     */
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


    /**
     * prepare and send message to user
     *
     * @param chatId     user's telegram id
     * @param textToSend text to send to the user
     */
    private void prepareAndSendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }
}
