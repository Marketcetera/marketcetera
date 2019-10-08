package com.marketcetera.colin.backend.data;

import java.util.Locale;

import com.vaadin.flow.shared.util.SharedUtil;

public enum OrderState {
	NEW, CONFIRMED, READY, DELIVERED, PROBLEM, CANCELLED;

	/**
	 * Gets a version of the enum identifier in a human friendly format.
	 *
	 * @return a human friendly version of the identifier
	 */
	public String getDisplayName() {
		return SharedUtil.capitalize(name().toLowerCase(Locale.ENGLISH));
	}
}
