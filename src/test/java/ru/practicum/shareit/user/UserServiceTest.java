package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.ModelNotFoundException;

import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private final EntityManager em;
    private final UserService userService;

    @Order(1)
    @Test
    void addUser() {
        User newUser = getUser("login", "test@test.com");
        userService.add(newUser);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));
    }

    @Order(2)
    @Test
    void getUser() {
        User user = getUser("sara", "sara@test.com");
        em.persist(user);
        em.flush();
        User userDb = userService.get(2);
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDb.getName()));
        assertThat(user.getEmail(), equalTo(userDb.getEmail()));
    }

    @Order(3)
    @Test
    void getAllUsers() {
        List<User> users = List.of(
                getUser("new", "new@test.com"),
                getUser("test", "login@test.com"),
                getUser("vasya", "vasya@test.com")
        );
        for (User user : users) {
            em.persist(user);
        }
        em.flush();
        Collection<User> targetUsers = userService.getAll();
        assertThat(targetUsers, hasSize(users.size()));
        for (User user : users) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Order(4)
    @Test
    void deleteUser() {
        User user = getUser("sara", "sara@test.com");
        em.persist(user);
        em.flush();
        userService.delete(6);
        Assertions.assertThrows(ModelNotFoundException.class, () -> userService.get(6));
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
