package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link ExchangeRequest} and {@link ExchangeRequestBuilder}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
                                             DateUtils.dateToString(new Date(),
                                                                    DateUtils.DAYS),
                                             EventTestBase.generateDecimalValue(),
                                             OptionType.Call);
}
