package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.position.Grouping;
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
 * Tests {@link PositionsView} in grouped mode ({@link PositionsViewTreePage}).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class PositionsViewGroupedTest extends PhotonTestBase {

    private PositionsViewFixture mFixture;

    @Before
    public void before() throws Exception {
        setLevel("org.eclipse.swtbot", Level.ALL);
        BasicConfigurator.configure();
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
        PositionKey<Equity> positionKey = PositionKeyFactory.createEquityKey(
                "ABC", "123", "admin");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "10", "10"));
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Underlying, Grouping.Account);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("ABC", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("123", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("admin", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChildEquityPosition("ABC", "10.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
        
        mFixture.addTrade(MockTrade.createTrade(positionKey, "10", "10"));
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("ABC", "20.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("123", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("admin", "20.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChildEquityPosition("ABC", "20.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
    }

    @Test
    public void testSingleOptionPosition() throws Exception {
        PositionKey<Option> positionKey = PositionKeyFactory.createOptionKey(
                "ABC", "20091010", BigDecimal.ONE, OptionType.Put, null,
                "admin");
        mFixture.addTrade(MockTrade.createTrade(positionKey, "10", "10"));
        mFixture.registerModel();
        mFixture.groupBy(Grouping.Trader, Grouping.Underlying);
        mFixture.assertGroupTree();
        mFixture.assertGroupedPositionsCount(1);
        mFixture.assertGroupedPosition("admin", "10.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("ABC", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("<none>", "10.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChildOptionPosition("Oct 10 09 ABC Put 1.00", "ABC", "Oct 10 09", "Put", "1.00", "10.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
        
        mFixture.addTrade(MockTrade.createTrade(positionKey, "5", "10"));
        mFixture.assertGroupedPositionsCount(1);

        mFixture.assertGroupedPosition("admin", "15.00", "0.00", "NA", "NA",
                "NA", "NA", "NA")

        .withChild("ABC", "15.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChild("<none>", "15.00", "0.00", "NA", "NA", "NA", "NA", "NA")

        .withChildOptionPosition("Oct 10 09 ABC Put 1.00", "ABC", "Oct 10 09", "Put", "1.00", "15.00", "0.00", "NA", "NA", "NA",
                "NA", "NA");
    }
}
