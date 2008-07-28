package org.marketcetera.orderloader;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

/* $License */

/**
 * Indicates that an order could not be parsed.
 * 
 * @author Toli Kuznets
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderParsingException 
    extends MarketceteraException
    implements Messages
{
    private static final long serialVersionUID = 8562647175132494522L;

    public OrderParsingException(String inField)
    {
        super(inField);
    }

    public OrderParsingException(String inField, Throwable nested)
    {
        super(inField, nested);
    }
    public OrderParsingException(String inField, 
                                 String inValue, 
                                 Throwable nested)
    {
        super(ERROR_PARSING_MESSAGE.getText(inField,
                                            inValue),
              nested);
    }
    public OrderParsingException(Throwable nested)
    {
        super(nested);
    }
}
