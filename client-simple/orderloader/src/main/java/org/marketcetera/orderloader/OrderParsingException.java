package org.marketcetera.orderloader;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/* $License */

/**
 * Indicates that an order could not be parsed.
 * 
 * @author Toli Kuznets
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class OrderParsingException 
    extends I18NException
    implements Messages
{
    private static final long serialVersionUID = 8562647175132494522L;

    public OrderParsingException(I18NBoundMessage message)
    {
        super(message);
    }

    public OrderParsingException(Throwable nested, I18NBoundMessage message)
    {
        super(nested, message);
    }
    public OrderParsingException(Throwable nested,
                                 String inField, 
                                 String inValue)
    {
        super(nested,
              new I18NBoundMessage2P(ERROR_PARSING_MESSAGE,
                                     inField,
                                     inValue));
    }
    public OrderParsingException(Throwable nested)
    {
        super(nested);
    }
}
