package com.shaurya.hospitalManagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    // 5 login tries in 1 hr
    @Value("${rate.limit.login.attempts:5}")
    private int loginMaxAttempts;

    @Value("${rate.limit.login.duration:3600}")
    private long loginDuration;

    // 3 signup tries in a day
    @Value("${rate.limit.signup.attempts:3}")
    private int signupMaxAttempts;

    @Value("${rate.limit.signup.duration:86400}")
    private long signupDuration;

    private static final String LOGIN_PREFIX = "rate:login:";
    private static final String SIGNUP_PREFIX = "rate:signup:";
    private static final String IP_LOGIN_PREFIX = "rate:ip:login:";
    private static final String IP_SIGNUP_PREFIX = "rate:ip:signup:";

    // Username-based methods
    public boolean isLoginAllowed(String identifier) {
        return isAllowed(LOGIN_PREFIX + identifier, loginMaxAttempts);
    }

    public boolean isSignupAllowed(String identifier) {
        return isAllowed(SIGNUP_PREFIX + identifier, signupMaxAttempts);
    }

    public void recordLoginAttempt(String identifier) {
        recordAttempt(LOGIN_PREFIX + identifier, loginDuration);
    }

    public void recordSignupAttempt(String identifier) {
        recordAttempt(SIGNUP_PREFIX + identifier, signupDuration);
    }

    public void resetLoginAttempts(String identifier) {
        redisTemplate.delete(LOGIN_PREFIX + identifier);
    }

    public void resetSignupAttempts(String identifier) {
        redisTemplate.delete(SIGNUP_PREFIX + identifier);
    }

    public int getRemainingLoginAttempts(String identifier) {
        return getRemainingAttempts(LOGIN_PREFIX + identifier, loginMaxAttempts);
    }

    public int getRemainingSignupAttempts(String identifier) {
        return getRemainingAttempts(SIGNUP_PREFIX + identifier, signupMaxAttempts);
    }

    public long getLoginTimeUntilReset(String identifier) {
        return getTimeUntilReset(LOGIN_PREFIX + identifier);
    }

    public long getSignupTimeUntilReset(String identifier) {
        return getTimeUntilReset(SIGNUP_PREFIX + identifier);
    }

    // IP-based methods
    public boolean isIpLoginAllowed(String ip) {
        return isAllowed(IP_LOGIN_PREFIX + ip, loginMaxAttempts);
    }

    public boolean isIpSignupAllowed(String ip) {
        return isAllowed(IP_SIGNUP_PREFIX + ip, signupMaxAttempts);
    }

    public void recordIpLoginAttempt(String ip) {
        recordAttempt(IP_LOGIN_PREFIX + ip, loginDuration);
    }

    public void recordIpSignupAttempt(String ip) {
        recordAttempt(IP_SIGNUP_PREFIX + ip, signupDuration);
    }

    public void resetIpLoginAttempts(String ip) {
        redisTemplate.delete(IP_LOGIN_PREFIX + ip);
    }

    public void resetIpSignupAttempts(String ip) {
        redisTemplate.delete(IP_SIGNUP_PREFIX + ip);
    }

    public int getRemainingIpLoginAttempts(String ip) {
        return getRemainingAttempts(IP_LOGIN_PREFIX + ip, loginMaxAttempts);
    }

    public int getRemainingIpSignupAttempts(String ip) {
        return getRemainingAttempts(IP_SIGNUP_PREFIX + ip, signupMaxAttempts);
    }

    public long getIpLoginTimeUntilReset(String ip) {
        return getTimeUntilReset(IP_LOGIN_PREFIX + ip);
    }

    public long getIpSignupTimeUntilReset(String ip) {
        return getTimeUntilReset(IP_SIGNUP_PREFIX + ip);
    }

    // Private helper methods
    private boolean isAllowed(String key, int maxAttempts) {
        String count = redisTemplate.opsForValue().get(key);
        return count == null || Integer.parseInt(count) < maxAttempts;
    }

    private void recordAttempt(String key, long duration) {
        String count = redisTemplate.opsForValue().get(key);
        if (count == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(duration));
        } else {
            redisTemplate.opsForValue().increment(key);
        }
    }

    private int getRemainingAttempts(String key, int maxAttempts) {
        String count = redisTemplate.opsForValue().get(key);
        if (count == null) return maxAttempts;
        return Math.max(0, maxAttempts - Integer.parseInt(count));
    }

    private long getTimeUntilReset(String key) {
        Long ttl = redisTemplate.getExpire(key);
        return ttl != null && ttl > 0 ? ttl : 0;
    }
}