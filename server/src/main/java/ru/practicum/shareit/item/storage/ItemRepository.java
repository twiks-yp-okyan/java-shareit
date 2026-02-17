package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    @Query("select i " +
            "from Item i " +
            "where i.available = true and (lower(i.name) like %?1% or i.description like %?1%)")
    List<Item> findByNameOrDescription(String searchText);

    @Query("select i " +
            "from Item i " +
            "where i.requestId in :requestIds")
    List<Item> findByRequestIds(@Param("requestIds") List<Long> requestIds);
}
