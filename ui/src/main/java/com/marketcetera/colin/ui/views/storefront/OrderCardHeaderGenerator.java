package com.marketcetera.colin.ui.views.storefront;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.marketcetera.colin.backend.data.entity.Order;
import com.marketcetera.colin.ui.views.storefront.beans.OrderCardHeader;

public class OrderCardHeaderGenerator {

	private class HeaderWrapper {
		private Predicate<LocalDate> matcher;

		private OrderCardHeader header;

		private Long selected;

		public HeaderWrapper(Predicate<LocalDate> matcher, OrderCardHeader header) {
			this.matcher = matcher;
			this.header = header;
		}

		public boolean matches(LocalDate date) {
			return matcher.test(date);
		}

		public Long getSelected() {
			return selected;
		}

		public void setSelected(Long selected) {
			this.selected = selected;
		}

		public OrderCardHeader getHeader() {
			return header;
		}
	}

	private final DateTimeFormatter HEADER_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d");

	private final Map<Long, OrderCardHeader> ordersWithHeaders = new HashMap<>();
	private List<HeaderWrapper> headerChain = new ArrayList<>();

	private OrderCardHeader getRecentHeader() {
		return new OrderCardHeader("Recent", "Before this week");
	}

	private OrderCardHeader getYesterdayHeader() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		return new OrderCardHeader("Yesterday", secondaryHeaderFor(yesterday));
	}

	private OrderCardHeader getTodayHeader() {
		LocalDate today = LocalDate.now();
		return new OrderCardHeader("Today", secondaryHeaderFor(today));
	}

	private OrderCardHeader getThisWeekBeforeYesterdayHeader() {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		LocalDate thisWeekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
		return new OrderCardHeader("This week before yesterday", secondaryHeaderFor(thisWeekStart, yesterday));
	}

	private OrderCardHeader getThisWeekStartingTomorrow(boolean showPrevious) {
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		LocalDate nextWeekStart = today.minusDays(today.getDayOfWeek().getValue()).plusWeeks(1);
		return new OrderCardHeader(showPrevious ? "This week starting tomorrow" : "This week",
				secondaryHeaderFor(tomorrow, nextWeekStart));
	}

	private OrderCardHeader getUpcomingHeader() {
		return new OrderCardHeader("Upcoming", "After this week");
	}

	private String secondaryHeaderFor(LocalDate date) {
		return HEADER_DATE_TIME_FORMATTER.format(date);
	}

	private String secondaryHeaderFor(LocalDate start, LocalDate end) {
		return secondaryHeaderFor(start) + " - " + secondaryHeaderFor(end);
	}

	public OrderCardHeader get(Long id) {
		return ordersWithHeaders.get(id);
	}

	public void resetHeaderChain(boolean showPrevious) {
		this.headerChain = createHeaderChain(showPrevious);
		ordersWithHeaders.clear();
	}

	public void ordersRead(List<Order> orders) {
		Iterator<HeaderWrapper> headerIterator = headerChain.stream().filter(h -> h.getSelected() == null).iterator();
		if (!headerIterator.hasNext()) {
			return;
		}

		HeaderWrapper current = headerIterator.next();
		for (Order order : orders) {
			// If last selected, discard orders that match it.
			if (current.getSelected() != null && current.matches(order.getDueDate())) {
				continue;
			}
			while (current != null && !current.matches(order.getDueDate())) {
				current = headerIterator.hasNext() ? headerIterator.next() : null;
			}
			if (current == null) {
				break;
			}
			current.setSelected(order.getId());
			ordersWithHeaders.put(order.getId(), current.getHeader());
		}
	}

	private List<HeaderWrapper> createHeaderChain(boolean showPrevious) {
		List<HeaderWrapper> headerChain = new ArrayList<>();
		LocalDate today = LocalDate.now();
		LocalDate startOfTheWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		if (showPrevious) {
			LocalDate yesterday = today.minusDays(1);
			// Week starting on Monday
			headerChain.add(new HeaderWrapper(d -> d.isBefore(startOfTheWeek), this.getRecentHeader()));
			if (startOfTheWeek.isBefore(yesterday)) {
				headerChain.add(new HeaderWrapper(d -> d.isBefore(yesterday) && !d.isAfter(startOfTheWeek),
						this.getThisWeekBeforeYesterdayHeader()));
			}
			headerChain.add(new HeaderWrapper(yesterday::equals, this.getYesterdayHeader()));
		}
		LocalDate firstDayOfTheNextWeek = startOfTheWeek.plusDays(7);
		headerChain.add(new HeaderWrapper(today::equals, getTodayHeader()));
		headerChain.add(new HeaderWrapper(d -> d.isAfter(today) && d.isBefore(firstDayOfTheNextWeek),
				getThisWeekStartingTomorrow(showPrevious)));
		headerChain.add(new HeaderWrapper(d -> !d.isBefore(firstDayOfTheNextWeek), getUpcomingHeader()));
		return headerChain;
	}
}