package com.aptana.rdt.internal.core.gems;

import org.eclipse.osgi.util.NLS;

public class GemsMessages extends NLS {

	private static final String BUNDLE_NAME = GemsMessages.class.getName();
	
	public static String GemManager_loading_local_gems;
	public static String GemManager_loading_remote_gems;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, GemsMessages.class);
	}
}
