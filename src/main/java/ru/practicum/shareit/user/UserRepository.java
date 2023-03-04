package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository  extends JpaRepository<User, Long> {
    List<User> findByEmailEqualsIgnoreCaseAndIdNot(String email, Long userId);
}