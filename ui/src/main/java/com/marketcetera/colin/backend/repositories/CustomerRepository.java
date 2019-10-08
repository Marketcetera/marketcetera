package com.marketcetera.colin.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketcetera.colin.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
