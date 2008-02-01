package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.core.MSymbol;
import org.marketcetera.core.Pair;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

public class OptionMessageHolder extends EnumMap<OptionInfoComponent, FieldMap> implements Comparable<OptionMessageHolder> {

	private OptionPairKey key;
	private static Pattern optionSymbolPattern = Pattern.compile("(\\w{1,3})\\+[a-zA-Z]{2}");
	
	
	public OptionMessageHolder(String optionRootSymbol,
			FieldMap strikeInfo) throws ParseException, FieldNotFound
	{
		super(OptionInfoComponent.class);
		Pair<Integer, Integer> monthYear = OptionMarketDataUtils.getMaturityMonthYear(strikeInfo);
		this.key = new OptionPairKey(optionRootSymbol, monthYear.getSecondMember(), monthYear.getFirstMember(), 0, strikeInfo.getDecimal(StrikePrice.FIELD));
		this.put(OptionInfoComponent.STRIKE_INFO, strikeInfo);
	}
	
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

	private static String getRootSymbol(String baseSymbol) {
		Matcher matcher = optionSymbolPattern.matcher(baseSymbol);
		if (matcher.matches()){
			return matcher.group(1);
		} else {
			return baseSymbol;
		}
	}


	public OptionPairKey getKey() {
		return key;
	}

	public int compareTo(OptionMessageHolder o) {
		OptionPairKey otherKey = o.getKey();
		if (otherKey == null)
			return 1;
		return this.key.compareTo(otherKey);
	}
	
	public FieldMap getMarketData(int putOrCall){
		switch (putOrCall){
		case PutOrCall.PUT:
			return get(OptionInfoComponent.PUT_MARKET_DATA);
		case PutOrCall.CALL:
			return get(OptionInfoComponent.CALL_MARKET_DATA);
		default:
			throw new IllegalArgumentException(""+putOrCall);
		}
	}
	
	public void setMarketData(int putOrCall, FieldMap marketData){
		switch (putOrCall){
		case PutOrCall.PUT:
			this.put(OptionInfoComponent.PUT_MARKET_DATA, marketData);
			break;
		case PutOrCall.CALL:
			this.put(OptionInfoComponent.CALL_MARKET_DATA, marketData);
			break;
		default:
			throw new IllegalArgumentException(""+putOrCall);
		}
		
	}
	
	public void setExtraInfo(int putOrCall, FieldMap otherData)
	{
		switch (putOrCall){
		case PutOrCall.PUT:
			this.put(OptionInfoComponent.PUT_EXTRA_INFO, otherData);
			break;
		case PutOrCall.CALL:
			this.put(OptionInfoComponent.CALL_EXTRA_INFO, otherData);
			break;
		default:
			throw new IllegalArgumentException(""+putOrCall);
		}
	}
	
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
					getRootSymbol(optionSymbol.getBaseSymbol()),
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
