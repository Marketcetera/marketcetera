package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MemoizedHashCombinator;

@ClassVersion("$Id$")
public class SymbolSide extends MemoizedHashCombinator<MSymbol, String> {
	public SymbolSide(MSymbol symbol, String side){
		super(symbol, side);
	}
}
