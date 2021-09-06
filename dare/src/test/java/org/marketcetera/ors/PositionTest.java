package org.marketcetera.ors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.junit.Test;
import org.marketcetera.admin.User;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.DareTestBase;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.google.common.collect.Maps;

import junitparams.Parameters;

/* $License$ */

/**
 * Test position queries.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class PositionTest
        extends DareTestBase
{
    /**
     * Test retrieving a single position.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentFixVersionParameters")
    public void testSinglePosition(Instrument inInstrument,
                                   FIXVersion inFixVersion)
            throws Exception
    {
        doSinglePositionTest(inInstrument,
                             inFixVersion);
    }
    /**
     * Test retrieving all positions.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentFixVersionParameters")
    public void testAllPositions(Instrument inInstrument,
                                 FIXVersion inFixVersion)
            throws Exception
    {
        doAllPositionsTest(inInstrument,
                           inFixVersion);
    }
    /**
     * Test all positions for a single instrument and FIX version.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAllPositionsNoParameters()
            throws Exception
    {
        doAllPositionsTest(new Option("METC","201811",EventTestBase.generateDecimalValue(),OptionType.Put),
                           FIXVersion.FIX42);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.test.MarketceteraTestBase#getFixVersion()
     */
    @Override
    protected FIXVersion getFixVersion()
    {
        return fixVersion;
    }
    /**
     * Execute a single all positions test with the given instrument and FIX version.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doAllPositionsTest(Instrument inInstrument,
                                    FIXVersion inFixVersion)
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "All positions test with instrument: {} FIX version: {}",
                              inInstrument,
                              inFixVersion);
        setupSession(inFixVersion);
        Date positionDate = new Date();
        BigDecimal expectedPosition = BigDecimal.ZERO;
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        BigDecimal orderQty = new BigDecimal(10000);
        BigDecimal fillQty = new BigDecimal(1000);
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        expectedPosition = fillQty;
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        // add a second order for the same instrument, leave at new
        generatePosition(inInstrument,
                         orderQty,
                         BigDecimal.ZERO);
        positionDate = new Date();
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        // add a second order for the same instrument, fill
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        expectedPosition = fillQty.add(fillQty);
        positionDate = new Date();
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        Instrument otherInstrument = new Equity("NEW_INSTRUMENT_" + counter.incrementAndGet());
        generatePosition(otherInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        expectedPosition = fillQty;
        verifyPositionFromAllPositions(otherInstrument,
                                       expectedPosition,
                                       positionDate);
        positionDate = new Date(0);
        expectedPosition = BigDecimal.ZERO;
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        verifyPositionFromAllPositions(otherInstrument,
                                       expectedPosition,
                                       positionDate);
    }
    /**
     * Execute a single iteration of the single position test.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doSinglePositionTest(Instrument inInstrument,
                                      FIXVersion inFixVersion)
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Single position test with instrument: {} FIX version: {}",
                              inInstrument,
                              inFixVersion);
        setupSession(inFixVersion);
        Date positionDate = new Date();
        BigDecimal expectedPosition = BigDecimal.ZERO;
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        BigDecimal orderQty = new BigDecimal(10000);
        BigDecimal fillQty = new BigDecimal(1000);
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        expectedPosition = fillQty;
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        // add a second order for the same instrument, leave at new
        generatePosition(inInstrument,
                         orderQty,
                         BigDecimal.ZERO);
        positionDate = new Date();
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        // add a second order for the same instrument, fill
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        expectedPosition = fillQty.add(fillQty);
        positionDate = new Date();
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        Instrument otherInstrument = new Equity("NEW_INSTRUMENT_" + counter.incrementAndGet());
        generatePosition(otherInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        expectedPosition = fillQty;
        verifySinglePosition(otherInstrument,
                             expectedPosition,
                             positionDate);
        positionDate = new Date(0);
        expectedPosition = BigDecimal.ZERO;
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        verifySinglePosition(otherInstrument,
                             expectedPosition,
                             positionDate);
    }
    /**
     * Verify the position of the given instrument at the given time matches the given expected value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifySinglePosition(Instrument inInstrument,
                                      BigDecimal inExpectedPosition,
                                      Date inPositionDate)
            throws Exception
    {
        verifySinglePosition(inInstrument,
                             inExpectedPosition,
                             inPositionDate,
                             normalUser);
        verifySinglePosition(inInstrument,
                             inExpectedPosition,
                             inPositionDate,
                             adminUser);
        verifySinglePosition(inInstrument,
                             BigDecimal.ZERO,
                             inPositionDate,
                             otherUser);
    }
    /**
     * Verify the position of the given instrument at the given time matches the given expected value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @param inUser a <code>User</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifySinglePosition(Instrument inInstrument,
                                      BigDecimal inExpectedPosition,
                                      Date inPositionDate,
                                      User inUser)
            throws Exception
    {
        try {
            wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    BigDecimal actualPosition = getSinglePosition(inUser,
                                                                  inPositionDate,
                                                                  inInstrument);
                    if(actualPosition == null) {
                        return false;
                    }
                    return actualPosition.compareTo(inExpectedPosition) == 0;
                }}
            );
        } catch (AssertionError e) {
            BigDecimal actualPosition = getSinglePosition(inUser,
                                                          inPositionDate,
                                                          inInstrument);
            assertNotNull("No position for " + inInstrument + " for " + inUser + " as of " + inPositionDate,
                          actualPosition);
            assertTrue("Expected: " + inExpectedPosition.toPlainString() + " actual: " + actualPosition.toPlainString() + " for " + inUser + " as of " + new DateTime(inPositionDate),
                       inExpectedPosition.compareTo(actualPosition) == 0);
            throw e;
        }
    }
    /**
     * Verify the position of the given instrument as of the given date.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyPositionFromAllPositions(Instrument inInstrument,
                                                BigDecimal inExpectedPosition,
                                                Date inPositionDate)
            throws Exception
    {
        verifyPositionFromAllPositions(inInstrument,
                                       inExpectedPosition,
                                       inPositionDate,
                                       normalUser);
        verifyPositionFromAllPositions(inInstrument,
                                       inExpectedPosition,
                                       inPositionDate,
                                       adminUser);
        verifyPositionFromAllPositions(inInstrument,
                                       BigDecimal.ZERO,
                                       inPositionDate,
                                       otherUser);
    }
    /**
     * Verify the position of the given instrument as of the given date owned or viewable by the given user.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @param inUser a <code>User</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyPositionFromAllPositions(Instrument inInstrument,
                                                BigDecimal inExpectedPosition,
                                                Date inPositionDate,
                                                User inUser)
            throws Exception
    {
        Map<PositionKey<? extends Instrument>,BigDecimal> allPositions = getAllPositions(inUser,
                                                                                         inPositionDate);
        BigDecimal actualPosition = BigDecimal.ZERO;
        for(Map.Entry<PositionKey<? extends Instrument>,BigDecimal> entry : allPositions.entrySet()) {
            Instrument positionInstrument = entry.getKey().getInstrument();
            if(!inInstrument.equals(positionInstrument)) {
                continue;
            }
            BigDecimal position = entry.getValue();
            actualPosition = actualPosition.add(position);
        }
        assertTrue("Expected: " + inExpectedPosition.toPlainString() + " actual: " + actualPosition.toPlainString() + " for " + inUser + " as of " + new DateTime(inPositionDate),
                   inExpectedPosition.compareTo(actualPosition) == 0);
        if(inInstrument instanceof Option) {
            Option option = (Option)inInstrument;
            Map<PositionKey<Option>,BigDecimal> actualPositionMap = reportService.getOptionPositionsAsOf(inUser,
                                                                                                         inPositionDate,
                                                                                                         new String[] { option.getSymbol() });
            for(Map.Entry<PositionKey<Option>,BigDecimal> entry : actualPositionMap.entrySet()) {
                Option positionOption = entry.getKey().getInstrument();
                actualPosition = entry.getValue();
                if(positionOption.equals(option)) {
                    assertTrue("Expected: " + inExpectedPosition.toPlainString() + " actual: " + actualPosition.toPlainString() + " for " + inUser + " as of " + new DateTime(inPositionDate),
                               inExpectedPosition.compareTo(actualPosition) == 0);
                }
            }
        }
    }
    /**
     * Get all positions as of the given date owned by or viewable by the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inPositionDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;? extends Instrument&gt;,BigDecimal&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private Map<PositionKey<? extends Instrument>,BigDecimal> getAllPositions(User inUser,
                                                                              Date inPositionDate)
            throws Exception
    {
        Map<PositionKey<? extends Instrument>,BigDecimal> results = Maps.newHashMap();
        results.putAll(reportService.getAllEquityPositionsAsOf(inUser,
                                                               inPositionDate));
        results.putAll(reportService.getAllOptionPositionsAsOf(inUser,
                                                               inPositionDate));
        results.putAll(reportService.getAllFuturePositionsAsOf(inUser,
                                                               inPositionDate));
        results.putAll(reportService.getAllCurrencyPositionsAsOf(inUser,
                                                                 inPositionDate));
        results.putAll(reportService.getAllConvertibleBondPositionsAsOf(inUser,
                                                                        inPositionDate));
        return results;
    }
    /**
     * Get the position of the given instrument as of the given date owned or viewable by the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inPositionDate a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    private BigDecimal getSinglePosition(User inUser,
                                         Date inPositionDate,
                                         Instrument inInstrument)
            throws Exception
    {
        if(inInstrument instanceof Equity) {
            return reportService.getEquityPositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Equity)inInstrument);
        } else if(inInstrument instanceof Option) {
            return reportService.getOptionPositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Option)inInstrument);
        } else if(inInstrument instanceof Future) {
            return reportService.getFuturePositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Future)inInstrument);
        } else if(inInstrument instanceof Currency) {
            return reportService.getCurrencyPositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Currency)inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            return reportService.getConvertibleBondPositionAsOf(inUser,
                                                                inPositionDate,
                                                                (ConvertibleBond)inInstrument);
        }
        fail("Unsupported instrument: " + inInstrument);
        return null;
    }
}
