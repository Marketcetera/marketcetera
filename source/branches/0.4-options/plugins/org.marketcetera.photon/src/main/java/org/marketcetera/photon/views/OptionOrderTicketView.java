package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * Option order ticket view.
 * 
 * @author andrei.lissovski@softwaregoodness.com
 */
public class OptionOrderTicketView extends ViewPart {

	public static String ID = "org.marketcetera.photon.views.OptionOrderTicketView";  //$NON-NLS-1$


	public OptionOrderTicketView() {
	}

	@Override
	public void createPartControl(Composite parent) {
        Image image = new Image(parent.getDisplay(), 
        		OptionOrderTicketView.class.getResourceAsStream("/icons/mockui/optionorder.png"));  //$NON-NLS-1$
        Rectangle imageBounds = image.getBounds();

        Composite composite = new Composite(parent, SWT.NONE);
		Label viewer = new Label(composite, SWT.NONE);
		viewer.setImage(image);
		RowData rowData = new RowData(imageBounds.width, imageBounds.height);
		viewer.setLayoutData(rowData);
		composite.setLayout(new RowLayout());
	}

	@Override
	public void dispose() {
	}

	@Override
	public void setFocus() {
	}
}
