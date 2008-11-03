package org.marketcetera.photon.views;

import java.util.HashSet;
import java.util.Iterator;
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
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import ca.odell.glazedlists.BasicEventList;

/* $License$ */

/**
 * Option market data view.
 * 
 * @author caroline.leung@softwaregoodness.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OptionMarketDataView
    extends ViewPart
    implements IMSymbolListener, ISubscriber, Messages
{

	public static final String ID = "org.marketcetera.photon.views.OptionMarketDataView"; //$NON-NLS-1$

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

	private final Set<IMarketDataFeedToken<?>> tokens = new HashSet<IMarketDataFeedToken<?>>();

	public OptionMarketDataView() {
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

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

		optionMessagesComposite = new OptionMessagesComposite(form.getBody(), getSite(), viewStateMemento, false);
		GridData tableGridData = createTopAlignedHorizontallySpannedGridData();
		tableGridData.grabExcessVerticalSpace = true;
		optionMessagesComposite.setLayoutData(tableGridData);		
		optionMessagesComposite.setInput(new BasicEventList<OptionMessageHolder>());
		
		toolBarManager = getViewSite().getActionBars().getToolBarManager();
		initializeToolBar(toolBarManager);	
		
		copyMessagesAction = new CopyMessagesAction(getClipboard(),
		                                            optionMessagesComposite.getMessageTable(),
		                                            COPY_LABEL.getText());
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
		symbolEntryText = new TextContributionItem(""); //$NON-NLS-1$
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
	private void onMessageImpl(Message message) {
		if (FIXMessageUtil.isDerivativeSecurityList(message)) {
			optionMessagesComposite.handleDerivativeSecuritiyList(message, marketDataTracker);
		} else if (underlyingSymbolInfoComposite.matchUnderlyingSymbol(message)) {
			underlyingSymbolInfoComposite.onQuote(message);
			return;
		} else if (FIXMessageUtil.isMarketDataIncrementalRefresh(message)
				|| FIXMessageUtil.isMarketDataSnapshotFullRefresh(message)){
			optionMessagesComposite.handleQuote(message);
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


	public void onAssertSymbol(MSymbol symbol) {
		if (symbol == null || symbol.getBaseSymbol().length() <= 0) {
			return;
		}

		// Request for new option symbol
		if (!underlyingSymbolInfoComposite.hasSymbol(symbol)) {
			
			updateTitleFromSymbol(symbol);			
			if (underlyingSymbolInfoComposite.hasUnderlyingSymbolInfo()) {
				// Clear all existing option symbol data
				unsubscribeAllMarketData();
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
			doSubscribe(symbol);
			requestOptionSecurityList(symbol);
			requestOptionMarketData(symbol);
		} catch (CoreException e) {
			PhotonPlugin.getMainConsoleLogger().error(CANNOT_SUBSCRIBE_TO_MARKET_DATA.getText(symbol));
		}
	}

	private void requestOptionMarketData(MSymbol root) throws CoreException {
		Message subscribeMessage = MarketDataUtils.newSubscribeOptionUnderlying(root);
		MarketDataFeedService<?> marketDataFeed = marketDataTracker.getMarketDataFeedService();
		if (marketDataFeed != null){
			tokens.add(marketDataFeed.execute(subscribeMessage, new ISubscriber() {

				public boolean isInteresting(Object arg0) {
					return true;
				}

				public void publishTo(Object arg0) {
					optionMessagesComposite.handleQuote((Message) arg0);
				}
				
			}));
		}
	}

	private void doSubscribe(MSymbol symbol) {
		MarketDataFeedService<?> service = (MarketDataFeedService<?>)marketDataTracker.getService();
		try {
			if (service == null){
				PhotonPlugin.getMainConsoleLogger().warn(MISSING_QUOTE_FEED.getText());
			} else {
				service.execute(MarketDataUtils.newSubscribeBBO(symbol), this);
			}
		} catch (CoreException e) {
			PhotonPlugin.getMainConsoleLogger().warn(CANNOT_SUBSCRIBE_TO_MARKET_DATA.getText(symbol));
		}
	}


	private void requestOptionSecurityList(final MSymbol symbol) throws CoreException {
		PhotonPlugin.getMainConsoleLogger().debug("Requesting options for underlying: " + symbol); //$NON-NLS-1$
		MarketDataFeedService<?> service = marketDataTracker.getMarketDataFeedService();
		//Returns a query for all option contracts for the underlying symbol 
		//symbol = underlyingSymbol  (e.g. MSFT)
		Message query = OptionMarketDataUtils.newRelatedOptionsQuery(symbol);

		IMarketDataFeedToken<?> token = service.execute(query, new ISubscriber(){
			public boolean isInteresting(Object arg0) {
				return true;
			}

			public void publishTo(Object message) {
				optionMessagesComposite.handleDerivativeSecuritiyList((Message) message, marketDataTracker);
			}
		});
		tokens.add(token);
	}
	

	private String getTitlePrefix() {
		return OPTIONS_LABEL.getText();
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
		Iterator<IMarketDataFeedToken<?>> iter = tokens .iterator();
		while (iter.hasNext()){
			iter.next().unsubscribe(this);
			iter.remove();
		}
		optionMessagesComposite.clear();
	}

	@Override
	public void setFocus() {
		if(symbolEntryText.isEnabled())
			symbolEntryText.setFocus();
	}

	public boolean isListeningSymbol(MSymbol symbol) {
		return underlyingSymbolInfoComposite.hasSymbol(symbol);
	}

	public boolean isInteresting(Object arg0) {
		return true;
	}

	public void publishTo(Object obj) {
	    throw new UnsupportedOperationException("This needs to be fixed");
//		Message message;
//		if (obj instanceof SymbolExchangeEvent){
//			message = ((SymbolExchangeEvent) obj).getLatestTick();
//		} else {
//			message = (Message) obj;
//		}
//		onMessage(message);
	}
}
