package org.marketcetera.photon.editors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;

public class EnumTableFormat implements WritableTableFormat, AdvancedTableFormat {

	
	
	private final Enum[] columns;
	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();
	private DataDictionary dataDictionary;



	public EnumTableFormat(Enum [] columns) {
		this.columns = columns;
		
		dataDictionary = FIXDataDictionaryManager.getDictionary();
		// TODO: how can we get the max number of fields?
		for (int fieldNum = 1; fieldNum < 1000; fieldNum++) {
			if (dataDictionary.isField(fieldNum))
				fieldMap.put(dataDictionary.getFieldName(fieldNum), fieldNum);
		}

	}

	public boolean isEditable(Object arg0, int arg1) {
		return false;
	}

	public Object setColumnValue(Object arg0, Object arg1, int arg2) {
		return null;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public String getColumnName(int arg0) {
		return columns[arg0].toString();
	}

	public Object getColumnValue(Object arg0, int arg1) {
		Message message = null;
		if (arg0 instanceof MessageHolder) {
			MessageHolder holder = (MessageHolder) arg0;
			message = holder.getMessage();
		} else if (arg0 instanceof Message) {
			message = (Message) arg0;
		}
		if (message != null){
			Integer fieldID = fieldMap.get(columns[arg1].toString());
			if (fieldID != null) {
				try {
					String value;
					if (dataDictionary.isHeaderField(fieldID)) {
						value = message.getHeader().getString(fieldID);
					} else if (dataDictionary.isTrailerField(fieldID)) {
						value = message.getTrailer().getString(fieldID);
					} else {
						value = message.getString(fieldID);
					}
					if (dataDictionary.hasFieldValue(fieldID)) {
						return FIXDataDictionaryManager.getHumanFieldValue(fieldID, value);
					}
					return value;
				} catch (FieldNotFound e) {
					return null;
				}
			}
		}
		return null;
	}
	
	public Class getColumnClass(int arg0) {
		return String.class;
	}

	public Comparator getColumnComparator(int arg0) {
        return GlazedLists.caseInsensitiveComparator();
	}

}
