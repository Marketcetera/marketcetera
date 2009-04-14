package org.marketcetera.photon.commons.ui;

import org.eclipse.osgi.util.NLS;

/* $License$ */

/**
 * Eclipse-style internationalization to allow this plugin to be used without core marketcetera
 * jars.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.marketcetera.photon.commons.ui.messages"; //$NON-NLS-1$
	public static String FilterBox_clearButton_tooltip;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
