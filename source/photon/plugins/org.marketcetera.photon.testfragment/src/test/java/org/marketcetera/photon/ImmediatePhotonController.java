package org.marketcetera.photon;

import org.marketcetera.trade.Order;

import quickfix.Message;

public class ImmediatePhotonController extends PhotonController {

	private Order mLastOrder;

	public ImmediatePhotonController() {
		super();
	}

	@Override
	protected void asyncExec(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void sendOrder(Order inOrder) {
		mLastOrder = inOrder;
	}
	public Order getLastOrder() {
		return mLastOrder;
	}

}
