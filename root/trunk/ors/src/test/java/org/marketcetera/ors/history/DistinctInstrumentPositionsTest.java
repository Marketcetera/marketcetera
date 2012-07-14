package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.Side;
import org.marketcetera.core.position.PositionKey;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/* $License$ */
/**
 * Verifies that retrieved positions for different types of instruments
 * having similar attributes do not interfere with each other.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class DistinctInstrumentPositionsTest extends ReportsTestBase {
    /**
     * Verifies that equity position for a symbol that is the same as the
     * option root symbol for an option doesn't lead to equity/option
     * positions being computed incorrectly.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void distinctEquityOptionPositions() throws Exception {
        Equity equity = new Equity("A");
        Option option = new Option(equity.getSymbol(), "20001010", BigDecimal.ONE, OptionType.Call);
        //Create positions for each instrument.
        String opx = "ord-"; //order prefix
        int orderid = 0;
        //equity postions
        createAndSaveER(opx + ++orderid, null, equity, Side.Buy, BigDecimal.TEN);
        sleepForSignificantTime();
        assertEquals(BigDecimal.TEN.setScale(SCALE), getPosition(new Date(), equity));
        createAndSaveER(opx + ++orderid, null, equity, Side.Sell, BigDecimal.ONE);
        sleepForSignificantTime();
        BigDecimal finalEqPosition = BigDecimal.TEN.subtract(BigDecimal.ONE).setScale(SCALE);
        assertEquals(finalEqPosition, getPosition(new Date(), equity));
        //option positions
        createAndSaveER(opx + ++orderid, null, option, Side.Buy, BigDecimal.ONE);
        sleepForSignificantTime();
        assertEquals(BigDecimal.ONE.setScale(SCALE), getPosition(new Date(), option));
        createAndSaveER(opx + ++orderid, null, option, Side.Sell, BigDecimal.TEN);
        sleepForSignificantTime();
        Date date = new Date();
        BigDecimal finalOpPosition = BigDecimal.ONE.subtract(BigDecimal.TEN).setScale(SCALE);

        //now verify all the equity/option position APIs
        assertEquals(finalOpPosition, getPosition(date, option));
        assertEquals(finalEqPosition, getPosition(date, equity));

        Map<PositionKey<Equity>, BigDecimal> eqPositions = getPositions(date);
        assertEquals(1, eqPositions.size());
        assertThat(eqPositions, Matchers.hasEntry(pos(equity),finalEqPosition));

        Map<PositionKey<Option>, BigDecimal> opPositions = getOptionPositions(date, option.getSymbol());
        assertEquals(1, opPositions.size());
        assertThat(opPositions, Matchers.hasEntry(pos(option),finalOpPosition));
        
        opPositions = getAllOptionPositions(date);
        assertEquals(1, opPositions.size());
        assertThat(opPositions, Matchers.hasEntry(pos(option),finalOpPosition));
    }

}
