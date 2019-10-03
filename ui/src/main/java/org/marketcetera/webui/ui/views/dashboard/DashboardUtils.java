package org.marketcetera.webui.ui.views.dashboard;

public class DashboardUtils {


//	private static final String NEXT_DELIVERY_PATTERN = "Next Delivery %s";

//	public static OrdersCountDataWithChart getTodaysOrdersCountData(DeliveryStats deliveryStats,
//			Iterator<OrderSummary> ordersIterator) {
//		OrdersCountDataWithChart ordersCountData = new OrdersCountDataWithChart("Remaining Today", null,
//				deliveryStats.getDueToday() - deliveryStats.getDeliveredToday(), deliveryStats.getDueToday());
//
//		LocalDate date = LocalDate.now();
//		LocalTime time = LocalTime.now();
//		while (ordersIterator.hasNext()) {
//
//			OrderSummary order = ordersIterator.next();
//			if (isOrderNextToDeliver(order, date, time)) {
//				if (order.getDueDate().isEqual(date))
//					ordersCountData.setSubtitle(String.format(NEXT_DELIVERY_PATTERN, order.getDueTime()));
//				else
//					ordersCountData.setSubtitle(String.format(NEXT_DELIVERY_PATTERN,
//							order.getDueDate().getMonthValue() + "/" + order.getDueDate().getDayOfMonth()));
//
//				break;
//			}
//
//		}
//		return ordersCountData;
//	}
//
//	private static boolean isOrderNextToDeliver(OrderSummary order, LocalDate nowDate, LocalTime nowTime) {
//		// ready order starting from current time
//		return order.getState() == OrderState.READY
//				&& ((order.getDueDate().isEqual(nowDate) && order.getDueTime().isAfter(nowTime))
//						|| order.getDueDate().isAfter(nowDate));
//	}
//
//	public static OrdersCountData getNotAvailableOrdersCountData(DeliveryStats deliveryStats) {
//		OrdersCountData ordersCountData = new OrdersCountData("Not Available", "Delivery tomorrow",
//				deliveryStats.getNotAvailableToday());
//
//		return ordersCountData;
//	}

//	public static OrdersCountData getTomorrowOrdersCountData(DeliveryStats deliveryStats,
//			Iterator<OrderSummary> ordersIterator) {
//		OrdersCountData ordersCountData = new OrdersCountData("Tomorrow", null, deliveryStats.getDueTomorrow());
//
//		LocalDate date = LocalDate.now().plusDays(1);
//		LocalTime minTime = LocalTime.MAX;
//		while (ordersIterator.hasNext()) {
//			OrderSummary order = ordersIterator.next();
//			if (order.getDueDate().isBefore(date)) {
//				continue;
//			}
//
//			if (order.getDueDate().isEqual(date)) {
//				if (order.getDueTime().isBefore(minTime)) {
//					minTime = order.getDueTime();
//				}
//			}
//
//			if (order.getDueDate().isAfter(date)) {
//				break;
//			}
//		}
//
//		if (!LocalTime.MAX.equals(minTime))
//			ordersCountData.setSubtitle("First delivery " + minTime);
//
//		return ordersCountData;
//	}
//
//	public static OrdersCountData getNewOrdersCountData(DeliveryStats deliveryStats, Order lastOrder) {
//		return new OrdersCountData("New", createSubtitle(lastOrder), deliveryStats.getNewOrders());
//	}
//
//	private static final String NEW_ORDERS_COUNT_SUBTITLE_PATTERN = "Last %d%s ago";
//
//	private static String createSubtitle(Order lastOrder) {
//		LocalDateTime currTime = LocalDateTime.now();
//		LocalDateTime timestamp = lastOrder.getHistory().get(0).getTimestamp();
//
//		long value = timestamp.until(currTime, ChronoUnit.DAYS);
//		if (value > 0) {
//			return String.format(NEW_ORDERS_COUNT_SUBTITLE_PATTERN, value, "d");
//		}
//
//		value = timestamp.until(currTime, ChronoUnit.HOURS);
//		if (value > 0) {
//			return String.format(NEW_ORDERS_COUNT_SUBTITLE_PATTERN, value, "h");
//		}
//
//		value = timestamp.until(currTime, ChronoUnit.MINUTES);
//		if (value > 0) {
//			return String.format(NEW_ORDERS_COUNT_SUBTITLE_PATTERN, value, "m");
//		}
//
//		// option if data contain orders from the future
//		return "Last just added";
//	}
}
