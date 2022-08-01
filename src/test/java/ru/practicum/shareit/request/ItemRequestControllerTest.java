package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ItemRequestDto requestDto;

    @MockBean
    private ItemRequestService requestService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        requestDto = new ItemRequestDto();
        requestDto.setRequesterId(1);
        requestDto.setDescription("test");
    }

    @Order(1)
    @Test
    public void shouldPostRequest() throws Exception {
        doNothing()
                .when(requestService)
                .add(any());
        mockMvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.requesterId", is(requestDto.getRequesterId())))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Order(2)
    @Test
    public void shouldGetAllRequests() throws Exception {
        when(requestService.getRequestsByUser(anyInt()))
                .thenReturn(List.of(
                        getItemRequest(1, "I need boat"),
                        getItemRequest(2, "I need guitar")
                ));
        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].requesterId", is(1)))
                .andExpect(jsonPath("$[1].requesterId", is(2)))
                .andExpect(jsonPath("$[0].description", is("I need boat")))
                .andExpect(jsonPath("$[1].description", is("I need guitar")));
    }


    @Order(3)
    @Test
    public void shouldGetRequests() throws Exception {
        when(requestService.getRequestById(1, 1))
                .thenReturn(getItemRequest(1, "I need boat")
                );
        mockMvc.perform(
                        get("/requests/{id}", 1)
                                .header("X-Sharer-User-Id", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.requesterId", is(1)))
                .andExpect(jsonPath("$.description", is("I need boat")));
    }

    private ItemRequest getItemRequest(int userId, String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequesterId(userId);
        itemRequest.setDescription(description);
        return itemRequest;
    }
}
