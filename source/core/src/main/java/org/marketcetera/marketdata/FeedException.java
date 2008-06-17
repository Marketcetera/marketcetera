package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

/**
 * Indicates an error throw during Market Data Feed operations.
 * 
 * @author Graham Miller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FeedException 
    extends MarketceteraException
{
    private static final long serialVersionUID = 1963381129325900265L;

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