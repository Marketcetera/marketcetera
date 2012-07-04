package org.marketcetera.core.marketdata;

import java.util.Date;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.event.EventTestBase;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.Option;
import org.marketcetera.core.trade.OptionType;

import static org.junit.Assert.*;
import static org.marketcetera.core.marketdata.Messages.INSTRUMENT_OR_UNDERLYING_INSTRUMENT_REQUIRED;
import static org.marketcetera.core.marketdata.Messages.OPTION_REQUIRES_UNDERLYING_INSTRUMENT;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.marketdata.ExchangeRequest} and {@link org.marketcetera.core.marketdata.ExchangeRequestBuilder}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExchangeRequestTest.java 82329 2012-04-10 16:28:13Z colin $
 * @since 2.0.0
 */
public class ExchangeRequestTest
{
    /**
     * Tests the underlying instrument.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void underlying()
            throws Exception
    {
        ExchangeRequestBuilder builder = ExchangeRequestBuilder.newRequest();
        builder.withInstrument(equity);
        verifyBuilder(builder.withUnderlyingInstrument(equity),
                      equity,
                      equity);
        verifyBuilder(builder.withUnderlyingInstrument(option),
                      equity,
                      option);
        verifyBuilder(builder.withUnderlyingInstrument(null),
                      equity,
                      null);
    }
    /**
     * Tests the instrument.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void instrument()
            throws Exception
    {
        ExchangeRequestBuilder builder = ExchangeRequestBuilder.newRequest();
        builder.withUnderlyingInstrument(equity);
        verifyBuilder(builder.withInstrument(equity),
                      equity,
                      equity);
        verifyBuilder(builder.withInstrument(option),
                      option,
                      equity);
        verifyBuilder(builder.withInstrument(null),
                      null,
                      equity);
    }
    /**
     * Tests validation of <code>ExchangeRequest</code> creation. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validation()
            throws Exception
    {
        // neither instrument nor underlying instrument specified
        new ExpectedFailure<IllegalArgumentException>(INSTRUMENT_OR_UNDERLYING_INSTRUMENT_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                ExchangeRequestBuilder.newRequest().withInstrument(null).withUnderlyingInstrument(null).create();
            }
        };
        new ExpectedFailure<IllegalArgumentException>(OPTION_REQUIRES_UNDERLYING_INSTRUMENT.getText(option)) {
            @Override
            protected void run()
                    throws Exception
            {
                ExchangeRequestBuilder.newRequest().withInstrument(option).withUnderlyingInstrument(null).create();
            }
        };
    }
    /**
     * Verifies that the given builder creates an <code>ExchangeRequest</code> with
     * the given attributes. 
     *
     * @param inBuilder an <code>ExchangeRequestBuilder</code> value
     * @param inExpectedInstrument an <code>Instrument</code> value
     * @param inExpectedUnderlyingInstrument an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyBuilder(ExchangeRequestBuilder inBuilder,
                                      Instrument inExpectedInstrument,
                                      Instrument inExpectedUnderlyingInstrument)
            throws Exception
    {
        ExchangeRequest request = inBuilder.create();
        assertEquals(inExpectedInstrument,
                     request.getInstrument());
        assertEquals(inExpectedUnderlyingInstrument,
                     request.getUnderlyingInstrument());
        if(inExpectedInstrument == null) {
            assertNull(request.getInstrumentAsString());
        } else {
            assertEquals(inExpectedInstrument.getSymbol(),
                         request.getInstrumentAsString());
        }
        assertNotNull(request.toString());
    }
    /**
     * test equity
     */
    private final Equity equity = new Equity("METC");
    /**
     * test option
     */
    private final Option option = new Option(equity.getSymbol(),
                                             DateUtils.dateToString(new Date(), DateUtils.DAYS),
                                             EventTestBase.generateDecimalValue(),
                                             OptionType.Call);
}
