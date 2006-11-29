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
	protected void handleCounterpartyMessage(Message aMessage) {
		super.handleCounterpartyMessage(aMessage);
	}

	@Override
	protected void handleInternalMessage(Message aMessage) {
		super.handleInternalMessage(aMessage);
	}

}
