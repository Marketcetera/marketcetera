package org.marketcetera.photon;

import org.apache.bsf.BSFManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.scripting.IScript;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the Photon application.
 */
@ClassVersion("$Id$")
public class PhotonPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static PhotonPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public PhotonPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		BSFManager.registerScriptingEngine(IScript.RUBY_LANG_STRING,
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static PhotonPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, path);
	}
}
