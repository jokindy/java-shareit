package ru.practicum.shareit.booking;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking implements Comparable<Booking> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "start_date_time")
    private LocalDateTime start;

    @Column(name = "end_date_time")
    private LocalDateTime end;

    @Column(name = "item_id")
    private int itemId;

    @Column(name = "booker_id")
    private int bookerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Override
    public int compareTo(Booking o) {
        return getStart().compareTo(o.getStart());
    }

}