package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingCreateDto bookingCreateDto, Long userId) {
        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> resolveBooking(Long bookingId, Long userId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingDetailInfoById(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByUserIdAndState(Long userId, String stateStr, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "stateStr", stateStr,
                "from", from,
                "size", size
        );
        return get("?state={stateStr}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByOwnerIdAndState(Long ownerId, String stateStr, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "stateStr", stateStr,
                "from", from,
                "size", size
        );
        return get("/owner?state={stateStr}&from={from}&size={size}", ownerId, parameters);
    }
}
