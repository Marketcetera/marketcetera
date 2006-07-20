package org.marketcetera.quotefeed;

import org.marketcetera.core.MSymbol;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class AdminMessageBase implements FeedMessage {
    private String message;

    public AdminMessageBase(String message) {
        this.message = message;
    }

    public String toString()
    {
        return message;
    }

    public MSymbol getSymbol() {
        return null;
    }
}
