package org.marketcetera.symbology;

public interface IEquityOptionSymbol {
	public String getRoot();
	public char getExpirationCode();
	public char getStrikeCode();
}
