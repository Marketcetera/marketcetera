package org.marketcetera.photon.parser;

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class Token {
	protected String image;

	protected int position;

	/**
	 * Creates a new Token, given the string image of the token, and its
	 * position in the input to the Lexer.
	 * 
	 * @param image
	 *            the string representation of the token
	 * @param position
	 *            the position of the first character of the token in the input
	 *            string
	 */
	public Token(String image, int position) {
		super();
		this.image = image;
		this.position = position;
	}

	/**
	 * The image of the token from the input string to the lexer
	 * 
	 * @return Returns the string image of the token
	 */
	public String getImage() {
		return image;
	}

	public String toString() {
		return image;
	}

	/**
	 * Gets the position of this image in the input string of the lexer
	 * 
	 * @return The position of this token in the input string to the lexer
	 */
	public int getPosition() {
		return position;
	}

}
