package org.marketcetera.core.marketdata;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.event.HasInstrument;
import org.marketcetera.core.event.HasUnderlyingInstrument;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.Option;

/**
 * Encapsulates elements of a marker data request to an {@link Exchange}.
 * 
 * <p>To create an <code>ExchangeRequest</code>, use an {@link ExchangeRequestBuilder}.
 *
 * @version $Id: ExchangeRequest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@Immutable
public final class ExchangeRequest
        implements Serializable, HasInstrument, HasUnderlyingInstrument
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
     * Get the underlying instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getUnderlyingInstrument()
    {
        return underlyingInstrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        if(instrument == null) {
            return null;
        }
        return getInstrument().getSymbol();
    }
    /**
     * Indicates if this request specifies only an
     * underlying <code>Instrument</code>.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isForUnderlyingOnly()
    {
        return instrument == null &&
               underlyingInstrument != null;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("ExchangeRequest [instrument=%s, underlyingInstrument=%s]", //$NON-NLS-1$
                             instrument,
                             underlyingInstrument);
    }
    /**
     * Create a new ExchangeRequest instance.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @throws IllegalArgumentException if the request is not valid
     */
    ExchangeRequest(Instrument inInstrument,
                    Instrument inUnderlyingInstrument)
    {
        instrument = inInstrument;
        underlyingInstrument = inUnderlyingInstrument;
        validate();
    }
    /**
     * Validates the <code>ExchangeRequest</code>.
     *
     * @throws IllegalArgumentException if the request is not valid
     */
    private void validate()
    {
        // if instrument is null, underlying must be specified
        if(instrument == null &&
           underlyingInstrument == null) {
            throw new IllegalArgumentException(Messages.INSTRUMENT_OR_UNDERLYING_INSTRUMENT_REQUIRED.getText());
        }
        // if instrument is specified and its an option, underlying must be specified
        if(instrument != null &&
           instrument instanceof Option &&
           underlyingInstrument == null) {
            throw new IllegalArgumentException(Messages.OPTION_REQUIRES_UNDERLYING_INSTRUMENT.getText(instrument));
        }
    }
    /**
     * the instrument of the exchange request, may be <code>null</code>
     */
    private final Instrument instrument;
    /**
     * the underlying instrument of the exchange request, may be <code>null</code>
     */
    private final Instrument underlyingInstrument;
    private static final long serialVersionUID = 1L;
}
