package io.hhplus.tdd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserPointErrorResult {
    DUPLICATED_USER_POINT_REGISTER(HttpStatus.BAD_REQUEST,"Duplicated User Point register"),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception"),
    USER_POINT_NOT_FOUND(HttpStatus.NOT_FOUND,"User Point not found"),
    WRONG_USER_ID(HttpStatus.BAD_REQUEST,"Wrong User Id"),
    WRONG_POINT_AMOUNT(HttpStatus.BAD_REQUEST,"Wrong Point Amount"),
    WRONG_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST,"Wrong TransactionType"),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "Not_Enough_Point"),;
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
