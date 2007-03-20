package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FeedException extends Exception
{

    public FeedException() {
        super();
    }

    public FeedException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public FeedException(String arg0) {
        super(arg0);
    }

    public FeedException(Throwable arg0) {
        super(arg0);
    }

}