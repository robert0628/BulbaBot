package com.belka.newDiary.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Component
public class CalendarService {
    private final static String CALENDAR = "Calendar";

    public SendMessage sendCalendarMessage(Long chatId, Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(addHeadersToCalendar(calendar, year, month));
        rows.add(addDaysOfTheWeekToCalendar(calendar));
        calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.add(Calendar.MONTH, 1);
        for (int i = 1; i <= 5; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(addDaysToCalendar(row, calendar));
        }
        markup.setKeyboard(rows);
       SendMessage message = new SendMessage(String.valueOf(chatId), CALENDAR);
        message.setReplyMarkup(markup);
        return message;
    }

    private List<InlineKeyboardButton> addHeadersToCalendar(Calendar calendar, Integer year, Integer month) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        Integer previousYear = year;
        Integer previousMonth = month;
        if(month.equals(0)){
            previousYear--;
            previousMonth = 11;
        } else {
            previousMonth--;
        }

        row.add(InlineKeyboardButton.builder()
                .text("<<")
                .callbackData("PREV-MONTH-" + previousYear + "-" + previousMonth)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + calendar.get(Calendar.YEAR))
                .callbackData("IGNORE")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(">>")
                .callbackData("NEXT-MONTH-" + year + "-" + (month + 1))
                .build());
        return row;
    }

    private List<InlineKeyboardButton> addDaysOfTheWeekToCalendar(Calendar calendar) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
            inlineKeyboardButton.setCallbackData("DAY-" + calendar.get(Calendar.DAY_OF_MONTH));
            row.add(inlineKeyboardButton);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return row;
    }

    private List<InlineKeyboardButton> addDaysToCalendar(List<InlineKeyboardButton> row, Calendar calendar) {
        for (int j = 1; j <= 7; j++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            inlineKeyboardButton.setCallbackData("DAY-" + calendar.get(Calendar.YEAR)
                    + "." + calendar.get(Calendar.MONTH)
                    + "." + calendar.get(Calendar.DAY_OF_MONTH));
            row.add(inlineKeyboardButton);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return row;
    }
}
