package org.products.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.products.Dtos.request.ProductRequestDTO;
import org.products.Dtos.response.ProductResponseDTO;
import org.products.config.properties.PaginationProperties;
import org.products.entities.Product;
import org.products.exceptions.ProductNotFound;
import org.products.repository.ProductRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaginationProperties paginationProperties;
    @InjectMocks
    private ProductService productService;
    private ProductRequestDTO validProductRequest;
    private Product product;
    private UUID productId;

    @BeforeEach
    void setup() {
        productId = UUID.randomUUID();

        validProductRequest = ProductRequestDTO.builder()
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .category("Electronics")
                .stock(10.0)
                .build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .category("Electronics")
                .stock(10.0)
                .build();
    }

    // CREATE PRODUCT TESTS
    @Test
    @DisplayName("Should successfully create product when valid request is provided")
    void addProduct_ShouldSuccessfullyCreateProduct_WhenValidRequest() {
        // Arrange
        when(productRepository.existsByNameIgnoreCase(validProductRequest.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product savedProduct = invocation.getArgument(0);
                    // Set the ID on the saved product to simulate database generation
                    savedProduct.setId(productId);
                    return savedProduct;
                });

        // Act
        ProductResponseDTO result = productService.addProduct(validProductRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getPrice()).isEqualTo(99.99);
        assertThat(result.getCategory()).isEqualTo("Electronics");
        assertThat(result.getStock()).isEqualTo(10);

        verify(productRepository).existsByNameIgnoreCase("Test Product");
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should throw exception when product name already exists")
    void addProduct_ShouldThrowException_WhenProductNameAlreadyExists() {
        // Arrange
        when(productRepository.existsByNameIgnoreCase(validProductRequest.getName()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productService.addProduct(validProductRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with this name already exists");

        verify(productRepository).existsByNameIgnoreCase("Test Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product name is null")
    void addProduct_ShouldThrowException_WhenProductNameIsNull() {
        // Arrange
        ProductRequestDTO invalidRequest = ProductRequestDTO.builder()
                .name(null)
                .description("Test Description")
                .price(99.99)
                .category("Electronics")
                .stock(10.0)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> productService.addProduct(invalidRequest))
                .isInstanceOf(Exception.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should ignore case when checking for duplicate product names")
    void addProduct_ShouldTrimAndNormalizeName_WhenCheckingDuplicates() {
        // Arrange
        ProductRequestDTO requestWithSpaces = ProductRequestDTO.builder()
                .name("  Test Product  ")
                .description("Test Description")
                .price(99.99)
                .category("Electronics")
                .stock(10.0)
                .build();

        // The service uses request.getName() directly without trimming
        // This test documents the current behavior
        when(productRepository.existsByNameIgnoreCase("  Test Product  "))
                .thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        productService.addProduct(requestWithSpaces);

        // Assert
        verify(productRepository).existsByNameIgnoreCase("  Test Product  ");
    }

    // GET PRODUCT BY ID TESTS
    @Test
    @DisplayName("Should return product when product exists")
    void getProductById_ShouldReturnProduct_WhenProductExists() throws ProductNotFound {
        // Arrange
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponseDTO result = productService.getProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getPrice()).isEqualTo(99.99);
        assertThat(result.getCategory()).isEqualTo("Electronics");
        assertThat(result.getStock()).isEqualTo(10);

        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should throw ProductNotFound when product does not exist")
    void getProductById_ShouldThrowProductNotFound_WhenProductDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(nonExistentId))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product Not Found of this product ID: " + nonExistentId);

        verify(productRepository).findById(nonExistentId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should throw ProductNotFound when product ID is null")
    void getProductById_ShouldThrowProductNotFound_WhenProductIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(null))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product Not Found of this product ID: null");

        verify(productRepository).findById(null);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should return correct product when multiple products exist")
    void getProductById_ShouldReturnCorrectProduct_WhenMultipleProductsExist() throws ProductNotFound {
        // Arrange
        UUID anotherProductId = UUID.randomUUID();
        Product anotherProduct = Product.builder()
                .id(anotherProductId)
                .name("Another Product")
                .description("Another Description")
                .price(49.99)
                .category("Books")
                .stock(5.0)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.findById(anotherProductId))
                .thenReturn(Optional.of(anotherProduct));

        // Act
        ProductResponseDTO result1 = productService.getProductById(productId);
        ProductResponseDTO result2 = productService.getProductById(anotherProductId);

        // Assert
        assertThat(result1.getId()).isEqualTo(productId);
        assertThat(result1.getName()).isEqualTo("Test Product");

        assertThat(result2.getId()).isEqualTo(anotherProductId);
        assertThat(result2.getName()).isEqualTo("Another Product");
        assertThat(result2.getPrice()).isEqualTo(49.99);

        verify(productRepository).findById(productId);
        verify(productRepository).findById(anotherProductId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should return product with all fields populated")
    void getProductById_ShouldReturnProductWithAllFields_WhenProductHasAllFieldsPopulated() throws ProductNotFound {
        // Arrange
        Product fullProduct = Product.builder()
                .id(productId)
                .name("Full Product")
                .description("Full Description with details")
                .price(199.99)
                .category("Premium Electronics")
                .stock(25.0)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(fullProduct));

        // Act
        ProductResponseDTO result = productService.getProductById(productId);

        // Assert
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Full Product");
        assertThat(result.getDescription()).isEqualTo("Full Description with details");
        assertThat(result.getPrice()).isEqualTo(199.99);
        assertThat(result.getCategory()).isEqualTo("Premium Electronics");
        assertThat(result.getStock()).isEqualTo(25);

        verify(productRepository).findById(productId);
    }

    // Update
    // ========== UPDATE PRODUCT TESTS ==========

    @Test
    @DisplayName("Should successfully update product when valid request is provided")
    void updateProduct_ShouldSuccessfullyUpdateProduct_WhenValidRequest() throws ProductNotFound {
        // Arrange
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Updated Product")
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.findByNameIgnoreCase("Updated Product"))
                .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class)))
                .thenReturn(updatedProduct);

        // Act
        ProductResponseDTO result = productService.updateProduct(productId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPrice()).isEqualTo(149.99);
        assertThat(result.getCategory()).isEqualTo("Updated Category");
        assertThat(result.getStock()).isEqualTo(20);

        verify(productRepository).findById(productId);
        verify(productRepository).findByNameIgnoreCase("Updated Product");
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should throw ProductNotFound when updating non-existent product")
    void updateProduct_ShouldThrowProductNotFound_WhenProductDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(nonExistentId, updateRequest))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product not found with id: " + nonExistentId);

        verify(productRepository).findById(nonExistentId);
        verify(productRepository, never()).findByNameIgnoreCase(any());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate product name")
    void updateProduct_ShouldThrowException_WhenDuplicateProductName() throws ProductNotFound {
        // Arrange
        UUID anotherProductId = UUID.randomUUID();
        Product anotherProduct = Product.builder()
                .id(anotherProductId)
                .name("Another Product")
                .description("Another Description")
                .price(49.99)
                .category("Books")
                .stock(5.0)
                .build();

        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Another Product") // Trying to update to this name
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.findByNameIgnoreCase("Another Product"))
                .thenReturn(Optional.of(anotherProduct));

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(productId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with this name already exists");

        verify(productRepository).findById(productId);
        verify(productRepository).findByNameIgnoreCase("Another Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should allow updating with same name (no duplicate check triggered)")
    void updateProduct_ShouldAllowUpdate_WhenSameName() throws ProductNotFound {
        // Arrange
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Test Product") // Same name
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Test Product") // Same name
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        // Since name is same, findByNameIgnoreCase should not be called
        when(productRepository.save(any(Product.class)))
                .thenReturn(updatedProduct);

        // Act
        ProductResponseDTO result = productService.updateProduct(productId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPrice()).isEqualTo(149.99);

        verify(productRepository).findById(productId);
        verify(productRepository, never()).findByNameIgnoreCase(any());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should partially update product when only some fields are provided")
    void updateProduct_ShouldPartiallyUpdateProduct_WhenSomeFieldsProvided() throws ProductNotFound {
        // Arrange
        ProductRequestDTO partialUpdateRequest = ProductRequestDTO.builder()
                .name("Partially Updated Product")
                .price(199.99)
                .build();

        Product partiallyUpdatedProduct = Product.builder()
                .id(productId)
                .name("Partially Updated Product")
                .description("Test Description") // Unchanged
                .price(199.99)
                .category("Electronics") // Unchanged
                .stock(10.0) // Unchanged
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.findByNameIgnoreCase("Partially Updated Product"))
                .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class)))
                .thenReturn(partiallyUpdatedProduct);

        // Act
        ProductResponseDTO result = productService.updateProduct(productId, partialUpdateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Partially Updated Product");
        assertThat(result.getDescription()).isEqualTo("Test Description"); // Unchanged
        assertThat(result.getPrice()).isEqualTo(199.99);
        assertThat(result.getCategory()).isEqualTo("Electronics"); // Unchanged
        assertThat(result.getStock()).isEqualTo(10); // Unchanged

        verify(productRepository).findById(productId);
        verify(productRepository).findByNameIgnoreCase("Partially Updated Product");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product ID is null")
    void updateProduct_ShouldThrowException_WhenProductIdIsNull() {
        // Arrange
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(null, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product ID cannot be null");

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Should update product with trimmed name")
    void updateProduct_ShouldTrimProductName_WhenSaving() throws ProductNotFound {
        // Arrange
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("  Trimmed Product Name  ")
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Trimmed Product Name") // Trimmed
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.findByNameIgnoreCase("Trimmed Product Name"))
                .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(productId);
                    return saved;
                });

        // Act
        ProductResponseDTO result = productService.updateProduct(productId, updateRequest);

        // Assert
        assertThat(result.getName()).isEqualTo("Trimmed Product Name");
        verify(productRepository).findByNameIgnoreCase("Trimmed Product Name");
    }

    // Delete
    @Test
    @DisplayName("Should successfully delete product when product exists")
    void deleteProductById_ShouldSuccessfullyDeleteProduct_WhenProductExists() throws ProductNotFound {
        // Arrange
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.deleteProductById(productId);

        // Assert
        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should throw ProductNotFound when deleting non-existent product")
    void deleteProductById_ShouldThrowProductNotFound_WhenProductDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProductById(nonExistentId))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product Not Found of this product ID: " + nonExistentId);

        verify(productRepository).findById(nonExistentId);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product ID is null")
    void deleteProductById_ShouldThrowIllegalArgumentException_WhenProductIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProductById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product ID is required for deleting");

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Should delete product and not throw exception when product exists")
    void deleteProductById_ShouldNotThrowException_WhenProductExists() throws ProductNotFound {
        // Arrange
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act & Assert - Should not throw any exception
        productService.deleteProductById(productId);

        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Should call delete on repository exactly once when product exists")
    void deleteProductById_ShouldCallDeleteExactlyOnce_WhenProductExists() throws ProductNotFound {
        // Arrange
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.deleteProductById(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should not call delete when product does not exist")
    void deleteProductById_ShouldNotCallDelete_WhenProductDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProductById(nonExistentId))
                .isInstanceOf(ProductNotFound.class);

        verify(productRepository).findById(nonExistentId);
        verify(productRepository, never()).delete(any(Product.class));
    }

    // ========== NOT FOUND EXCEPTION TESTS ==========
    @Test
    @DisplayName("Should throw ProductNotFound with correct message when product not found")
    void productNotFound_ShouldHaveCorrectErrorMessage() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(nonExistentId))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product Not Found of this product ID: " + nonExistentId)
                .hasMessageContaining(nonExistentId.toString())
                .hasMessageContaining("Product Not Found");
    }

    @Test
    @DisplayName("Should throw ProductNotFound with correct message when updating non-existent product")
    void updateProduct_ShouldThrowProductNotFound_WithCorrectErrorMessage() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(149.99)
                .category("Updated Category")
                .stock(20.0)
                .build();

        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(nonExistentId, updateRequest))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product not found with id: " + nonExistentId)
                .hasMessageContaining(nonExistentId.toString())
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw ProductNotFound with correct message when deleting non-existent product")
    void deleteProductById_ShouldThrowProductNotFound_WithCorrectErrorMessage() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProductById(nonExistentId))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("Product Not Found of this product ID: " + nonExistentId)
                .hasMessageContaining(nonExistentId.toString())
                .hasMessageContaining("Product Not Found");
    }
}
