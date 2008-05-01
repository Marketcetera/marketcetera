package org.marketcetera.photon.ui.validation;

public interface IMessageDisplayer {
	public void showErrorMessage(String errorString, int severity);
	public void clearMessage();
}
