package com.belka.newDiary.service;

import com.belka.core.services.AbstractCalendarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Component
@AllArgsConstructor
public class DiaryCalendarService extends AbstractCalendarService {
    public final static String DAY_DIARY_CALENDAR_CODE = "DIARY_CALENDAR";
    public final static String DIARY_CALENDAR_PREV_MONTH = "DIARY_PREV_MONTH";
    public final static String DIARY_CALENDAR_NEXT_MONTH = "DIARY_NEXT_MONTH";
    private final DiaryService diaryService;

    @Override
    protected List<InlineKeyboardButton> addHeadersToCalendar(Calendar calendar, Integer year, Integer month) {
        List<InlineKeyboardButton> rowHeaders = new ArrayList<>();
        Integer previousYear = year;
        Integer previousMonth = month;
        if (month.equals(0)) {
            previousYear--;
            previousMonth = 11;
        } else {
            previousMonth--;
        }

        rowHeaders.add(InlineKeyboardButton.builder()
                .text("<<")
                .callbackData(DIARY_CALENDAR_PREV_MONTH + "-" + previousYear + "-" + previousMonth)
                .build());
        rowHeaders.add(InlineKeyboardButton.builder()
                .text(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + calendar.get(Calendar.YEAR))
                .callbackData("IGNORE")
                .build());
        rowHeaders.add(InlineKeyboardButton.builder()
                .text(">>")
                .callbackData(DIARY_CALENDAR_NEXT_MONTH + "-" + year + "-" + (month + 1))
                .build());
        return rowHeaders;
    }

    @Override
    protected List<InlineKeyboardButton> addDaysToCalendar(List<InlineKeyboardButton> row, Calendar calendar, Long userId) {
        for (int j = 1; j <= 7; j++) {
            InlineKeyboardButton dayButton = new InlineKeyboardButton();
            LocalDate date = calendar.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (diaryService.existsByUserIdAndDate(userId, date)) {
                dayButton.setText("-" + calendar.get(Calendar.DAY_OF_MONTH) + "-");
            } else {
                dayButton.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            }
            dayButton.setCallbackData(DAY_DIARY_CALENDAR_CODE
                    + "." + calendar.get(Calendar.YEAR)
                    + "." + calendar.get(Calendar.MONTH)
                    + "." + calendar.get(Calendar.DAY_OF_MONTH));
            row.add(dayButton);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return row;
    }
}
