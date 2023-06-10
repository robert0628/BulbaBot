package com.belka.newDiary;

import lombok.*;
import javax.persistence.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Diary")
@Entity
public class DiaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String note;
    private Date date;
}
