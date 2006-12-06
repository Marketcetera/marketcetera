package org.marketcetera.photon.ui.validation;

public interface IMessageDisplayer {
	public void showError(String errorString);
	public void showWarning(String warningString);
	public void clearMessage();
}
