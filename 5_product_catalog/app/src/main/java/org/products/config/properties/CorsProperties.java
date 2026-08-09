package org.products.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {
    @NotEmpty(message = "app.cors.allowed-origins must not be empty")
    private List<String> allowedOrigins;

    @NotEmpty(message = "app.cors.allowed-methods must not be empty")
    private List<String> allowedMethods;

    @NotEmpty(message = "app.cors.allowed-headers must not be empty")
    private List<String> allowedHeaders;

    @NotNull(message = "app.cors.allow-credentials must be specified")
    private Boolean allowCredentials;

    @NotNull(message = "app.cors.max-age must be specified")
    private Long maxAge;
}
