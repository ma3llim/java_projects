package org.products.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    @NestedConfigurationProperty
    private PaginationProperties pagination = new PaginationProperties();

    @NestedConfigurationProperty
    private ServiceProperties service = new ServiceProperties();

    @NestedConfigurationProperty
    private JwtProperties jwtProperties = new JwtProperties();

    @NestedConfigurationProperty
    private CorsProperties corsProperties = new CorsProperties();
}
