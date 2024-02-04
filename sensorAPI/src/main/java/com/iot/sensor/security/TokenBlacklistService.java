package com.iot.sensor.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.iot.sensor.model.BlacklistedToken;
import com.iot.sensor.model.BlacklistedTokenRepository;

@Service
public class TokenBlacklistService {
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklistToken(String token, long expirationTime) {
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expirationTime);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.findById(token).isPresent();
    }

    @Scheduled(fixedRate = 300000) // Executes every 5 minutes
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        blacklistedTokenRepository.findAll().forEach(token -> {
            if (token.getExpirationTime() < currentTime) {
                blacklistedTokenRepository.delete(token);
            }
        });
    }
}
