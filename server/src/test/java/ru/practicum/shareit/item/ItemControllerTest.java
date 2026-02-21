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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void shouldReturnUserItems() throws Exception {
        ItemWithBookingDatesAndCommentsDto response = ItemWithBookingDatesAndCommentsDto.builder()
                .id(1L)
                .name("Test")
                .description("Test")
                .ownerId(1L)
                .available(true)
                .lastBookingDate(LocalDateTime.now().minusDays(1).minusHours(2))
                .nextBookingDate(LocalDateTime.now().plusDays(1))
                .comments(new ArrayList<>())
                .build();

        Mockito
                .when(itemService.getUserItems(anyLong()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/items")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        ItemDto response = ItemDto.builder()
                .id(1L)
                .name("Test update")
                .description("Test update description")
                .available(true)
                .build();

        Mockito
                .when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1)
                        .content(mapper.writeValueAsString(itemUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test update"));
    }

    @Test
    public void shouldReturnItemById() throws Exception {
        ItemWithBookingDatesAndCommentsDto response = ItemWithBookingDatesAndCommentsDto.builder()
                .id(1L)
                .name("Test")
                .description("Test")
                .ownerId(1L)
                .available(true)
                .lastBookingDate(LocalDateTime.now().minusDays(1).minusHours(2))
                .nextBookingDate(LocalDateTime.now().plusDays(1))
                .comments(new ArrayList<>())
                .build();

        Mockito
                .when(itemService.getItemById(anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/items/1")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    public void shouldReturnSearchResults() throws Exception {
        itemDto.setId(1L);
        Mockito
                .when(itemService.getItemsBySearchText(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=est")
                .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    public void shouldAddNewComment() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("comment").build();

        CommentDto commentResponse = CommentDto.builder()
                .itemId(1L)
                .text("comment")
                .authorName("Test")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito
                .when(itemService.addNewComment(anyLong(), anyLong(), any()))
                .thenReturn(commentResponse);

        mockMvc.perform(post("/items/1/comment")
                .header(HttpHeadersConstants.X_SHARER_USER_ID, 1)
                .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
