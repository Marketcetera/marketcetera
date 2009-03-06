package org.marketcetera.photon.views;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.Pair;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.I18NBoundMessage2P;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NoRelatedSym;
import quickfix.field.SecurityReqID;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;

/* $License$ */

/**
 * The Option Order Ticket View Controller
 * @version $Id$
 *
 */
@ClassVersion ("$Id$")
public class OptionOrderTicketController 
	extends OrderTicketController<OptionOrderTicketModel>
    implements Messages
{

	/**
	 * Maps option roots and underlyings to a complete set of OptionContractData
	 */
	HashMap<String, List<OptionContractData>> receivedOptionRoots = new HashMap<String, List<OptionContractData>>();
	
	/**
	 * Create a new {@link OptionOrderTicketController} wrapping the given model.
	 * 
	 * @param model
	 */
	public OptionOrderTicketController(final OptionOrderTicketModel model) {
		super(model);
		
		clear();
	}
	
	/**
	 * Clear the order ticket message
	 */
	public void clear() {
		getOrderTicketModel().clearOrderMessage();
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
				String reqID = ""; //$NON-NLS-1$
				try { reqID = derivativeSecurityList.getString(SecurityReqID.FIELD); } catch (FieldNotFound fnf) {}
				mainConsoleLogger.debug("Received derivative list for "+underlyingSymbol+" ("+reqID+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
	            PhotonPlugin.getMainConsoleLogger().warn(SKIPPING_MARKET_DATA.getText(),
	                                                     anyException);
	            return;
	        }
		}
		catch(FieldNotFound anyException) {
			mainConsoleLogger.debug("Failed to find underlying symbol in DerivativeSecurityList: " + derivativeSecurityList, anyException); //$NON-NLS-1$
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
			throws CoreException {

		List<OptionContractData> optionContractDataList = new ArrayList<OptionContractData>();
		Set<String> optionRootList = new HashSet<String>();

		if(derivativeSecurityListMessage != null) {
			String messageUnderlyingSymbolStr = ""; //$NON-NLS-1$
			if (FIXMessageUtil.isDerivativeSecurityList(derivativeSecurityListMessage)) {
				try {
					messageUnderlyingSymbolStr = derivativeSecurityListMessage
							.getString(UnderlyingSymbol.FIELD);
					MSymbol messageUnderlyingSymbol = new MSymbol(
							messageUnderlyingSymbolStr);
					getOptionContractDataFromMessage(messageUnderlyingSymbol,
							derivativeSecurityListMessage, optionContractDataList, optionRootList);
				} catch (Exception anyException) {
					throw new CoreException(new I18NBoundMessage2P(CANNOT_GET_OPTION_CONTRACT_INFO_SPECIFIED,
					                                               messageUnderlyingSymbolStr,
					                                               derivativeSecurityListMessage));
				}
			} else {
				throw new MarketceteraFIXException(MESSAGE_NOT_DERIVATIVE_SECURITY_LIST);
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
				PhotonPlugin.getDefault().getMainLogger().info(e.getLocalizedMessage());
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
					MSymbol optionSymbol = new MSymbol(optionSymbolString);
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
					MSymbol underlying =new MSymbol(derivativeSecurityList.getString(Symbol.FIELD));
					PhotonPlugin.getDefault().getMainLogger().error(CANNOT_PARSE_OPTION_INFO_SPECIFIED.getText(underlying),
					                                                      e);
				}
			}
		} catch (FieldNotFound e) {
			PhotonPlugin.getDefault().getMainLogger().error(CANNOT_PARSE_OPTION_INFO.getText(),
			                                                      e);
		}
	}
}
