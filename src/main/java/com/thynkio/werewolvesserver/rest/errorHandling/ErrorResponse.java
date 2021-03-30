package com.thynkio.werewolvesserver.rest.errorHandling;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {

    //General error message about nature of error
    private final String message;
    //Specific errors in API request processing
    private final List<String> details;

    public ErrorResponse(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
    }
}
