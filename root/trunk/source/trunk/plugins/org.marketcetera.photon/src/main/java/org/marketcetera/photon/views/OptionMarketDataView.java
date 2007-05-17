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
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.ui.TextContributionItem;

import quickfix.Message;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * Option market data view.
 * 
 * @author caroline.leung@softwaregoodness.com
 */
public class OptionMarketDataView extends ViewPart implements
		IMSymbolListener {

	public static final String ID = "org.marketcetera.photon.views.OptionMarketDataView";

	private UnderlyingSymbolInfoComposite underlyingSymbolInfoComposite;
	
	private OptionMessagesComposite optionMessagesComposite;
	
	private IMemento viewStateMemento; 

	private FormToolkit formToolkit;

	private ScrolledForm form = null;
	
	private IToolBarManager toolBarManager;
	private Clipboard clipboard;
	private CopyMessagesAction copyMessagesAction;


	private MarketDataFeedTracker marketDataTracker;

	public OptionMarketDataView() {
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

		marketDataListener = new MDVMarketDataListener();
		marketDataTracker.setMarketDataListener(marketDataListener);
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

	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		TextContributionItem textContributionItem = new TextContributionItem("");
		theToolBarManager.add(textContributionItem);
		theToolBarManager.add(new AddSymbolAction(textContributionItem, this));
	}

	/**
	 * Perform one of two tasks here. 
	 * 1. Update the underlying info on top if matching the underlying symbol 
	 * 2. Update the call or put side in the MessagesTable if matching put/call contract in the table row
	 */
	private void updateQuote(Message quote) {
		
		if (underlyingSymbolInfoComposite.matchUnderlyingSymbol(quote)) {
			underlyingSymbolInfoComposite.onQuote(quote);
			return;
		}
		optionMessagesComposite.updateQuote(quote);	
	}

	public void onQuote(final Message aQuote) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			updateQuote(aQuote);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					if (!optionMessagesComposite.getMessagesViewer().getTable().isDisposed())
						updateQuote(aQuote);
				}
			});
		}
	}

	private MDVMarketDataListener marketDataListener;

	public void onAssertSymbol(MSymbol symbol) {
		addSymbol(symbol);
	}
	
	public void addSymbol(MSymbol symbol) {
		if (symbol == null || symbol.getBaseSymbol().length() <= 0) {
			return;
		}
		if (underlyingSymbolInfoComposite.hasSymbol(symbol)) {
			return; // do nothing, already subscribed
		}
		if (underlyingSymbolInfoComposite.hasUnderlyingSymbolInfo()) {
			removeUnderlyingSymbol();			
		}
		// Step 1 - subscribe to the underlying symbol
		// Step 2 - retrieve and subscribe to all put/call options 
		underlyingSymbolInfoComposite.addUnderlyingSymbolInfo(symbol
				.getBaseSymbol());

		try {
			marketDataTracker.simpleSubscribe(symbol);
			optionMessagesComposite.requestOptionSecurityList(marketDataTracker, symbol);

		} catch (MarketceteraException e) {
			PhotonPlugin.getMainConsoleLogger().error(
					"Exception subscribing to market data for " + symbol);
		}
		optionMessagesComposite.getMessagesViewer().refresh();
	}

	private void removeUnderlyingSymbol() {
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
		underlyingSymbolInfoComposite.removeUnderlyingSymbol(); 
		optionMessagesComposite.unsubscribeOptions(marketDataTracker);

		// clear out all maps and list
		EventList<OptionMessageHolder> list = optionMessagesComposite.getInput();
		list.clear();
		optionMessagesComposite.clearDataMaps();
		optionMessagesComposite.getMessagesViewer().refresh();
	}

	public class MDVMarketDataListener extends MarketDataListener {
		public void onMessage(Message aMessage) {
			OptionMarketDataView.this.onQuote(aMessage);
		}
	}

	@Override
	public void setFocus() {
		
	}

}
