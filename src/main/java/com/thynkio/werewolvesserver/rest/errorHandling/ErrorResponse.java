package com.thynkio.werewolvesserver.rest.errorHandling;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {

    public ErrorResponse(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
    }

    //General error message about nature of error
    private String message;

    //Specific errors in API request processing
    private List<String> details;
}
