package org.marketcetera.photon.views;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.Pair;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.NoRelatedSym;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityReqID;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;
/*
 * $License$
 */

/**
 * The Option Order Ticket View Controller
 * @version $Id$
 *
 */
@ClassVersion ("$Id$")
public class OptionOrderTicketController 
	 extends OrderTicketController<OptionOrderTicketModel> {

	/**
	 * Maps option roots and underlyings to a complete set of OptionContractData
	 */
	HashMap<String, List<OptionContractData>> receivedOptionRoots = new HashMap<String, List<OptionContractData>>();
	private IMarketDataFeedToken<?> currentOptionToken;
	private Logger logger;
	
	/**
	 * Create a new {@link OptionOrderTicketController} wrapping the given model.
	 * 
	 * @param model
	 */
	public OptionOrderTicketController(final OptionOrderTicketModel model) {
		super(model);
		
		clear();

		PhotonPlugin plugin = PhotonPlugin.getDefault();
		logger = plugin.getMainLogger();
		
//		String symbol = "ABC";
//		String[] callSuffixes = new String[] {"RE"};
//		String[] putSuffixes = new String[] {"GE"};
//		BigDecimal[] strikePrices = new BigDecimal[] {BigDecimal.TEN};
//		onMessage(_createDummySecurityList(symbol, callSuffixes, putSuffixes, strikePrices));
	}


	public void doOnOptionQuote(final Message message) 
	{
	    Runnable block = new Runnable() {
            public void run()
            {
                String symbol;
                OptionOrderTicketModel model = getOrderTicketModel();
                try {
                    symbol = message.getString(Symbol.FIELD);
                    WritableList mdList = model.getOptionMarketDataList();
                    for (int i = 0; i < mdList.size(); i++){
                        OptionMessageHolder holder = (OptionMessageHolder) mdList.get(i);
                        if (symbol.equals(holder.getSymbol(PutOrCall.PUT)) ||
                                symbol.equals(holder.getSymbol(PutOrCall.CALL))){
                            FieldMap marketDataForSymbol = holder.getMarketDataForSymbol(symbol);
                            if (marketDataForSymbol != null && FIXMessageUtil.isMarketDataIncrementalRefresh(message)){
                                FIXMessageUtil.mergeMarketDataMessages(message, 
                                        (Message)marketDataForSymbol, messageFactory);
                            } else {
                                marketDataForSymbol = message;
                            }
                            int putOrCall = holder.symbolOptionType(symbol);
                            holder.setMarketData(putOrCall, marketDataForSymbol);
                            // TODO: isn't there a better way to fire the changed event?
                            mdList.set(i, holder);
                        }
                    }
                } catch (FieldNotFound e) {
                    PhotonPlugin.getDefault().getMarketDataLogger().debug("Field missing from option quote", e);
                }
            }	        
	    };
        Display theDisplay = Display.getDefault();
        if (theDisplay.getThread() == Thread.currentThread()){
            block.run();
        } else { 
            theDisplay.asyncExec(block);
        }	    
	}

	public void doOnPrimaryQuote(Message message) {
		getOrderTicketModel().clearUnderlyingMarketData();

		OptionOrderTicketModel model = getOrderTicketModel();
		try {
			try {
				model.getUnderlyingSymbol().setValue(message.getString(Symbol.FIELD));
			} catch (FieldNotFound fnf) {}
			int noMDEntries = message.getInt(NoMDEntries.FIELD);
			for (int i = 1; i <= noMDEntries; i++){
				try {
					Group group = messageFactory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
					message.getGroup(i, group);
					char entryType = group.getChar(MDEntryType.FIELD);
					switch (entryType){
					case MDEntryType.BID:
						model.getUnderlyingBidPrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						model.getUnderlyingBidSize().setValue(group.getDecimal(MDEntrySize.FIELD));
						break;
					case MDEntryType.OFFER:
						model.getUnderlyingOfferPrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						model.getUnderlyingOfferSize().setValue(group.getDecimal(MDEntrySize.FIELD));
						break;
					case MDEntryType.TRADE:
						model.getUnderlyingLastPrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						model.getUnderlyingLastUpdated().setValue(group.getUtcTimeOnly(MDEntryTime.FIELD));
						break;
					case MDEntryType.TRADING_SESSION_HIGH_PRICE:
						model.getUnderlyingHighPrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						break;
					case MDEntryType.TRADING_SESSION_LOW_PRICE:
						model.getUnderlyingLowPrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						break;
					case MDEntryType.OPENING_PRICE:
						model.getUnderlyingOpenPrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						break;
					case MDEntryType.CLOSING_PRICE:
						model.getUnderlyingPreviousClosePrice().setValue(group.getDecimal(MDEntryPx.FIELD));
						break;
					case MDEntryType.TRADE_VOLUME:
						model.getUnderlyingVolume().setValue(group.getDecimal(MDEntrySize.FIELD));
						break;
					}
				} catch (FieldNotFound fnf) { /* invalid group */ }
			}
		} catch (FieldNotFound fnf) { /* missing NoMDEntries */ }
	}
	

	/**
	 * Cancel all subscriptions (stock and options).
	 */
	@Override
	protected void doUnlistenMarketData(MarketDataFeedService<?> service) throws MarketceteraException {
		super.doUnlistenMarketData(service);
		if (currentOptionToken != null){
			currentOptionToken.cancel();
			currentOptionToken = null;
		}
	}

	/**
	 * Listen for market data on stocks or options as appropriate.  If the submitted
	 * symbol is an option symbol, use the root as the subscription symbol, if it is not
	 * treat it as an underlying symbol.
	 * 
	 * If we previously had not received information for that option root/underlying, 
	 * request the descriptive data for the options.
	 * 
	 * Otherwise simply subscribe to updates for the market data.
	 */
	@Override
	protected void doListenMarketData(MarketDataFeedService<?> service, MSymbol symbol) throws MarketceteraException {
		String symbolString = symbol.toString();

		String optionRootOrUnderlying;
		if (OptionMarketDataUtils.isOptionSymbol(symbolString)){
			logger.debug("Is option symbol");
			optionRootOrUnderlying = OptionMarketDataUtils.getOptionRootSymbol(symbolString);
		} else {
			logger.debug("Treating as underlying...");
			optionRootOrUnderlying = symbolString;
		}
		
		if (!receivedOptionRoots.containsKey(symbolString)){
			requestOptionRootInfo(optionRootOrUnderlying);
		}

		String optionSymbolString = symbol.toString();
		if (receivedOptionRoots.containsKey(optionSymbolString)){
			MSymbol underlyingSymbol = receivedOptionRoots.get(optionSymbolString).get(0).getUnderlyingSymbol();
			super.doListenMarketData(service, underlyingSymbol);

			Message subscriptionMessage = MarketDataUtils.newSubscribeOptionUnderlying(underlyingSymbol);
			currentOptionToken = service.execute(subscriptionMessage, new ISubscriber() {
				public boolean isInteresting(Object arg0) {
					return true;
				}
				public void publishTo(Object obj) {
					Message message;
					if (obj instanceof SymbolExchangeEvent){
						message = ((SymbolExchangeEvent) obj).getFIXMessage();
					} else {
						message = (Message) obj;
					}
					doOnOptionQuote(message);
				}
			});

		}
	}
	


	/**
	 * Clear the order ticket message
	 */
	public void clear() {
		getOrderTicketModel().clearOrderMessage();
		getOrderTicketModel().clearUnderlyingMarketData();
	}


	public void requestOptionRootInfo(String optionRoot) throws FeedException {
		PhotonPlugin.getMainConsoleLogger().debug("Requesting option root info for :"+optionRoot);

		MarketDataFeedService<?> service = getMarketDataTracker().getMarketDataFeedService();

		Message query = OptionMarketDataUtils.newOptionRootQuery(optionRoot);

		service.execute(query, new ISubscriber() {
			public boolean isInteresting(Object arg0) {
				return true;
			}
			public void publishTo(Object obj) {
				Message message;
				if (obj instanceof SymbolExchangeEvent){
					message = ((SymbolExchangeEvent) obj).getFIXMessage();
				} else {
					message = (Message) obj;
				}

				handleDerivativeSecurityList(message);
			}
			
		});
	}

	
	/**
	 * Parses out the option specifier information in this message of type
	 * {@link MsgType#DERIVATIVE_SECURITY_LIST} into OptionContractData
	 * descriptors and add them to the model.
	 * 
	 * For historical reasons this parsing is done separately from the parsing
	 * done in {@link #handleDerivativeSecurityList(Message)}, but
	 * they could probably be combined.
	 * 
	 * @param derivativeSecurityList
	 */
	protected void addDerivativeSecurityListToCache(Message derivativeSecurityList) {
		String underlyingSymbol = null;
		Logger mainConsoleLogger = PhotonPlugin.getMainConsoleLogger();
		try {
			underlyingSymbol = derivativeSecurityList.getString(UnderlyingSymbol.FIELD);
			
			if (mainConsoleLogger.isDebugEnabled()){
				String reqID = "";
				try { reqID = derivativeSecurityList.getString(SecurityReqID.FIELD); } catch (FieldNotFound fnf) {}
				mainConsoleLogger.debug("Received derivative list for "+underlyingSymbol+" ("+reqID+")");
			}
	        Pair<Set<String>, List<OptionContractData>> optionRootsAndContracts;
	        
	        try {
	            optionRootsAndContracts = getOptionContractData(derivativeSecurityList);
	            Set<String> optionRoots = optionRootsAndContracts.getFirstMember();
	            List<OptionContractData> contractData = optionRootsAndContracts.getSecondMember();
	            
	            for (String optionRoot : optionRoots) {
	            		getOrderTicketModel().removeDataForOptionRoot(optionRoot);
	                	if (contractData.size() > 0){
	    					receivedOptionRoots.put(optionRoot, contractData);
	                	} else {
	                		receivedOptionRoots.remove(optionRoot);
	                	}
				}
				getOrderTicketModel().addOptionContractData(contractData);
	        } catch (Exception anyException) {
	            PhotonPlugin.getMainConsoleLogger().warn("Error getting market data - skipping", anyException);
	            return;
	        }
		}
		catch(FieldNotFound anyException) {
			mainConsoleLogger.debug("Failed to find underlying symbol in DerivativeSecurityList: " + derivativeSecurityList, anyException);
		}
    }

    
	/**
	 * Given a message of type {@link MsgType#DERIVATIVE_SECURITY_LIST}, 
	 * parse it into a set of strings representing the option roots,
	 * and a List of {@link OptionContractData} descriptors.
	 * 
	 * @param the message of type {@link MsgType#DERIVATIVE_SECURITY_LIST}
	 * @throws MarketceteraFIXException
	 */
	private Pair<Set<String>, List<OptionContractData>> getOptionContractData(
			Message derivativeSecurityListMessage) 
			throws MarketceteraException {

		List<OptionContractData> optionContractDataList = new ArrayList<OptionContractData>();
		Set<String> optionRootList = new HashSet<String>();

		if(derivativeSecurityListMessage != null) {
			String messageUnderlyingSymbolStr = "";
			if (FIXMessageUtil.isDerivativeSecurityList(derivativeSecurityListMessage)) {
				try {
					messageUnderlyingSymbolStr = derivativeSecurityListMessage
							.getString(UnderlyingSymbol.FIELD);
					MSymbol messageUnderlyingSymbol = new MSymbol(
							messageUnderlyingSymbolStr);
					getOptionContractDataFromMessage(messageUnderlyingSymbol,
							derivativeSecurityListMessage, optionContractDataList, optionRootList);
				} catch (Exception anyException) {
					throw new MarketceteraException(
							"Failed to get option contracts data for underlying symbol \""
									+ messageUnderlyingSymbolStr + "\" - "
									+ "\nProblematic message is : [" + derivativeSecurityListMessage
									+ "]", anyException);
				}
			} else {
				throw new MarketceteraFIXException(
						"FIX message was not a DerivativeSecurityList ("
								+ MsgType.DERIVATIVE_SECURITY_LIST + ").");
			}
		}
		return new Pair<Set<String>, List<OptionContractData>>(optionRootList, optionContractDataList);
	}

	/**
	 * Does the work of extracting the OptionContractData descriptors
	 * from the message of type {@link MsgType#DERIVATIVE_SECURITY_LIST}
	 * @param underlyingSymbol the underlying symbol for the options
	 * @param message the message containing the options descriptive data
	 * @param optionExpirations the list into which to put the option expirations
	 * @param optionRootSet the set into which to put the option roots
	 * @throws FieldNotFound if a required field is missing
	 * @throws MarketceteraFIXException 
	 */
	private void getOptionContractDataFromMessage(MSymbol underlyingSymbol,
			Message message, List<OptionContractData> optionExpirations, Set<String> optionRootSet)
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
				optionRootSet.add(optionData.getOptionRoot());
				optionExpirations.add(optionData);
			} catch (ParseException e) {
				PhotonPlugin.getDefault().getMarketDataLogger().info(e.getLocalizedMessage());
			}
		}
	}


	/**
	 * Parses out a message of type {@link MsgType#DERIVATIVE_SECURITY_LIST}
	 * creating a set of OptionMessageHolders.  The option message holders
	 * hold both a put and a call with identical option root, expiration, and 
	 * strike.  Therefore this message must correlate entries in the message
	 * to allow matching of put descriptors and call descriptors.  In general
	 * the number of OptionMessageHolders should be one half the number of
	 * option specifiers in the derivative security list
	 * 
	 * The OptionMessageHolders are used as the basis for displaying option
	 * market data to the user in the order ticket.
	 * 
	 * @param derivativeSecurityList the message containgin option specifier information
	 */
	public void handleDerivativeSecurityList(Message derivativeSecurityList){
		addDerivativeSecurityListToCache(derivativeSecurityList);
		
		WritableList optionHolderList = getOrderTicketModel().getOptionMarketDataList();

		MarketDataFeedService<?> feed = getMarketDataTracker().getMarketDataFeedService();
		optionHolderList.clear();
		try {
			int numDerivs = 0;
			if (derivativeSecurityList.isSetField(NoRelatedSym.FIELD)){
				numDerivs = derivativeSecurityList.getInt(NoRelatedSym.FIELD);
			}
			HashMap<OptionPairKey, OptionMessageHolder> optionContractMap = new HashMap<OptionPairKey, OptionMessageHolder>();
			HashMap<MSymbol, OptionPairKey> optionSymbolToKeyMap = new HashMap<MSymbol, OptionPairKey>();

			for (int i = 1; i <= numDerivs; i++)
			{
				try {
					DerivativeSecurityList.NoRelatedSym info = new DerivativeSecurityList.NoRelatedSym();
					derivativeSecurityList.getGroup(i, info);

					int putOrCall = OptionMarketDataUtils.getOptionType(info);
					String optionSymbolString = info.getString(Symbol.FIELD);
					MSymbol optionSymbol = feed.symbolFromString(optionSymbolString);
					OptionPairKey optionKey;
						optionKey = OptionPairKey.fromFieldMap(optionSymbol, info);
					optionSymbolToKeyMap.put(optionSymbol, optionKey);
					OptionMessageHolder holder;
					if (optionContractMap.containsKey(optionKey)){
						holder = optionContractMap.get(optionKey);
					} else {
						holder = new OptionMessageHolder(OptionMarketDataUtils.getOptionRootSymbol(optionSymbolString), info);
						optionContractMap.put(optionKey, holder);

						optionHolderList.add(holder);
					}						
					
					holder.setExtraInfo(putOrCall, info);
				} catch (ParseException e) {
					MSymbol underlying =feed.symbolFromString(derivativeSecurityList.getString(Symbol.FIELD));
					PhotonPlugin.getDefault().getMarketDataLogger().error("Exception parsing option info: "+underlying, e);
				}
			}
		} catch (FieldNotFound e) {
			PhotonPlugin.getDefault().getMarketDataLogger().error("Exception parsing option info", e);
		}
	}

	/**
	 * Get the current subscription for option market data
	 * @return the ISubscription for market data
	 */
	public IMarketDataFeedToken<?> getOptionMarketDataToken() {
		return currentOptionToken;
	}
	

