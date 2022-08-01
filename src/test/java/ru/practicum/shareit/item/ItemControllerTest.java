package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.dto.ItemInputDto;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ItemInputDto itemDto;
    private Item item;

    @MockBean
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        itemDto = new ItemInputDto();
        itemDto.setName("test item");
        itemDto.setDescription("Mock test");
        itemDto.setAvailable(true);
        item = new Item();
        item.setName("test item");
        item.setDescription("Mock test");
        item.setAvailable(true);
    }

    @Order(1)
    @Test
    public void shouldPostItem() throws Exception {
        doNothing()
                .when(itemService)
                .add(any());
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Order(2)
    @Test
    public void shouldGetItem() throws Exception {
        when(itemService.getByItemId(anyInt()))
                .thenReturn(item);
        mockMvc.perform(
                        get("/items/{id}", 1)
                                .header("X-Sharer-User-Id", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Order(3)
    @Test
    public void shouldGetItems() throws Exception {
        when(itemService.getItemsByOwnerIdInPage(1, 0, 10))
                .thenReturn(List.of(
                        getItem("item 1", "test item 1"),
                        getItem("item 2", "test item 2"),
                        getItem("item 3", "test item 3")
                ));
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("item 1")))
                .andExpect(jsonPath("$[1].name", is("item 2")))
                .andExpect(jsonPath("$[2].name", is("item 3")))
                .andExpect(jsonPath("$[0].description", is("test item 1")))
                .andExpect(jsonPath("$[1].description", is("test item 2")))
                .andExpect(jsonPath("$[2].description", is("test item 3")));
    }

    @Order(4)
    @Test
    public void shouldGetSearchItems() throws Exception {
        when(itemService.getItemsBySearch("test", 0, 10))
                .thenReturn(List.of(
                        getItem("item 1", "test item 1"),
                        getItem("item 2", "test item 2")
                ));

        mockMvc.perform(
                        get("/items/search")
                                .param("text", "test")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("item 1")))
                .andExpect(jsonPath("$[1].name", is("item 2")))
                .andExpect(jsonPath("$[0].description", is("test item 1")))
                .andExpect(jsonPath("$[1].description", is("test item 2")));
    }

    private Item getItem(String name, String description) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        return item;
    }
}
