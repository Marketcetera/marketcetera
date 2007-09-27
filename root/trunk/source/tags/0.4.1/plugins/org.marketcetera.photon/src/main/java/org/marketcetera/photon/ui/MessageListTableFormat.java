package org.marketcetera.photon.ui;

import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.photon.core.MessageHolder;

import quickfix.DataDictionary;
import quickfix.FieldMap;



public class MessageListTableFormat extends MessageListTableFormatBase<MessageHolder> {
	
	public MessageListTableFormat(Table table, Enum[] columns, IWorkbenchPartSite site, DataDictionary dataDictionary) {
		super(table, columns, site, dataDictionary);
	}

	public MessageListTableFormat(Table table, Enum[] columns, IWorkbenchPartSite site) {
		super(table, columns, site);
	}

	@Override
	public FieldMap getFieldMap(MessageHolder element, int columnIndex) {
		FieldMap fieldMap = ((MessageHolder) element).getMessage();
		return fieldMap;
	}

}
