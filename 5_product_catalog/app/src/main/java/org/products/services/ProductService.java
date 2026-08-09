package org.products.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.products.Dtos.request.ProductRequestDTO;
import org.products.Dtos.response.PageResponse;
import org.products.Dtos.response.ProductResponseDTO;
import org.products.config.properties.PaginationProperties;
import org.products.entities.Product;
import org.products.exceptions.ProductNotFound;
import org.products.repository.ProductRepository;
import org.products.utils.ProductSpecificationUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final PaginationProperties paginationProperties;

    @Cacheable(
            cacheNames = "products",
            key = "#page + '-' + #limit + '-' + #sortBy + '-' + #direction + '-' + #search + '-' + #minPrice + '-' + #maxPrice"
    )
    public PageResponse<ProductResponseDTO> getProducts(
            int page,
            int limit,
            String sortBy,
            String direction,
            String search,
            Double minPrice,
            Double maxPrice) {
        page = Math.max(0, page);
        limit = (limit < 1) ? paginationProperties.getDefaultSize() : limit;
        limit = Math.min(limit, paginationProperties.getMaxSize());
        sortBy = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
        direction = (direction == null || direction.isBlank()) ? "asc" : direction;

        log.info("After validation - page: {}, limit: {}, sortBy: '{}', direction: '{}'",
                page, limit, sortBy, direction);

        // Build Sort
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        // Build Pageable
        Pageable pageable = PageRequest.of(page, limit, sort);

        // Build specification (filters)
        Specification<Product> specification = ProductSpecificationUtil.withFilters(search, minPrice, maxPrice);

        // Database query: filter + sort + paginate in ONE query
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        log.info("Database result - totalElements: {}, totalPages: {}, contentSize: {}",
                productPage.getTotalElements(), productPage.getTotalPages(), productPage.getContent().size());

        // Map to DTOs
        var content = productPage.getContent().stream()
                .map(Product::toResponseDTO)
                .toList();

        PageResponse<ProductResponseDTO> response = new PageResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                (int) productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.hasPrevious(),
                productPage.hasNext()
        );

        log.info("=== RESPONSE === page: {}, size: {}, totalElements: {}, totalPages: {}, hasPrevious: {}, hasNext: {}",
                response.getPage(), response.getSize(), response.getTotalElements(),
                response.getTotalPages(), response.isHasPrevious(), response.isHasNext());

        return response;
    }

    @Cacheable(cacheNames = "productById", key = "#productId")
    public ProductResponseDTO getProductById(UUID productId) throws ProductNotFound {
        log.info("Fetching product with ID: {}", productId);

        log.info("Fetching from Database...");
        Product product = productRepository.findById(productId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ProductNotFound("Product Not Found of this product ID: " + productId));

        ProductResponseDTO productResponseDTO = product.toResponseDTO();
        log.info("Product found: {}", product.getName());
        return productResponseDTO;
    }

    @Cacheable(cacheNames = "productByCategory", key = "#category")
    public List<ProductResponseDTO> getProductByCategory(String category) {
        log.info("Fetching products for category: {}", category);
        List<ProductResponseDTO> products = productRepository.findByCategory(category.toLowerCase().trim())
                .stream().map(Product::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Found {} products in category '{}'", products.size(), category);
        return products;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "productById", key = "#productId"),
            @CacheEvict(cacheNames = "products", allEntries = true),
            @CacheEvict(cacheNames = "productByCategory", allEntries = true)
    })
    public void deleteProductById(UUID productId) throws ProductNotFound {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required for deleting");
        }

        if (productId.toString().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required for deleting");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product Not Found of this product ID: " + productId));

        productRepository.delete(product);
    }

    @Caching(evict = {
            @CacheEvict(
                    cacheNames = "products",
                    allEntries = true
            ),
            @CacheEvict(
                    cacheNames = "productByCategory",
                    allEntries = true
            )
    })
    public ProductResponseDTO addProduct(ProductRequestDTO productRequest) {
        log.info("Adding new product: {}", productRequest.getName());
        if (productRequest.getName() == null) {
            throw new IllegalArgumentException("Product request cannot be null");
        }
        // Optional: Check for duplicate name
        boolean exists = productRepository.existsByNameIgnoreCase(productRequest.getName());
        if (exists) {
            log.warn("Product with name '{}' already exists", productRequest.getName());
            throw new IllegalArgumentException("Product with this name already exists");
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .stock(productRequest.getStock())
                .build();

        productRepository.save(product);
        log.info("Product added with ID: {}", product.getId());

        return product.toResponseDTO();
    }

    @Caching(
            put = {
                    @CachePut(
                            cacheNames = "productById",
                            key = "#productId"
                    )
            },
            evict = {
                    @CacheEvict(
                            cacheNames = "products",
                            allEntries = true
                    ),
                    @CacheEvict(
                            cacheNames = "productByCategory",
                            allEntries = true
                    )
            }
    )
    public ProductResponseDTO updateProduct(UUID productId, @Valid ProductRequestDTO productRequest) throws ProductNotFound {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        log.info("Updating product: {}", productId);

        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> {
            log.warn("Product not found for update: {}", productId);
            return new ProductNotFound("Product not found with id: " + productId);
        });

        String newName = productRequest.getName() != null ? productRequest.getName().trim() : null;

        if (newName != null && !existingProduct.getName().equalsIgnoreCase(newName)) {
            Optional<Product> duplicateProduct = productRepository.findByNameIgnoreCase(newName);
            if (duplicateProduct.isPresent()) {
                log.warn("Product with name '{}' already exists", productRequest.getName());
                throw new IllegalArgumentException("Product with this name already exists");
            }
        }

        // Only update non-null fields (partial update support)
        if (productRequest.getName() != null) existingProduct.setName(newName);
        if (productRequest.getDescription() != null) existingProduct.setDescription(productRequest.getDescription());
        if (productRequest.getPrice() != null) existingProduct.setPrice(productRequest.getPrice());
        if (productRequest.getCategory() != null) existingProduct.setCategory(productRequest.getCategory());
        if (productRequest.getStock() != null) existingProduct.setStock(productRequest.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated: {}", existingProduct.getId());

        return existingProduct.toResponseDTO();
    }
}
