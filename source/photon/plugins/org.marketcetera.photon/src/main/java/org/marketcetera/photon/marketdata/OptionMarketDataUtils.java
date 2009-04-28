package org.marketcetera.photon.marketdata;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.marketcetera.core.Pair;
import org.marketcetera.core.ThreadLocalSimpleDateFormat;
import org.marketcetera.photon.Messages;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.cficode.OptionCFICode;
import org.marketcetera.trade.MSymbol;

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

public class OptionMarketDataUtils
    implements Messages
{
	private static FIXMessageFactory messageFactory = FIXVersion.FIX44
			.getMessageFactory();

	/* SimpleDateFormats are not thread safe... */
	public static final ThreadLocalSimpleDateFormat MONTH_YEAR_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("yyyyMM"); //$NON-NLS-1$
	public static final ThreadLocalSimpleDateFormat DAY_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("dd"); //$NON-NLS-1$
	public static final ThreadLocalSimpleDateFormat DATE_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("yyyyMMdd"); //$NON-NLS-1$
	public static final ThreadLocalSimpleDateFormat SHORT_MONTH_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("MMM"); //$NON-NLS-1$
	public static final ThreadLocalSimpleDateFormat OPTION_EXPIRATION_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("yyMMM"); //$NON-NLS-1$

	{
		MONTH_YEAR_FORMAT_LOCAL.setTimeZone(TimeZone.getTimeZone(MarketDataUtils.UTC_TIME_ZONE));
		DAY_FORMAT_LOCAL.setTimeZone(TimeZone.getTimeZone(MarketDataUtils.UTC_TIME_ZONE));
		DATE_FORMAT_LOCAL.setTimeZone(TimeZone.getTimeZone(MarketDataUtils.UTC_TIME_ZONE));
		SHORT_MONTH_FORMAT_LOCAL.setTimeZone(TimeZone.getTimeZone(MarketDataUtils.UTC_TIME_ZONE));
		OPTION_EXPIRATION_FORMAT_LOCAL.setTimeZone(TimeZone.getTimeZone(MarketDataUtils.UTC_TIME_ZONE));
	}
	/**
	 * The pattern used to determine if a symbol represents an option.
	 */
	public static final Pattern OPTION_SYMBOL_PATTERN = Pattern.compile("(\\w{1,3})\\+(\\w)(\\w)"); //$NON-NLS-1$



	/**
	 * Create a query for requesting all related options to the specified underlying
	 * 
	 * @param underlyingSymbol the underlying symbol for the options query
	 * @return a message representing the query
	 */
	public static Message newRelatedOptionsQuery(MSymbol underlyingSymbol) {
		Message requestMessage = messageFactory
				.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		/**
		 * specifies that the receiver should look in SecurityType field for
		 * more info
		 */
		requestMessage.setField(new SecurityListRequestType(SecurityListRequestType.SECURITYTYPE_AND_OR_CFICODE));
//		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new UnderlyingSymbol(underlyingSymbol
				.getFullSymbol()));
		return requestMessage;
	}

	/**
	 * Given an option root, returns a list of all the month/strike/call-put
	 * combos related to that option root.
	 * 
	 * @param optionRoot the option root
	 * @return a message
	 */
	public static Message newOptionRootQuery(String optionRoot) {
		Assert.isNotNull(optionRoot, "Parameter optionRoot cannot be null."); //$NON-NLS-1$
		Message requestMessage = messageFactory
				.createMessage(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
		/**
		 * specifies that the receiver should look in SecurityType field for
		 * more info
		 */
		requestMessage.setField(new SecurityListRequestType(SecurityListRequestType.SYMBOL));
//		requestMessage.setField(new SecurityType(SecurityType.OPTION));
		requestMessage.setField(new Symbol(optionRoot));
		return requestMessage;
	}

	/**
	 * Determines if the given symbol represents an option, based on
	 * {@link #OPTION_SYMBOL_PATTERN}.  If symbol is null, will return false.
	 * @param symbol the symbol
	 * @return true if the symbol represents an option symbol
	 */
	public static boolean isOptionSymbol(String symbol){
		if (symbol == null) {
			return false;
		}
		Matcher matcher = OPTION_SYMBOL_PATTERN.matcher(symbol);
		return matcher.matches();
	}
	
	/**
	 * Get the root symbol from the given option symbol ("MSQ" from "MSQ+RE" for example)
	 * @param symbol the symbol from which to get the root
	 * @return the root
	 */
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


	/**
	 * Get the type of option (put or call) represented by this 
	 * {@link FieldMap} (such as a Message).  
	 * @param fieldMap the FieldMap
	 * @return {@link PutOrCall#PUT} if put, {@link PutOrCall#CALL} if call
	 * @throws FieldNotFound if a required field is not present in the FieldMap
	 */
	public static int getOptionType(
			FieldMap fieldMap)
			throws FieldNotFound {

		if (fieldMap.isSetField(PutOrCall.FIELD)){
			return fieldMap.getInt(PutOrCall.FIELD);
		}
		OptionCFICode cfiCode = new OptionCFICode(fieldMap
				.getString(CFICode.FIELD));

		int putOrCall;
		if (cfiCode.getType() == OptionCFICode.TYPE_PUT) {
			putOrCall = PutOrCall.PUT;
		} else if (cfiCode.getType() == OptionCFICode.TYPE_CALL) {
			putOrCall = PutOrCall.CALL;
		} else {
			throw new IllegalArgumentException(INVALID_PUT_OR_CALL.getText(cfiCode.getType()));
		}
		return putOrCall;
	}
	
	/**
	 * Get the "opposite" type of option from the specified type
	 * 
	 * @param thisOptionType either {@link PutOrCall#PUT} or {@link PutOrCall#CALL}
	 * @return {@link PutOrCall#PUT} when thisOptionType is call, and {@link PutOrCall#CALL}, when thisOptionType is put
	 */
	public static int getOtherOptionType(int thisOptionType){
		switch (thisOptionType){
		case PutOrCall.PUT:
			return PutOrCall.CALL;
		case PutOrCall.CALL:
			return PutOrCall.PUT;
		default:
            throw new IllegalArgumentException(INVALID_PUT_OR_CALL.getText(thisOptionType));
		}
	}
	
	/**
	 * Return a pair of integers representing the month (1-indexed) and year
	 * of the "maturity date" of the option specified by fieldMap.
	 * 
	 * @param fieldMap FieldMap representing an option
	 * @return a {@link Pair} whose first element is the month, and second element the year of maturity for the given option
	 * @throws FieldNotFound if a required field is missing
	 * @throws ParseException if a required field is improperly formatted
	 */
	public static Pair<Integer, Integer> getMaturityMonthYear(FieldMap fieldMap) throws ParseException, FieldNotFound
	{
		Calendar cal = parseCalendar(fieldMap);
		return new Pair<Integer, Integer>(cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
	}

	private static Calendar parseCalendar(FieldMap map) throws ParseException,
			FieldNotFound {
		Date parsed;
		try {
			String maturityDate = map.getString(MaturityDate.FIELD);
			parsed = parseDateString(maturityDate);
		} catch (FieldNotFound fnf){
			String maturityMonthYear = map.getString(MaturityMonthYear.FIELD);
			parsed = parseMonthYear(maturityMonthYear);
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(parsed);
		return cal;
	}
	
	/**
	 * Get a string representing the expiration month with a year identifier.  e.g.
	 * 09JAN for January of 2009.
	 * @param map the FieldMap representing the option
	 * @return a string representing the expiration year and month
	 * @throws FieldNotFound if a required field is missing
	 * @throws ParseException if a required field is improperly formatted
	 */
	public static String getOptionExpirationMonthString(FieldMap map) throws ParseException, FieldNotFound{
		Calendar cal = parseCalendar(map);
		return OPTION_EXPIRATION_FORMAT_LOCAL.get().format(cal.getTime());
		
	}

	/**
	 * Given a string representing the "maturity month year" in standard
	 * FIX format, (yyyyMM), parse out a Date object.  The DAY_OF_MONTH is 
	 * unspecified in the returned Date.
	 * 
	 * @param maturityMonthYear the string representing the year and month, eg "200901" for January of 2009
	 * @return the date object representing the month and year
	 * @throws ParseException if the string is improperly formatted
	 */
	public static Date parseMonthYear(String maturityMonthYear)
			throws ParseException {
		return MONTH_YEAR_FORMAT_LOCAL.get().parse(maturityMonthYear);
	}

	/**
	 * Given a string representing the "maturity date" in standard
	 * FIX format, (yyyyMMdd), parse out a Date object.
	 * @param maturityDate the string representing the year and month, eg "200901" for January of 2009
	 * @return the date object representing the date
	 * @throws ParseException if the string is improperly formatted
	 */
	public static Date parseDateString(String maturityDate) throws ParseException {
		return DATE_FORMAT_LOCAL.get().parse(maturityDate);
	}
	
}
