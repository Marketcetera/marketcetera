package org.marketcetera.photon.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;

public class DirectionalMessageTableFormat extends MessageListTableFormat {

	private Image arrowInImage;
	private Image arrowOutImage;

	public DirectionalMessageTableFormat(Table table, Enum[] columns, IWorkbenchPartSite site) {
		super(table, columns, site);
		arrowInImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_IN).createImage();
		arrowOutImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_OUT).createImage();
	}

	@Override
	public void dispose() {
		arrowInImage.dispose();
		arrowOutImage.dispose();
		super.dispose();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0 && element instanceof MessageHolder)
		{
			if (element instanceof IncomingMessageHolder){
				return arrowInImage;
			} else {
				return arrowOutImage;
			}
		}
		return super.getColumnImage(element, columnIndex);
	}
	
	

}
