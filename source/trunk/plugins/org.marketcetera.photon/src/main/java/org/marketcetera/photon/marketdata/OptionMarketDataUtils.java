package org.marketcetera.photon.marketdata;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.Pair;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.cficode.OptionCFICode;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityListRequestType;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;

public class OptionMarketDataUtils {
	private static FIXMessageFactory messageFactory = FIXVersion.FIX44
			.getMessageFactory();

	public static final DateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("yyyyMM");
	public static final DateFormat DAY_FORMAT = new SimpleDateFormat("dd");
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private static final Pattern OPTION_SYMBOL_PATTERN = Pattern.compile("(\\w{1,3})\\+(\\w)(\\w)");



	private static Pattern optionSymbolRootSeparatorPattern;

	public static Message newRelatedOptionsQuery(MSymbol underlyingSymbol) {
		Message requestMessage = messageFactory
				.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		/**
		 * specifies that the receiver should look in SecurityType field for
		 * more info
		 */
		requestMessage.setField(new SecurityListRequestType(1));
//		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new UnderlyingSymbol(underlyingSymbol
				.getBaseSymbol()));
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
	public static Message newOptionRootQuery(String optionRoot) {
		Assert.isNotNull(optionRoot, "Parameter optionRoot cannot be null."); //$NON-NLS-1$
		Message requestMessage = messageFactory
				.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		/**
		 * specifies that the receiver should look in SecurityType field for
		 * more info
		 */
		requestMessage.setField(new SecurityListRequestType(0));
//		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new Symbol(optionRoot));
		return requestMessage;
	}

	public static boolean isOptionSymbol(String symbol){
		Matcher matcher = OPTION_SYMBOL_PATTERN.matcher(symbol);
		return matcher.matches();
	}
	
	public static String getOptionRootSymbol(String symbol) {
		if (symbol == null) {
			return null;
		}
		Matcher matcher = OPTION_SYMBOL_PATTERN.matcher(symbol);
		if (matcher.matches()){
			return matcher.group(1);
		} else {
			return null;
		}
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



	public static int getOptionType(
			FieldMap optionGroup)
			throws FieldNotFound {

		if (optionGroup.isSetField(PutOrCall.FIELD)){
			return optionGroup.getInt(PutOrCall.FIELD);
		}
		OptionCFICode cfiCode = new OptionCFICode(optionGroup
				.getString(CFICode.FIELD));

		int putOrCall;
		if (cfiCode.getType() == OptionCFICode.TYPE_PUT) {
			putOrCall = PutOrCall.PUT;
		} else if (cfiCode.getType() == OptionCFICode.TYPE_CALL) {
			putOrCall = PutOrCall.CALL;
		} else {
			throw new IllegalArgumentException(
					"Option data is neither a put nor a call. CFICode="
							+ cfiCode.getType());
		}
		return putOrCall;
	}
	
	public static int getOtherOptionType(int thisOptionType){
		switch (thisOptionType){
		case PutOrCall.PUT:
			return PutOrCall.CALL;
		case PutOrCall.CALL:
			return PutOrCall.PUT;
		default:
			throw new IllegalArgumentException(""+thisOptionType);
		}
	}
	
	public static Pair<Integer, Integer> getMaturityMonthYear(FieldMap map) throws ParseException, FieldNotFound
	{
		Date parsed;
		try {
			String maturityDate = map.getString(MaturityDate.FIELD);
			parsed = DATE_FORMAT.parse(maturityDate);
		} catch (FieldNotFound fnf){
			String maturityMonthYear = map.getString(MaturityMonthYear.FIELD);
			parsed = MONTH_YEAR_FORMAT.parse(maturityMonthYear);
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(parsed);
		return new Pair<Integer, Integer>(cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
	}
}
