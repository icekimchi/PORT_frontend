package com.hp028.portpilot.api.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T body;
}
