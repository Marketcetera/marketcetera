package com.marketcetera.colin.backend.data.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.marketcetera.colin.backend.data.OrderState;

public interface OrderSummary {
	Long getId();

	OrderState getState();

	Customer getCustomer();

	List<OrderItem> getItems();

	LocalDate getDueDate();

	LocalTime getDueTime();

	PickupLocation getPickupLocation();

	Integer getTotalPrice();
}
