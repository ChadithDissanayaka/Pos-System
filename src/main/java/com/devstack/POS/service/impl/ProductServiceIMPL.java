package com.devstack.POS.service.impl;

import com.devstack.POS.dto.request.ProductRequestDTO;
import com.devstack.POS.dto.response.PagedResponseDTO;
import com.devstack.POS.dto.response.ProductResponseDTO;
import com.devstack.POS.entity.Product;
import com.devstack.POS.exception.EntryNotFoundException;
import com.devstack.POS.repo.ProductRepo;
import com.devstack.POS.service.ProductService;
import com.devstack.POS.util.ProductMapper;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceIMPL implements ProductService {
    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public void createProduct(ProductRequestDTO dto) {
        productRepo.save(productMapper.toProduct(dto));
    }

    @Override
    public void updateProduct(ProductRequestDTO dto, UUID id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new EntryNotFoundException("Product not found for provided id: " + id));
        product.setDescription(dto.getDescription());
        product.setUnitPrice(dto.getUnitPrice());
        product.setQtyOnHand(dto.getQtyOnHand());  
        productRepo.save(product);
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepo.deleteById(id);

    }

    @Override
    public ProductResponseDTO findProductById(UUID id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new EntryNotFoundException("Product not found for provided id: " + id));
        return productMapper.toProductResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productRepo.findAll().stream().map(productMapper::toProductResponseDTO).toList();
    }

    @Override
    public PagedResponseDTO<ProductResponseDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, int page, int size) {
        String normalizedSearch = searchText == null ? "" : searchText.trim();
        Specification<Product> spec = buildProductSpecification(normalizedSearch, minPrice, maxPrice);

        return PagedResponseDTO.<ProductResponseDTO>builder()
                .dataList(productRepo.findAll(spec, PageRequest.of(page, size))
                        .stream().map(productMapper::toProductResponseDTO).toList())
                .dataCount(productRepo.count(spec))
                .build();
    }

    private Specification<Product> buildProductSpecification(String searchText, Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchText != null && !searchText.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + searchText.toLowerCase() + "%"));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("unitPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("unitPrice"), maxPrice));
            }

            return predicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
