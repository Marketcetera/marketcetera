package org.marketcetera.photon.actions;

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
