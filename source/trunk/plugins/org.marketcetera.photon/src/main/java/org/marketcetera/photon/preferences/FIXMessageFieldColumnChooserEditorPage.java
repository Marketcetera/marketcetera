package org.marketcetera.photon.preferences;

import ca.odell.glazedlists.BasicEventList;

public class FIXMessageFieldColumnChooserEditorPage {

	private char orderType;

	private BasicEventList<String> availableFieldsList;

	private BasicEventList<String> chosenFieldsList;

	public FIXMessageFieldColumnChooserEditorPage(char orderType) {
		this(orderType, new BasicEventList<String>(),
				new BasicEventList<String>());
	}

	public FIXMessageFieldColumnChooserEditorPage(char orderType,
			BasicEventList<String> availableFieldsList,
			BasicEventList<String> chosenFieldsList) {
		this.orderType = orderType;
		this.availableFieldsList = availableFieldsList;
		this.chosenFieldsList = chosenFieldsList;
	}

	protected BasicEventList<String> getAvailableFieldsList() {
		return availableFieldsList;
	}

	protected void setAvailableFieldsList(BasicEventList<String> availableFieldsList) {
		this.availableFieldsList = availableFieldsList;
	}

	protected BasicEventList<String> getChosenFieldsList() {
		return chosenFieldsList;
	}

	protected void setChosenFieldsList(BasicEventList<String> chosenFieldsList) {
		this.chosenFieldsList = chosenFieldsList;
	}

	protected char getOrderType() {
		return orderType;
	}

	protected void setOrderType(char orderType) {
		this.orderType = orderType;
	}

	
}
