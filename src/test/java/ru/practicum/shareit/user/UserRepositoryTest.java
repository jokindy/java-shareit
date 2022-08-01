package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("demo-user@email.com");
        user.setName("demo");
        em.persist(user);
        em.flush();
        repository.setUserInfoById("name", "test@test.com", 1);
        User user2 = repository.findById(1).get();
        em.refresh(user2);
        Assertions.assertEquals("name", user2.getName());
        Assertions.assertEquals("test@test.com", user2.getEmail());
    }
}
