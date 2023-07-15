package com.belka.users.service;

import com.belka.users.entities.UserEntity;
import com.belka.users.model.UserDto;

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
     * get names of the people user subscribed to
     *
     * @param userId {@link UserEntity user}'s id
     * @return list of the names
     */
    Collection<String> showSubscribes(Long userId);

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
