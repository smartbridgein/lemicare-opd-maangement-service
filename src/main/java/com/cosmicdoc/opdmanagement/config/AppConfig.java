package com.cosmicdoc.opdmanagement.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Configuration class to selectively include components from the common module
 * while excluding the problematic ones causing JSON parsing issues.
 */
@Configuration
@ComponentScan(
    basePackages = {"com.cosmicdoc.common", "com.cosmicdoc.opdmanagement"},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.cosmicdoc\\.common\\.util\\.JsonDataLoader"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.cosmicdoc\\.common\\.config\\.FirebaseConfig"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.cosmicdoc\\.common\\.repository\\.impl\\.AppointmentRepositoryImpl"
        )
    }
)
public class AppConfig {
    // Configuration class to control component scanning
}
