package org.marketcetera.photon.scripting;

import java.util.HashSet;
import java.util.Set;

import org.marketcetera.core.MSymbol;
import org.marketcetera.quotefeed.IMessageListener;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Symbol;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * todo:doc
 *
 * @author andrei@lissovski.org
 */
public class MockMessageListener implements IMessageListener {
	Set<MSymbol> onQuoteCallsSymbols = new HashSet<MSymbol>();
	
	
	/**
	 * Checks if <code>onQuote()</code> has been called on this mock object for the given symbol. 
	 */
	public boolean onQuoteCalled(MSymbol symbol) {
		return onQuoteCallsSymbols.contains(symbol);
	}

	/**
	 * Resets all call tracking data on this mock object.
	 */
	public void reset() {
		onQuoteCallsSymbols.clear();
	}

	
	public void onQuote(Message message) {
		try {
			onQuoteCallsSymbols.add(new MSymbol(message.getString(Symbol.FIELD)));
		} catch (FieldNotFound e) {
			//agl shouldn't happen
			e.printStackTrace();
		}
	}

	public void onQuotes(Message[] messages) {
		throw new NotImplementedException();
	}

	public void onTrade(Message message) {
		throw new NotImplementedException();
	}

	public void onTrades(Message[] messages) {
		throw new NotImplementedException();
	}

}

