package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.belka.newDiary.handler.DiaryBaseHandler.GET_DIARY;

@Component
@AllArgsConstructor
public class DiaryCalendarHandler implements BelkaHandler {

    private final static String CODE = "READ_DIARY";
    private final PreviousService previousService;


    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.getUpdate().hasCallbackQuery() && event.getData().equals(GET_DIARY)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            return Flux.just(sendCalendarMessage(chatId));
        }
        return null;
    }

    public SendMessage sendCalendarMessage(long chatId) {
        Calendar calendar = Calendar.getInstance();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("<<")
                .callbackData("PREV-MONTH")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + calendar.get(Calendar.YEAR))
                .callbackData("IGNORE")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(">>")
                .callbackData("NEXT-MONTH")
                .build());
        rows.add(row);
        row = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
            inlineKeyboardButton.setCallbackData("DAY-" + calendar.get(Calendar.DAY_OF_MONTH));
            row.add(inlineKeyboardButton);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        rows.add(row);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        for (int i = 1; i <= 5; i++) {
            row = new ArrayList<>();
            for (int j = 1; j <= 7; j++) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                inlineKeyboardButton.setCallbackData("DAY-" + calendar.get(Calendar.YEAR)
                        + "." + calendar.get(Calendar.MONTH)
                        + "." + calendar.get(Calendar.DAY_OF_MONTH));
                row.add(inlineKeyboardButton);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            rows.add(row);
        }
        markup.setKeyboard(rows);
        SendMessage message = new SendMessage(String.valueOf(chatId), "Календарь");
        message.setReplyMarkup(markup);
        return message;
    }
}
