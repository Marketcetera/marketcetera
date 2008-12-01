package org.marketcetera.photon.ui;

import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.messagehistory.ReportHolder;

import quickfix.DataDictionary;
import quickfix.FieldMap;



public class MessageListTableFormat extends MessageListTableFormatBase<ReportHolder> {
	
	public MessageListTableFormat(Table table, Enum<?>[] columns, IWorkbenchPartSite site, DataDictionary dataDictionary) {
		super(table, columns, site, dataDictionary);
	}

	public MessageListTableFormat(Table table, Enum<?>[] columns, IWorkbenchPartSite site) {
		super(table, columns, site);
	}

	@Override
	public FieldMap getFieldMap(ReportHolder element, int columnIndex) {
		FieldMap fieldMap = ((ReportHolder) element).getMessage();
		return fieldMap;
	}

}
