package org.marketcetera.marketdata.marketcetera;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.symbology.IEquityOptionSymbol;
import org.marketcetera.trade.MSymbol;

@ClassVersion("$Id$") //$NON-NLS-1$
public class MarketceteraOptionSymbol 
    extends MSymbol 
    implements IEquityOptionSymbol, Messages
{

	static final Pattern optionSymbolPattern = Pattern.compile("(\\w{1,3})\\+(\\w)(\\w)"); //$NON-NLS-1$
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
			throw new IllegalArgumentException(INVALID_EQUITY_OPTION_SYMBOL.getText(symbolString));
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


	public String getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(root).append("+").append(expirationCode).append(strikeCode).toString(); //$NON-NLS-1$ 
	}

	
}
