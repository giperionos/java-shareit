package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(MethodArgumentNotValidException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), getPrettyMessageForMethodArgumentNotValidException(exception.getMessage()));
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }

    @ExceptionHandler({EndDateBeforeStartDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemUnavailableException(RuntimeException exception) {
        log.info("400: {}", exception.getMessage(), exception);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
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
