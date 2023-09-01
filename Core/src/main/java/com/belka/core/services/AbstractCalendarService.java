package com.belka.core.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class AbstractCalendarService {
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
        for (int i = 1; i <= 5; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(addDaysToCalendar(row, calendar, chatId));
        }
        markup.setKeyboard(rows);
        SendMessage message = new SendMessage(String.valueOf(chatId), CALENDAR);
        message.setReplyMarkup(markup);
        return message;
    }

    protected abstract List<InlineKeyboardButton> addHeadersToCalendar(Calendar calendar, Integer year, Integer month);

    private List<InlineKeyboardButton> addDaysOfTheWeekToCalendar(Calendar calendar) {
        List<InlineKeyboardButton> rowDaysOfTheWeek = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
            inlineKeyboardButton.setCallbackData("DAY-" + calendar.get(Calendar.DAY_OF_MONTH));
            rowDaysOfTheWeek.add(inlineKeyboardButton);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return rowDaysOfTheWeek;
    }

    protected abstract List<InlineKeyboardButton> addDaysToCalendar(List<InlineKeyboardButton> row, Calendar calendar, Long userId);
}
