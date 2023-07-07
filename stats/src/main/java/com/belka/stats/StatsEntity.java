package com.belka.stats;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stats")
@Entity
public class StatsEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "handler_code")
    private String handlerCode;
    @Column(name = "request_time")
    private Timestamp requestTime;
}
