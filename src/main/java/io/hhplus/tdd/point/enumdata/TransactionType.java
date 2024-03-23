package io.hhplus.tdd.point.enumdata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TransactionType {
    CHARGE("charge"),
    USE("use"),;

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TransactionType from(String value) {
        for (TransactionType status : TransactionType.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
