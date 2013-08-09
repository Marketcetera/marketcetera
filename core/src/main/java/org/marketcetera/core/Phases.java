package org.marketcetera.core;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum Phases
{
    SERVICE(0),
    DATAFLOW(1);
    /**
     * Get the phase value.
     *
     * @return an <code>int</code> value
     */
    public int getPhase()
    {
        return phase;
    }
    private Phases(int inPhase)
    {
        phase = inPhase;
    }
    private final int phase;
}
