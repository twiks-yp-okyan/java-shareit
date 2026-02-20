package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User basicUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        basicUser = new User();
        basicUser.setName("Test");
        basicUser.setEmail("test@gmail.com");
        basicUser = userRepository.save(basicUser);
    }

    @Test
    public void shouldFindById() {
        Optional<User> foundUser = userRepository.findById(basicUser.getId());
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals(basicUser.getName(), foundUser.get().getName());
        Assertions.assertEquals(basicUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    public void shouldNotFindById() {
        Double random = Math.random() * 100 * 1337;
        Long wrongUserId = random.longValue();
        Optional<User> foundUser = userRepository.findById(wrongUserId);
        Assertions.assertEquals(Optional.empty(), foundUser);
    }

    @Test
    public void shouldReturn4Users() {
        for (int i = 0; i < 3; i++) {
            String name = "Test" + i;
            String email = String.format("test%d@gmail.com", i);
            User currentUser = createUserWithSpecificData(name, email);
            userRepository.save(currentUser);
        }
        List<User> foundUsers = userRepository.findAll();
        Assertions.assertEquals(4, foundUsers.size()); // 4 из-за beforeAll
    }

    @Test
    public void shouldUpdateUser() {
        User userForUpdate = userRepository.findById(basicUser.getId()).orElse(new User());
        userForUpdate.setEmail("update-test@gmail.com");
        userForUpdate.setName("update-Test");
        User updatedUser = userRepository.save(userForUpdate);

        Assertions.assertEquals(basicUser.getId(), updatedUser.getId());
        Assertions.assertEquals("update-test@gmail.com", updatedUser.getEmail());
        Assertions.assertEquals("update-Test", updatedUser.getName());
    }

    @Test
    public void shouldDeleteUser() {
        Long deletedUserId = basicUser.getId();
        userRepository.delete(basicUser);
        Optional<User> deletedUser = userRepository.findById(deletedUserId);

        Assertions.assertEquals(Optional.empty(), deletedUser);
    }

    private User createUserWithSpecificData(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
