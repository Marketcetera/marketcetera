package org.marketcetera.photon.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;

@Deprecated
public class EnumLabelProvider implements ITableLabelProvider {

	Enum [] columns;
	private DataDictionary dataDictionary;
	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();

	public EnumLabelProvider(Enum [] columns) {
		this.columns = columns;
		
		dataDictionary = FIXDataDictionaryManager.getDictionary();
		// TODO: how can we get the max number of fields?
		for (int fieldNum = 1; fieldNum < 1000; fieldNum++) {
			if (dataDictionary.isField(fieldNum))
				fieldMap.put(dataDictionary.getFieldName(fieldNum), fieldNum);
		}
	}

	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Message message = null;
		if (element instanceof MessageHolder) {
			MessageHolder holder = (MessageHolder) element;
			message = holder.getMessage();
		} else if (element instanceof Message) {
			message = (Message) element;
		}
		if (message != null){
			Integer fieldID = fieldMap.get(columns[columnIndex].toString());
			if (fieldID != null) {
				try {
					String value = "";
					if (dataDictionary.isHeaderField(fieldID)) {
						value = message.getHeader().getString(fieldID);
					} else if (dataDictionary.isTrailerField(fieldID)) {
						value = message.getTrailer().getString(fieldID);
					} else {
						value = message.getString(fieldID);
					}
					
					if (dataDictionary.hasFieldValue(fieldID)) {
						value = FIXDataDictionaryManager.getHumanFieldValue(fieldID, value);
					}
					
					return value;
				} catch (FieldNotFound e) {
					return "";
				}
			}
		}
		return "";
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}
