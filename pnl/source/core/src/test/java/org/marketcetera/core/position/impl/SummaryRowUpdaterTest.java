package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.module.ExpectedFailure;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * Test {@link SummaryRowUpdaterTest}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SummaryRowUpdaterTest {

    @Test
    public void constructor() throws Exception {
        EventList<PositionRow> list = new BasicEventList<PositionRow>();
        list.add(new PositionRowImpl(null, null, null, BigDecimal.ZERO));
        SummaryRowUpdater fixture = new SummaryRowUpdater(list, Grouping.Symbol);
        assertSummary(fixture, null, null, null, null, "0", "0", null, null, "0", null, null);

        list = new BasicEventList<PositionRow>();
        list.add(new PositionRowImpl("ABC", "SYZ", "123", PositionMetricsImplTest.createMetrics(
                        "0", "5", "4", "3", "2", "1", "7")));
        fixture = new SummaryRowUpdater(list, Grouping.Symbol);
        assertSummary(fixture, "ABC", "SYZ", "123", "ABC", "0", "5", "4", "3", "2", "1", "7");

        list = new BasicEventList<PositionRow>();
        list.add(new PositionRowImpl("ABC", "SYZ", "123", PositionMetricsImplTest.createMetrics(
                        "0", "5", "4", "3", "2", "1", "7")));
        list.add(new PositionRowImpl(null, null, null, PositionMetricsImplTest.createMetrics("1",
                        "4.5", "14", "4.3", "52", "18", "97")));
        fixture = new SummaryRowUpdater(list, Grouping.Trader);
        assertSummary(fixture, "ABC", "SYZ", "123", "123", "1", "9.5", "18", "7.3", "54", "19", "104");

        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new SummaryRowUpdater(null, null);
            }
        };

        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new SummaryRowUpdater(new BasicEventList<PositionRow>(), null);
            }
        };

        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                EventList<PositionRow> list = new BasicEventList<PositionRow>();
                list.add(new PositionRowImpl(null, null, null, BigDecimal.ZERO));
                new SummaryRowUpdater(list, null);
            }
        };
    }

    @Test
    public void testListChanged() throws Exception {
        final EventList<PositionRow> list = new BasicEventList<PositionRow>();
        PositionRowImpl microsoft = new PositionRowImpl("MSFT", "Account", "Yoram", BigDecimal.ZERO);
        list.add(microsoft);
        SummaryRowUpdater fixture = new SummaryRowUpdater(list, Grouping.Symbol);
        assertSummary(fixture, "MSFT", "Account", "Yoram", "MSFT", "0", "0", null, null, "0", null, null);
        // update position
        microsoft.setPositionMetrics(PositionMetricsImplTest.createMetrics("0", "5", "4", "3", "2",
                "1", "7"));
        list.set(0, microsoft);
        assertSummary(fixture, "MSFT", "Account", "Yoram", "MSFT", "0", "5", "4", "3", "2", "1", "7");
        // update again
        microsoft.setPositionMetrics(PositionMetricsImplTest.createMetrics("1", "3", "2.2", "77",
                "11", "10000.3", "123"));
        list.set(0, microsoft);
        assertSummary(fixture, "MSFT", "Account", "Yoram", "MSFT", "1", "3", "2.2", "77", "11", "10000.3",
                "123");
        // add a new position
        PositionRowImpl ibm = new PositionRowImpl("IBM", "Account", "Yoram", BigDecimal.ZERO);
        ibm.setPositionMetrics(PositionMetricsImplTest.createMetrics("4", "7", "8", "3", "9", ".7",
                "7"));
        list.add(ibm);
        assertSummary(fixture, "MSFT", "Account", "Yoram", "MSFT", "5", "10", "10.2", "80", "20", "10001",
                "130");
        // remove a position
        list.remove(0);
        assertSummary(fixture, "MSFT", "Account", "Yoram", "MSFT", "4", "7", "8", "3", "9", ".7", "7");
    }

    private void assertSummary(SummaryRowUpdater fixture, String symbol, String account,
            String trader, String grouping, String incomingPosition, String position, String positional,
            String trading, String realized, String unrealized, String total) {
        PositionRow summary = fixture.getSummary();
        assertThat(summary.getSymbol(), is(symbol));
        assertThat(summary.getAccount(), is(account));
        assertThat(summary.getTraderId(), is(trader));
        assertThat(summary.getGrouping(), is(grouping));
        PositionMetricsImplTest.assertPNL(summary.getPositionMetrics(), incomingPosition, position,
                positional, trading, realized, unrealized, total);
    }

}
