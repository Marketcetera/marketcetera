package com.marketcetera.colin.ui.utils;

public class TemplateUtil {

	public static String generateLocation(String basePage, String entityId) {
		return basePage + (entityId == null || entityId.isEmpty() ? "" : "/" + entityId);
	}
}
