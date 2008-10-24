package org.marketcetera.photon.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.messagehistory.IncomingMessageHolder;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXDataDictionary;

/**
 * 
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class DirectionalMessageTableFormat<T>
    extends FIXMessageTableFormat<T>
    implements Messages
{

	private static final int DirectionFieldNum = -2;
	
	private Image arrowInImage;

	private Image arrowOutImage;

	public DirectionalMessageTableFormat(Table table,
			final String assignedViewID, Class<T> underlyingClass) {
		super(table, assignedViewID, underlyingClass);
		arrowInImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_IN)
				.createImage();
		arrowOutImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_OUT)
				.createImage();
	}

	@Override
	public void dispose() {
		try {
			arrowInImage.dispose();
			arrowOutImage.dispose();
		} catch (Exception anyException) {
			PhotonPlugin.getMainConsoleLogger().warn(CANNOT_DISPOSE_OF_ARROWS.getText(),
			                                         anyException);
		}
		super.dispose();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0 && element instanceof MessageHolder) {
			if (element instanceof IncomingMessageHolder) {
				return arrowInImage;
			} else {
				return arrowOutImage;
			}
		}
		return super.getColumnImage(element, columnIndex);
	}

	@Override
	protected void createExtraColumns() {
		createColumn(DirectionFieldNum);
	}

	@Override
	public String getFIXFieldColumnName(int fixFieldNum, FIXDataDictionary fixDataDictionary) {
		if(fixFieldNum == DirectionFieldNum) {
			return "D"; //$NON-NLS-1$
		}
		return super.getFIXFieldColumnName(fixFieldNum, fixDataDictionary);
	}
}
