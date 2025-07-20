package com.cosmicdoc.opdmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI cosmicDocOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CosmicDoc OPD Management API")
                        .description("REST API for OPD (Outpatient Department) Management")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("CosmicDoc")
                                .url("https://cosmicdoc.com")
                                .email("info@cosmicdoc.com"))
                        .license(new License()
                                .name("Private License")
                                .url("https://cosmicdoc.com/license")));
    }
}
