package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage6P} and its six parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessage6P
    extends I18NBoundMessageBase
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Object...)
     */

    public I18NBoundMessage6P
        (I18NMessage6P message,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        super(message,p1,p2,p3,p4,p5,p6);
    }


    // I18NBoundMessageBase.
    
    @Override
    public I18NMessage6P getMessage()
    {
        return (I18NMessage6P)(super.getMessage());
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

    /**
     * Returns the receiver's fourth parameter.
     *
     * @return The parameter.
     */

    public Object getParam4()
    {
        return getParams()[3];
    }

    /**
     * Returns the receiver's fifth parameter.
     *
     * @return The parameter.
     */

    public Object getParam5()
    {
        return getParams()[4];
    }

    /**
     * Returns the receiver's sixth parameter.
     *
     * @return The parameter.
     */

    public Object getParam6()
    {
        return getParams()[5];
    }
}
