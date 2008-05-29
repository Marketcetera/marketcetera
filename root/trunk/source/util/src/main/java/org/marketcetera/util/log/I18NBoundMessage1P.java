package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage1P} and its one parameter.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessage1P
    extends I18NBoundMessageBase<I18NMessage1P>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Object...)
     */

    public I18NBoundMessage1P
        (I18NMessage1P message,
         Object p1)
    {
        super(message,p1);
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
}
