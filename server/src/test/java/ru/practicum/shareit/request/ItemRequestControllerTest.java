package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemRequestDto itemRequestDto;
    private ItemRequestWithResponseDto itemRequestWithResponseDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");

        itemRequestWithResponseDto = new ItemRequestWithResponseDto();
        itemRequestWithResponseDto.setId(1L);
        itemRequestWithResponseDto.setDescription("Need a drill");
        itemRequestWithResponseDto.setCreated(LocalDateTime.now());
        itemRequestWithResponseDto.setItems(new ArrayList<>());
    }

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Need a drill");
        responseDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getAllItemRequests_shouldReturnPaginatedList() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");
        dto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() throws Exception {
        Mockito
                .when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(itemRequestWithResponseDto));

        mockMvc.perform(get("/requests")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getById_shouldReturnRequestWithResponse() throws Exception {
        Mockito
                .when(itemRequestService.getByIdWithResponse(anyLong(), anyLong()))
                .thenReturn(itemRequestWithResponseDto);

        mockMvc.perform(get("/requests/1")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }
}
