package com.portfolio;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Admin login. The password is stored only as a BCrypt hash (see application.yml).
 */
@ConfigurationProperties(prefix = "admin")
public record AdminProperties(String username, String passwordHash) {
}
