package ru.practicum.user;

public class UserMapper {

    public static User toUser(UserDto userDto) {

        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;

    }

    public static UserDto toUserDto(UserDto userDto) {

        UserDto user = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;

    }

}
