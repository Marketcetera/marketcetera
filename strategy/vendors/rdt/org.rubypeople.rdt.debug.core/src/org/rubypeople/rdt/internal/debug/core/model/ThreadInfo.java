package org.rubypeople.rdt.internal.debug.core.model;

public class ThreadInfo {
	private int id;
	private String status;
	
	public ThreadInfo(int id, String status) {
		this.id = id;
		this.status = status;
	}
		
	public int getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}
}
