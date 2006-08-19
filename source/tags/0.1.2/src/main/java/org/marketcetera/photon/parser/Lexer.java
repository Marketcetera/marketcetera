package org.marketcetera.photon.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.core.ClassVersion;



/**
 * This is a polymorphic lexer that will divide an input string into tokens based
 * on regular expression pattern matching.  The lexer will attempt to parse all
 * tokens as floating point numbers, integers, and finally as any string of non-
 * whitespace characters.
 * 
 * @author gmiller
s *
 */
@ClassVersion("$Id$")
public class Lexer {

    String mInput;

    Pattern tokenPattern = Pattern.compile(
            "(?:^|\\s)(-?(?:\\d+\\.\\d*|\\.\\d+))(?:$|\\s)|" // floating point number with word boundary
            + "(?:^|\\s)(-?\\d+)(?:$|\\s)|" // integer with word boundary
            + "([\\S]+)" // any string of non-whitespace characters
            );

    Matcher mTokenMatcher;
    int mTokenMatcherGroupCount;
    Token mNextToken;

    int position = 0;

    /**
     * Creates a new Lexer.  Basic usage pattern will create a new Lexer, then
     * call setInput(), followed by multiple calls to getNextToken()
     * 
     * @see Lexer#setInput(String)
     * @see Lexer#getNextToken()
     */
    public Lexer() {
        super();
    }

    
    /**
     * Sets the lexical input string for this Lexer.
     * 
     * @param inputString the string from which to extract tokens
     */
    public void setInput(String inputString) {
        this.mInput = inputString;
        this.mTokenMatcher = tokenPattern.matcher(inputString);
        this.mTokenMatcherGroupCount = mTokenMatcher.groupCount();
        position = 0;
    }

    /**
     * Gets the next token from the input string.  Returns one of the subclasses
     * of Token depending on the first type matched.  First the lexer will attempt
     * to read the next token as a floating point number (FloatToken).  Failing that,
     * it will attempt to read the next token as an integer (IntToken).  Finally it 
     * will simply return the token as a StringToken.
     * 
     * @see Token
     * @see FloatToken
     * @see IntToken
     * @see StringToken
     * @return the next token from the input string
     */
    public Token getNextToken() {
    	// return the stored next token, if any
    	if (mNextToken != null)
        {
            Token returnToken = mNextToken;
            mNextToken = null;
            return returnToken;
        }

        String result = null;
        Token resultToken = null;
        int startPosition;
        int theLength = mInput.length();
        if (position < theLength) {
            mTokenMatcher.region(position, theLength); // sets the region we're interested in
            if (mTokenMatcher.find())
            {
                startPosition = position;
                position = mTokenMatcher.end();
                // this is kind of ugly.  We need to loop through 
                // all of the groups in the regular expression to
                // see which if any matched the input
                for (int i = 1; i < mTokenMatcherGroupCount + 1; i++)
                {
                    if ((result = mTokenMatcher.group(i)) != null)
                    {
                        switch (i)
                        {
                        case 1:
                            resultToken = new FloatToken(result, startPosition);
                            break;
                        case 2:
                            resultToken = new IntToken(result, startPosition);
                            break;
                        case 3:
                            resultToken = new StringToken(result, startPosition);
                            break;

                        }
                    }
                }
            } else {
                position = mInput.length();
            }
        }
        return resultToken;
    }


    /**
     * Returns the next token in the input string, but does not advance the lexer
     * to the next token.  Subsequent calls to peek() and the very next call to 
     * nextToken will return the same object.
     * 
     * @return the next token in the input string
     */
    public Token peek()
    {
        if (mNextToken == null)
        {
            mNextToken =  getNextToken();
        }
        return mNextToken;
    }

}
