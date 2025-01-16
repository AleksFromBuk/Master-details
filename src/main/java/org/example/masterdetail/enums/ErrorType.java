package org.example.masterdetail.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    DOC_NUMBER_DUPLICATE("document number already exists"),
    DOC_NOT_FOUND("Document not found");
    private final String message;

    ErrorType(String message) {
        this.message = message;
    }
}
