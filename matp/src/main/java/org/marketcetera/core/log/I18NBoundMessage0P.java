package org.marketcetera.core.log;

import java.io.Serializable;

import org.marketcetera.core.misc.ClassVersion;

/**
 * A bound message, representing a {@link I18NMessage0P}.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NBoundMessage0P.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NBoundMessage0P.java 16154 2012-07-14 16:34:05Z colin $")
public class I18NBoundMessage0P
    extends I18NBoundMessageBase<I18NMessage0P>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */

    public I18NBoundMessage0P
        (I18NMessage0P message)
    {
        super(message);
    }
}
