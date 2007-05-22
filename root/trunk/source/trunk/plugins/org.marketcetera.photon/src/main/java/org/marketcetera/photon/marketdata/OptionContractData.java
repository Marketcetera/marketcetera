package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;

import org.marketcetera.core.MSymbol;

public class OptionContractData {
	private MSymbol underlyingSymbol;
	private MSymbol optionSymbol;
	private String expirationYear;
	private String expirationMonth;
	private BigDecimal strikePrice;
	private boolean putWhenTrue;
	
	/**
	 * @return true if this is a put option, false if this is a call
	 */
	public boolean isPut() {
		return putWhenTrue;
	}

	/**
	 * @param optionSymbol the option symbol for the specific contract, e.g. MSQ+FN
	 */
	public OptionContractData(MSymbol underlyingSymbol, MSymbol optionSymbol, String expirationYear, String expirationMonth, BigDecimal strikePrice, boolean putWhenTrue) {
		this.underlyingSymbol = underlyingSymbol;
		this.optionSymbol = optionSymbol;
		this.expirationYear = expirationYear;
		this.expirationMonth = expirationMonth;
		this.strikePrice = strikePrice;
		this.putWhenTrue = putWhenTrue;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	/**
	 * @return the option symbol for the specific contract, e.g. MSQ+FN
	 */
	public MSymbol getOptionSymbol() {
		return optionSymbol;
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
		result = PRIME * result + (putWhenTrue ? 1231 : 1237);
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
		if (putWhenTrue != other.putWhenTrue)
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
}
