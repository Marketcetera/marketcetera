/**
 * 
 */
package org.marketcetera.photon.parser;

/**
 * Number token represents a token in the Lexer input stream that is a number,
 * either simple floating point (e.g 1.754) or integer (e.g. 7).  Number format
 * problems will not show up until a call to doubleValue() or intValue() at 
 * which time a NumberFormatException will be thrown.
 * 
 * @author gmiller
 *
 */
public class NumberToken extends Token
{

	
	/**
	 * Creates a new NumberToken with the given string image, and its position
	 * in the Lexer input string.  This will not throw an exception if the 
	 * input string is not formatted as a number.
	 * 
	 * @param image the string image of the token
	 * @param position the position of the token in the input string
	 */
	public NumberToken(String image, int position)
    {
        super(image, position);
    }

	
    /**
	 * Converts the string image of the token into a double.
	 * 
     * @return a double with the value represented by the token image
     * @throws java.lang.NumberFormatException if the image of the token is not formatted as a double
     * @see java.lang.Double#parseDouble(String)
     */
    public double doubleValue()
    {
        return Double.parseDouble(image);
    }

    /**
	 * Converts the string image of the token into an integer.  If the token represents
	 * a floating point number, the number will be truncated before it is returned.
	 * 
     * @return an int with the value represented by the token image
     * @throws java.lang.NumberFormatException if the image of the token is not formatted as a number
     */
    public int intValue()
    {
        return (int)doubleValue();
    }
}