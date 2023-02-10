package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.exceptions.BadRequestException;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getName() == null)
            throw new BadRequestException("No empty fields allowed");
        if (userRepository.getUserByName(userDto.getName()) != null)
            throw new ConflictException("User already exists");

        User user = UserMapper.toUser(userDto);
        userRepository.save(user);

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
        if (getUsers(List.of(userId), 0, 1).size() == 0)
            throw new NotFoundException("User with id=" + userId + " was not found.");
    }

}
