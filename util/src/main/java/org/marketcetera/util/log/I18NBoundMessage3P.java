package org.marketcetera.util.log;

import java.io.Serializable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage3P} and its three parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NBoundMessage3P.java 17760 2018-11-14 14:54:11Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NBoundMessage3P.java 17760 2018-11-14 14:54:11Z colin $")
public class I18NBoundMessage3P
    extends I18NBoundMessageBase<I18NMessage3P>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Create a new I18NBoundMessage3P instance.
     *
     * @param message an <code>I18NMessage1P</code> value
     * @param p1 a <code>Serializable</code> value
     * @param p2 a <code>Serializable</code> value
     * @param p3 a <code>Serializable</code> value
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */
    public I18NBoundMessage3P(I18NMessage3P message,
                              Serializable p1,
                              Serializable p2,
                              Serializable p3)
    {
        super(message,p1,p2,p3);
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
}
