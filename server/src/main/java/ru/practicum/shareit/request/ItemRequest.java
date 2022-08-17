package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "item_requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @Column(name = "requester_id")
    private int requesterId;

    @OneToMany
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private List<Item> items;

    private LocalDateTime created;
}
