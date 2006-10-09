package org.marketcetera.photon.ui;


public interface IControlValidator {
	
	public static final String VALIDATOR_KEY = "VALIDATOR";
	public static final String CONTROL_HIGHLIGHTER_KEY = "CONTROL_HIGHLIGHTER";

	/**
	 * Validate the data referenced by this instance of 
	 * the IValidator, with the assumption that the data may be
	 * incomplete.  This method is called when the user is
	 * still in the process of editing form data, and therefore
	 * fields may be empty.  
	 * 
	 * @return A string representing a warning message, if any, null otherwise
	 */
	public String validateEditing() throws ValidationException;
	
	/**
	 * Validate the data referenced by this instance of 
	 * the IValidator, returning a warning message,
	 * if any.  
	 * 
	 * @return A string representing a warning message, if any, null otherwise
	 */
	public String validate() throws ValidationException;
	
	
}
