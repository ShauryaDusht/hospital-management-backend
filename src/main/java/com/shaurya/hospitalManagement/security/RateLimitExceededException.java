package com.shaurya.hospitalManagement.security;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final long retryAfter;
    private final int remaining;

    public RateLimitExceededException(String message, long retryAfter, int remaining) {
        super(message);
        this.retryAfter = retryAfter;
        this.remaining = remaining;
    }
}