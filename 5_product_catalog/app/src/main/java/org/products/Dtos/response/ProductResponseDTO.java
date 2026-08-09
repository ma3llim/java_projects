package org.products.Dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Double stock;
}
