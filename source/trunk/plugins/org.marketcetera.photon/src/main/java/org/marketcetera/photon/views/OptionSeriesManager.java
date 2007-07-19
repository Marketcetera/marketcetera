package org.marketcetera.photon.views;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.IMarketDataListCallback;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.ui.ToggledListener;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.MsgType;
import quickfix.field.NoRelatedSym;
import quickfix.field.SecurityReqID;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;

/**
 * @author michael.lossos@softwaregoodness.com
 */
public class OptionSeriesManager implements IMarketDataListCallback {
	IOptionOrderTicket ticket;
	
	/**
	 * Map from option root symbol to cache entry.
	 */
	private HashMap<String, OptionSeriesCollection> optionContractCache = new HashMap<String, OptionSeriesCollection>();

	private HashMap<MSymbol, OptionContractData> symbolToContractMap = new HashMap<MSymbol, OptionContractData>();

	private String lastOptionRoot;

	private List<String> putOrCallComboChoices = new ArrayList<String>();

	private List<ToggledListener> optionSpecifierModifyListeners;
	
	private ToggledListener optionContractSymbolModifyListener;

	private MarketDataFeedTracker marketDataTracker;

	/**
	 * When the user requests an option contract symbol that we don't yet have
	 * cached, we need to remember it for when the market data for it arrives.
	 * This will be "MSQ+HB" for example.
	 */
	private String lastUncachedOptionContractSymbol;

	private ISubscription lastSubscription;
	
	public OptionSeriesManager(IOptionOrderTicket ticket) {
		this.ticket = ticket;
		initPutOrCallComboChoices();
		marketDataTracker = new MarketDataFeedTracker(
				PhotonPlugin.getDefault().getBundleContext());
		marketDataTracker.open();
		initListeners();
	}

