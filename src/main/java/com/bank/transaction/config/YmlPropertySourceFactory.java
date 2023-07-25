package com.bank.transaction.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

public class YmlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("Resource must not be null");
        }
        if (name == null) {
            name = resource.getResource().getFilename();
        }
        return new YamlPropertySourceLoader().load(name, resource.getResource()).get(0);
    }
}
