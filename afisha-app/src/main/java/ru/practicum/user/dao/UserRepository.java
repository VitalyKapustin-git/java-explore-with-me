package ru.practicum.user.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> getUsersByIdIn(List<Long> ids, Pageable pageable);

    User getUserById(Long userId);

    void deleteUserById(long userId);

    @Query("select u.name from User u where u.email = ?1")
    String getUserByEmail(String email);

}
