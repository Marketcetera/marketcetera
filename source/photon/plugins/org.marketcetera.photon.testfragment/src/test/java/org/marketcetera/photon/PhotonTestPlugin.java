package org.marketcetera.photon;

import org.eclipse.core.runtime.Plugin;

public class PhotonTestPlugin extends Plugin {

	//The shared instance.
	private static PhotonTestPlugin plugin;

	public PhotonTestPlugin() {
		plugin = this;
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static PhotonTestPlugin getDefault() {
		return plugin;
	}


}
