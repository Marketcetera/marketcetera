package org.marketcetera.core.util.log;

import java.io.Serializable;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage1P} and its one parameter.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NBoundMessage1P.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NBoundMessage1P.java 16063 2012-01-31 18:21:55Z colin $")
public class I18NBoundMessage1P
    extends I18NBoundMessageBase<I18NMessage1P>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */

    public I18NBoundMessage1P
        (I18NMessage1P message,
         Serializable p1)
    {
        super(message,p1);
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
}
