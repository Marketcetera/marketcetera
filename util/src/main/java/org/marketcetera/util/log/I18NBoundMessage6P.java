package org.marketcetera.util.log;

import java.io.Serializable;
import org.marketcetera.util.misc.ClassVersion;

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
    extends I18NBoundMessageBase<I18NMessage6P>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */

    public I18NBoundMessage6P
        (I18NMessage6P message,
         Serializable p1,
         Serializable p2,
         Serializable p3,
         Serializable p4,
         Serializable p5,
         Serializable p6)
    {
        super(message,p1,p2,p3,p4,p5,p6);
    }


    // INSTANCE METHODS.
    
    /**
     * Returns the receiver's first parameter.
     *
     * @return The parameter.
     */

    public Serializable getParam1()
    {
        return getParams()[0];
    }

    /**
     * Returns the receiver's second parameter.
     *
     * @return The parameter.
     */

    public Serializable getParam2()
    {
        return getParams()[1];
    }

    /**
     * Returns the receiver's third parameter.
     *
     * @return The parameter.
     */

    public Serializable getParam3()
    {
        return getParams()[2];
    }

    /**
     * Returns the receiver's fourth parameter.
     *
     * @return The parameter.
     */

    public Serializable getParam4()
    {
        return getParams()[3];
    }

    /**
     * Returns the receiver's fifth parameter.
     *
     * @return The parameter.
     */

    public Serializable getParam5()
    {
        return getParams()[4];
    }

    /**
     * Returns the receiver's sixth parameter.
     *
     * @return The parameter.
     */

    public Serializable getParam6()
    {
        return getParams()[5];
    }
}
