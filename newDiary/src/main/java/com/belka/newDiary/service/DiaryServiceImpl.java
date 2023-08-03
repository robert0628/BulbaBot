package com.belka.newDiary.service;


import com.belka.newDiary.entity.DiaryEntity;
import com.belka.newDiary.repository.DiaryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    private final DiaryRepository repository;

    @Override
    public void addNote(Long chatID, String text) {
        LocalDate today = LocalDate.now();
        Optional<DiaryEntity> entity = repository.getByDateAndUserId(today, chatID);
        if (entity.isPresent()) {
            DiaryEntity entityForUpdate = entity.get();
            String newNote = entityForUpdate.getNote() + "\n" + text;
            entityForUpdate.setNote(newNote);
            repository.save(entityForUpdate);
            log.info("added a note to the diary");
        } else {
            DiaryEntity entityForSave = DiaryEntity.builder()
                    .date(today)
                    .userId(chatID)
                    .note(text)
                    .build();
            repository.save(entityForSave);
            log.info("added a note to the diary");
        }
    }

    @Override
    public String getNote(LocalDate date, Long chatID) {
        Optional<String> entity = repository.findNoteByUserIdAndDate(chatID, date);
        if(entity.isEmpty()){
            throw new RuntimeException("sorry but there are no notes for this date");
        }
        return entity.get();
    }
}

