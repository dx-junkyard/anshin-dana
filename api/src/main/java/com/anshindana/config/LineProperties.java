package com.anshindana.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.line")
public record LineProperties(String channelId, String channelSecret, String jwksUri, String issuer) {
}
