package org.marketcetera.photon.views;

import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.Pair;
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.ui.TextContributionItem;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.NoUnderlyings;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import ca.odell.glazedlists.BasicEventList;

/**
 * Option market data view.
 * 
 * @author caroline.leung@softwaregoodness.com
 */
public class OptionMarketDataView extends ViewPart implements
		IMSymbolListener, IMarketDataListener{

	public static final String ID = "org.marketcetera.photon.views.OptionMarketDataView";

	private UnderlyingSymbolInfoComposite underlyingSymbolInfoComposite;
	
	private OptionMessagesComposite optionMessagesComposite;
	
	private IMemento viewStateMemento; 

	private FormToolkit formToolkit;

	private ScrolledForm form = null;
	
	private IToolBarManager toolBarManager;
	private Clipboard clipboard;
	private CopyMessagesAction copyMessagesAction;

	private TextContributionItem symbolEntryText;

	private MarketDataFeedTracker marketDataTracker;

	private ISubscription optionListRequest;

	private ISubscription optionMarketDataSubscription;

	public OptionMarketDataView() {
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

		marketDataTracker.setMarketDataListener(this);
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);		
		this.viewStateMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);		
		optionMessagesComposite.saveTableState(memento);
	}

	@Override
	public void createPartControl(Composite parent) {
		createForm(parent);
		underlyingSymbolInfoComposite = new UnderlyingSymbolInfoComposite(
				form.getBody());
		underlyingSymbolInfoComposite
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());

		optionMessagesComposite = new OptionMessagesComposite(form.getBody(), getSite(), viewStateMemento);
		GridData tableGridData = createTopAlignedHorizontallySpannedGridData();
		tableGridData.grabExcessVerticalSpace = true;
		optionMessagesComposite.setLayoutData(tableGridData);		
		optionMessagesComposite.setInput(new BasicEventList<OptionMessageHolder>());
		
		toolBarManager = getViewSite().getActionBars().getToolBarManager();
		initializeToolBar(toolBarManager);	
		
		copyMessagesAction = new CopyMessagesAction(getClipboard(), optionMessagesComposite.getMessageTable(), "Copy");
		IWorkbench workbench = PlatformUI.getWorkbench();
		ISharedImages platformImages = workbench.getSharedImages();
		copyMessagesAction.setImageDescriptor(platformImages .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyMessagesAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		
		Object menuObj = optionMessagesComposite.getData(MenuManager.class.toString());
		if (menuObj != null && menuObj instanceof MenuManager) {
			MenuManager menuManager = (MenuManager) menuObj;
			menuManager.add(copyMessagesAction);
		}
		hookGlobalActions();
	}

	/**
	 * This method initializes formToolkit
	 * 
	 * @return org.eclipse.ui.forms.widgets.FormToolkit
	 */
	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	private void createForm(Composite top) {
		form = getFormToolkit().createScrolledForm(top);
		form.setLayout(createBasicGridLayout(1));
		form.getBody().setLayout(createBasicGridLayout(1));
		form.getBody().setLayoutData(
				createTopAlignedHorizontallySpannedGridData());
	}

	private GridData createTopAlignedHorizontallySpannedGridData() {
		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = GridData.FILL;
//		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = GridData.FILL;
		return formGridData;
	}

	private GridLayout createBasicGridLayout(int numColumns) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		return gridLayout;
	}

	@Override
	public void dispose() {
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
		underlyingSymbolInfoComposite.dispose();
		optionMessagesComposite.dispose();
		if (clipboard != null)
			clipboard.dispose();
		super.dispose();
	}

	private void hookGlobalActions() {
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.COPY.getId(), copyMessagesAction);
	}

	protected Clipboard getClipboard()
	{
		if (clipboard == null){
			clipboard = new Clipboard(getSite().getShell().getDisplay());
		}
		return clipboard;
	}

	// todo: This duplicates code from MarketDataView.
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		symbolEntryText = new TextContributionItem("");
		if(marketDataTracker.getMarketDataFeedService() == null) {
			symbolEntryText.setEnabled(false);
		} else {
			FeedStatus feedStatus = marketDataTracker.getMarketDataFeedService().getFeedStatus();
			updateSymbolEntryTextFromFeedStatus(feedStatus);
		}
		marketDataTracker.addFeedEventListener(new MarketDataFeedTracker.FeedEventListener() {
			public void handleEvent(FeedStatus status) {
				if(symbolEntryText == null) {
					return;
				}
				updateSymbolEntryTextFromFeedStatus(status);
			}
		});
		
		theToolBarManager.add(symbolEntryText);
		theToolBarManager.add(new ShowSymbolAction(symbolEntryText, this));
		theToolBarManager.add(new ShowSymbolInNewViewAction(getSite().getWorkbenchWindow(), ID, symbolEntryText));
	}
	
	// todo: This duplicates code from MarketDataView.
	private void updateSymbolEntryTextFromFeedStatus(FeedStatus status) {
		if(symbolEntryText == null 
			|| symbolEntryText.isDisposed()) {
			return;
		}
		if(status == FeedStatus.AVAILABLE) {
			symbolEntryText.setEnabled(true);
		} else {
			symbolEntryText.setEnabled(false);
		}
	}

	/**
	 * Perform one of two tasks here. 
	 * 1. Update the underlying info on top if matching the underlying symbol 
	 * 2. Update the call or put side in the MessagesTable if matching put/call contract in the table row
	 */
	private void onMessageImpl(Message quote) {
		if (optionListRequest != null && optionListRequest.isResponse(quote)){
			optionMessagesComposite.handleDerivativeSecuritiyList(quote, marketDataTracker);
			optionListRequest = null;
		} else if (underlyingSymbolInfoComposite.matchUnderlyingSymbol(quote)) {
			underlyingSymbolInfoComposite.onQuote(quote);
			return;
		} else {
			optionMessagesComposite.handleQuote(quote);
		}
	}

	private void onMessageImpl(Message [] messages){
		for (Message message : messages) {
			onMessageImpl(message);
		}
	}
	
	public void onMessage(final Message aQuote) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			onMessageImpl(aQuote);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					if (!optionMessagesComposite.getMessagesViewer().getTable().isDisposed())
						onMessageImpl(aQuote);
				}
			});
		}
	}

	public void onMessages(final Message[] messages) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			onMessageImpl(messages);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					if (!optionMessagesComposite.getMessagesViewer().getTable().isDisposed())
						onMessageImpl(messages);
				}
			});
		}
    }

	public void onAssertSymbol(MSymbol symbol) {
		if (symbol == null || symbol.getBaseSymbol().length() <= 0) {
			return;
		}

		// Request for new option symbol
		if (!underlyingSymbolInfoComposite.hasSymbol(symbol)) {
			
			updateTitleFromSymbol(symbol);			
			if (underlyingSymbolInfoComposite.hasUnderlyingSymbolInfo()) {
				// Clear all existing option symbol data
				underlyingSymbolInfoComposite.removeUnderlyingSymbol();
			}
			// Add the requested new option symbol
			underlyingSymbolInfoComposite.addUnderlyingSymbolInfo(symbol
					.getBaseSymbol());
		}
		renewSubscriptions(symbol);
		optionMessagesComposite.getMessagesViewer().refresh();
	}

	private void renewSubscriptions(MSymbol symbol) {
		try {
			// Step 1 - unsubscribe all subscribed market data, if there is any
			// Step 2 - subscribe to the underlying symbol
			// Step 3 - retrieve and subscribe to all put/call options
			unsubscribeAllMarketData();
			marketDataTracker.simpleSubscribe(symbol);
			requestOptionSecurityList(symbol);
			requestOptionMarketData(symbol);
		} catch (MarketceteraException e) {
			PhotonPlugin.getMainConsoleLogger().error(
					"Exception subscribing to market data for " + symbol);
		}
	}

	private void requestOptionMarketData(MSymbol root) throws MarketceteraException {
		Message subscribeMessage = MarketDataUtils.newSubscribeOptionUnderlying(root);
		MarketDataFeedService marketDataFeed = marketDataTracker.getMarketDataFeedService();
		if (marketDataFeed != null){
			optionMarketDataSubscription = marketDataFeed.subscribe(subscribeMessage);
		}

	}
	

	private String getTitlePrefix() {
		return "Options: ";
	}
	
	private void updateTitleFromSymbol(MSymbol symbol) {
		if(symbol == null) {
			return;
		}
		String symbolStr = symbol.getFullSymbol();
		if(symbolStr != null && symbolStr.trim().length() > 0) {
			String partName = getTitlePrefix() + symbolStr;
			setPartName(partName);
		}
	}

	private void unsubscribeAllMarketData() {
		// retrieve all related contract symbols, unsubscribe and remove them
		MarketDataFeedService service = (MarketDataFeedService) marketDataTracker
				.getService();
		if (service == null) {
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}
		
		// remove and unsubscribe underlying symbols and all contracts
		Set<String> subscribedUnderlyingSymbols = underlyingSymbolInfoComposite
				.getUnderlyingSymbolInfoMap().keySet();
		for (String subscribedUnderlyingSymbol : subscribedUnderlyingSymbols) {
			// unsubscribe and remove the underlying symbol
			MSymbol symbol = service.symbolFromString(subscribedUnderlyingSymbol);
			marketDataTracker.simpleUnsubscribe(symbol);
		}
		optionMessagesComposite.unlistenAllMarketData(marketDataTracker);
		try {
			service.unsubscribe(optionMarketDataSubscription);
		} catch (MarketceteraException e) {
		}
	}

	protected void requestOptionSecurityList(final MSymbol symbol) throws MarketceteraException {
		MarketDataFeedService service = marketDataTracker.getMarketDataFeedService();

		Message query = null;

		//Returns a query for all option contracts for the underlying symbol 
		//symbol = underlyingSymbol  (e.g. MSFT)
		query = OptionMarketDataUtils.newRelatedOptionsQuery(symbol);

		optionListRequest = service.getMarketDataFeed().asyncQuery(query);
	}


	@Override
	public void setFocus() {
		
	}

	public boolean isListeningSymbol(MSymbol symbol) {
		return underlyingSymbolInfoComposite.hasSymbol(symbol);
	}
}
