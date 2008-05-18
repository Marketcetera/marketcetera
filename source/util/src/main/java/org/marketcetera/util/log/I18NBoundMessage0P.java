package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * A bound message, representing a {@link I18NMessage0P}.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessage0P
    extends I18NBoundMessageBase
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Object...)
     */

    public I18NBoundMessage0P
        (I18NMessage0P message)
    {
        super(message);
    }


    // I18NBoundMessageBase.
    
    @Override
    public I18NMessage0P getMessage()
    {
        return (I18NMessage0P)(super.getMessage());
    }
}
