package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.item.exceptions.CommentForNotExistBookingException;
import ru.practicum.shareit.item.exceptions.ItemSecurityException;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistEmailException;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(MethodArgumentNotValidException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), getPrettyMessageForMethodArgumentNotValidException(exception.getMessage()));
    }

    @ExceptionHandler({BookingUnknownStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingUnknownStateException(BookingUnknownStateException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }

    @ExceptionHandler({ItemSecurityException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(RuntimeException exception) {
        log.info("403: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.FORBIDDEN.toString(), exception.getMessage());
    }

    @ExceptionHandler({UserUnknownException.class, ItemUnknownException.class, BookingUnknownException.class, BookingSecurityException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUnknownEntityException(RuntimeException exception) {
        log.info("404: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(), exception.getMessage());
    }

    @ExceptionHandler({ItemUnavailableException.class, EndDateBeforeStartDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemUnavailableException(RuntimeException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistEmailException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(RuntimeException exception) {
        log.info("409: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.CONFLICT.toString(), exception.getMessage());
    }

    @ExceptionHandler({BookingTryToUpdateSameStatusException.class, CommentForNotExistBookingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUpdateException(RuntimeException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }

    @ExceptionHandler({BookingHimSelfException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingHimSelfException(RuntimeException exception) {
        log.info("404: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(), exception.getMessage());
    }

    private String getPrettyMessageForMethodArgumentNotValidException(String message) {
        String prettyMessage = message;

        try {
            String defaultMessage = message.substring(message.indexOf("default message") + 15);
            prettyMessage = defaultMessage.substring(defaultMessage.indexOf("default message") + 15)
                    .replace("[", "")
                    .replace("]","")
                    .trim();
        } catch (Exception e) {
            log.info("getPrettyMessageForMethodArgumentNotValidException: {}", e.getMessage(), e);
        }

        return prettyMessage;
    }
}
