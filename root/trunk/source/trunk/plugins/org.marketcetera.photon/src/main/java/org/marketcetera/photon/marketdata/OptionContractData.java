package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Comparator;

import org.marketcetera.core.MSymbol;
import org.marketcetera.core.Pair;
import org.marketcetera.photon.views.OptionDateHelper;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

public class OptionContractData {

	private MSymbol underlyingSymbol;
	private MSymbol optionSymbol;
	private String optionRoot;
	private Integer expirationYear;
	private Integer expirationMonth;
	private String expirationYearUIString;
	private String expirationMonthUIString;
	private String strikePriceUIString;
	private BigDecimal strikePrice;
	private Integer putOrCall;
	private static OptionDateHelper OPTION_DATE_HELPER = new OptionDateHelper();

	/**
	 * @param optionSymbol the option symbol for the specific contract, e.g. MSQ+FN
	 */
	public OptionContractData(MSymbol underlyingSymbol, MSymbol optionSymbol, Integer expirationYear, Integer expirationMonth, BigDecimal strikePrice, int putOrCall) {
		this.underlyingSymbol = underlyingSymbol;
		this.optionSymbol = optionSymbol;
		this.expirationYear = expirationYear;
		this.expirationMonth = expirationMonth;
		this.strikePrice = strikePrice;
		this.putOrCall = putOrCall;
		
		optionRoot = OptionMarketDataUtils.getOptionRootSymbol(optionSymbol.getFullSymbol());
		parseUIValues();
	}

	public OptionContractData(String pOptionRoot, String uiExpirationYear, String uiExpirationMonth, String uiStrikePrice, Integer pPutOrCall) {
		expirationYearUIString = uiExpirationYear;
		expirationMonthUIString = uiExpirationMonth;
		strikePriceUIString = uiStrikePrice;
		optionRoot = pOptionRoot;
		putOrCall = pPutOrCall;
	}

	/**
	 * @return true if this is a put option, false if this is a call
	 */
	public int getPutOrCall() {
		return putOrCall;
	}

	public Integer getExpirationMonth() {
		return expirationMonth;
	}

	public Integer getExpirationYear() {
		return expirationYear;
	}

	/**
	 * @return the option symbol for the specific contract, e.g. MSQ+FN
	 */
	public MSymbol getOptionSymbol() {
		return optionSymbol;
	}

	public String getOptionRoot() {
		return optionRoot;
	}

	public BigDecimal getStrikePrice() {
		return strikePrice;
	}

	public MSymbol getUnderlyingSymbol() {
		return underlyingSymbol;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((expirationMonth == null) ? 0 : expirationMonth.hashCode());
		result = PRIME * result + ((expirationYear == null) ? 0 : expirationYear.hashCode());
		result = PRIME * result + ((optionSymbol == null) ? 0 : optionSymbol.hashCode());
		result = PRIME * result + (putOrCall == PutOrCall.PUT ? 1231 : 1237);
		result = PRIME * result + ((strikePrice == null) ? 0 : strikePrice.hashCode());
		result = PRIME * result + ((underlyingSymbol == null) ? 0 : underlyingSymbol.hashCode());
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
		final OptionContractData other = (OptionContractData) obj;
		if (expirationMonth == null) {
			if (other.expirationMonth != null)
				return false;
		} else if (!expirationMonth.equals(other.expirationMonth))
			return false;
		if (expirationYear == null) {
			if (other.expirationYear != null)
				return false;
		} else if (!expirationYear.equals(other.expirationYear))
			return false;
		if (optionSymbol == null) {
			if (other.optionSymbol != null)
				return false;
		} else if (!optionSymbol.equals(other.optionSymbol))
			return false;
		if (putOrCall != other.putOrCall)
			return false;
		if (strikePrice == null) {
			if (other.strikePrice != null)
				return false;
		} else if (!strikePrice.equals(other.strikePrice))
			return false;
		if (underlyingSymbol == null) {
			if (other.underlyingSymbol != null)
				return false;
		} else if (!underlyingSymbol.equals(other.underlyingSymbol))
			return false;
		return true;
	}

	public static OptionContractData fromFieldMap(MSymbol underlyingSymbol, FieldMap optionGroup) throws FieldNotFound, ParseException{

		String optionSymbolStr = optionGroup.getString(Symbol.FIELD);

		int putOrCall;

		putOrCall = OptionMarketDataUtils.getOptionType(optionGroup);

		Pair<Integer, Integer> yearMonth = OptionMarketDataUtils.getMaturityMonthYear(optionGroup);
		Integer expirationMonth = yearMonth.getFirstMember();
		Integer expirationYear = yearMonth.getSecondMember();

		String strikeStr = optionGroup.getString(StrikePrice.FIELD);
		BigDecimal strike = new BigDecimal(strikeStr);

		MSymbol optionSymbol = new MSymbol(optionSymbolStr);
		OptionContractData optionData = new OptionContractData(
				underlyingSymbol, optionSymbol, expirationYear, expirationMonth, strike,
				putOrCall);
		return optionData;
	}
	
	private void parseUIValues() {
		expirationMonthUIString = OPTION_DATE_HELPER.getMonthAbbreviation(expirationMonth);
		expirationYearUIString = OPTION_DATE_HELPER.formatYear(expirationYear);
		strikePriceUIString = strikePrice.toPlainString();
	}

	public String getExpirationYearUIString() {
		return expirationYearUIString;
	}

	public String getExpirationMonthUIString() {
		return expirationMonthUIString;
	}

	public String getStrikePriceUIString() {
		return strikePriceUIString;
	}

	public static class UIOnlyComparator implements
		Comparator<OptionContractData> {

		public int compare(OptionContractData ocd1, OptionContractData ocd2) {
			if (ocd1 == ocd2)
				return 0;
			
			if (ocd1 == null)
				return ocd2 == null ? 0 : -1;
			else if (ocd2 == null){
				return 1;
			}
			int compVal;
			compVal = compareHelper(ocd1.expirationMonthUIString, ocd2.expirationMonthUIString);
			if (compVal != 0) return compVal;

			compVal = compareHelper(ocd1.expirationYearUIString, ocd2.expirationYearUIString);
			if (compVal != 0) return compVal;

			compVal = compareHelper(ocd1.optionRoot, ocd2.optionRoot);
			if (compVal != 0) return compVal;
			
			compVal = compareHelper(ocd1.putOrCall, ocd2.putOrCall);
			if (compVal != 0) return compVal;

			compVal = compareHelper(ocd1.strikePriceUIString, ocd2.strikePriceUIString);
			return compVal;

		}

		private final <T> int compareHelper(Comparable<T> c1, T c2){
			if (c1 == null){
				return c2 == null ? 0 : -1;
			} else if (c2 == null){
				// c1 is not null
				return 1;
			}
			return c1.compareTo(c2);
		}
	}
}
