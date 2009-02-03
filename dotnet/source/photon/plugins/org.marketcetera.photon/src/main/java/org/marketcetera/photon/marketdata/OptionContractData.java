package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.text.ParseException;

import org.marketcetera.core.Pair;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

/**
 * This class represents a descriptor for a particular option series.
 * It includes the underlying symbol, option symbol, option root, 
 * expiration year, expiration month, strike price, and put or call
 * 
 * @author gmiller
 *
 */
public class OptionContractData {

	private MSymbol underlyingSymbol;
	private MSymbol optionSymbol;
	private String optionRoot;
	private Integer expirationYear;
	private Integer expirationMonth;
	private BigDecimal strikePrice;
	private Integer putOrCall;

	/**
	 * Constructor that takes all the items in the descriptor.
	 * 
	 * @param underlyingSymbol the underlying (stock) symbol, like IBM
	 * @param optionSymbol the option symbol, like IBM+RE
	 * @param expirationYear the year of expiration for this option series
	 * @param expirationMonth the month of expiration for this option series
	 * @param strikePrice the strike price of this option series
	 * @param putOrCall {@link PutOrCall#PUT} if put, {@link PutOrCall#CALL} if call
	 * 
	 */
	public OptionContractData(MSymbol underlyingSymbol, MSymbol optionSymbol, Integer expirationYear, Integer expirationMonth, BigDecimal strikePrice, int putOrCall) {
		this.underlyingSymbol = underlyingSymbol;
		this.optionSymbol = optionSymbol;
		this.expirationYear = expirationYear;
		this.expirationMonth = expirationMonth;
		this.strikePrice = strikePrice;
		this.putOrCall = putOrCall;
		
		optionRoot = OptionMarketDataUtils.getOptionRootSymbol(optionSymbol.getFullSymbol());
	}


	/**
	 * Get whether this represents a put or call.
	 * 
	 * @return {@link PutOrCall#PUT} if put, {@link PutOrCall#CALL} if call
	 */
	public int getPutOrCall() {
		return putOrCall;
	}

	/**
	 * Get the expiration month
	 * @return the expiration month
	 */
	public Integer getExpirationMonth() {
		return expirationMonth;
	}

	/**
	 * Get the expiration year
	 * @return the expiration year
	 */
	public Integer getExpirationYear() {
		return expirationYear;
	}

	/**
	 * Get the full option symbol.
	 * 
	 * @return the option symbol for the specific contract, e.g. MSQ+FN
	 */
	public MSymbol getOptionSymbol() {
		return optionSymbol;
	}

	/**
	 * Get the root of the option symbol
	 * @return the root of the option symbol, for example, MSQ
	 */
	public String getOptionRoot() {
		return optionRoot;
	}

	/**
	 * Get the strike price of this option
	 * @return the strike price as a BigDecimal
	 */
	public BigDecimal getStrikePrice() {
		return strikePrice;
	}

	/**
	 * Get the underlying symbol
	 * @return the underlying symbol as an {@link MSymbol}
	 */
	public MSymbol getUnderlyingSymbol() {
		return underlyingSymbol;
	}

	/**
	 * Calculate the hash code based on the internal components of this
	 */
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

	/**
	 * Determine if this is equal to the specified object by comparing
	 * all component parts, optionally ignoring the put or call field.
	 * @param obj the object to compare
	 * @param ignorePutOrCall true if put or call field should be ignored when determining equality
	 * @return true if equal to obj, false otherwise
	 */
	private boolean equalsImpl(Object obj, boolean ignorePutOrCall) {
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
		if (!ignorePutOrCall) {
			if (optionSymbol == null) {
				if (other.optionSymbol != null)
					return false;
			} else if (!optionSymbol.equals(other.optionSymbol))
				return false;

			if (!putOrCall.equals(other.putOrCall))
				return false;
		}
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
	
	@Override
	public boolean equals(Object obj) {
		return equalsImpl(obj, false);
	}
	
	/**
	 * Use this method to help find the corresponding put for a call contract,
	 * and vice versa.
	 * 
	 * @return true if this OptionContractData equals obj while ignoring
	 *         put/call.
	 */
	public boolean equalsIgnorePutOrCall(Object obj) {
		return equalsImpl(obj, true);
	}

	/**
	 * Construct an OptionContractData object from a {@link FieldMap} (such
	 * as a message), and some additional data.
	 * @param underlyingSymbol the underlying symbol to put into the new OptionContractData
	 * @param fieldMap the field map from which to extract values
	 * @return the new OptionContractData containing values from the FieldMap
	 * @throws FieldNotFound if a required field is missing from the field map
	 * @throws ParseException if a required field is incorrectly formatted
	 */
	public static OptionContractData fromFieldMap(MSymbol underlyingSymbol, FieldMap fieldMap) throws FieldNotFound, ParseException{

		String optionSymbolStr = fieldMap.getString(Symbol.FIELD);

		int putOrCall;

		putOrCall = OptionMarketDataUtils.getOptionType(fieldMap);

		Pair<Integer, Integer> yearMonth = OptionMarketDataUtils.getMaturityMonthYear(fieldMap);
		Integer expirationMonth = yearMonth.getFirstMember();
		Integer expirationYear = yearMonth.getSecondMember();

		String strikeStr = fieldMap.getString(StrikePrice.FIELD);
		BigDecimal strike = new BigDecimal(strikeStr);

		MSymbol optionSymbol = new MSymbol(optionSymbolStr);
		return new OptionContractData(
				underlyingSymbol, optionSymbol, expirationYear, expirationMonth, strike,
				putOrCall);
	}
	
}
