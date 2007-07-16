package org.marketcetera.photon.ui;

import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.photon.marketdata.OptionInfoComponent;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.views.OptionMessagesComposite;
import org.marketcetera.photon.views.OptionMessagesComposite.OptionDataColumns;

import quickfix.DataDictionary;
import quickfix.FieldMap;



public class OptionMessageListTableFormat extends MessageListTableFormatBase<OptionMessageHolder> {

	public OptionMessageListTableFormat(Table table, Enum[] columns, IWorkbenchPartSite site, DataDictionary dataDictionary) {
		super(table, columns, site, dataDictionary);
	}

	@Override
	public FieldMap getFieldMap(OptionMessageHolder element, int columnIndex) {
		OptionMessagesComposite.OptionDataColumns columnEnum = (OptionDataColumns) columns[columnIndex];
		OptionInfoComponent component = columnEnum.getComponent();
		return element.get(component);
	}

	@Override
	public String getColumnText(Object element, int index) {
		if (index == 0) {
			return ""; //$NON-NLS-1$
		}
		return super.getColumnText(element, index);
	}

}
