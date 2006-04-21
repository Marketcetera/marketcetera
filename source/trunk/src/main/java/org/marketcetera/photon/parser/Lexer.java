package org.marketcetera.photon.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.core.ClassVersion;


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

    public Lexer() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void setInput(String inputString) {
        this.mInput = inputString;
        this.mTokenMatcher = tokenPattern.matcher(inputString);
        this.mTokenMatcherGroupCount = mTokenMatcher.groupCount();
        position = 0;
    }

    public Token getNextToken() {
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
                for (int i = 1; i < mTokenMatcherGroupCount + 1; i++)
                {
                    if ((result = mTokenMatcher.group(i)) != null)
                    {
                        switch (i)
                        {
                        case 1:
                            resultToken = new Token.FloatToken(result, startPosition);
                            break;
                        case 2:
                            resultToken = new Token.IntToken(result, startPosition);
                            break;
                        case 3:
                            resultToken = new Token.StringToken(result, startPosition);
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

    Token peek()
    {
        if (mNextToken == null)
        {
            mNextToken =  getNextToken();
        }
        return mNextToken;
    }


}
