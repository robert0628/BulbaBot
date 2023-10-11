package com.belka.users.service;

import com.belka.users.dto.UserDto;
import com.belka.users.entities.UserEntity;

import java.util.Collection;

/**
 * service for interaction with {@link UserEntity users}
 */
public interface UserService {
    Collection<UserDto> getAll();

    void save(UserDto userDto);

    boolean existsById(Long userId);

    /**
     * subscribe for users updates;
     *
     * @param userId     {@link UserEntity follower}'s ID
     * @param producerId {@link UserEntity producer}'s ID
     */
    void toSubscribe(Long userId, Long producerId);

    /**
     * get names and usernames of the people user subscribed to
     *
     * @param userId {@link UserEntity user}'s id
     * @return list of the names and usernames
     */
    Collection<String> getProducers(Long userId);

    /**
     * get names and usernames of the people who are subscribed to the user
     *
     * @param userId {@link UserEntity user}'s id
     * @return list of the names and usernames
     */
    Collection<String> getFollowers(Long userId);

    /**
     * get the ID of the user's subscribers
     *
     * @param userId {@link UserEntity user}'s id
     * @return list of the ID
     */
    Collection<Long> getFollowersId(Long userId);

    /**
     * get {@link UserEntity user}'s id
     *
     * @param userId {@link UserEntity user}'s id
     * @return name
     */
    String getName(Long userId);
}
