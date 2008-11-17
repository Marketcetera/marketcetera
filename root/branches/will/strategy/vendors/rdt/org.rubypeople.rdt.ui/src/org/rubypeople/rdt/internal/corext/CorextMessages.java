package org.rubypeople.rdt.internal.corext;

import org.eclipse.osgi.util.NLS;

public class CorextMessages extends NLS {
	
	private static final String BUNDLE_NAME = CorextMessages.class.getName();
	
	public static String History_error_serialize;
	public static String History_error_read;
	public static String TypeInfoHistory_consistency_check;

	public static String Resources_fileModified;
	public static String Resources_modifiedResources;
	public static String Resources_outOfSync;
	public static String Resources_outOfSyncResources;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CorextMessages.class);
	}
}
