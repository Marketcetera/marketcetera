package org.marketcetera.photon.editors;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.model.FIXMessageHistory;

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

		ARROW_RIGHT = AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.ARROW_RIGHT).createImage();
		ARROW_LEFT = AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.ARROW_LEFT).createImage();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof FIXMessageHistory.IncomingMessageHolder) {
				return ARROW_RIGHT;
			} else if (element instanceof FIXMessageHistory.OutgoingMessageHolder) {
				return ARROW_LEFT;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Message fixMessage;
		if (element instanceof FIXMessageHistory.MessageHolder) {
			FIXMessageHistory.MessageHolder holder = (FIXMessageHistory.MessageHolder) element;
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
