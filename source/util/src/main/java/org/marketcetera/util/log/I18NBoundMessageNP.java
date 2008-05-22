package org.marketcetera.util.log;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessageNP} and its arbitrary number of parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class I18NBoundMessageNP
    extends I18NBoundMessageBase
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


    // I18NBoundMessageBase.
    
    @Override
    public I18NMessageNP getMessage()
    {
        return (I18NMessageNP)(super.getMessage());
    }
}
