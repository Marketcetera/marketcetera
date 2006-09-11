package org.marketcetera.photon.actions;

/**
 * ICommandListener is an interface implemented by objects
 * that want to be notified when the user has issued
 * a Photon-specific command, such as entering a new
 * order.
 * 
 * @author gmiller
 *
 */
public interface ICommandListener {

	/**
	 * Called when the user has entered a command,
	 * such as a new stock order, given the CommandEvent
	 * object representing the event.  The substance of
	 * the command can be obtained by extracting the actual
	 * {@link quickfix.Message} by calling {@link CommandEvent#getMessage()}
	 * 
	 * @param evt the command event.
	 */
	public void commandIssued(CommandEvent evt);
}
