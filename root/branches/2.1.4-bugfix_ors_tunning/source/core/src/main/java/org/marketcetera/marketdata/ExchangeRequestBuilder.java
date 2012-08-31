package org.marketcetera.marketdata;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Builds and creates <code>ExchangeRequest</code> objects.
 * 
 * <p>No validation is done of the attribute values until the
 * <code>ExchangeRequest</code> is created.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class ExchangeRequestBuilder
{
    /**
     * Creates a new <code>ExchangeRequestBuilder</code>.
     *
     * @return an <code>ExchangeRequestBuilder</code>
     */
    public static ExchangeRequestBuilder newRequest()
    {
        return new ExchangeRequestBuilder();
    }
    /**
     * Creates a new <code>ExchangeRequest</code> with the current values
     * of this <code>ExchangeRequestBuilder</code>.
     * 
     * <p>Validation of the <code>ExchangeRequest</code> is done at this time.
     * See {@link ExchangeRequest#ExchangeRequest(Instrument, Instrument)} for
     * details.
     *
     * @return an <code>ExchangeRequest</code> value
     */
    public ExchangeRequest create()
    {
        return new ExchangeRequest(getInstrument(),
                                   getUnderlyingInstrument());
    }
    /**
     * Sets the underlying instrument value.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     */
    public ExchangeRequestBuilder withUnderlyingInstrument(Instrument inUnderlyingInstrument)
    {
        underlyingInstrument = inUnderlyingInstrument;
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public ExchangeRequestBuilder withInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
        return this;
    }
    /**
     * Create a new ExchangeRequestBuilder instance.
     */
    private ExchangeRequestBuilder()
    {
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    private Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the underlying instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    private Instrument getUnderlyingInstrument()
    {
        return underlyingInstrument;
    }
    /**
     * the instrument to use to create the <code>ExchangeRequest</code> 
     */
    private Instrument instrument = null;
    /**
     * the underlying instrument to use to create the <code>ExchangeRequest</code>
     */
    private Instrument underlyingInstrument = null;
}
