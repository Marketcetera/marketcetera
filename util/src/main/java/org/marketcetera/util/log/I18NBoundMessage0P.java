package org.marketcetera.util.log;

import java.io.Serializable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message, representing a {@link I18NMessage0P}.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NBoundMessage0P.java 17760 2018-11-14 14:54:11Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NBoundMessage0P.java 17760 2018-11-14 14:54:11Z colin $")
public class I18NBoundMessage0P
    extends I18NBoundMessageBase<I18NMessage0P>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Create a new I18NBoundMessage0P instance.
     *
     * @param message an <code>I18NMessage0P</code> value
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */
    public I18NBoundMessage0P(I18NMessage0P message)
    {
        super(message);
    }
}
