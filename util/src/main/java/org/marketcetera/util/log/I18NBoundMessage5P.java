package org.marketcetera.util.log;

import java.io.Serializable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage5P} and its five parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NBoundMessage5P.java 17760 2018-11-14 14:54:11Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NBoundMessage5P.java 17760 2018-11-14 14:54:11Z colin $")
public class I18NBoundMessage5P
    extends I18NBoundMessageBase<I18NMessage5P>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Create a new I18NBoundMessage5P instance.
     *
     * @param message an <code>I18NMessage1P</code> value
     * @param p1 a <code>Serializable</code> value
     * @param p2 a <code>Serializable</code> value
     * @param p3 a <code>Serializable</code> value
     * @param p4 a <code>Serializable</code> value
     * @param p5 a <code>Serializable</code> value
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */
    public I18NBoundMessage5P(I18NMessage5P message,
                              Serializable p1,
                              Serializable p2,
                              Serializable p3,
                              Serializable p4,
                              Serializable p5)
    {
        super(message,p1,p2,p3,p4,p5);
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
}
