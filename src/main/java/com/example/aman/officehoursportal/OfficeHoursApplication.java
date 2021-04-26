package com.example.aman.officehoursportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class OfficeHoursApplication implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
         registry.addResourceHandler("/img/**").addResourceLocations("/static/img/")
                    .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
        }


    public static void main(String[] args) {
        SpringApplication.run(OfficeHoursApplication.class, args);
    }


}
