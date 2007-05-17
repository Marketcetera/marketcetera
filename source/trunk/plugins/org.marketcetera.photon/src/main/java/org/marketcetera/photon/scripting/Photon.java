package org.marketcetera.photon.scripting;

import org.marketcetera.photon.PhotonPlugin;

/** top-level class for sending messages and other interactions with Photon app */
public class Photon {
	public static void sendFIXMessage(quickfix.Message message)
	{
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		plugin.getPhotonController().handleInternalMessage(message);
	}
}
