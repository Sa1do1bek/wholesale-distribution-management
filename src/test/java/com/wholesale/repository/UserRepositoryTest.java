package com.wholesale.repository;

import com.wholesale.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByEmail() {
        User user = User.builder()
                .username("testuser2")
                .email("test2@example.com")
                .password("password")
                .role(User.Role.MANAGER)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test2@example.com");
        assertTrue(found.isPresent());
        assertEquals("test2@example.com", found.get().getEmail());
    }

    @Test
    void testExistsByUsername() {
        User user = User.builder()
                .username("testuser3")
                .email("test3@example.com")
                .password("password")
                .role(User.Role.EMPLOYEE)
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("testuser3"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        User user = User.builder()
                .username("testuser4")
                .email("test4@example.com")
                .password("password")
                .role(User.Role.EMPLOYEE)
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("test4@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
