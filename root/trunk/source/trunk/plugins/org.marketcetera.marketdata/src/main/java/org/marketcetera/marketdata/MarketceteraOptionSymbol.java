package org.marketcetera.marketdata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.core.MSymbol;
import org.marketcetera.symbology.IEquityOptionSymbol;


public class MarketceteraOptionSymbol extends MSymbol implements IEquityOptionSymbol {

	static final Pattern optionSymbolPattern = Pattern.compile("(\\w{1,3})\\+(\\w)(\\w)");
	private char expirationCode;
	private char strikeCode;
	private String root;
	
	public MarketceteraOptionSymbol(String symbolString) {
		super(symbolString);
		Matcher matcher = optionSymbolPattern.matcher(symbolString);
		if (matcher.matches()){
			root = matcher.group(1);
			expirationCode = matcher.group(2).charAt(0);
			strikeCode = matcher.group(3).charAt(0);
		} else {
			throw new IllegalArgumentException(symbolString+" is not a valid equity option symbol");
		}
	}
	
	
	public char getExpirationCode() {
		return expirationCode;
	}

	public char getStrikeCode() {
		return strikeCode;
	}
	
	public static boolean matchesPattern(String symbolString) {
		return optionSymbolPattern.matcher(symbolString).matches();
	}


	@Override
	public String toString() {
		return root+"+"+expirationCode+strikeCode;
	}

	
}
