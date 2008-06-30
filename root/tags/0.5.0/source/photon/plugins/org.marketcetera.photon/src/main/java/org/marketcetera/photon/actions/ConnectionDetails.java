package org.marketcetera.photon.actions;

//TODO: Attn toli -- you may want to keep this class, or replace it with your own model object

public class ConnectionDetails {
	private String userId, password;

	public ConnectionDetails(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getResource() {
		return String.valueOf(System.currentTimeMillis());
	}
}
