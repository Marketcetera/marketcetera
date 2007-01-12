package org.marketcetera.photon.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;


/**
 * Bare-bones new command entry area widget for the status line.
 * 
 * @author andrei@lissovski.org
 */
public class CommandLineTrimWidget extends AbstractWorkbenchTrimWidget {

	private Composite composite = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#dispose()
	 */
	public void dispose() {
		if (composite != null && !composite.isDisposed())
			composite.dispose();
		composite = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#fill(org.eclipse.swt.widgets.Composite, int, int)
	 */
	public void fill(Composite parent, int oldSide, int newSide) {
		composite = new Composite(parent, SWT.NONE);
		
		FillLayout layout = new FillLayout();
		layout.marginHeight = 4;
		layout.marginWidth  = 2;
		composite.setLayout(layout);
		
		Label control = new Label(composite, SWT.BORDER | SWT.CENTER);
		control.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		control.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		control.setText("New command line will go here and it's going to be just the right size"); //$NON-NLS-1$
	}

}
