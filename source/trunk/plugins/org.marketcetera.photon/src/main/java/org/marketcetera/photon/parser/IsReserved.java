package org.marketcetera.photon.parser;

import jfun.parsec.tokens.TokenType;
import jfun.parsec.tokens.TypedToken;

class IsReserved extends FromReserved<Boolean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6097891393109049821L
	;
	private String expected;
	public IsReserved(String expected) {
		this.expected = expected;
	}
	
	@Override
	public Boolean fromReserved(TypedToken<TokenType> reserved) {
		if (expected.equals(reserved.getText())) {
			return Boolean.TRUE;
		} else {
			return null;
		}
	}
}
