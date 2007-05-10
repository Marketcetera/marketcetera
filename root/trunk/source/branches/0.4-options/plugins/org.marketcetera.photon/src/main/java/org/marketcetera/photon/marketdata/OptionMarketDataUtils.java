package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.photon.PhotonPlugin;
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
	private static FIXMessageFactory messageFactory = FIXVersion.FIX44.getMessageFactory();
	
	private static Pattern underlyingSymbolPattern;
	
	public static Message newRelatedOptionsQuery(MSymbol underlyingSymbol, boolean subscribe){
		Message requestMessage = messageFactory.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		requestMessage.setField(new SecurityListRequestType(1));// specifies that the receiver should look in SecurityType field for more info
		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new UnderlyingSymbol(underlyingSymbol.toString()));
		if (subscribe){
			requestMessage.setField(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		} else {
			requestMessage.setField(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
		}
		return requestMessage;
	}

	/**
	 * Create a job that retrieves all the option contracts for a given
	 * underlier.
	 * 
	 * @param callback
	 *            the callback will be invoked when the market data is available
	 *            or on failure. The List<Message> passed to the callback is
	 *            the derivative security list response.
	 * @param callbackOnUIThread
	 *            when true, use Display.asyncExec to perform the callback on
	 *            the UI thread. Otherwise the callback occurs in the Job
	 *            thread.
	 */
	public static void asyncOptionSecurityList(final MSymbol underlyingSymbol,
			final IMarketDataFeed marketDataFeed,
			final IMarketDataListCallback callback,
			final boolean callbackOnUIThread) {
		final long OPTION_SECURITY_LIST_TIMEOUT_MILLIS = 8000;

		Job securityListJob = new Job("Getting options for " + underlyingSymbol.getFullSymbol()) {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Message query = newRelatedOptionsQuery(underlyingSymbol, false);
					final List<Message> messages = marketDataFeed.syncQuery(
							query, OPTION_SECURITY_LIST_TIMEOUT_MILLIS,
							TimeUnit.MILLISECONDS);

					if (callbackOnUIThread) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								callback.onMarketDataListAvailable(messages);
							}
						});
					} else {
						callback.onMarketDataListAvailable(messages);
					}
				} catch (Exception anyException) {
					if (callbackOnUIThread) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								callback.onMarketDataFailure(underlyingSymbol);
							}
						});
					} else {
						callback.onMarketDataFailure(underlyingSymbol);
					}
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		securityListJob.schedule();
	}

	/**
	 * @see org.marketcetera.photon.marketdata.MarketDataUtils.getUnderlyingSymbol(String
	 *      symbol)
	 */
	public static String getUnderlyingSymbol(String symbol) {
		if (underlyingSymbolPattern == null) {
			underlyingSymbolPattern = Pattern.compile("\\+");
		}
		if (symbol == null) {
			return null;
		}
		String[] underlierPieces = underlyingSymbolPattern.split(symbol);
		if (underlierPieces == null || underlierPieces.length == 0) {
			return symbol;
		}
		return underlierPieces[0];
	}

	//	 todo: This method is irrelevant if MarketceteraOptionSymbol is moved to core.
	/**
	 * For a given option symbol, return the underlier. For example, "AMD+FA"
	 * will return "AMD".
	 * <p>
	 * This method is not thread safe.
	 * </p>
	 * <p>
	 * todo: This will not work for symbol schemes other than SymbolScheme.BASIC
	 * </p>
	 */
	public static MSymbol getUnderlyingSymbol(MSymbol symbol) {
		String underlier = getUnderlyingSymbol(symbol.getFullSymbol());
		return new MSymbol(underlier);
	}

	public static List<OptionContractData> getOptionExpirationMarketData(
			final MSymbol underlyingSymbol, List<Message> derivativeSecurityList) {
		String underlyingSymbolStr = underlyingSymbol.getBaseSymbol();
		List<OptionContractData> optionExpirations = new ArrayList<OptionContractData>();
		for (Message message : derivativeSecurityList) {
			try {
				String messageType = message.getHeader().getString(
						MsgType.FIELD);
				if (MsgType.DERIVATIVE_SECURITY_LIST.equals(messageType)) {
					String messageUnderlyingSymbol = message
							.getString(UnderlyingSymbol.FIELD);
					if (messageUnderlyingSymbol != null
							&& messageUnderlyingSymbol
									.startsWith(underlyingSymbolStr)) {
						addExpirationFromMessage( underlyingSymbol, message, optionExpirations );
					}
				} else {
					throw new MarketceteraFIXException(
							"FIX message was not a DerivativeSecurityList ("
									+ MsgType.DERIVATIVE_SECURITY_LIST + ").");
				}
			} catch (Exception anyException) {
				// Ignore the error
				if (PhotonPlugin.getMainConsoleLogger().isDebugEnabled()) {
					PhotonPlugin.getMainConsoleLogger().debug(
							"Failed to process option expiration date in message: "
									+ message, anyException);
				}
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
			OptionContractData optionData = new OptionContractData(underlyingSymbol, optionSymbol, year, month, strike, optionIsPut);;
					
			optionExpirations.add(optionData);
		}
	}
}
