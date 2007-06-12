package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.marketcetera.quickfix.cficode.OptionCFICode;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.CFICode;
import quickfix.field.MaturityDate;
import quickfix.field.MsgType;
import quickfix.field.NoRelatedSym;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityType;
import quickfix.field.StrikePrice;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;

public class OptionMarketDataUtils {
	private static FIXMessageFactory messageFactory = FIXVersion.FIX44
			.getMessageFactory();

	private static Pattern optionSymbolRootSeparatorPattern;

	public static Message newRelatedOptionsQuery(MSymbol underlyingSymbol,
			boolean subscribe) {
		Message requestMessage = messageFactory
				.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		/**
		 * specifies that the receiver should look in SecurityType field for
		 * more info
		 */
		requestMessage.setField(new SecurityListRequestType(1));
		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new UnderlyingSymbol(underlyingSymbol
				.getBaseSymbol()));
		if (subscribe) {
			requestMessage.setField(new SubscriptionRequestType(
					SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		} else {
			requestMessage.setField(new SubscriptionRequestType(
					SubscriptionRequestType.SNAPSHOT));
		}
		return requestMessage;
	}

	/**
	 * Given an option root, returns a list of all the month/strike/call-put
	 * combos related to that option root.
	 * 
	 * @param optionRoot
	 * @param subscribe
	 * @return
	 */
	public static Message newOptionRootQuery(MSymbol optionRoot,
			boolean subscribe) {
		Message requestMessage = messageFactory
				.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		/**
		 * specifies that the receiver should look in SecurityType field for
		 * more info
		 */
		requestMessage.setField(new SecurityListRequestType(0));
		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new Symbol(optionRoot.getBaseSymbol()));
		if (subscribe) {
			requestMessage.setField(new SubscriptionRequestType(
					SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		} else {
			requestMessage.setField(new SubscriptionRequestType(
					SubscriptionRequestType.SNAPSHOT));
		}
		return requestMessage;
	}

	/**
	 * @see org.marketcetera.photon.marketdata.MarketDataUtils.getUnderlyingSymbol(String
	 *      symbol)
	 */
	public static String getOptionRootSymbol(String symbol) {
		if (optionSymbolRootSeparatorPattern == null) {
			optionSymbolRootSeparatorPattern = Pattern.compile("\\+");
		}
		if (symbol == null) {
			return null;
		}
		String[] underlierPieces = optionSymbolRootSeparatorPattern.split(symbol);
		if (underlierPieces == null || underlierPieces.length == 0) {
			return symbol;
		}
		return underlierPieces[0];
	}

	// todo: This method is irrelevant if MarketceteraOptionSymbol is moved to
	// core.
	/**
	 * For a given option symbol, return the option root. For example, "MSQ+GE"
	 * will return "MSQ". Note that MSQ is not the underlier MSFT.
	 * <p>
	 * This method is not thread safe.
	 * </p>
	 * <p>
	 * todo: This will not work for symbol schemes other than SymbolScheme.BASIC
	 * </p>
	 */
	public static MSymbol getOptionRootSymbol(MSymbol symbol) {
		String underlier = getOptionRootSymbol(symbol.getFullSymbol());
		return new MSymbol(underlier);
	}

	private static boolean isApplicableUnderlyingSymbol(
			String messageUnderlyingSymbolStr, String symbolFilter) {
		if (messageUnderlyingSymbolStr != null) {
			if (symbolFilter == null || symbolFilter.trim().length() == 0
					|| messageUnderlyingSymbolStr.startsWith(symbolFilter)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param symbolFilter
	 *            the UnderlyingSymbol in each message is checked to ensure that
	 *            it starts with the symbolFilter. Underliers that do not match
	 *            are not processed. Specify null to process all messages.
	 * @throws MarketceteraFIXException 
	 */
	public static List<OptionContractData> getOptionExpirationMarketData(
			final String symbolFilter, List<Message> derivativeSecurityList) 
			throws MarketceteraException {
		List<OptionContractData> optionExpirations = new ArrayList<OptionContractData>();
		String messageUnderlyingSymbolStr = "";
		for (Message message : derivativeSecurityList) {
			try {
				String messageType = message.getHeader().getString(
						MsgType.FIELD);
				if (MsgType.DERIVATIVE_SECURITY_LIST.equals(messageType)) {
					messageUnderlyingSymbolStr = message
							.getString(UnderlyingSymbol.FIELD);
					if (isApplicableUnderlyingSymbol(
							messageUnderlyingSymbolStr, symbolFilter)) {
						MSymbol messageUnderlyingSymbol = new MSymbol(
								messageUnderlyingSymbolStr);
						addExpirationFromMessage(messageUnderlyingSymbol,
								message, optionExpirations);
					}
				} else {
					throw new MarketceteraFIXException(
							"FIX message was not a DerivativeSecurityList ("
									+ MsgType.DERIVATIVE_SECURITY_LIST + ").");
				}
			} catch (Exception anyException) {				
				throw new MarketceteraException(
						"Failed to get option contracts data for underlying symbol \""
								+ messageUnderlyingSymbolStr + "\" - "
								+ "\nProblematic message is : [" + message
								+ "]", anyException);
			}
		}
		return optionExpirations;
	}

	private static void addExpirationFromMessage(MSymbol underlyingSymbol,
			Message message, List<OptionContractData> optionExpirations)
			throws FieldNotFound, MarketceteraFIXException {
		int numDerivs = message.getInt(NoRelatedSym.FIELD);
		for (int index = 1; index <= numDerivs; index++) {
			DerivativeSecurityList.NoRelatedSym optionGroup = new DerivativeSecurityList.NoRelatedSym();
			message.getGroup(index, optionGroup);

			String optionSymbolStr = optionGroup.getString(Symbol.FIELD);

			OptionCFICode cfiCode = new OptionCFICode(optionGroup
					.getString(CFICode.FIELD));

			boolean optionIsPut = false;
			if (cfiCode.getType() == OptionCFICode.TYPE_PUT) {
				optionIsPut = true;
			} else if (cfiCode.getType() == OptionCFICode.TYPE_CALL) {
				optionIsPut = false;
			} else {
				throw new MarketceteraFIXException(
						"Option data is neither a put nor a call. CFICode="
								+ cfiCode.getType());
			}

			StringField maturityDateField = optionGroup
					.getField(new MaturityDate());
			String maturityDateString = maturityDateField.getValue();
			String year = maturityDateString.substring(0, 4);
			String month = maturityDateString.substring(4, 6);

			String strikeStr = optionGroup.getString(StrikePrice.FIELD);
			BigDecimal strike = new BigDecimal(strikeStr);

			MSymbol optionSymbol = new MSymbol(optionSymbolStr);
			OptionContractData optionData = new OptionContractData(
					underlyingSymbol, optionSymbol, year, month, strike,
					optionIsPut);

			optionExpirations.add(optionData);
		}
	}
}
