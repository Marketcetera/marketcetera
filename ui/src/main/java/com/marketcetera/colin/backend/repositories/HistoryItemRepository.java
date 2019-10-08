package com.marketcetera.colin.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketcetera.colin.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}
