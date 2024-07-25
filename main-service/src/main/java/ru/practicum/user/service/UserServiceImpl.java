package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.DataConflictException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserNewRequestDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Transactional
    @Override
    public UserDto add(UserNewRequestDto userNewRequestDto) {
        if (userRepository.existsByEmail(userNewRequestDto.getEmail())) {
            throw new DataConflictException(UserNewRequestDto.class, String.valueOf(userNewRequestDto),
                    "Пользователь с email " + userNewRequestDto.getEmail() + " уже существует.");
        }

        User user = userRepository.save(UserMapper.toUser(userNewRequestDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getList(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        Stream<User> usersStream =
                (ids != null) ? userRepository.findByIdIn(ids, page).stream() : userRepository.findAll(page).stream();

        return usersStream.map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, String.valueOf(userId),
                    "Пользователь с id " + userId + " не найден.");
        }
        userRepository.deleteById(userId);
    }

}