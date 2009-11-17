package org.marketcetera.photon.internal.positions.ui;

import static org.marketcetera.photon.internal.positions.ui.PositionsViewFlatTest.EQ_ABC_123_admin;
import static org.marketcetera.photon.internal.positions.ui.PositionsViewFlatTest.EQ_IBM_null_admin2;
import static org.marketcetera.photon.internal.positions.ui.PositionsViewFlatTest.OPT_ABC20091010P1_null_admin;
import static org.marketcetera.photon.internal.positions.ui.PositionsViewFlatTest.OPT_ABC200910C1_123_admin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.MockTrade;
import org.marketcetera.photon.positions.ui.PositionsViewFixture;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Tests {@link PositionsView} in grouped mode ({@link PositionsViewTreePage}).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class PositionsViewGroupedTest extends PhotonTestBase {

    private PositionsViewFixture mFixture;

    @Before
    public void before() throws Exception {
        mFixture = new PositionsViewFixture();
        mFixture.assertEmptyPage();
    }

    @After
    public void after() throws Exception {
        mFixture.dispose();
    }

    @Test
    public void testEmptyTable() throws Exception {
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Underlying, Grouping.Account);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(0);
    }

    @Test
    public void testSingleEquityPosition() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "10", "10"));
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Underlying, Grouping.Account);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("ABC", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("123", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("admin", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withEquityPosition("ABC", "10.00", "0.00", "NA", "NA", "NA", "NA",
                "NA");

        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "10", "10"));
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("ABC", "20.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("123", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("admin", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withEquityPosition("ABC", "20.00", "0.00", "NA", "NA", "NA", "NA",
                "NA");
    }

    @Test
    public void testSingleOptionPosition() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC20091010P1_null_admin,
                "10", "10"));
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Trader, Grouping.Underlying);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("admin", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("ABC", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("<none>", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withOptionPosition("Oct 10 09 ABC Put 1.00", "ABC", "Oct 10 09",
                "Put", "1.00", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA");

        mFixture.addTrade(MockTrade.createTrade(OPT_ABC20091010P1_null_admin,
                "5", "10"));
        mFixture.assertGroupedPositionsCount(1);

        mFixture.assertGroupedPosition("admin", "15.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("ABC", "15.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("<none>", "15.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withOptionPosition("Oct 10 09 ABC Put 1.00", "ABC", "Oct 10 09",
                "Put", "1.00", "15.00", "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testMultiplePositions() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC20091010P1_null_admin,
                "10", "10"));
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC200910C1_123_admin,
                "10", "10"));
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "10", "10"));
        mFixture
                .addTrade(MockTrade.createTrade(EQ_IBM_null_admin2, "10", "10"));
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Trader, Grouping.Underlying);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(2);
        mFixture.assertGroupedPosition("admin", "30.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("ABC", "30.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("<none>", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withOptionPosition("Oct 10 09 ABC Put 1.00", "ABC", "Oct 10 09",
                "Put", "1.00", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .up()

        .withChild("123", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withEquityPosition("ABC", "10.00", "0.00", "NA", "NA", "NA", "NA",
                "NA")

        .withOptionPosition("Oct 09 ABC Call 1.00", "ABC", "Oct 09", "Call",
                "1.00", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA");

        mFixture.assertGroupedPosition("admin2", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("IBM", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("<none>", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withEquityPosition("IBM", "10.00", "0.00", "NA", "NA", "NA", "NA",
                "NA");

        mFixture.groupBy(Grouping.Account, Grouping.Trader);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(2);
        mFixture.assertGroupedPosition("<none>", "20.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("admin", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("ABC", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withOptionPosition("Oct 10 09 ABC Put 1.00", "ABC", "Oct 10 09",
                "Put", "1.00", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .up().up()

        .withChild("admin2", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("IBM", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withEquityPosition("IBM", "10.00", "0.00", "NA", "NA", "NA", "NA",
                "NA");

        mFixture.assertGroupedPosition("123", "20.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("admin", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("ABC", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withEquityPosition("ABC", "10.00", "0.00", "NA", "NA", "NA", "NA",
                "NA")

        .withOptionPosition("Oct 09 ABC Call 1.00", "ABC", "Oct 09", "Call",
                "1.00", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testPNL() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "7", "10"));
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "-5", "12"));
        Equity instrument = EQ_ABC_123_admin.getInstrument();
        mFixture.registerModel();
        mFixture.fireTrade(instrument, "12");
        mFixture.fireClosingPrice(instrument, "8");
        /*
         * Give time for market data changes to propagate to UI.
         */
        Thread.sleep(500);
        mFixture.groupBy(Grouping.Underlying, Grouping.Account);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("ABC", "2.00", "0.00", "0.00", "14.00",
                "10.00", "4.00", "14.00")

        .withChild("123", "2.00", "0.00", "0.00", "14.00", "10.00", "4.00",
                "14.00")

        .withChild("admin", "2.00", "0.00", "0.00", "14.00", "10.00", "4.00",
                "14.00")

        .withEquityPosition("ABC", "2.00", "0.00", "0.00", "14.00", "10.00",
                "4.00", "14.00");
    }

    @Test
    public void testFilter() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC20091010P1_null_admin,
                "10", "10"));
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC200910C1_123_admin,
                "10", "10"));
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "10", "10"));
        mFixture
                .addTrade(MockTrade.createTrade(EQ_IBM_null_admin2, "10", "10"));
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Trader, Grouping.Underlying);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(2);
        mFixture.assertGroupedPosition("admin", "30.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        mFixture.assertGroupedPosition("admin2", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        
        mFixture.filter("bogus");
        mFixture.assertGroupedPositionsCount(0);
        
        mFixture.filter("");
        mFixture.assertGroupedPositionsCount(2);
        mFixture.assertGroupedPosition("admin", "30.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        mFixture.assertGroupedPosition("admin2", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        
        mFixture.groupBy(Grouping.Underlying, Grouping.Trader);
        mFixture.assertGroupedPositionsCount(2);
        mFixture.assertGroupedPosition("ABC", "30.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        mFixture.assertGroupedPosition("IBM", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        
        mFixture.filter("ABC");
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("ABC", "30.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        
        mFixture.filter("IBM");
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("IBM", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        
        mFixture.filter("");
        mFixture.assertGroupedPosition("ABC", "30.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
        mFixture.assertGroupedPosition("IBM", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA");
    }
}
