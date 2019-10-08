package com.marketcetera.colin.ui.entities;

public class PageInfo {
	private final String link;
	private final String icon;
	private final String title;

	public PageInfo(String link, String icon, String title) {
		this.link = link;
		this.icon = icon;
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public String getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

}
