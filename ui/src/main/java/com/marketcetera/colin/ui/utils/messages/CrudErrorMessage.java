package com.marketcetera.colin.ui.utils.messages;

public final class CrudErrorMessage {
	public static final String ENTITY_NOT_FOUND = "The selected entity was not found.";

	public static final String CONCURRENT_UPDATE = "Somebody else might have updated the data. Please refresh and try again.";

	public static final String OPERATION_PREVENTED_BY_REFERENCES = "The operation can not be executed as there are references to entity in the database.";

	public static final String REQUIRED_FIELDS_MISSING = "Please fill out all required fields before proceeding.";

	private CrudErrorMessage() {
	}
}
