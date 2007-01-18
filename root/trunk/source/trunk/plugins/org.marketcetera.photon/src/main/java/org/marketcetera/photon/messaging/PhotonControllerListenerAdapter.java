package org.marketcetera.photon.messaging;

import org.marketcetera.photon.PhotonController;

import quickfix.Message;

public class PhotonControllerListenerAdapter extends
		DirectMessageListenerAdapter {

	PhotonController photonController;
	
	@Override
	protected Object doOnMessage(Object convertedMessage) {
		if (photonController!= null)
			photonController.handleMessage((Message) convertedMessage);
		return null;
	}

	public PhotonController getPhotonController() {
		return photonController;
	}

	public void setPhotonController(PhotonController photonController) {
		this.photonController = photonController;
	}

	
	
}
