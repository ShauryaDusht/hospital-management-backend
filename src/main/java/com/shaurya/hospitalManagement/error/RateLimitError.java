package com.shaurya.hospitalManagement.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RateLimitError extends ApiError {
    private long retryAfter;
    private int remaining;

    public RateLimitError(String error, HttpStatus statusCode, long retryAfter, int remaining) {
        super(error, statusCode);
        this.retryAfter = retryAfter;
        this.remaining = remaining;
    }


}