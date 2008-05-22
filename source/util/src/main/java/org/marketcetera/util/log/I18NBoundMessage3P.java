package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage3P} and its three parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessage3P
    extends I18NBoundMessageBase
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Object...)
     */

    public I18NBoundMessage3P
        (I18NMessage3P message,
         Object p1,
         Object p2,
         Object p3)
    {
        super(message,p1,p2,p3);
    }


    // I18NBoundMessageBase.
    
    @Override
    public I18NMessage3P getMessage()
    {
        return (I18NMessage3P)(super.getMessage());
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

    /**
     * Returns the receiver's third parameter.
     *
     * @return The parameter.
     */

    public Object getParam3()
    {
        return getParams()[2];
    }
}
