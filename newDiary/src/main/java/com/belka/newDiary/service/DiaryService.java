package com.belka.newDiary.service;

import java.time.LocalDate;

/**
 * service for interaction with {@link com.belka.newDiary.entity.DiaryEntity}
 */
public interface DiaryService {
    /**
     * add a note to the diary
     *
     * @param chatID user's id
     * @param text   the text that the user sent to the diary
     */
    void addNote(Long chatID, String text);

    String getNote(LocalDate date, Long chatID);

    /**
     * checks if a note exists on that day
     *
     * @param userId user's id
     * @param date   which day we check
     * @return true if any note exists or false if not
     */
    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}
