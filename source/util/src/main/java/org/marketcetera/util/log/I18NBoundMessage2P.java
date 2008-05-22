package org.marketcetera.util.log;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage2P} and its two parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class I18NBoundMessage2P
    extends I18NBoundMessageBase
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Object...)
     */

    public I18NBoundMessage2P
        (I18NMessage2P message,
         Object p1,
         Object p2)
    {
        super(message,p1,p2);
    }


    // I18NBoundMessageBase.
    
    @Override
    public I18NMessage2P getMessage()
    {
        return (I18NMessage2P)(super.getMessage());
    }


    // INSTANCE METHODS.
    
    /**
     * Returns the receiver's first parameter.
     *
     * @return The parameter.
     */

    public Object getParam1()
    {
        return getParams()[0];
    }

    /**
     * Returns the receiver's second parameter.
     *
     * @return The parameter.
     */

    public Object getParam2()
    {
        return getParams()[1];
    }
}
