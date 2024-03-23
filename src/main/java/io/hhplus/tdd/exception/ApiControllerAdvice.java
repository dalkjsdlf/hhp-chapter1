package io.hhplus.tdd.exception;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.UserPoint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Invalid DTO Parameter errors : {}", errorList);
        return this.makeErrorResponseEntity(errorList.toString());
    }

    private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorDescription));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("error: ", e);
        ErrorResponse errorResponse = new ErrorResponse("500", "에러가 발생했습니다.");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler({UserPointException.class})
    public ResponseEntity<ErrorResponse> handleRestApiException(final UserPointException exception) {
        log.warn("MembershipException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final UserPointErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}
