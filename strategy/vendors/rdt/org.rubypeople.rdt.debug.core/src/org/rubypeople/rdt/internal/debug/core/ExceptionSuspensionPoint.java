package org.rubypeople.rdt.internal.debug.core;

/**
 * @author Markus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ExceptionSuspensionPoint extends SuspensionPoint {
	private String exceptionMessage ;
	private String exceptionType ;
	public boolean isBreakpoint() {
		return false;
	}

	public boolean isException() {
		return true;
	}

	public boolean isStep() {
		return false;
	}

	public String toString() {
		return this.getExceptionType() + " occurred :" + this.getExceptionMessage() ;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

}
