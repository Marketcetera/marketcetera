package org.marketcetera.photon.views;

import quickfix.Message;

public interface IFIXMessageDetail {
	void showMessage(Message fixMessage);
}
