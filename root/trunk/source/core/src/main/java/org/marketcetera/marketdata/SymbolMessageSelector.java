package org.marketcetera.marketdata;

import org.marketcetera.core.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Symbol;

public class SymbolMessageSelector implements IMessageSelector {

	MSymbol exactSymbol;
	public SymbolMessageSelector(MSymbol exactSymbol) {
		this.exactSymbol = exactSymbol;
	}
	public MSymbol getExactSymbol() {
		return exactSymbol;
	}

	public boolean select(Message aMessage) {
		if (exactSymbol != null){
			try {
				if (exactSymbol.equals(aMessage.getString(Symbol.FIELD)))
				{
					return true;
				}
			} catch (FieldNotFound e) {
			}
		}
		return false;
			
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SymbolMessageSelector) {
			SymbolMessageSelector symbolSelector = (SymbolMessageSelector) obj;
			if (exactSymbol == null)
				return (symbolSelector.exactSymbol==null);
			else
				return exactSymbol.equals(symbolSelector.exactSymbol);
		} else {
			return false;
		}
	}
	
	
	
}