	public void initListeners(){
		optionSpecifierModifyListeners = new ArrayList<ToggledListener>();
		addOptionSeriesInfoModifyListener(ticket.getExpireYearCombo());
		addOptionSeriesInfoModifyListener(ticket.getExpireMonthCombo());
		addOptionSeriesInfoModifyListener(ticket.getStrikePriceControl());
		addOptionSeriesInfoModifyListener(ticket.getPutOrCallCombo());
	
		/**
		 * This modify listener ensures that the option contract specifiers
		 * (month, year, strike, put/call) stay in sync when the user enters an
		 * option contract symbol (MSQ+HA) directly.
		 */
		optionContractSymbolModifyListener = new ToggledListener() {
			@Override
			protected void handleEventWhenEnabled(Event event) {
				try {
					setOptionSeriesInfoListenersEnabled(false);
					updateOptionSeriesInfoForSymbol(ticket.getOptionSymbolControl().getText());
				} finally {
					setOptionSeriesInfoListenersEnabled(true);
				}
			}
		};
		optionContractSymbolModifyListener.setEnabled(true);
		ticket.getOptionSymbolControl().addListener(SWT.Modify,
				optionContractSymbolModifyListener);
		ticket.getSymbolText().addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				String optionRoot = ((Text)e.getSource()).getText();
				String optionContractSymbol = null;
				if (OptionMarketDataUtils.isOptionSymbol(optionRoot)){
					optionContractSymbol = optionRoot;
					optionRoot = OptionMarketDataUtils.getOptionRootSymbol(optionRoot);
					// What the user enters in the "Symbol/Root" field should never be changed by the application 
					//ticket.getSymbolText().setText(optionRoot);
				}
				requestOptionRootInfo(optionRoot, optionContractSymbol);
				
				// Looks like the following code was causing: http://trac.marketcetera.org/trac.fcgi/ticket/327
//				try {
//					optionContractSymbolModifyListener.setEnabled(false);
//					ticket.getOptionSymbolControl().setText(requestSymbol);
//				} finally {
//					optionContractSymbolModifyListener.setEnabled(true);
//				}
			}
		});
	}
	
	
	/**
	 * Update the option series details (expiration etc.) based on the
	 * option contract symbol (e.g. MSQ+GE).
	 */
	private void updateOptionSeriesInfoForSymbol(String optionSymbolText) {
		if (optionSymbolText == null
				|| optionSymbolText.length() == 0) {
			return;
		}
		MSymbol optionContractSymbol = new MSymbol(
				optionSymbolText);
		if (symbolToContractMap.containsKey(optionContractSymbol)) {
			OptionContractData optionInfo = symbolToContractMap.get(optionContractSymbol);
			if (optionInfo != null) {
				String expirationYear = optionInfo.getExpirationYearUIString();
				ticket.getExpireYearCombo().setText(expirationYear);
				String expirationMonth = optionInfo.getExpirationMonthUIString();
				ticket.getExpireMonthCombo().setText(expirationMonth);
				String strikePrice = optionInfo.getStrikePriceUIString();
				ticket.getStrikePriceControl().setText(strikePrice);
				Integer optionIsPut = optionInfo.getPutOrCall();
				ticket.setPutOrCall(optionIsPut);
			}
		}
	}

	/**
	 * Update the option symbol (e.g. MSQ+GE) based on the option specifiers
	 * (expiration etc.)
	 */
	private boolean updateOptionSymbol(
			OptionSeriesCollection cacheEntry) {
		String expirationYear = ticket.getExpireYearCombo().getText();
		String expirationMonth = ticket.getExpireMonthCombo().getText();
		String strikePrice = ticket.getStrikePriceControl().getText();
		Integer putOrCall = ticket.getPutOrCall();
		String optionSymbolOrRoot = ticket.getSymbolText().getText();

		boolean textWasSet = false;
		OptionContractData optionContract;
		if (OptionMarketDataUtils.isOptionSymbol(optionSymbolOrRoot)){
			optionContract = symbolToContractMap.get(optionSymbolOrRoot);
		} else {
			optionContract = cacheEntry.getOptionContractData(
					optionSymbolOrRoot, expirationYear, expirationMonth, strikePrice, putOrCall);
		}
		if (optionContract != null) {
			MSymbol optionContractSymbol = optionContract.getOptionSymbol();
			if (optionContractSymbol != null) {
				String fullSymbol = optionContractSymbol.getFullSymbol();
				try {
					optionContractSymbolModifyListener.setEnabled(false);
					ticket.getOptionSymbolControl().setText(fullSymbol);
					textWasSet = true;
				} finally {
					optionContractSymbolModifyListener.setEnabled(true);
				}
			}
		}
		if (!textWasSet) {
			clearOptionSymbolControl();
		}
		return textWasSet;
	}

	/**
	 * Only update the option contract symbol (e.g. MSQ+GE) based on the option
	 * info (expiration etc.) if there is a mapping available for this
	 * option series. If not, clears the option contract symbol text.
	 */
	public void updateOptionSymbolFromLocalCache() {
		String symbolText = ticket.getSymbolText().getText();
		if (symbolText != null) {
			OptionSeriesCollection cacheEntry = optionContractCache
					.get(symbolText);
			if (cacheEntry != null) {
				updateOptionSymbol(cacheEntry);
			}
		}
	}

	private void requestOptionRootInfo(String optionRoot, String optionContractSymbol) {

		if (!optionContractCache.containsKey(optionRoot)) {
			PhotonPlugin.getMainConsoleLogger().debug("Requesting option root info for :"+optionRoot+"("+optionContractSymbol+")");

			MarketDataFeedService service = marketDataTracker.getMarketDataFeedService();

			// Remember the option contract e.g. "MSQ+HB" so that when the
			// market data arrives later, we can update the combo choices
			// accordingly.
			lastUncachedOptionContractSymbol = optionContractSymbol;
			
			Message query = OptionMarketDataUtils.newOptionRootQuery(optionRoot);
			try {
				synchronized (this){
					lastSubscription = service.getMarketDataFeed().asyncQuery(query);
				}
			} catch (MarketceteraException e) {
				PhotonPlugin.getDefault().getMarketDataLogger().error("Exception getting market data: "+e);
			}
		} else {
			conditionallyUpdateInputControls(optionRoot, optionContractSymbol);
		}
	}
	
	// todo: Remove this method if it remains unused 
