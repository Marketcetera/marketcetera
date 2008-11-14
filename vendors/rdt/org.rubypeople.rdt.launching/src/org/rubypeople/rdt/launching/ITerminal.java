package org.rubypeople.rdt.launching;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.IConsole;

/**
 * The id of the terminal must match the type returned by getType()
 * 
 * @author Chris Williams
 *
 */
public interface ITerminal extends IConsole, org.eclipse.ui.console.IConsole {
	
	public void attach(IProcess process);

	public void activate();
		
	/**
	 * Please use one of the following for the stream id:
	 * {@link IDebugUIConstants.ID_STANDARD_ERROR_STREAM}, {@link IDebugUIConstants.ID_STANDARD_INPUT_STREAM}, or 
	 * {@link IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM}
	 * 
	 * @param streamIdentifier
	 * @param text
	 */
	public void write(String streamIdentifier, String text);
	
	public void setProject(IProject project);
}
