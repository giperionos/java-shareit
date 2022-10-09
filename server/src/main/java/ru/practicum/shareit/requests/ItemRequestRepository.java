package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> getAllByRequester_IdOrderByCreatedDesc(Long ownerId);
}
