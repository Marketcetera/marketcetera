package org.marketcetera.photon.parser;

import jfun.parsec.FromToken;
import jfun.parsec.Tok;
import jfun.parsec.tokens.TokenType;
import jfun.parsec.tokens.TypedToken;

abstract class FromReserved<T> implements FromToken<T>{
	public T fromToken(Tok tok) {
		TypedToken reservedToken = (TypedToken)tok.getToken();
		if (reservedToken.getType()==TokenType.Reserved){
			return fromReserved(reservedToken);
		} else {
			return null;
		}
	}

	public abstract T fromReserved(TypedToken<TokenType> reserved);
	
}