//	/**
//	 * Debug code
//	 * @return
//	 */
//	@Deprecated
//    public static DerivativeSecurityList _createDummySecurityList(String symbol, String[] callSuffixes, String [] putSuffixes, BigDecimal[] strikePrices) {
//        SecurityRequestResult resultCode = new SecurityRequestResult(SecurityRequestResult.VALID_REQUEST);
//        DerivativeSecurityList responseMessage = new DerivativeSecurityList();
//        responseMessage.setField(new SecurityReqID("bob"));
//        responseMessage.setField(new SecurityResponseID("123"));
//
//        responseMessage.setField(new UnderlyingSymbol(symbol));
//        for (int i = 0; i < callSuffixes.length; i++) {
//            MSymbol putSymbol = new MSymbol(symbol+"+"+putSuffixes[i]);
//            // put first
//            Group optionGroup = new DerivativeSecurityList.NoRelatedSym();
//            optionGroup.setField(new Symbol(putSymbol.toString()));
//            optionGroup.setField(new StrikePrice(strikePrices[i]));
//            optionGroup.setField(new CFICode("OPASPS"));
//            optionGroup.setField(new MaturityMonthYear("200801"));
//            optionGroup.setField(new MaturityDate("20080122"));
//            responseMessage.addGroup(optionGroup);
//
//            MSymbol callSymbol = new MSymbol(symbol + "+" + callSuffixes[i]);
//            // now call
//            optionGroup.setField(new Symbol(callSymbol.toString()));
//            optionGroup.setField(new StrikePrice(strikePrices[i]));
//            optionGroup.setField(new CFICode("OCASPS"));
//            optionGroup.setField(new MaturityMonthYear("200801"));
//            optionGroup.setField(new MaturityDate("20080122"));
//            responseMessage.addGroup(optionGroup);
//
//        }
//        responseMessage.setField(resultCode);
//        return responseMessage;
//    }


}