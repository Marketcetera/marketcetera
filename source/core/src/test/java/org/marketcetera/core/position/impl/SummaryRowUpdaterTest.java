package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.sameInstance;
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
 * @since 1.5.0
 */
public class SummaryRowUpdaterTest {

    @Test
    public void constructor() throws Exception {
        EventList<PositionRow> list = new BasicEventList<PositionRow>();
        createAndAssert(list, "0", "0", "0", "0", "0", "0", "0");

        list.add(new PositionRowImpl(null, null, null, BigDecimal.ZERO));
        createAndAssert(list, "0", "0", null, null, null, null, null);

        list = new BasicEventList<PositionRow>();
        list.add(new PositionRowImpl("ABC", "SYZ", "123", PositionMetricsImplTest.createMetrics(
                "0", "5", "4", "3", "2", "1", "7")));
        createAndAssert(list, "0", "5", "4", "3", "2", "1", "7");

        list = new BasicEventList<PositionRow>();
        list.add(new PositionRowImpl("ABC", "SYZ", "123", PositionMetricsImplTest.createMetrics(
                "0", "5", "4", "3", "2", "1", "7")));
        list.add(new PositionRowImpl(null, null, null, PositionMetricsImplTest.createMetrics("1",
                "4.5", "14", null, "-52", "18", "97")));
        createAndAssert(list, "1", "9.5", "18", null, "-50", "19", "104");

        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new SummaryRowUpdater(null);
            }
        };

        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new SummaryRowUpdater(new PositionRowImpl(null, null, null, BigDecimal.ZERO));
            }
        };
    }

    @Test
    public void testListChanged() throws Exception {
        EventList<PositionRow> list = new BasicEventList<PositionRow>();
        PositionRowImpl summary = createTestSummary(list);
        SummaryRowUpdater fixture = new SummaryRowUpdater(summary);
        assertSummary(fixture, summary, "0", "0", "0", "0", "0", "0", "0");
        // add a position
        PositionRowImpl microsoft = new PositionRowImpl("MSFT", "Account", "Yoram", BigDecimal.ZERO);
        list.add(microsoft);
        assertSummary(fixture, summary, "0", "0", null, null, null, null, null);
        // start over with a position already in the list
        fixture.dispose();
        fixture = new SummaryRowUpdater(summary);
        assertSummary(fixture, summary, "0", "0", null, null, null, null, null);
        // update position
        microsoft.setPositionMetrics(PositionMetricsImplTest.createMetrics("0", "5", "-4", "3",
                "2", "1", "7"));
        list.set(0, microsoft);
        assertSummary(fixture, summary, "0", "5", "-4", "3", "2", "1", "7");
        // update again
        microsoft.setPositionMetrics(PositionMetricsImplTest.createMetrics("1", "3", "2.2", "77",
                "11", "10000.3", "123"));
        list.set(0, microsoft);
        assertSummary(fixture, summary, "1", "3", "2.2", "77", "11", "10000.3", "123");
        // add a new position
        PositionRowImpl ibm = new PositionRowImpl("IBM", "Account", "Yoram",
                PositionMetricsImplTest.createMetrics("4", "7", "8", "3", "9", ".7", "7"));
        list.add(ibm);
        assertSummary(fixture, summary, "5", "10", "10.2", "80", "20", "10001", "130");
        // remove a position
        list.remove(0);
        assertSummary(fixture, summary, "4", "7", "8", "3", "9", ".7", "7");
        // remove the other position
        list.remove(0);
        assertSummary(fixture, summary, "0", "0", "0", "0", "0", "0", "0");
        // add a row back
        list.add(microsoft);
        assertSummary(fixture, summary, "1", "3", "2.2", "77", "11", "10000.3", "123");
        // dispose and make sure successive addition is not processed
        fixture.dispose();
        list.add(ibm);
        assertSummary(fixture, summary, "1", "3", "2.2", "77", "11", "10000.3", "123");
    }

    private void createAndAssert(EventList<PositionRow> list, String incomingPosition,
            String position, String positional, String trading, String realized, String unrealized,
            String total) {
        // the symbol, account, trader, and grouping values don't matter to a SummaryRowUpdater
        PositionRowImpl summary = createTestSummary(list);
        SummaryRowUpdater fixture = new SummaryRowUpdater(summary);
        assertSummary(fixture, summary, incomingPosition, position, positional, trading, realized,
                unrealized, total);
    }

    private PositionRowImpl createTestSummary(EventList<PositionRow> list) {
        return new PositionRowImpl("ABC", "XYZ", "123", new Grouping[] { Grouping.Account }, list);
    }

    private void assertSummary(SummaryRowUpdater fixture, PositionRow summary,
            String incomingPosition, String position, String positional, String trading,
            String realized, String unrealized, String total) {
        assertThat(fixture.getSummary(), sameInstance(summary));
        PositionMetricsImplTest.assertPositionMetrics(summary.getPositionMetrics(), incomingPosition, position,
                positional, trading, realized, unrealized, total);
    }

}
