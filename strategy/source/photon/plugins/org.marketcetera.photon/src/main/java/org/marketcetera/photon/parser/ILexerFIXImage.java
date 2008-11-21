package org.marketcetera.photon.parser;

public interface ILexerFIXImage extends ILexerImage {
	char getFIXCharValue();
	
	int getFIXIntValue();
	
	String getFIXStringValue();
}
