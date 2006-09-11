package org.marketcetera.photon.parser;

/**
 * A simple token representing a string of non-whitespace characters
 * 
 * @author gmiller
 *
 */
public class StringToken extends Token
{
    /**
     * Creates a new StringToken given the string image of the token
     * and its position in the input text
     * @param image the string image of the token
     * @param position the position of the token in the input text
     */
    public StringToken(String image, int position)
    {
        super(image, position);
    }
}