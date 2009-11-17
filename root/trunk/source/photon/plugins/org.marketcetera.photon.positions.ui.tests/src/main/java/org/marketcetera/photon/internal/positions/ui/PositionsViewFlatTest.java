package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.position.MockTrade;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.photon.positions.ui.PositionsViewFixture;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link PositionsView} in flat mode ({@link PositionsViewTablePage}).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class PositionsViewFlatTest extends PhotonTestBase {

    public static final PositionKey<Equity> EQ_ABC_123_admin = PositionKeyFactory
            .createEquityKey("ABC", "123", "admin");
    public static final PositionKey<Equity> EQ_IBM_null_admin2 = PositionKeyFactory
            .createEquityKey("IBM", null, "admin2");
    public static final PositionKey<Equity> EQ_METC_acc_null = PositionKeyFactory
            .createEquityKey("METC", "acc", null);
    public static final PositionKey<Option> OPT_ABC200910C1_123_admin = PositionKeyFactory
            .createOptionKey("ABC", "200910", BigDecimal.ONE, OptionType.Call,
                    "123", "admin");
    public static final PositionKey<Option> OPT_ABC20091010P1_null_admin = PositionKeyFactory
            .createOptionKey("ABC", "20091010", BigDecimal.ONE, OptionType.Put,
                    null, "admin");

    private PositionsViewFixture mFixture;

    @Before
    public void before() throws Exception {
        mFixture = new PositionsViewFixture();
        mFixture.setFlatView();
        mFixture.assertEmptyPage();
    }

    @After
    public void after() throws Exception {
        mFixture.dispose();
    }

    @Test
    public void testEmptyTable() throws Exception {
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(0);
    }

    @Test
    public void testSingleEquityPosition() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "10", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "10.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testNullAccount() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_IBM_null_admin2, "20.5",
                "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("IBM", "<none>", "admin2", "20.50",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testNullTrader() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_METC_acc_null, "-5.0001",
                "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("METC", "acc", "<none>", "-5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testTrade() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "10", "10"));
        mFixture.registerModel();
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "-10", "10"));
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "0.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testSingleOptionPosition() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC200910C1_123_admin,
                "10.55555", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatOptionPosition("ABC", "123", "admin", "ABC",
                "Oct 09", "Call", "1.00", "10.56", "0.00", "NA", "NA", "NA",
                "NA", "NA");
    }

    @Test
    public void testMultiplePositions() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "5", "5"));
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC200910C1_123_admin, "10", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(2);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
        mFixture.assertFlatOptionPosition("ABC", "123", "admin", "ABC",
                "Oct 09", "Call", "1.00", "10.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
    }

    @Test
    public void testPNL() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "5", "5"));
        Equity instrument = EQ_ABC_123_admin.getInstrument();
        mFixture.registerModel();
        mFixture.fireTrade(instrument, "15.865");
        mFixture.fireClosingPrice(instrument, "10");
        /*
         * Give time for market data changes to propogate to UI.
         */
        Thread.sleep(500);
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "5.00",
                "0.00", "0.00", "54.33", "0.00", "54.33", "54.33");
    }

    @Test
    @Ignore("fails on Windows 7 due to http://bugs.eclipse.org/142593")
    public void testFiltering() throws Exception {
        mFixture.addTrade(MockTrade.createTrade(EQ_ABC_123_admin, "5", "5"));
        mFixture.addTrade(MockTrade.createTrade(EQ_IBM_null_admin2, "5", "5"));
        mFixture.addTrade(MockTrade.createTrade(EQ_METC_acc_null, "5", "5"));
        mFixture.addTrade(MockTrade.createTrade(OPT_ABC200910C1_123_admin, "10", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(4);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
        mFixture.assertFlatEquityPosition("IBM", "<none>", "admin2", "5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
        mFixture.assertFlatEquityPosition("METC", "acc", "<none>", "5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
        mFixture.assertFlatOptionPosition("ABC", "123", "admin", "ABC",
                "Oct 09", "Call", "1.00", "10.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
        mFixture.filter("ABC");
        mFixture.assertFlatPositionsCount(2);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
        mFixture.assertFlatOptionPosition("ABC", "123", "admin", "ABC",
                "Oct 09", "Call", "1.00", "10.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
    }
}
