package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.regex.Matcher;

import org.marketcetera.core.Pair;
import org.marketcetera.photon.Messages;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

/**
 * This class allows multiple {@link FieldMap} instances, representing
 * a single "row" in an equity option UI to be aggregated and accessed.
 * There are five components to each "row", encapsulated inthe {@link OptionInfoComponent}
 * enumeration:  STRIKE_INFO, CALL_MARKET_DATA, PUT_MARKET_DATA, CALL_EXTRA_INFO, PUT_EXTRA_INFO.
 * STRIKE_INFO represents the common data to both put and call, e.g. strike price and
 * maturity date.  The *_MARKET_DATA components represent the current market prices
 * for the call and put.  And *_EXTRA_INFO holds any information specific to the put or
 * call.
 * 
 * @author gmiller
 *
 */
public class OptionMessageHolder
    extends EnumMap<OptionInfoComponent, FieldMap>
    implements Comparable<OptionMessageHolder>, Messages
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -727881128321843742L;
	
	private OptionPairKey key;
	private String putSymbol;
	private String callSymbol;
	
	/**
	 * Construct a new OptionMessageHolder form the specified option root symbol (e.g. "MSQ")
	 * and a {@link FieldMap} containing the strike info (fields common to the put and call).
	 * 
	 * The FieldMap must contain at least {@link MaturityDate} or {@link MaturityMonthYear}
	 * 
	 * @param optionRootSymbol the option root symbol
	 * @param strikeInfo the field map
	 * @throws ParseException if a required field is improperly formatted
	 * @throws FieldNotFound if a required field is missing
	 */
	public OptionMessageHolder(String optionRootSymbol,
			FieldMap strikeInfo) throws ParseException, FieldNotFound
	{
		super(OptionInfoComponent.class);
		Pair<Integer, Integer> monthYear = OptionMarketDataUtils.getMaturityMonthYear(strikeInfo);
		this.key = new OptionPairKey(optionRootSymbol, monthYear.getSecondMember(), monthYear.getFirstMember(), 0, strikeInfo.getDecimal(StrikePrice.FIELD));
		this.put(OptionInfoComponent.STRIKE_INFO, strikeInfo);
	}
	
	/**
	 * Construct a new OptionMessageHolder form the specified option root symbol (e.g. "MSQ")
	 * and a {@link FieldMap} containing the strike info (fields common to the put and call), as
	 * well as the fields specific to each the put and call.
	 * 
	 * The strikeInfo FieldMap must contain at least {@link MaturityDate} or {@link MaturityMonthYear}
	 * 
	 * @param optionRootSymbol the root symbol of the option
	 * @param strikeInfo the field map representing the common information for the put and call
	 * @param callExtraInfo the fields specific to the call
	 * @param putExtraInfo the fields specific to the put
	 * @throws ParseException if a required field is improperly formatted
	 * @throws FieldNotFound if a required field is missing
	 */
	public OptionMessageHolder(String optionRootSymbol,
			FieldMap strikeInfo, FieldMap callExtraInfo,
			FieldMap putExtraInfo) throws ParseException, FieldNotFound {
		super(OptionInfoComponent.class);
		Pair<Integer, Integer> monthYear = OptionMarketDataUtils.getMaturityMonthYear(strikeInfo);
		this.key = new OptionPairKey(optionRootSymbol, monthYear.getSecondMember(), monthYear.getFirstMember(), 0, strikeInfo.getDecimal(StrikePrice.FIELD));
		
		this.put(OptionInfoComponent.STRIKE_INFO, strikeInfo);
		this.put(OptionInfoComponent.CALL_EXTRA_INFO, callExtraInfo);
		this.put(OptionInfoComponent.PUT_EXTRA_INFO, putExtraInfo);
	}

	/**
	 * Get the root symbol from the given option symbol.
	 * @param optionSymbol option symbol, e.g. "IBM+RE"
	 * @return the root symbol, e.g "IBM"
	 */
	private static String getRootSymbol(String optionSymbol) {
		Matcher matcher = FIXMessageUtil.optionSymbolPattern.matcher(optionSymbol);
		if (matcher.matches()){
			return matcher.group(1);
		} else {
			return optionSymbol;
		}
	}


	
	private OptionPairKey getKey() {
		return key;
	}

	/**
	 * Compares this OptionMessageHolder to the specified OptionMessageHolder,
	 * based on the values in the "key".
	 * 
	 * @see OptionPairKey#compareTo(org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey)
	 */
	public int compareTo(OptionMessageHolder o) {
		if (o == null){
			return 1;
		}
		OptionPairKey otherKey = o.getKey();
		if (otherKey == null)
			return 1;
		return this.key.compareTo(otherKey);
	}
	
	/**
	 * Return the market data {@link FieldMap} for either the put or call
	 * @param putOrCall {@link PutOrCall#PUT} or {@link PutOrCall#CALL}
	 * @return the field map containing market data
	 */
	public FieldMap getMarketData(int putOrCall){
		switch (putOrCall){
		case PutOrCall.PUT:
			return get(OptionInfoComponent.PUT_MARKET_DATA);
		case PutOrCall.CALL:
			return get(OptionInfoComponent.CALL_MARKET_DATA);
		default:
			throw new IllegalArgumentException(INVALID_PUT_OR_CALL.getText(putOrCall));
		}
	}

	/**
	 * Set the market data field map for the put or call.
	 * @param putOrCall {@link PutOrCall#PUT} or {@link PutOrCall#CALL}
	 * @param marketData the market data FieldMap to set.
	 */
	public void setMarketData(int putOrCall, FieldMap marketData){
		switch (putOrCall){
		case PutOrCall.PUT:
			this.put(OptionInfoComponent.PUT_MARKET_DATA, marketData);
			break;
		case PutOrCall.CALL:
			this.put(OptionInfoComponent.CALL_MARKET_DATA, marketData);
			break;
		default:
            throw new IllegalArgumentException(INVALID_PUT_OR_CALL.getText(putOrCall));
		}
	}
	
	/**
	 * Set the field map representing info specific to a put or call.
	 * @param putOrCall {@link PutOrCall#PUT} or {@link PutOrCall#CALL}
	 * @param otherData the FieldMap to set.
	 * @throws FieldNotFound if the Symbol field is not found
	 */
	public void setExtraInfo(int putOrCall, FieldMap otherData) throws FieldNotFound
	{
		String symbol = null;
		if (otherData.isSetField(Symbol.FIELD)){
			symbol = otherData.getString(Symbol.FIELD); 
		}
		switch (putOrCall){
		case PutOrCall.PUT:
			this.put(OptionInfoComponent.PUT_EXTRA_INFO, otherData);
			if (symbol != null){
				this.setSymbol(PutOrCall.PUT, symbol);
			}
			break;
		case PutOrCall.CALL:
			this.put(OptionInfoComponent.CALL_EXTRA_INFO, otherData);
			if (symbol != null){
				this.setSymbol(PutOrCall.CALL, symbol);
			}
			break;
		default:
            throw new IllegalArgumentException(INVALID_PUT_OR_CALL.getText(putOrCall));
		}
	}
	
	/**
	 * Given a symbol, determine if it represents a put or a call.
	 * @param symbol the symbol to discriminate
	 * @return {@link PutOrCall#PUT} if symbol represents a put, {@link PutOrCall#CALL} if symbol represents a call
	 */
	public int symbolOptionType(String symbol){
		FieldMap fieldMap = get(OptionInfoComponent.CALL_EXTRA_INFO);
		try {
			if (fieldMap != null && symbol.equals(fieldMap.getString(Symbol.FIELD))){
				return PutOrCall.CALL;
			}
		} catch (FieldNotFound e) {
		}
		
		fieldMap = get(OptionInfoComponent.PUT_EXTRA_INFO);
		try {
			if (fieldMap != null && symbol.equals(fieldMap.getString(Symbol.FIELD))){
				return PutOrCall.PUT;
			}
		} catch (FieldNotFound e) {
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Get the market data {@link FieldMap} for a symbol, or null
	 * if the symbol does not correspond to either the put or call in this.
	 * 
	 * @param symbol the symbol to find
	 * @return FieldMap representing the appropriate market data
	 */
	public FieldMap getMarketDataForSymbol(String symbol){
		FieldMap fieldMap = get(OptionInfoComponent.CALL_EXTRA_INFO);
		try {
			if (fieldMap != null && symbol.equals(fieldMap.getString(Symbol.FIELD))){
				return get(OptionInfoComponent.CALL_MARKET_DATA);
			}
		} catch (FieldNotFound e) {
		}
		
		fieldMap = get(OptionInfoComponent.PUT_EXTRA_INFO);
		try {
			if (fieldMap != null && symbol.equals(fieldMap.getString(Symbol.FIELD))){
				return get(OptionInfoComponent.PUT_MARKET_DATA);
			}
		} catch (FieldNotFound e) {
		}
		return null;
	}

	/**
	 * Get the extra info {@link FieldMap} for a symbol, or null
	 * if the symbol does not correspond to either the put or call in this.
	 * 
	 * @param symbol the symbol to find
	 * @return FieldMap representing the appropriate extra info
	 */
	public FieldMap getExtraInfoForSymbol(String symbol){
		FieldMap fieldMap = get(OptionInfoComponent.CALL_EXTRA_INFO);
		try {
			if (fieldMap != null && symbol.equals(fieldMap.getString(Symbol.FIELD))){
				return fieldMap;
			}
		} catch (FieldNotFound e) {
		}
		
		fieldMap = get(OptionInfoComponent.PUT_EXTRA_INFO);
		try {
			if (fieldMap != null && symbol.equals(fieldMap.getString(Symbol.FIELD))){
				return fieldMap;
			}
		} catch (FieldNotFound e) {
		}
		return null;
	}

	/**
	 * Get the symbol for the put or call in this.
	 * @param putOrCall {@link PutOrCall#PUT} or {@link PutOrCall#CALL}
	 * @return the symbol for the put or call.
	 */
	public String getSymbol(int putOrCall){
		switch(putOrCall){
		case PutOrCall.PUT:
			return putSymbol;
		case PutOrCall.CALL:
			return callSymbol;
		default:
            throw new IllegalArgumentException(INVALID_PUT_OR_CALL.getText(putOrCall));
		}
	}

	/**
	 * Set the symbol for the put or call.
	 * @param putOrCall {@link PutOrCall#PUT} or {@link PutOrCall#CALL}
	 * @param value the symbol to set for the put or call
	 */
	public void setSymbol(int putOrCall, String value){
		switch(putOrCall){
		case PutOrCall.PUT:
			putSymbol = value;
			break;
		case PutOrCall.CALL:
			callSymbol = value;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * key for option message holders.
	 */
	public static class OptionPairKey implements Comparable<OptionPairKey> {

		
		private final int expirationYear;

		private final int expirationMonth;

		private final int expirationDay;

		private final BigDecimal strikePrice;

		private final String optionRoot;

		public OptionPairKey(String optionRoot,
				int expirationYear, int expirationMonth,
				int expirationDay,
				BigDecimal strikePrice) {
			this.optionRoot = optionRoot;
			this.expirationYear = expirationYear;
			this.expirationMonth = expirationMonth;
			this.expirationDay = expirationDay;
			this.strikePrice = strikePrice;
		}

		public int getExpirationMonth() {
			return expirationMonth;
		}

		public int getExpirationDay() {
			return expirationDay;
		}

		public int getExpirationYear() {
			return expirationYear;
		}

		public BigDecimal getStrikePrice() {
			return strikePrice;
		}

		public String getOptionRoot() {
			return optionRoot;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME
					* result
					+ (expirationDay);
			result = PRIME
					* result
					+ (expirationMonth);
			result = PRIME
					* result
					+ (expirationYear);
			result = PRIME * result
					+ ((strikePrice == null) ? 0 : strikePrice.hashCode());
			result = PRIME * result
					+ ((optionRoot == null) ? 0 : optionRoot.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final OptionPairKey other = (OptionPairKey) obj;
			if (strikePrice == null) {
				if (other.strikePrice != null)
					return false;
			} else if (!strikePrice.equals(other.strikePrice))
				return false;
			if (optionRoot == null) {
				if (other.optionRoot != null)
					return false;
			} else if (!optionRoot.equals(other.optionRoot))
				return false;
			if (expirationMonth != other.expirationMonth)
				return false;
			if (expirationYear != other.expirationYear)
				return false;
			return true;
		}

		public static OptionPairKey fromFieldMap(MSymbol optionSymbol, FieldMap info) throws ParseException, FieldNotFound{
			Pair<Integer, Integer> maturityMonthYear = OptionMarketDataUtils.getMaturityMonthYear(info);
			OptionPairKey optionKey = new OptionPairKey(
					getRootSymbol(optionSymbol.getFullSymbol()),
					maturityMonthYear.getSecondMember(), //year
					maturityMonthYear.getFirstMember(), //month
					0,
					info.getDecimal(StrikePrice.FIELD));
			return optionKey;

		}

		public int compareTo(OptionPairKey other) {
			int compareExpYear = this.getExpirationYear() - other.getExpirationYear();
			if (compareExpYear != 0){
				return compareExpYear;
			}

			int compareExpMonth = this.getExpirationMonth() - other.getExpirationMonth();
			if (compareExpMonth != 0){
				return compareExpMonth;
			}

			int compareExpDay = this.getExpirationDay() - other.getExpirationDay();
			if (compareExpDay != 0){
				return compareExpDay;
			}

			String thisOptionRoot = this.getOptionRoot();
			String otherOptionRoot = other.getOptionRoot();
			int compareOptionRoot = thisOptionRoot
					.compareToIgnoreCase(otherOptionRoot);
			if (compareOptionRoot != 0)
				return compareOptionRoot;

			int compareStrike = this.getStrikePrice().compareTo(
					other.getStrikePrice());

			return compareStrike;
		}
	}
}
