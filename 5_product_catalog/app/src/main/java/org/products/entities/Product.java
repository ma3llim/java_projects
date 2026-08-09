package org.products.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.products.Dtos.request.ProductRequestDTO;
import org.products.Dtos.response.ProductResponseDTO;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Double stock;

    // Convert from Request DTO to Entity
    public static Product fromRequestDTO(ProductRequestDTO requestDTO) {
        return Product.builder().name(requestDTO.getName()).description(requestDTO.getDescription())
                .price(requestDTO.getPrice())
                .category(requestDTO.getCategory())
                .stock(requestDTO.getStock())
                .build();
    }

    // Convert from Entity to Response DTO
    public ProductResponseDTO toResponseDTO() {
        return ProductResponseDTO.builder().id(this.id).name(this.name).description(this.description)
                .price(this.price)
                .category(this.category)
                .stock(this.stock)
                .build();
    }
}
