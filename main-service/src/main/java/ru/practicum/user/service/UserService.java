package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserNewRequestDto;

import java.util.List;


public interface UserService {

    UserDto add(UserNewRequestDto newUserRequest);

    List<UserDto> getList(List<Long> ids, Long from, Long size);

    void delete(Long userId);

}