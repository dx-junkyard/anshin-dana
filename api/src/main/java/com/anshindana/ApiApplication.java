package com.anshindana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.anshindana.config.JwtProperties;
import com.anshindana.config.LineProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({LineProperties.class, JwtProperties.class})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
