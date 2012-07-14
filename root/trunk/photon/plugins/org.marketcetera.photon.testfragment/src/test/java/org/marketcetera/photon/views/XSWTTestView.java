package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class XSWTTestView extends ViewPart {

	private Composite parent;

	public static final String ID = "org.marketcetera.photon.views.XSWTTestView";
	
	public XSWTTestView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

	}

	@Override
	public void setFocus() {

	}
	
	public Composite getParent() {
		return parent;
	}

}
