package org.marketcetera.photon.actions;


public class CommandEvent {

	private String stringValue;

	public CommandEvent(String value) {
		stringValue = value;
	}

	/**
	 * @return Returns the stringValue.
	 */
	public String getStringValue() {
		return stringValue;
	}

	
}
