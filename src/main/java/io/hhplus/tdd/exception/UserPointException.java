package io.hhplus.tdd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPointException extends RuntimeException{

    private final UserPointErrorResult errorResult;
}
