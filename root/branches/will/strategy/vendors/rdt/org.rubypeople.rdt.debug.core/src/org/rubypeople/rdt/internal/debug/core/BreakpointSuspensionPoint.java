package org.rubypeople.rdt.internal.debug.core;

/**
 * @author Markus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BreakpointSuspensionPoint extends SuspensionPoint {
	public String toString() {
		return "Breakpoint at " + this.getPosition();
	}

	public boolean isBreakpoint() {
		return true;
	}

	public boolean isException() {
		return false;
	}

	public boolean isStep() {
		return false;
	}

}
