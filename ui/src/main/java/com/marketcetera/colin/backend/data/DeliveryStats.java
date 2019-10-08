package com.marketcetera.colin.backend.data;

public class DeliveryStats {

	private int deliveredToday;
	private int dueToday;
	private int dueTomorrow;
	private int notAvailableToday;
	private int newOrders;

	public int getDeliveredToday() {
		return deliveredToday;
	}

	public void setDeliveredToday(int deliveredToday) {
		this.deliveredToday = deliveredToday;
	}

	public int getDueToday() {
		return dueToday;
	}

	public void setDueToday(int dueToday) {
		this.dueToday = dueToday;
	}

	public int getDueTomorrow() {
		return dueTomorrow;
	}

	public void setDueTomorrow(int dueTomorrow) {
		this.dueTomorrow = dueTomorrow;
	}

	public int getNotAvailableToday() {
		return notAvailableToday;
	}

	public void setNotAvailableToday(int notAvailableToday) {
		this.notAvailableToday = notAvailableToday;
	}

	public int getNewOrders() {
		return newOrders;
	}

	public void setNewOrders(int newOrders) {
		this.newOrders = newOrders;
	}

}