//	private void requestOptionInfoForUnderlying() {
//		MarketDataFeedService service = marketDataTracker.getMarketDataFeedService();
//
//		Message query = null;
//
//		//Returns a query for all option contracts for the underlying symbol 
//		//symbol = underlyingSymbol  (e.g. MSFT)
//		query = OptionMarketDataUtils.newRelatedOptionsQuery(selectedSymbol);
//
//		try {
//			service.getMarketDataFeed().asyncQuery(query);
//		} catch (MarketceteraException e) {
//			PhotonPlugin.getDefault().getMarketDataLogger().error("Exception getting market data: "+e);
//		}
//	}

	/**
	 * This method must be called from the UI thread.
	 */
	private void handleMarketDataList(Message derivativeSecurityList) {
		String optionRoot = null;
		Logger mainConsoleLogger = PhotonPlugin.getMainConsoleLogger();
		try {
			optionRoot = derivativeSecurityList.getString(UnderlyingSymbol.FIELD);
			
			if (mainConsoleLogger.isDebugEnabled()){
				String reqID = "";
				try { reqID = derivativeSecurityList.getString(SecurityReqID.FIELD); } catch (FieldNotFound fnf) {}
				mainConsoleLogger.debug("Received derivative list for "+optionRoot+" ("+reqID+")");
			}
			// This old code assumed the derivativeSecurityList coming back 
			// was for the same underlying symbol as the current SymbolText.
			//
//	        String symbolText = ticket.getSymbolText().getText();
//	        if (OptionMarketDataUtils.isOptionSymbol(symbolText)){
//	        	symbolText = OptionMarketDataUtils.getOptionRootSymbol(symbolText);
//	        }
			handleMarketDataList(derivativeSecurityList, optionRoot);
		}
		catch(FieldNotFound anyException) {
			mainConsoleLogger.debug("Failed to find underlying symbol in DerivativeSecurityList: " + derivativeSecurityList, anyException);
		}
    }
	
	/**
	 * This method must be called from the UI thread.
	 * todo: Add check.
	 */
	private void handleMarketDataList(Message derivativeSecurityList, String optionRootToDisplay) {
        List<OptionContractData> optionContracts = new ArrayList<OptionContractData>();
        
        try {
            optionContracts = getOptionExpirationMarketData(derivativeSecurityList);
        } catch (Exception anyException) {
            PhotonPlugin.getMainConsoleLogger().warn("Error getting market data - ", anyException);
            return;
        }
        if (optionContracts != null) {
        	// Always cache the new contract market data, even if it's not what
			// was requested.
        	OptionSeriesCollection cacheEntry = new OptionSeriesCollection(
                    optionContracts);
        	Set<String> optionRoots = cacheEntry.getOptionRoots();
        	for (String optionRoot : optionRoots) {
            	optionContractCache.put(optionRoot, cacheEntry);
			}
            for (OptionContractData optionContractData : optionContracts) {
            	symbolToContractMap.put(
            			optionContractData.getOptionSymbol(),
            			optionContractData);
			}

            boolean doUpdateInputControls = false;
            // todo: This method must always be called from the UI thread, so
			// this doesn't need to be synchronized.
			synchronized (this) {
				// Only update the combo boxes if the option contracts match
				// what was last requested.
				if (lastSubscription != null
						&& lastSubscription.isResponse(derivativeSecurityList)) {
					doUpdateInputControls = true;
					lastSubscription = null;
				}
			}
			
			if (doUpdateInputControls) {
				conditionallyUpdateInputControls(optionRootToDisplay,
						lastUncachedOptionContractSymbol);
				lastUncachedOptionContractSymbol = null;
			}
        }
    }
    
	/**
	 * @throws MarketceteraFIXException
	 */
	private List<OptionContractData> getOptionExpirationMarketData(
			Message derivativeSecurityListMessage) 
			throws MarketceteraException {
		List<OptionContractData> optionExpirations = new ArrayList<OptionContractData>();
		if(derivativeSecurityListMessage == null) {
			return optionExpirations;
		}
		String messageUnderlyingSymbolStr = "";
		try {
			if (FIXMessageUtil.isDerivativeSecurityList(derivativeSecurityListMessage)) {
				messageUnderlyingSymbolStr = derivativeSecurityListMessage
						.getString(UnderlyingSymbol.FIELD);
//				if (isApplicableUnderlyingSymbol(
//						messageUnderlyingSymbolStr, symbolFilter)) {
					MSymbol messageUnderlyingSymbol = new MSymbol(
							messageUnderlyingSymbolStr);
					addExpirationFromMessage(messageUnderlyingSymbol,
							derivativeSecurityListMessage, optionExpirations);
//				}
			} else {
				throw new MarketceteraFIXException(
						"FIX message was not a DerivativeSecurityList ("
								+ MsgType.DERIVATIVE_SECURITY_LIST + ").");
			}
		} catch (Exception anyException) {				
			throw new MarketceteraException(
					"Failed to get option contracts data for underlying symbol \""
							+ messageUnderlyingSymbolStr + "\" - "
							+ "\nProblematic message is : [" + derivativeSecurityListMessage
							+ "]", anyException);
		}
		return optionExpirations;
	}

	private void addExpirationFromMessage(MSymbol underlyingSymbol,
			Message message, List<OptionContractData> optionExpirations)
			throws FieldNotFound, MarketceteraFIXException {
		int numDerivs = 0;
		try {
			numDerivs = message.getInt(NoRelatedSym.FIELD);
		} catch (FieldNotFound fnf){
			// do nothing...
		}
		for (int index = 1; index <= numDerivs; index++) {
			DerivativeSecurityList.NoRelatedSym optionGroup = new DerivativeSecurityList.NoRelatedSym();
			message.getGroup(index, optionGroup);
			OptionContractData optionData;
			try {
				optionData = OptionContractData.fromFieldMap(underlyingSymbol, optionGroup);
				optionExpirations.add(optionData);
			} catch (ParseException e) {
				PhotonPlugin.getDefault().getMarketDataLogger().info(e.getLocalizedMessage());
			}
		}
	}

