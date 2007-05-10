package org.marketcetera.photon.marketdata;

import org.marketcetera.core.MSymbol;

public class OptionExpirationMarketData {
	private MSymbol underlyingSymbol;

	private MSymbol optionSymbol;

	private String expirationDateString;

	private String expirationYear;

	private String expirationMonth;
	
	private boolean putWhenTrue;

	/**
	 * @return true if this is a put option, false if this is a call
	 */
	public boolean isPut() {
		return putWhenTrue;
	}

	public OptionExpirationMarketData(MSymbol underlyingSymbol, MSymbol optionSymbol, String expirationDateString, String expirationYear, String expirationMonth, boolean putWhenTrue) {
		super();
		this.underlyingSymbol = underlyingSymbol;
		this.optionSymbol = optionSymbol;
		this.expirationDateString = expirationDateString;
		this.expirationYear = expirationYear;
		this.expirationMonth = expirationMonth;
		this.putWhenTrue = putWhenTrue;
	}


	public String getExpirationDateString() {
		return expirationDateString;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public MSymbol getOptionSymbol() {
		return optionSymbol;
	}

	public boolean isPutWhenTrue() {
		return putWhenTrue;
	}

	public MSymbol getUnderlyingSymbol() {
		return underlyingSymbol;
	}
	
	
}