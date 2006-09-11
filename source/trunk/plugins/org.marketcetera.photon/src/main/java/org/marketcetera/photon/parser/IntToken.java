/**
 * 
 */
package org.marketcetera.photon.parser;

/**
 * Token representing a simple integer (e.g. 42).
 * 
 * @author gmiller
 * 
 */
public class IntToken extends NumberToken {
	/**
	 * Creates a new IntToken given a string token image and the position of
	 * that token in the input stream to the Lexer. No exception will be thrown
	 * if the image is not in the format of an integer
	 * 
	 * 
	 * @param image
	 *            the string image of the token
	 * @param position
	 *            the position of the token in the input to the Lexer
	 * @see NumberToken#doubleValue()
	 * @see NumberToken#intValue()
	 */
	public IntToken(String image, int position) {
		super(image, position);
	}

}