//	private static boolean isApplicableUnderlyingSymbol(
//			String messageUnderlyingSymbolStr, String symbolFilter) {
//		if (messageUnderlyingSymbolStr != null) {
//			if (symbolFilter == null || symbolFilter.trim().length() == 0
//					|| messageUnderlyingSymbolStr.startsWith(symbolFilter)) {
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * @param optionRoot
	 *            the option root must always be specified, for example "MSQ"
	 * @param optionContractSymbol
	 *            the option contract can optionall be specified, for example
	 *            "MSQ+HB". This results in the combo choices being updated to
	 *            reflect the specific option contract.
	 */
    private void conditionallyUpdateInputControls(String optionRoot,
			String optionContractSymbol) {
		if (optionRoot == null) {
			return;
		}
		boolean doUpdate = lastOptionRoot == null
				|| !lastOptionRoot.equals(optionRoot);
		if (optionContractSymbol != null) {
			doUpdate = true;
		}
		if (doUpdate) {
			lastOptionRoot = optionRoot;
			OptionSeriesCollection cacheEntry = optionContractCache
					.get(optionRoot);
			if (cacheEntry != null) {
				updateComboChoices(cacheEntry, optionContractSymbol);
				updateOptionSymbol(cacheEntry);
			}
		}
	}
    
