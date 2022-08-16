package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Integer id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private int itemId;
    private int bookerId;
    private BookingStatus status;


    public BookingDto(Integer id, LocalDateTime start, LocalDateTime end, int itemId, int bookerId,
                      BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
        validate();
    }

    private void validate() {
        if (end.isBefore(start)) {
            throw new ValidationException("End can't be before start");
        }
    }
}
