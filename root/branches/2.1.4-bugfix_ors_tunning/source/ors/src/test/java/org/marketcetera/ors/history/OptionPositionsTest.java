package org.marketcetera.ors.history;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.trade.*;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

import java.util.*;
import java.math.BigDecimal;

/* $License$ */
/**
 * Verifies {@link org.marketcetera.ors.history.ReportHistoryServices#getOptionPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Option)}
 * , {@link org.marketcetera.ors.history.ReportHistoryServices#getAllOptionPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)} &
 * {@link org.marketcetera.ors.history.ReportHistoryServices#getOptionPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, String[])}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class OptionPositionsTest extends PositionsTestBase<Option> {

    @Override
    protected Option getInstrument() {
        return TEST_OPTION;
    }

    @Override
    protected Option getInstrumentA() {
        return OPTION_A;
    }

    @Override
    protected Option getInstrumentB() {
        return OPTION_B;
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Option inOption) throws Exception {
        return getPosition(inDate, inOption);
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Option inOption, SimpleUser inUser) throws Exception {
        return getPosition(inDate, inOption, inUser);
    }

    @Override
    protected Map<PositionKey<Option>, BigDecimal> getInstrumentPositions(Date inDate) throws Exception {
        return getAllOptionPositions(inDate);
    }

    @Override
    protected Map<PositionKey<Option>, BigDecimal> getInstrumentPositions(Date inAfter, SimpleUser inUser) throws Exception {
        return getAllOptionPositions(inAfter, inUser);
    }

    /**
     * Verifies that the positions for options are distinguished using all option attributes.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void optionsDistinguished() throws Exception {
        String [] symbols = {"abc", "pqr"};
        String[] expiries = {"201010", "20101010"};
        BigDecimal[] strikes = {BigDecimal.ONE, BigDecimal.TEN};
        OptionType [] types = {OptionType.Call, OptionType.Put};
        String [] accounts = {"blue", "green"};
        UserID superUserID = sActorID;
        UserID regularUserID = sViewerID;
        UserID [] actors = {superUserID, regularUserID};
        UserID [] viewers = {superUserID, regularUserID};
        BigDecimal individualPosition = BigDecimal.ONE;
        SimpleUser []viewerUsers = {sActor, sViewer};
        int orderID = 1;
        //Create ERs for all combinations
        for(String symbol: symbols) {
            for(String expiry: expiries) {
                for(BigDecimal strike: strikes) {
                    for(OptionType opType: types) {
                        Option option = new Option(symbol, expiry, strike, opType);
                        for(String account: accounts) {
                            for(UserID actor: actors) {
                                for(UserID viewer: viewers) {
                                    createAndSaveER(String.valueOf(orderID++), null,
                                            option, Side.Buy, individualPosition,
                                            account, actor, viewer);
                                }
                            }
                        }
                    }
                }
            }
        }
        sleepForSignificantTime();
        Date date = new Date();
        //Verify each option symbol
        for(String symbol: symbols) {
            for(String expiry: expiries) {
                for(BigDecimal strike: strikes) {
                    for(OptionType opType: types) {
                        Option option = new Option(symbol, expiry, strike, opType);
                        for(SimpleUser viewer: viewerUsers) {
                            BigDecimal position = getPosition(date, option, viewer);
                            SLF4JLoggerProxy.error(this, "Option:{}: Viewer: {}: Position: {}",
                                    option, viewer, position);
                            assertBigDecimalEquals(individualPosition.multiply(
                                    BigDecimal.valueOf(superUserID.equals(viewer.getUserID())
                                    //super user sees all the positions for all viewers
                                    ? accounts.length * actors.length * viewers.length
                                    //regular user only sees the positions that it's a viewer on 
                                    : accounts.length * actors.length)),
                                    position);
                        }
                    }
                }
            }
        }
        //Verify root symbol summaries
        Map<PositionKey<Option>, BigDecimal> positions;
        for(String symbol:symbols) {
            for(SimpleUser viewer: viewerUsers) {
                int numExpectedEntries = 0;
                positions = getOptionPositions(date, viewer, symbol);
                SLF4JLoggerProxy.error(this, "Symbol:{}: Viewer: {}: Positions: {}",
                        symbol, viewer, positions);
                for(String expiry: expiries) {
                    for(BigDecimal strike: strikes) {
                        for(OptionType opType: types) {
                            for(String account: accounts) {
                                BigDecimal expectedPosition = superUserID.equals(viewer.getUserID())
                                        ? individualPosition.multiply(BigDecimal.valueOf(viewers.length))
                                        : individualPosition;
                                for(UserID actor: actors) {
                                    numExpectedEntries++;
                                    assertThat(positions,
                                            hasEntry(PositionKeyFactory.createOptionKey(
                                                    symbol, expiry, strike, opType,
                                                    account, String.valueOf(actor.getValue())),
                                                    expectedPosition.setScale(SCALE)));
                                }
                            }
                        }
                    }
                }
                assertEquals(numExpectedEntries, positions.size());
            }
        }

        //Verify all positions
        for(SimpleUser viewer: viewerUsers) {
            int numExpectedEntries = 0;
            positions = getAllOptionPositions(date, viewer);
            SLF4JLoggerProxy.error(this, "Viewer: {}: Positions: {}",
                    viewer, positions);
            for (String symbol : symbols) {
                for (String expiry : expiries) {
                    for (BigDecimal strike : strikes) {
                        for (OptionType opType : types) {
                            for (String account : accounts) {
                                BigDecimal expectedPosition = superUserID.equals(viewer.getUserID())
                                        ? individualPosition.multiply(BigDecimal.valueOf(viewers.length))
                                        : individualPosition;
                                for (UserID actor : actors) {
                                    numExpectedEntries++;
                                    assertThat(positions,
                                            hasEntry(PositionKeyFactory.createOptionKey(
                                                    symbol, expiry, strike, opType,
                                                    account, String.valueOf(actor.getValue())),
                                                    expectedPosition.setScale(SCALE)));
                                }
                            }
                        }
                    }
                }
            }
            assertEquals(numExpectedEntries, positions.size());
        }
    }

    private static final Option OPTION_A = new Option("A", "20101010", BigDecimal.TEN, OptionType.Call);
    private static final Option OPTION_B = new Option("A", "20101010", BigDecimal.TEN, OptionType.Put);
    private static final Option TEST_OPTION = new Option("A", "20101010", BigDecimal.ONE, OptionType.Put);
}