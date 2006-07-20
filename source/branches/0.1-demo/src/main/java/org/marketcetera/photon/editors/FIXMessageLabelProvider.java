package org.marketcetera.photon.editors;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.OutgoingMessageHolder;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;

public class FIXMessageLabelProvider extends WorkbenchLabelProvider implements
		ITableLabelProvider {
	private Image ARROW_RIGHT;

	private Image ARROW_LEFT;

	private DataDictionary dataDictionary;

	private Map<String, Integer> fieldMap;

	private TableColumn[] columns;

	public FIXMessageLabelProvider(TableColumn[] columns) {
		this.columns = columns;
		fieldMap = new TreeMap<String, Integer>();
		try {
			dataDictionary = new DataDictionary("FIX42.xml");
		} catch (ConfigError e) {
		}

		// TODO: how can we get the max number of fields?
		for (int fieldNum = 1; fieldNum < 1000; fieldNum++) {
			if (dataDictionary.isField(fieldNum))
				fieldMap.put(dataDictionary.getFieldName(fieldNum), fieldNum);
		}

		ARROW_RIGHT = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_RIGHT).createImage();
		ARROW_LEFT = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_LEFT).createImage();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof IncomingMessageHolder) {
				return ARROW_RIGHT;
			} else if (element instanceof OutgoingMessageHolder) {
				return ARROW_LEFT;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Message fixMessage;
		if (element instanceof MessageHolder) {
			MessageHolder holder = (MessageHolder) element;
			fixMessage = holder.getMessage();
			if (columnIndex == 0) {
				return null;
			}
		} else if (element instanceof Message) {
			fixMessage = (Message) element;
		} else {
			return null;
		}
		TableColumn theColumn = columns[columnIndex];
		String columnText = theColumn.getText();
		Integer fieldID = fieldMap.get(columnText);
		if (fieldID != null) {
			try {
				String value;
				if (dataDictionary.isHeaderField(fieldID)) {
					value = fixMessage.getHeader().getString(fieldID);
				} else if (dataDictionary.isTrailerField(fieldID)) {
					value = fixMessage.getTrailer().getString(fieldID);
				} else {
					value = fixMessage.getString(fieldID);
				}
				if (dataDictionary.hasFieldValue(fieldID)) {
					return dataDictionary.getValueName(fieldID, value);
				}
				return value;
			} catch (FieldNotFound e) {
				return null;
			}
		}
		return null;
	}

}
