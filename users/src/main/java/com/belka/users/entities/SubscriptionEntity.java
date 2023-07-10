package com.belka.users.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscriptions")
@IdClass(SubscriptionKey.class)
public class SubscriptionEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "producer")
    private UserEntity producer;

    @Id
    @ManyToOne
    @JoinColumn(name = "subscriber")
    private UserEntity subscriber;

}
