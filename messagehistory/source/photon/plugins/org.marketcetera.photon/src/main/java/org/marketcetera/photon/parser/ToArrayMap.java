package org.marketcetera.photon.parser;

import jfun.parsec.Map;
import jfun.parsec.Tok;

class ToArrayMap implements Map<Tok, Tok[]>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8492031658233433132L;

	public Tok[] map(Tok v) {
		return new Tok[]{v};
	}
	
};
