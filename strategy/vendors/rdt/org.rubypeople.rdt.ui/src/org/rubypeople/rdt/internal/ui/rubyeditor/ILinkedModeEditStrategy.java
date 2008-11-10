package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.link.LinkedPosition;

/**
 * Interface for linked mode edit strategies.
 */
public interface ILinkedModeEditStrategy extends IAutoEditStrategy {

	/**
	 * Returns the linked positions to use for the most recently processed
	 * document command.
	 * 
	 * @return An array of linked positions to use in linked mode, or
	 *         <code>null</code> if no linked mode should be set up
	 */
	public LinkedPosition[] getLinkedPositions();
}
