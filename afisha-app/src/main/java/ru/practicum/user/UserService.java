package ru.practicum.user;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void removeUser(long userId);

}
