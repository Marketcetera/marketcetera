package org.marketcetera.quotefeed;

import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class QuoteFeedException extends Exception
{

    public QuoteFeedException() {
        super();
    }

    public QuoteFeedException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public QuoteFeedException(String arg0) {
        super(arg0);
    }

    public QuoteFeedException(Throwable arg0) {
        super(arg0);
    }

}