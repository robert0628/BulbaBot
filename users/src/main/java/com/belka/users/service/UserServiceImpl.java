package com.belka.users.service;

import com.belka.core.converter.ConverterService;
import com.belka.users.dto.UserDto;
import com.belka.users.entities.SubscriptionEntity;
import com.belka.users.entities.UserEntity;
import com.belka.users.repository.SubscriptionsRepository;
import com.belka.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final ConverterService converterService;

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userEntity -> converterService.ConvertTo(UserDto.class, userEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void save(UserDto userDto) {
        userRepository.save(converterService.ConvertTo(UserEntity.class, userDto));
        log.info("user was saved");
    }

    @Override
    public boolean existsById(Long chatId) {
        return userRepository.existsById(chatId);
    }

    @Override
    public void toSubscribe(Long chatId, Long producerId) {
        UserEntity subscriber = userRepository.findById(chatId).orElseThrow(RuntimeException::new);
        UserEntity producer = userRepository.findById(producerId).orElseThrow(RuntimeException::new);
        subscriptionsRepository.save(SubscriptionEntity.builder()
                .producer(producer)
                .subscriber(subscriber)
                .build());
    }

    @Override
    public Collection<String> showSubscribes(Long userId) {
        List<Long> producersId = subscriptionsRepository.findAllProducersID(userId);
        Collection<UserEntity> entities = userRepository.findAllById(producersId);
        return entities.stream().map(this::getNameFromEntity).toList();
    }

    @Override
    public Collection<Long> getFollowersId(Long userId) {
        return subscriptionsRepository.findAllFollowersID(userId);
    }

    @Override
    public String getName(Long userId) {
        UserEntity entity = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return getNameFromEntity(entity);
    }

    private String getNameFromEntity(UserEntity entity) {
        if (entity.getUsername() == null) {
            return entity.getFirstname();
        }
        return "@" + entity.getUsername();
    }
}
