package org.marketcetera.photon.preferences;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.core.ClassVersion;

/**
 * URL string field editor with syntactic validation of the URL. Uses the field label text 
 * (specified in the constructor) in the error message in case of a failed validation.
 *  
 * @author alissovski
 */
@ClassVersion("$Id: $")
public class UrlFieldEditor extends StringFieldEditor
{
	private boolean isValid = false;
	
	
	public UrlFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.StringFieldEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return isValid;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
	 */
	@Override
	protected void refreshValidState() {
		boolean oldValid = isValid;
		isValid = checkUrlSyntax(getTextControl().getText());
		if (!isValid)
			showErrorMessage(getErrorText());
		else
			clearErrorMessage();
		
		if (oldValid != isValid)
			fireValueChanged(FieldEditor.IS_VALID, new Boolean(oldValid), new Boolean(isValid));  // causes the framework to update the enabled state of the Ok and Apply buttons
	}
	
	private String getErrorText() {
		return "The " + getLabelText() + " is not a valid URL.";
	}

	/**
	 * Checks whether a given string represents a syntactically valid URL. Ignores leading and trailing
	 * whitespace.
	 * 
	 * @param url a URL string.
	 * @return <code>true</code> if the string is a syntactically valid URL; <code>false</code> otherwise.
	 */
	private boolean checkUrlSyntax(String url) {
		if (url == null || url.trim().equals(""))
			return false;
		
		try {
			new URI(url.trim());
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}