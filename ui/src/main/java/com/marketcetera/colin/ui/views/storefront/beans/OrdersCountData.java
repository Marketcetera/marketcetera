package com.marketcetera.colin.ui.views.storefront.beans;

public class OrdersCountData {

	private String title;
	private String subtitle;
	private Integer count;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public OrdersCountData() {

	}

	public OrdersCountData(String title, String subtitle, Integer count) {
		this.title = title;
		this.subtitle = subtitle;
		this.count = count;
	}

}