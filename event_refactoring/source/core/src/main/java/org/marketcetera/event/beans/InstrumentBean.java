package org.marketcetera.event.beans;

import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.HasInstrument;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link HasInstrument}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class InstrumentBean
        implements Serializable
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Sets the instrument value.
     *
     * @param an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * the instrument value 
     */
    private volatile Instrument instrument;
    private static final long serialVersionUID = 1L;
}
