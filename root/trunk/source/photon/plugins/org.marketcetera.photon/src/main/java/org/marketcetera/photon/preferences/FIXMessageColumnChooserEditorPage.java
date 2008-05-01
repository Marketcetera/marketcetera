package org.marketcetera.photon.preferences;

import ca.odell.glazedlists.BasicEventList;

public class FIXMessageColumnChooserEditorPage {

	private String subPageID;

	private BasicEventList<String> availableFieldsList;

	private BasicEventList<String> chosenFieldsList;

	public FIXMessageColumnChooserEditorPage(String subPageID) {
		this(subPageID, new BasicEventList<String>(),
				new BasicEventList<String>());
	}

	public FIXMessageColumnChooserEditorPage(String subPageID,
			BasicEventList<String> availableFieldsList,
			BasicEventList<String> chosenFieldsList) {
		this.subPageID = subPageID;
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

	protected String getSubPageID() {
		return subPageID;
	}

	protected void setSubPageID(String subPageID) {
		this.subPageID = subPageID;
	}

	
}
