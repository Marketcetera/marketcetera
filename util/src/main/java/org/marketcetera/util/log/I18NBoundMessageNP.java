package org.marketcetera.util.log;

import java.io.Serializable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessageNP} and its arbitrary number of parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessageNP
    extends I18NBoundMessageBase<I18NMessageNP>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Create a new I18NBoundMessageNP instance.
     *
     * @param message an <code>I18NMessage1P</code> value
     * @param params a <code>Serializable...</code> value
     * @see I18NBoundMessageBase#I18NBoundMessageBase(I18NMessage,Serializable...)
     */
    public I18NBoundMessageNP(I18NMessageNP message,
                              Serializable... params)
    {
        super(message,params);
    }
}
