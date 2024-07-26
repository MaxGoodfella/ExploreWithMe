package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserNewRequestDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;


@UtilityClass
public class UserMapper {

    public User toUser(UserNewRequestDto userNewRequestDto) {
        return User.builder()
                .name(userNewRequestDto.getName())
                .email(userNewRequestDto.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}