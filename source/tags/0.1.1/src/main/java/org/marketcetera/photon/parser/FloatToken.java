/**
 * 
 */
package org.marketcetera.photon.parser;

/**
 * Token representing a simple floating point number (e.g. 1.234).
 * 
 * @author gmiller
 *
 */
public class FloatToken extends NumberToken
{

	/**
	 * Creates a new FloatToken given a string token image and the position
	 * of that token in the input stream to the Lexer.  No exception will
	 * be thrown if the image is not in the format of a floating point number
	 * 
	 * 
	 * @param image the string image of the token
	 * @param position the position of the token in the input to the Lexer
	 * @see NumberToken#doubleValue()
	 * @see NumberToken#intValue()
	 */
	public FloatToken(String image, int position)
    {
        super(image, position);
    }
}