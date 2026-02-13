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

    @Getter
    public static class RateLimitExceededException extends RuntimeException {
        private final long retryAfter;
        private final int remaining;

        public RateLimitExceededException(String message, long retryAfter, int remaining) {
            super(message);
            this.retryAfter = retryAfter;
            this.remaining = remaining;
        }
    }
}