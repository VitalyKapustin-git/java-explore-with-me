package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.exceptions.BadRequestException;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getName() == null)
            throw new BadRequestException("No empty fields allowed");
        if (userRepository.getUserByEmail(userDto.getEmail()) != null)
            throw new ConflictException("User already exists");

        User user = userRepository.save(UserMapper.toUser(userDto));

        userDto.setId(user.getId());

        return userDto;

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        List<User> users = ids == null ? userRepository.findAll(pageable).toList() :
                userRepository.getUsersByIdIn(ids, pageable);

        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void removeUser(long userId) {

        checkUser(userId);
        userRepository.deleteUserById(userId);

    }

    private void checkUser(Long userId) {
        if (userRepository.getUserById(userId) == null)
            throw new NotFoundException("User with id=" + userId + " was not found.");
    }

}
