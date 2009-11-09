package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
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
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class PositionsViewFlatTest extends PhotonTestBase {

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
        PositionKey<Equity> positionKey = PositionKeyFactory.createEquityKey(
                "ABC", "123", "admin");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "10", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "10.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testNullAccount() throws Exception {
        PositionKey<Equity> positionKey = PositionKeyFactory.createEquityKey(
                "IBM", null, "admin2");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "20.5", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("IBM", "<none>", "admin2", "20.50",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testNullTrader() throws Exception {
        PositionKey<Equity> positionKey = PositionKeyFactory.createEquityKey(
                "METC", "acc", null);
        mFixture.addTrade(MockTrade.createTrade(positionKey, "-5.0001", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("METC", "acc", "<none>", "-5.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testTrade() throws Exception {
        PositionKey<Equity> positionKey = PositionKeyFactory.createEquityKey(
                "ABC", "123", "admin");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "10", "10"));
        mFixture.registerModel();
        mFixture.addTrade(MockTrade.createTrade(positionKey, "-10", "10"));
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatEquityPosition("ABC", "123", "admin", "0.00",
                "0.00", "NA", "NA", "NA", "NA", "NA");
    }

    @Test
    public void testSingleOptionPosition() throws Exception {
        PositionKey<Option> positionKey = PositionKeyFactory
                .createOptionKey("ABC", "200910", BigDecimal.ONE,
                        OptionType.Call, "123", "admin");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "10", "10"));
        mFixture.registerModel();
        mFixture.assertFlatTable();
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatOptionPosition("ABC", "123", "admin", "ABC",
                "Oct 09", "Call", "1.00", "10.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "5", "10"));
        mFixture.assertFlatPositionsCount(1);
        mFixture.assertFlatOptionPosition("ABC", "123", "admin", "ABC",
                "Oct 09", "Call", "1.00", "15.00", "0.00",
                "NA", "NA", "NA", "NA", "NA");
    }
}
