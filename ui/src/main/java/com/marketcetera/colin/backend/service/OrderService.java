package com.marketcetera.colin.backend.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.marketcetera.colin.backend.data.DashboardData;
import com.marketcetera.colin.backend.data.DeliveryStats;
import com.marketcetera.colin.backend.data.OrderState;
import com.marketcetera.colin.backend.data.entity.Order;
import com.marketcetera.colin.backend.data.entity.OrderSummary;
import com.marketcetera.colin.backend.data.entity.Product;
import com.marketcetera.colin.backend.data.entity.User;
import com.marketcetera.colin.backend.repositories.OrderRepository;

@Service
public class OrderService implements CrudService<Order> {

	private final OrderRepository orderRepository;

	@Autowired
	public OrderService(OrderRepository orderRepository) {
		super();
		this.orderRepository = orderRepository;
	}

	private static final Set<OrderState> notAvailableStates = Collections.unmodifiableSet(
			EnumSet.complementOf(EnumSet.of(OrderState.DELIVERED, OrderState.READY, OrderState.CANCELLED)));

	@Transactional(rollbackOn = Exception.class)
	public Order saveOrder(User currentUser, Long id, BiConsumer<User, Order> orderFiller) {
		Order order;
		if (id == null) {
			order = new Order(currentUser);
		} else {
			order = load(id);
		}
		orderFiller.accept(currentUser, order);
		return orderRepository.save(order);
	}

	@Transactional(rollbackOn = Exception.class)
	public Order saveOrder(Order order) {
		return orderRepository.save(order);
	}

	@Transactional(rollbackOn = Exception.class)
	public Order addComment(User currentUser, Order order, String comment) {
		order.addHistoryItem(currentUser, comment);
		return orderRepository.save(order);
	}

	public Page<Order> findAnyMatchingAfterDueDate(Optional<String> optionalFilter,
			Optional<LocalDate> optionalFilterDate, Pageable pageable) {
		if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
			if (optionalFilterDate.isPresent()) {
				return orderRepository.findByCustomerFullNameContainingIgnoreCaseAndDueDateAfter(
						optionalFilter.get(), optionalFilterDate.get(), pageable);
			} else {
				return orderRepository.findByCustomerFullNameContainingIgnoreCase(optionalFilter.get(), pageable);
			}
		} else {
			if (optionalFilterDate.isPresent()) {
				return orderRepository.findByDueDateAfter(optionalFilterDate.get(), pageable);
			} else {
				return orderRepository.findAll(pageable);
			}
		}
	}
	
	@Transactional
	public List<OrderSummary> findAnyMatchingStartingToday() {
		return orderRepository.findByDueDateGreaterThanEqual(LocalDate.now());
	}

	public long countAnyMatchingAfterDueDate(Optional<String> optionalFilter, Optional<LocalDate> optionalFilterDate) {
		if (optionalFilter.isPresent() && optionalFilterDate.isPresent()) {
			return orderRepository.countByCustomerFullNameContainingIgnoreCaseAndDueDateAfter(optionalFilter.get(),
					optionalFilterDate.get());
		} else if (optionalFilter.isPresent()) {
			return orderRepository.countByCustomerFullNameContainingIgnoreCase(optionalFilter.get());
		} else if (optionalFilterDate.isPresent()) {
			return orderRepository.countByDueDateAfter(optionalFilterDate.get());
		} else {
			return orderRepository.count();
		}
	}

	private DeliveryStats getDeliveryStats() {
		DeliveryStats stats = new DeliveryStats();
		LocalDate today = LocalDate.now();
		stats.setDueToday((int) orderRepository.countByDueDate(today));
		stats.setDueTomorrow((int) orderRepository.countByDueDate(today.plusDays(1)));
		stats.setDeliveredToday((int) orderRepository.countByDueDateAndStateIn(today,
				Collections.singleton(OrderState.DELIVERED)));

		stats.setNotAvailableToday((int) orderRepository.countByDueDateAndStateIn(today, notAvailableStates));
		stats.setNewOrders((int) orderRepository.countByState(OrderState.NEW));

		return stats;
	}

	public DashboardData getDashboardData(int month, int year) {
		DashboardData data = new DashboardData();
		data.setDeliveryStats(getDeliveryStats());
		data.setDeliveriesThisMonth(getDeliveriesPerDay(month, year));
		data.setDeliveriesThisYear(getDeliveriesPerMonth(year));

		Number[][] salesPerMonth = new Number[3][12];
		data.setSalesPerMonth(salesPerMonth);
		List<Object[]> sales = orderRepository.sumPerMonthLastThreeYears(OrderState.DELIVERED, year);

		for (Object[] salesData : sales) {
			// year, month, deliveries
			int y = year - (int) salesData[0];
			int m = (int) salesData[1] - 1;
			if (y == 0 && m == month - 1) {
				// skip current month as it contains incomplete data
				continue;
			}
			long count = (long) salesData[2];
			salesPerMonth[y][m] = count;
		}

		LinkedHashMap<Product, Integer> productDeliveries = new LinkedHashMap<>();
		data.setProductDeliveries(productDeliveries);
		for (Object[] result : orderRepository.countPerProduct(OrderState.DELIVERED, year, month)) {
			int sum = ((Long) result[0]).intValue();
			Product p = (Product) result[1];
			productDeliveries.put(p, sum);
		}

		return data;
	}

	private List<Number> getDeliveriesPerDay(int month, int year) {
		int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
		return flattenAndReplaceMissingWithNull(daysInMonth,
				orderRepository.countPerDay(OrderState.DELIVERED, year, month));
	}

	private List<Number> getDeliveriesPerMonth(int year) {
		return flattenAndReplaceMissingWithNull(12, orderRepository.countPerMonth(OrderState.DELIVERED, year));
	}

	private List<Number> flattenAndReplaceMissingWithNull(int length, List<Object[]> list) {
		List<Number> counts = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			counts.add(null);
		}

		for (Object[] result : list) {
			counts.set((Integer) result[0] - 1, (Number) result[1]);
		}
		return counts;
	}

	@Override
	public JpaRepository<Order, Long> getRepository() {
		return orderRepository;
	}

	@Override
	@Transactional
	public Order createNew(User currentUser) {
		Order order = new Order(currentUser);
		order.setDueTime(LocalTime.of(16, 0));
		order.setDueDate(LocalDate.now());
		return order;
	}

}
