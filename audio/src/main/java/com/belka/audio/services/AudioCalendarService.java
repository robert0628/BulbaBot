package com.belka.audio.services;

import com.belka.core.services.AbstractCalendarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class AudioCalendarService extends AbstractCalendarService {
    public final static String DATA_AUDIO_CODE = "AUDIO_CALENDAR";
    public final static String AUDIO_CALENDAR_PREV_MONTH = "AUDIO_PREV_MONTH";
    public final static String AUDIO_CALENDAR_NEXT_MONTH = "AUDIO_NEXT_MONTH";
    private final AudioService audioService;

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
                .callbackData(AUDIO_CALENDAR_PREV_MONTH + "-" + previousYear + "-" + previousMonth)
                .build());
        rowHeaders.add(InlineKeyboardButton.builder()
                .text(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + calendar.get(Calendar.YEAR))
                .callbackData("IGNORE")
                .build());
        rowHeaders.add(InlineKeyboardButton.builder()
                .text(">>")
                .callbackData(AUDIO_CALENDAR_NEXT_MONTH + "-" + year + "-" + (month + 1))
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
            if (audioService.existsByUserIdAndDate(userId, date)) {
                dayButton.setText("-" + calendar.get(Calendar.DAY_OF_MONTH) + "-");
            } else {
                dayButton.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            }
            dayButton.setCallbackData(DATA_AUDIO_CODE
                    + "." + calendar.get(Calendar.YEAR)
                    + "." + calendar.get(Calendar.MONTH)
                    + "." + calendar.get(Calendar.DAY_OF_MONTH));
            row.add(dayButton);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return row;
    }
}