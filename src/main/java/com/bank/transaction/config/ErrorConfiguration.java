package com.bank.transaction.config;

import com.bank.transaction.errors.ErrorMessage;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@ConfigurationProperties(prefix = "error-config")
@PropertySource(value = "classpath:config/errorConfig.yml", factory = YmlPropertySourceFactory.class)
public class ErrorConfiguration {
    Map<String, ErrorMessage> errorMessages = new HashMap<>();
}
