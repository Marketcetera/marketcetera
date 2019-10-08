package com.marketcetera.colin.ui.views.storefront.beans;

public class OrderCardHeader {

	private String main;
	private String secondary;

	public OrderCardHeader(String main, String secondary) {
		this.main = main;
		this.secondary = secondary;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getSecondary() {
		return secondary;
	}

	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}
}
