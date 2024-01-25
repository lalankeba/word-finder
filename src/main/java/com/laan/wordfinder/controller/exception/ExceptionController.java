package com.laan.wordfinder.controller.exception;

import com.laan.wordfinder.exception.WordFinderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(WordFinderException.class)
    public ProblemDetail onWordFinderException(WordFinderException exception) {
        log.error("WordFinderException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail onHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("HttpRequestMethodNotSupportedException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("MethodArgumentTypeMismatchException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail onMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.error("MissingServletRequestParameterException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail onMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.error("MaxUploadSizeExceededException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE, exception.getMessage() + ". Max file size: " + maxFileSize);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail onAccessDeniedException(AccessDeniedException exception) {
        log.error("AccessDeniedException occurred. {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onException(Exception exception) {
        log.error("Exception occurred.", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}
