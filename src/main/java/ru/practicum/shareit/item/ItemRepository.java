package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Modifying
    @Query("update Item i set i.name = ?1, i.description = ?2, i.available = ?3 where i.id = ?4")
    void setItemInfoById(String name, String description, Boolean available, Integer itemId);

    List<Item> getItemsByOwnerId(int userId);

    @Modifying
    @Query("select i from Item i where lower(i.name) like lower(?1) OR lower(i.description) like lower(?1) " +
            "and i.available = true")
    List<Item> findItemByText(String text);
}
