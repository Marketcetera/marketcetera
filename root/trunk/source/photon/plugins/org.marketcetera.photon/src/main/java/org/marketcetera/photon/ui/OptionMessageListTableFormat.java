package org.marketcetera.photon.ui;

import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.core.Pair;
import org.marketcetera.photon.marketdata.OptionInfoComponent;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.views.OptionDateHelper;
import org.marketcetera.photon.views.OptionMessagesComposite;
import org.marketcetera.photon.views.OptionMessagesComposite.OptionDataColumns;

import quickfix.DataDictionary;
import quickfix.FieldMap;



public class OptionMessageListTableFormat extends MessageListTableFormatBase<OptionMessageHolder> {

	private OptionDateHelper helper;

	public OptionMessageListTableFormat(Table table, Enum<?>[] columns, IWorkbenchPartSite site, DataDictionary dataDictionary) {
		super(table, columns, site, dataDictionary);
		helper = new OptionDateHelper();
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
		if (index == OptionMessagesComposite.EXP_DATE_INDEX){
			try{
				Pair<Integer, Integer> pair = OptionMarketDataUtils.getMaturityMonthYear(((OptionMessageHolder) element).get(OptionInfoComponent.STRIKE_INFO));
				Integer year = pair.getSecondMember();
				Integer month = pair.getFirstMember();
				return helper.getMonthAbbreviation(month)+(helper.formatYear(year).substring(2));
			} catch (Exception ex){
			}
		}
		return super.getColumnText(element, index);
	}

}
