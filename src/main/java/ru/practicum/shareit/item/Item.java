package ru.practicum.shareit.item;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private boolean available;

    @Column(name = "owner_id")
    private int ownerId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private List<Booking> bookings;

    @Column(name = "request_id")
    private Integer requestId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private List<Comment> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return id != null && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
