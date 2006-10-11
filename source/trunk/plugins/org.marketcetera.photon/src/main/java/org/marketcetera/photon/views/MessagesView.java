package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.ui.EnumLabelProvider;
import org.marketcetera.photon.ui.EventListContentProvider;

import ca.odell.glazedlists.EventList;

public abstract class MessagesView extends ViewPart {

	private Table messageTable;
	private TableViewer messagesViewer;
	private IToolBarManager toolBarManager;
	private FIXMessageHistory fixMessageHistory;


    protected void formatTable(Table messageTable, Enum[] columns) {
        messageTable.getVerticalBar().setEnabled(true);
		int i = 0;
        for (Enum aColumn : columns) {
			TableColumn tableColumn = new TableColumn(messageTable, SWT.LEFT);
			tableColumn.setText(columns[i++].toString());
		}
        messageTable.setBackground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

    }

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

        messageTable = createMessageTable(composite);
		messagesViewer = createTableViewer(messageTable, getEnumValues());
		
		formatTable(messageTable,getEnumValues());

		packColumns(messageTable);
        toolBarManager = getViewSite().getActionBars().getToolBarManager();
		initializeToolBar(toolBarManager);
	}

	protected abstract void initializeToolBar(IToolBarManager theToolBarManager);
	
	protected abstract Enum[] getEnumValues();
	
	
	protected void packColumns(final Table table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
	}

    protected Table createMessageTable(Composite parent) {
        Table messageTable = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
        GridData messageTableLayout = new GridData();
        messageTableLayout.horizontalSpan = 2;
        messageTableLayout.verticalSpan = 1;
        messageTableLayout.horizontalAlignment = GridData.FILL;
        messageTableLayout.verticalAlignment = GridData.FILL;
        messageTableLayout.grabExcessHorizontalSpace = true;
        messageTableLayout.grabExcessVerticalSpace = true;
        messageTable.setLayoutData(messageTableLayout);
        return messageTable;
    }
    
	protected TableViewer createTableViewer(Table aMessageTable, Enum[] enums) {
		TableViewer aMessagesViewer = new TableViewer(aMessageTable);
		aMessagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());
		aMessagesViewer.setLabelProvider(new EnumLabelProvider(enums));
		return aMessagesViewer;
	}
	

	protected TableViewer getMessagesViewer() {
		return messagesViewer;
	}
	
	public void setInput(FIXMessageHistory input)
	{
		fixMessageHistory = input;
		messagesViewer.setInput(extractList(input));
	}

	protected abstract EventList<MessageHolder> extractList(FIXMessageHistory input);


}
