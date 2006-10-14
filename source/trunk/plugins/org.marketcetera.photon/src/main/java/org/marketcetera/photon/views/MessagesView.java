package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.ui.EnumTableFormat;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.MessageListTableFormat;
import org.marketcetera.photon.ui.TableComparatorChooser;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

public abstract class MessagesView extends ViewPart {

	private Table messageTable;
	private TableViewer messagesViewer;
	private IToolBarManager toolBarManager;
	private FIXMessageHistory fixMessageHistory;
	private EnumTableFormat tableFormat;
	private TableComparatorChooser<MessageHolder> chooser;


    protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
		int i = 0;
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
		
		tableFormat = (EnumTableFormat)messagesViewer.getLabelProvider();
		formatTable(messageTable);

		packColumns(messageTable);
        toolBarManager = getViewSite().getActionBars().getToolBarManager();
		initializeToolBar(toolBarManager);
		FIXMessageHistory messageHistory = Application.getFIXMessageHistory();
		if (messageHistory!= null){
			setInput(messageHistory);
		}
		
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
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());
		aMessagesViewer.setLabelProvider(new MessageListTableFormat(aMessageTable, enums, getSite()));
		return aMessagesViewer;
	}
	

	public TableViewer getMessagesViewer() {
		return messagesViewer;
	}
	
	public void setInput(FIXMessageHistory input)
	{
		fixMessageHistory = input;
		SortedList<MessageHolder> extractedList = 
			new SortedList<MessageHolder>(extractList(input));

		if (chooser != null){
			chooser.dispose();
			chooser = null;
		}
		chooser = new TableComparatorChooser<MessageHolder>(
							messageTable, 
							tableFormat,
							extractedList, false);

		messagesViewer.setInput(extractedList);
	}

	protected abstract EventList<MessageHolder> extractList(FIXMessageHistory input);

	


}
