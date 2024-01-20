package com.laan.wordfinder.controller.exception;

import com.laan.wordfinder.exception.WordFrequencyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(WordFrequencyException.class)
    public ProblemDetail onWordFinderException(WordFrequencyException exception) {
        log.error("WordFinderException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail onHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("HttpRequestMethodNotSupportedException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("MethodArgumentTypeMismatchException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onException(Exception exception) {
        log.error("Exception occurred.", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}
