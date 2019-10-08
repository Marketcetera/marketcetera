package com.marketcetera.colin.ui.views.storefront.beans;

public class OrdersCountDataWithChart extends OrdersCountData {

	private Integer overall;

	public OrdersCountDataWithChart() {

	}

	public OrdersCountDataWithChart(String title, String subtitle, Integer count, Integer overall) {
		super(title, subtitle, count);
		this.overall = overall;
	}

	public Integer getOverall() {
		return overall;
	}

	public void setOverall(Integer overall) {
		this.overall = overall;
	}

}