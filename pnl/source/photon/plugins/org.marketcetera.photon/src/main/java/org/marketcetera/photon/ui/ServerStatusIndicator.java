package org.marketcetera.photon.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ServerStatusIndicator extends StatusIndicatorContributionItem {

	public enum State {
		Connected, Disconnected, Error
	};

	private static State lastState = State.Disconnected;
	private static ServerStatusIndicator instance;

	private Label imageLabel;

	@Override
	protected Control createControl(Composite parent) {
		if (instance != null) {
			// only one instance is allowed
			throw new IllegalStateException();
		}
		super.createControl(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 2;
		composite.setLayout(layout);
		imageLabel = new Label(composite, SWT.NONE);
		updateLabel();
		instance = this;
		return composite;
	}

	private void asyncUpdateLabel() {
		imageLabel.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!imageLabel.isDisposed()) {
					updateLabel();
				}
			}
		});
	}

	private void updateLabel() {
		switch (lastState) {
		case Connected:
			imageLabel.setImage(getOnImage());
			imageLabel.setToolTipText("Server connected");
			break;
		case Disconnected:
			imageLabel.setImage(getOffImage());
			imageLabel.setToolTipText("Server disconnected");
			break;
		case Error:
			imageLabel.setImage(getErrorImage());
			imageLabel.setToolTipText("A server connection error has occurred");
			break;
		}
	}

	@Override
	protected void doDispose() {
		imageLabel = null;
		instance = null;
		super.doDispose();
	}

	private static void setState(State state) {
		lastState = state;
		if (instance != null) {
			instance.asyncUpdateLabel();
		}
	}

	public static void setDisconnected() {
		setState(State.Disconnected);
	}

	public static void setConnected() {
		setState(State.Connected);
	}

	public static void setError() {
		setState(State.Error);
	}
}
