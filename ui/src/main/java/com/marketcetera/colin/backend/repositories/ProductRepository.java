package com.marketcetera.colin.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.marketcetera.colin.backend.data.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findBy(Pageable page);

	Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

	int countByNameLikeIgnoreCase(String name);

}
