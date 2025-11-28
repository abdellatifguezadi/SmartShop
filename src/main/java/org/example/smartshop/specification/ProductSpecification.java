package org.example.smartshop.specification;

import org.example.smartshop.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> withFilters(String nom, BigDecimal prixMin, BigDecimal prixMax,
                                                      Integer stockMin, Integer stockMax) {
        return Specification.where(isNotDeleted())
                .and(hasNomLike(nom))
                .and(hasPrixBetween(prixMin, prixMax))
                .and(hasStockBetween(stockMin, stockMax));
    }

    public static Specification<Product> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Product> hasNomLike(String nom) {
        return (root, query, cb) -> {
            if (nom == null || nom.isEmpty()) return null;
            return cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasPrixBetween(BigDecimal prixMin, BigDecimal prixMax) {
        return (root, query, cb) -> {
            if (prixMin != null && prixMax != null) {
                return cb.between(root.get("prixUnitaire"), prixMin, prixMax);
            } else if (prixMin != null) {
                return cb.greaterThanOrEqualTo(root.get("prixUnitaire"), prixMin);
            } else if (prixMax != null) {
                return cb.lessThanOrEqualTo(root.get("prixUnitaire"), prixMax);
            }
            return null;
        };
    }

    public static Specification<Product> hasStockBetween(Integer stockMin, Integer stockMax) {
        return (root, query, cb) -> {
            if (stockMin != null && stockMax != null) {
                return cb.between(root.get("stockDisponible"), stockMin, stockMax);
            } else if (stockMin != null) {
                return cb.greaterThanOrEqualTo(root.get("stockDisponible"), stockMin);
            } else if (stockMax != null) {
                return cb.lessThanOrEqualTo(root.get("stockDisponible"), stockMax);
            }
            return null;
        };
    }


}
