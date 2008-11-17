package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyCore;

public class RdtDebugCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.rubypeople.rdt.debug.core"; //$NON-NLS-1$
	public static final String MODEL_IDENTIFIER = "org.rubypeople.rdt.debug";

	private static boolean isRubyDebuggerVerbose = false;

	protected static RdtDebugCorePlugin plugin;

	public RdtDebugCorePlugin() {
		super();
		plugin = this;
	}

	public static Plugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyCore.getWorkspace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		String rubyDebuggerVerboseOption = Platform
				.getDebugOption(RdtDebugCorePlugin.PLUGIN_ID
						+ "/rubyDebuggerVerbose");
		isRubyDebuggerVerbose = rubyDebuggerVerboseOption == null ? false
				: rubyDebuggerVerboseOption.equalsIgnoreCase("true");
	}

	public static void log(int severity, String message) {
		Status status = new Status(severity, PLUGIN_ID, IStatus.OK, message,
				null);
		RdtDebugCorePlugin.log(status);
	}

	public static void log(String message, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, e));
	}

	public static void log(IStatus status) {
		if (RdtDebugCorePlugin.getDefault() != null) {
			getDefault().getLog().log(status);
		} else {
			System.out.println("Error: ");
			System.out.println(status.getMessage());
		}
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR,
				"RdtLaunchingPlugin.internalErrorOccurred", e)); //$NON-NLS-1$
	}

	public static void debug(Object message) {
		if (RdtDebugCorePlugin.getDefault() != null) {
			if (RdtDebugCorePlugin.getDefault().isDebugging()) {
				System.out.println(message.toString());
			}

		} else {
			// Called from Unit-Test, Plugin not initialized
			System.out.println(message.toString());
		}
	}

	public static void debug(String message, Throwable e) {
		if (RdtDebugCorePlugin.getDefault() != null) {
			if (RdtDebugCorePlugin.getDefault().isDebugging()) {
				System.out.println(message + ", Exception: " + e.getMessage());
				RdtDebugCorePlugin.log(e);
			}

		} else {
			// Called from Unit-Test, Plugin not initialized
			System.out.println(message);
			e.printStackTrace();
		}

	}

	public static boolean isRubyDebuggerVerbose() {
		return isRubyDebuggerVerbose;
	}

	public static String getPluginIdentifier() {
		return PLUGIN_ID;
	}
}