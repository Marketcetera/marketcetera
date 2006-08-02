package org.marketcetera.quotefeed;

import org.marketcetera.core.MSymbol;
import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
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
