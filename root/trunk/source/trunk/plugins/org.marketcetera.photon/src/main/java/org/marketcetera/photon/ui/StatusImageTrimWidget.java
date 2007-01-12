package org.marketcetera.photon.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;


/**
 * Bare-bones new status image widget for the status line.
 * 
 * @author andrei@lissovski.org
 */
public class StatusImageTrimWidget extends AbstractWorkbenchTrimWidget {

	private Composite composite;
	private Label imageLabel;

	private Image nullStatusImage;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#fill(org.eclipse.swt.widgets.Composite, int, int)
	 */
	public void fill(Composite parent, int oldSide, int newSide) {
		ImageDescriptor descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_OFFLINE);
		nullStatusImage = descriptor.createImage();

		composite = new Composite(parent, SWT.NONE);
		
		FillLayout layout = new FillLayout();
		layout.marginHeight = 4;
		layout.marginWidth  = 2;
		composite.setLayout(layout);
		
		imageLabel = new Label(composite, SWT.NONE);
		imageLabel.setImage(nullStatusImage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#dispose()
	 */
	public void dispose() {
		if (composite != null && !composite.isDisposed())
			composite.dispose();
		composite = null;

		if (nullStatusImage != null && !nullStatusImage.isDisposed())
			nullStatusImage.dispose();
		nullStatusImage = null;
	}
}
