package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.owner.id = ?1")
    List<Item> findItemsByOwnerId(Long ownerId);

    @Query("select i from Item i where lower(concat(i.name, ' ', i.description) ) like %?1% ")
    List<Item> findItemsByKeyWord(String keyWord);
}
