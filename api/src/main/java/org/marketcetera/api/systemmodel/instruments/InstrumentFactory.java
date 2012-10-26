package org.marketcetera.api.systemmodel.instruments;

/* $License$ */

/**
 * Creates values of a specific <code>Instrument</code> type.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface InstrumentFactory<Clazz extends Instrument>
{
    /**
     * Creates an instrument value.
     *
     * @param inSymbol a <code>String</code> value
     * @return a <code>Clazz</code> value
     */
    public Clazz create(String inSymbol);
    /**
     * Creates an instrument value from a full symbol representation.
     *
     * @param inFullSymbol a <code>String</code> value
     * @return a <code>Clazz</code> value
     */
    public Clazz createFromFullSymbol(String inFullSymbol);
}
