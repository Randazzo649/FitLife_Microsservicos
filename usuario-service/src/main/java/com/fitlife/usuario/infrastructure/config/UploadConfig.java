package com.fitlife.usuario.infrastructure.config;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadConfig implements WebMvcConfigurer {
    private final String directory;
    public UploadConfig(@Value("${fitlife.uploads.directory:./uploads}") String directory) { this.directory=directory; }
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location="file:"+Paths.get(directory).toAbsolutePath().normalize()+"/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
