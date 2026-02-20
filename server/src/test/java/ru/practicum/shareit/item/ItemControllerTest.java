package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;

    @BeforeEach
    public void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Test");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);

        itemUpdateDto = new ItemUpdateDto("Test update", "Test update description", true);
    }

    @Test
    public void shouldCreateNewItem() throws Exception {
        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setOwnerId(1L);
        responseDto.setName("Test");
        responseDto.setDescription("Test description");
        responseDto.setAvailable(true);

        Mockito
                .when(itemService.create(anyLong(), any()))
                .thenReturn(responseDto);


        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.name").value("Test"));
    }
}
