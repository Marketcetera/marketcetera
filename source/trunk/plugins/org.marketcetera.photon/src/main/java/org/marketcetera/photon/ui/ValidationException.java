package org.marketcetera.photon.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Control;

public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3722645783807383314L;
	private final Control offendingControl;
	private List<ValidationException> subExceptions =  new LinkedList<ValidationException>();

	public Control getOffendingControl() {
		return offendingControl;
	}

	public ValidationException(String message, Control offendingControl) {
		super(message);
		this.offendingControl = offendingControl;
	}

	public void addSubException(ValidationException ex){
		subExceptions.add(ex);
	}
	
	public List<ValidationException> getSubExceptions()
	{
		return subExceptions;
	}
}
