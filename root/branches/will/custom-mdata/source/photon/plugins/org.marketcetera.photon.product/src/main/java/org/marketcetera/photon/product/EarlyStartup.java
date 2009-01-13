package org.marketcetera.photon.product;

import org.eclipse.ui.IStartup;
import org.marketcetera.util.misc.ClassVersion;

/**
 * This class is used by the <code>org.eclipse.ui.startup</code> extension point
 * to trigger activation of this plugin after the workbench starts up.
 * Otherwise, the plugin would only be activated when a user requests something
 * from it.
 * <p>
 * In the long run, this plug-in should contain the application configuration
 * code and this class will not be necessary.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class EarlyStartup implements IStartup {
	@Override
	public void earlyStartup() {
		// Do nothing
	}
}
