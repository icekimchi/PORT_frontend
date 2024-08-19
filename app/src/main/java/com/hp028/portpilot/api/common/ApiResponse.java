package com.hp028.portpilot.api.common;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T body;
}
