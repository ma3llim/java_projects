package org.products.utils;

import org.products.entities.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecificationUtil {
    public static Specification<Product> hasNameLike(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction(); // no filter
            }
            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + search.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasPriceGreaterThanOrEqual(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> hasPriceLessThanOrEqual(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> withFilters(String search, Double minPrice, Double maxPrice) {
        return Specification.where(hasNameLike(search))
                .and(hasPriceGreaterThanOrEqual(minPrice))
                .and(hasPriceLessThanOrEqual(maxPrice));
    }
}
