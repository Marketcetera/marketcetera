package com.marketcetera.colin.backend.data;

public class Role {
	public static final String BARISTA = "barista";
	public static final String BAKER = "baker";
	// This role implicitly allows access to all views.
	public static final String ADMIN = "admin";

	private Role() {
		// Static methods and fields only
	}

	public static String[] getAllRoles() {
		return new String[] { BARISTA, BAKER, ADMIN };
	}

}
