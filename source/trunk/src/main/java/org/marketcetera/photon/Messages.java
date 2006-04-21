package org.marketcetera.photon;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.marketcetera.photon.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ApplicationActionBarAdvisor_WindowMenuName;

	public static String ApplicationActionBarAdvisor_OpenViewMenuItemName;

	public static String ApplicationActionBarAdvisor_FileMenuName;

	public static String ApplicationActionBarAdvisor_EditMenuName;

	public static String ApplicationActionBarAdvisor_ScriptMenuName;

	public static String ApplicationActionBarAdvisor_NavigationMenuName;

	public static String ApplicationActionBarAdvisor_HelpMenuName;

	public static String DebugConsole_Name;

	public static String MainConsole_Name;

	public static String ApplicationActionBarAdvisor_OpenPerspectiveMenuName;

	public static String ApplicationActionBarAdvisor_OpenPerspectiveMenuID;

	public static String CommandStatusLineContribution_CommandLabel;

}
