/**
 * 
 */
package org.marketcetera.photon.ui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * An event listener that can be disabled. Useful for avoiding infinite loops
 * when tying controls together via their Modify events.
 */
public class ToggledListener implements Listener {

	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void handleEvent(Event event) {
		if (enabled) {
			handleEventWhenEnabled(event);
		}
	}

	protected void handleEventWhenEnabled(Event event) {

	}
}