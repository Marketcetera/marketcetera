package org.marketcetera.photon.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.views.OptionMessagesComposite;
import org.marketcetera.photon.views.UnderlyingSymbolInfoComposite;

import quickfix.Group;
import quickfix.Message;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class OptionBookComposite extends Composite implements IBookComposite
{
	private UnderlyingSymbolInfoComposite underlyingSymbolInfoComposite;

	private OptionMessagesComposite optionMessagesComposite;

	private Message currentMarketRefresh;
	
	public OptionBookComposite(Composite parent, int style, IWorkbenchPartSite site, IMemento viewStateMemento){
		this(parent, style, null, site, viewStateMemento);
	}
	
	public OptionBookComposite(Composite parent, int style, FormToolkit toolkit, IWorkbenchPartSite site, IMemento viewStateMemento) 
	{
		super(parent, style);		
		GridLayout gridLayout = new GridLayout();
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace=true;
		layoutData.grabExcessVerticalSpace=true;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.horizontalSpan = 5;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		
		this.setLayout(gridLayout);
		this.setLayoutData(layoutData);
		
		underlyingSymbolInfoComposite = new UnderlyingSymbolInfoComposite(this);
		underlyingSymbolInfoComposite
				.setLayoutData(createUnderlyingSymbolInfoGridData());

		optionMessagesComposite = new OptionMessagesComposite(this, site, viewStateMemento);
		GridData tableGridData = createOptionMessagesGridData();
		optionMessagesComposite.setLayoutData(tableGridData);		
		optionMessagesComposite.setInput(new BasicEventList<OptionMessageHolder>());
	}

	private GridData createOptionMessagesGridData() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		return gridData;
	}

	
	private GridData createUnderlyingSymbolInfoGridData() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.verticalAlignment = GridData.FILL;
		return gridData;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void setInput(Message marketRefresh) {				
		// do nothing, handled by onQuote
	}
	
	public Message getInput(){
		return currentMarketRefresh;
	}
	
	public UnderlyingSymbolInfoComposite getUnderlyingSymbolInfoComposite() {
		return underlyingSymbolInfoComposite;
	}

	public OptionMessagesComposite getOptionMessagesComposite() {
		return optionMessagesComposite;
	}

	//This method is not used in OptionBookComposite
	public EventList<Group> getBookEntryList(Message marketRefresh, char mdEntryType)
	{
		EventList<Group> outputList = new BasicEventList<Group>();
//		try {
//			int numEntries = marketRefresh.getInt(NoMDEntries.FIELD);
//			for (int i = 1; i <= numEntries; i++){
//				MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
//				marketRefresh.getGroup(i, group);
//				if (group.getMDEntryType().getValue() == mdEntryType) {
//					outputList.add(group);
//				}
//			}
//		} catch (FieldNotFound e) {
//		}
		return outputList;
	}

	public void onQuote(final Message aMarketRefresh) {
		currentMarketRefresh = aMarketRefresh;
		
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()){
			optionMessagesComposite.handleQuote(aMarketRefresh);
		} else {
			theDisplay.asyncExec(
				new Runnable(){
					public void run()
					{
						optionMessagesComposite.handleQuote(aMarketRefresh);
					}
				}
			);
		}
	}

	public void saveState(IMemento memento) {
		optionMessagesComposite.saveTableState(memento);
	}
	
}
