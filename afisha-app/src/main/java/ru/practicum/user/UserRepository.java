package ru.practicum.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> getUsersByIdIn(List<Long> ids, Pageable pageable);

    void deleteUserById(long userId);

    @Query("select u.name from User u where u.name = ?1")
    String getUserByName(String name);

}
