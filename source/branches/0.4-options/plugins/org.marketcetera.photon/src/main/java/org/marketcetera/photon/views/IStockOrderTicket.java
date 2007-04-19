package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Table;

public interface IStockOrderTicket extends IOrderTicket {

	Table getCustomFieldsTable();

	CheckboxTableViewer getTableViewer();

}