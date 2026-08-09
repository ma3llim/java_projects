package org.products.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.products.Dtos.request.ProductRequestDTO;
import org.products.Dtos.response.ApiResponse;
import org.products.Dtos.response.PageResponse;
import org.products.Dtos.response.ProductResponseDTO;
import org.products.exceptions.ProductNotFound;
import org.products.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations related to product management")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get all products")
    @GetMapping
    public ApiResponse<PageResponse<ProductResponseDTO>> getAllProduct(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) String direction,
            @RequestParam(required = false) String search, @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return ApiResponse.success(productService.getProducts(page, size, sortBy, direction, search, minPrice, maxPrice), "Product Fetch successfully");
    }

    @Operation(summary = "Get products by ID")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDTO> getProductById(@PathVariable UUID productId) throws ProductNotFound {
        return ApiResponse.success(productService.getProductById(productId), "Product Fetch Successfully");
    }

    @Operation(summary = "Get products by Category")
    @GetMapping("/category/{category}")
    public ApiResponse<List<ProductResponseDTO>> getProductByCategory(@PathVariable String category) {
        List<ProductResponseDTO> products = productService.getProductByCategory(category);
        return ApiResponse.success(products, "Product Fetch By Category Successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete products by ID")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) throws ProductNotFound {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add new product")
    @PostMapping
    public ApiResponse<ProductResponseDTO> addProduct(@RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO product = productService.addProduct(productRequest);
        return ApiResponse.success(product, "Product Added Successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update the product by id")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID productId, @Valid @RequestBody ProductRequestDTO productRequest) throws ProductNotFound {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }
}