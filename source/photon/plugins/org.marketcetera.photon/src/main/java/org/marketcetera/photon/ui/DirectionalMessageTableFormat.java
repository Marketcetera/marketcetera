package org.marketcetera.photon.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXDataDictionary;

/* $License$ */

/**
 * Provides the in arrow image (out arrow was removed in 1.0)
 * 
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DirectionalMessageTableFormat<T>
    extends FIXMessageTableFormat<T>
    implements Messages
{

	private static final int DirectionFieldNum = -2;
	
	private Image arrowInImage;

	public DirectionalMessageTableFormat(Table table,
			final String assignedViewID, Class<T> underlyingClass) {
		super(table, assignedViewID, underlyingClass);
		arrowInImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_IN)
				.createImage();
	}

	@Override
	public void dispose() {
		try {
			arrowInImage.dispose();
		} catch (Exception anyException) {
			PhotonPlugin.getMainConsoleLogger().warn(CANNOT_DISPOSE_OF_ARROWS.getText(),
			                                         anyException);
		}
		super.dispose();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0 && element instanceof ReportHolder) {
			return arrowInImage;
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
