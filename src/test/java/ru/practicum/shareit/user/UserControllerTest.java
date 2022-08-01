package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setName("test");
    }

    @Order(1)
    @Test
    public void shouldPostTheUser() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Order(2)
    @Test
    public void shouldPostTheUserTwice() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isConflict());
    }

    @Order(3)
    @Test
    public void shouldPostTheUserWithEmptyEmail() throws Exception {
        userDto.setEmail(null);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Order(4)
    @Test
    public void shouldUpdateUser() throws Exception {
        userDto.setEmail("updated@mail.com");
        userDto.setId(1);
        mockMvc.perform(
                        patch("/users/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Order(5)
    @Test
    public void shouldGetUser() throws Exception {
        mockMvc.perform(
                        get("/users/{id}", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.email", is("updated@mail.com")))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Order(6)
    @Test
    public void shouldGetAllUsers() throws Exception {
        mockMvc.perform(
                        get("/users")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("updated@mail.com")))
                .andExpect(jsonPath("$[0].name", is("test")));
    }
}