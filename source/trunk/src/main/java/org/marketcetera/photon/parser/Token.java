package org.marketcetera.photon.parser;

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class Token {
    protected String image;
    protected int position;


    public static class NumberToken extends Token
    {
        public NumberToken(String image, int position)
        {
            super(image, position);
        }

        public double doubleValue()
        {
            return Double.parseDouble(image);
        }

        public int intValue()
        {
            return (int)doubleValue();
        }
    }

    public static class IntToken extends NumberToken
    {
        public IntToken(String image, int position)
        {
            super(image, position);
        }


    }

    public static class StringToken extends Token
    {
        public StringToken(String image, int position)
        {
            super(image, position);
        }
    }

    public static class FloatToken extends NumberToken
    {
        public FloatToken(String image, int position)
        {
            super(image, position);
        }
    }

    public Token(String image, int position) {
        super();
        this.image = image;
        this.position = position;
    }

    /**
     * @return Returns the image.
     */
    public String getImage() {
        return image;
    }

    public String toString() { return image; }

    /**
     * @return Returns the position.
     */
    public int getPosition() {
        return position;
    }

}
