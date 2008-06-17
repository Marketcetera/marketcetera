package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessageNP} and its arbitrary number of parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessageNP
    extends I18NBoundMessageBase<I18NMessageNP>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Object...)
     */

    public I18NBoundMessageNP
        (I18NMessageNP message,
         Object... params)
    {
        super(message,params);
    }
}
