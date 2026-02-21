package ru.practicum.shareit.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exceptions.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    @DisplayName("NotFoundException → 404 + правильное сообщение")
    void shouldReturn404WhenNotFoundException() {
        // given
        String message = "Пользователь с id=999 не найден";
        NotFoundException ex = new NotFoundException(message);

        // when
        ResponseEntity<ErrorInfo> response = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorHandler.handleNotFound(ex));

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(message);
    }

    @Test
    @DisplayName("ConditionsNotMetException → 422 Unprocessable Entity")
    void shouldReturn422WhenConditionsNotMet() {
        String message = "Дата начала бронирования не может быть в прошлом";
        ConditionsNotMetException ex = new ConditionsNotMetException(message);

        ResponseEntity<ErrorInfo> response = ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorHandler.handleBadConditions(ex));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().error()).isEqualTo(message);
    }

    @Test
    @DisplayName("ConflictDataException → 409 Conflict")
    void shouldReturn409WhenConflictData() {
        String message = "Пользователь с email user@mail.ru уже существует";
        ConflictDataException ex = new ConflictDataException(message);

        ResponseEntity<ErrorInfo> response = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorHandler.handleConflictData(ex));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().error()).isEqualTo(message);
    }

    @Test
    @DisplayName("UserNotOwnerException → 403 Forbidden")
    void shouldReturn403WhenUserNotOwner() {
        String message = "Только владелец вещи может одобрять/отклонять бронирования";
        UserNotOwnerException ex = new UserNotOwnerException(message);

        ResponseEntity<ErrorInfo> response = ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorHandler.handleUserNotOwner(ex));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().error()).isEqualTo(message);
    }

    @Test
    @DisplayName("BookingConflictException → 400 Bad Request")
    void shouldReturn400WhenBookingConflict() {
        String message = "Вещь уже забронирована на указанные даты";
        BookingConflictException ex = new BookingConflictException(message);

        ResponseEntity<ErrorInfo> response = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorHandler.handleBookingDates(ex));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error()).isEqualTo(message);
    }
}