//	private void updateComboChoicesFromDefaults() {
//		OptionDateHelper dateHelper = new OptionDateHelper();
//		List<String> months = dateHelper.createDefaultMonths();
//		updateComboChoices(ticket.getExpireMonthCombo(), months);
//		List<String> years = dateHelper.createDefaultYears();
//		updateComboChoices(ticket.getExpireYearCombo(), years);
//		// todo: What should the defaults be for strike price?
//		List<String> strikePrices = new ArrayList<String>();
//		updateComboChoices(ticket.getStrikePriceControl(), strikePrices);
//	}

	private void updateComboChoices(Combo combo, Collection<String> choices) {
		if (combo == null || combo.isDisposed()) {
			return;
		}
		String originalText = combo.getText();
		String newText = "";
		combo.removeAll();
//		boolean first = true;
		for (String choice : choices) {
			if (choice != null) {
				combo.add(choice);
//				// Use the first choice if none match the original text.
//				if (first) {
//					newText = choice;
//					first = false;
//				}
				if (choice.equals(originalText)) {
					newText = choice;
				}
			}
		}
		combo.setText(newText);
		if (combo.isFocusControl()) {
			combo.setSelection(new Point(0, 3));
		}
	}

	/**
	 * @param optionContractSymbol
	 *            can be null. When non-null, the current combo choices are
	 *            updated to reflect the contract symbol.
	 */
	private void updateComboChoices(OptionSeriesCollection cacheEntry, String optionContractSymbol) {
		try {
			setOptionSeriesInfoListenersEnabled(false);
			updateComboChoices(ticket.getExpireMonthCombo(), cacheEntry
					.getExpirationMonthsForUI());
			updateComboChoices(ticket.getExpireYearCombo(), cacheEntry
					.getExpirationYearsForUI());
			updateComboChoices(ticket.getStrikePriceControl(), cacheEntry
					.getStrikePricesForUI());
			updateComboChoices(ticket.getPutOrCallCombo(),
					putOrCallComboChoices);
			
			// Assign the current combo choice to match an option contract such as "MSQ+HB"
			if (optionContractSymbol != null
					&& optionContractSymbol.length() > 0) {
				
				OptionContractData contractData = cacheEntry
						.getOptionInfoForSymbol(new MSymbol(
								optionContractSymbol));
				if (contractData != null) {
					assignComboTextIfNonEmpty(ticket.getExpireMonthCombo(),
							contractData.getExpirationMonthUIString());
					assignComboTextIfNonEmpty(ticket.getExpireYearCombo(),
							contractData.getExpirationYearUIString());
					assignComboTextIfNonEmpty(ticket.getStrikePriceControl(),
							contractData.getStrikePriceUIString());
					ticket.setPutOrCall(contractData.getPutOrCall());
				}
			}
		} finally {
			setOptionSeriesInfoListenersEnabled(true);
		}
	}
	
	private void assignComboTextIfNonEmpty(Combo whichCombo, String text) {
		if(text == null || text.length() == 0) {
			return;
		}
		whichCombo.setText(text);
	}


	private void clearOptionSymbolControl() {
		ticket.getOptionSymbolControl().setText(ticket.getSymbolText().getText());
	}


	private void addOptionSeriesInfoModifyListener(Control targetControl) {
		ToggledListener modifyListener = new ToggledListener() {
			public void handleEventWhenEnabled(Event event) {
				try {
					setOptionSeriesInfoListenersEnabled(false);
					updateOptionSymbolFromLocalCache();
				} finally {
					setOptionSeriesInfoListenersEnabled(true);
				}
			}
		};
		optionSpecifierModifyListeners.add(modifyListener);
		targetControl.addListener(SWT.Modify, modifyListener);
	}

	private void setOptionSeriesInfoListenersEnabled(boolean enabled) {
		for (ToggledListener listener : optionSpecifierModifyListeners) {
			listener.setEnabled(enabled);
		}
	}

	private void initPutOrCallComboChoices() {
		Combo combo = ticket.getPutOrCallCombo();
		String[] items = combo.getItems();
		if(items != null) {
			putOrCallComboChoices = Arrays.asList(items);
		}
	}

	public void clear() {
		lastOptionRoot = null;
	}

	public void onMarketDataFailure(MSymbol symbol) {
	}

	public void onMessage(Message message) {
		handleMarketDataList(message);
	}

	public void onMessages(Message[] messages) {
		for (Message message : messages) {
			handleMarketDataList(message);
		}
	}

}
