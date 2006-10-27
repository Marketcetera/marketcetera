package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
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
	private EnumTableFormat<MessageHolder> tableFormat;
	private TableComparatorChooser<MessageHolder> chooser;
	private Clipboard clipboard;
	private CopyMessagesAction copyMessagesAction;
	private EventList<MessageHolder> rawInputList;


    protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
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
		
		tableFormat = (EnumTableFormat<MessageHolder>)messagesViewer.getLabelProvider();
		formatTable(messageTable);

		packColumns(messageTable);
        toolBarManager = getViewSite().getActionBars().getToolBarManager();
		initializeToolBar(toolBarManager);
		
		copyMessagesAction = new CopyMessagesAction(getClipboard(),messageTable, "Copy");
		IWorkbench workbench = PlatformUI.getWorkbench();
		ISharedImages platformImages = workbench.getSharedImages();
		copyMessagesAction.setImageDescriptor(platformImages .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyMessagesAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));

		Object menuObj = messageTable.getData(MenuManager.class.toString());
		if (menuObj != null && menuObj instanceof MenuManager) {
			MenuManager menuManager = (MenuManager) menuObj;
			menuManager.add(copyMessagesAction);
		}
		
		hookGlobalActions();
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
	
	public void setInput(EventList<MessageHolder> input)
	{
		SortedList<MessageHolder> extractedList = 
			new SortedList<MessageHolder>(rawInputList = input);

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
	
	public EventList<MessageHolder> getInput()
	{
		return rawInputList;
	}

	
	private void hookGlobalActions(){
		getViewSite().getActionBars()
		.setGlobalActionHandler(ActionFactory.COPY.getId(), copyMessagesAction);
	}

	@Override
	public void dispose() {
		if (clipboard != null)
			clipboard.dispose();
		super.dispose();

	}
	
	protected Clipboard getClipboard()
	{
		if (clipboard == null){
			clipboard = new Clipboard(getSite().getShell().getDisplay());
		}
		return clipboard;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
