package org.marketcetera.photon;

import quickfix.Message;

public class ImmediatePhotonController extends PhotonController {

	public ImmediatePhotonController() {
		super();
	}

	@Override
	protected void asyncExec(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void handleCounterpartyMessage(Message aMessage) {
		super.handleCounterpartyMessage(aMessage);
	}

	@Override
	public void handleInternalMessage(Message aMessage) {
		super.handleInternalMessage(aMessage);
	}

}
