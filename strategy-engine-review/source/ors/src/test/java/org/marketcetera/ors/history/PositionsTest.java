package org.marketcetera.ors.history;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.UserID;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import org.hamcrest.Matcher;

import java.util.*;
import java.math.BigDecimal;

/* $License$ */
/**
 * Verifies {@link ReportHistoryServices#getPositionAsOf(java.util.Date , org.marketcetera.trade.MSymbol)}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class PositionsTest extends ReportsTestBase {
    @Test
    public void noRecords() throws Exception {
        assertBigDecimalEquals(BigDecimal.ZERO,
                getPosition(new Date(), "what"));
        assertThat(getPositions(new Date()), isOfSize(0));
    }

    @Test
    public void singleReportBuy() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, TEST_SYMBOL, Side.Buy, value, sExtraUserID);
        Date after = new Date();
        assertBigDecimalEquals(value, getPosition(after, TEST_SYMBOL, sExtraUser));
        assertThat(getPositions(after, sExtraUser), allOf(isOfSize(1),
                hasEntry(pos(TEST_SYMBOL), value.setScale(SCALE))));
        assertBigDecimalEquals(BigDecimal.ZERO, getPosition(before, TEST_SYMBOL, sExtraUser));
        assertThat(getPositions(before, sExtraUser), allOf(isOfSize(0)));
    }

    @Test
    public void singleReportSell() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, TEST_SYMBOL, Side.Sell, value);
        Date after = new Date();
        verifyShortPosition(value, before, after);
    }

    @Test
    public void singleReportSellShort() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, TEST_SYMBOL, Side.SellShort, value);
        Date after = new Date();
        verifyShortPosition(value, before, after);
    }

    @Test
    public void singleReportSellShortExempt() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, TEST_SYMBOL, Side.SellShortExempt, value);
        Date after = new Date();
        verifyShortPosition(value, before, after);
    }

    @Test
    public void positionExcludedWhenZero() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("34895.434");
        Date before = new Date();
        //Establish a long position
        createAndSaveER("o1", null, TEST_SYMBOL, Side.Buy, value);
        Date after1 = new Date();
        //Now cancel it out with a short position
        createAndSaveER("o2", null, TEST_SYMBOL, Side.Sell, value);
        Date after2 = new Date();
        //Verify initial position
        assertBigDecimalEquals(BigDecimal.ZERO, getPosition(before, TEST_SYMBOL));
        assertThat(getPositions(before), allOf(isOfSize(2), hasAandB()));
        //verify the long position 
        assertBigDecimalEquals(value, getPosition(after1, TEST_SYMBOL));
        assertThat(getPositions(after1), allOf(isOfSize(3),
                hasEntry(pos(TEST_SYMBOL), value.setScale(SCALE)),
                hasAandB()));
        //verify that the final position zeros out and disappears
        assertBigDecimalEquals(BigDecimal.ZERO, getPosition(after2, TEST_SYMBOL));
        assertThat(getPositions(after2), allOf(isOfSize(2), hasAandB()));
    }

    @Test
    public void chainReportsBuyAndSell() throws Exception {
        Date before = new Date();
        createChainReportsForBuyA();
        Date after1 = new Date();
        createChainReportsForSellA();
        Date after2 = new Date();
        createChainReportsForSellShortA();
        Date after3 = new Date();
        createChainReportsForSellShortExemptA();
        Date after4 = new Date();

        BigDecimal position = BigDecimal.ZERO;
        assertBigDecimalEquals(position, getPosition(before, "A"));
        assertThat(getPositions(before), isOfSize(0));

        position = POSITION_BUY_A;
        assertBigDecimalEquals(position,
                getPosition(after1, "A"));
        assertThat(getPositions(after1), allOf(isOfSize(1),
                hasEntry(pos("A"), position.setScale(SCALE))));

        position = POSITION_BUY_A.subtract(POSITION_SELL_A);
        assertBigDecimalEquals(position,
                getPosition(after2, "A"));
        assertThat(getPositions(after2), allOf(isOfSize(1),
                hasEntry(pos("A"), position.setScale(SCALE))));

        position = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A);
        assertBigDecimalEquals(position, getPosition(after3, "A"));
        assertThat(getPositions(after3), allOf(isOfSize(1),
                hasEntry(pos("A"), position.setScale(SCALE))));

        position = FINAL_POSITION_A;
        assertBigDecimalEquals(position,
                getPosition(after4, "A"));
        assertThat(getPositions(after4), allOf(isOfSize(1),
                hasEntry(pos("A"), position.setScale(SCALE))));
    }

    @Test
    public void chainReportsBuyAndSellMultiAccountsActors()
        throws Exception
    {
        String oAccount=ACCOUNT+"a";

        BigDecimal one=BigDecimal.ONE;
        BigDecimal two=one.add(new BigDecimal(1));
        BigDecimal three=two.add(new BigDecimal(1));
        BigDecimal four=three.add(new BigDecimal(1));
        BigDecimal five=four.add(new BigDecimal(1));

        createChainReportsForBuyA
            ("1a",ACCOUNT,sViewerID,sViewerID,one);
        createChainReportsForBuyA
            ("1b",ACCOUNT,sViewerID,sViewerID,one);

        createChainReportsForBuyA
            ("2a",ACCOUNT,sExtraUserID,sViewerID,one);
        createChainReportsForBuyA
            ("2b",ACCOUNT,sExtraUserID,sViewerID,two);

        createChainReportsForBuyA
            ("3a",oAccount,sViewerID,sViewerID,one);
        createChainReportsForBuyA
            ("3b",oAccount,sViewerID,sViewerID,three);

        createChainReportsForBuyA
            ("4a",oAccount,null,sViewerID,one);
        createChainReportsForBuyA
            ("4b",oAccount,null,sViewerID,four);

        createChainReportsForSellB1
            ("1a",null,sViewerID,sExtraUserID,four);
        createChainReportsForSellB1
            ("1b",null,sViewerID,sExtraUserID,one);

        createChainReportsForSellB1
            ("2a",ACCOUNT,sExtraUserID,sExtraUserID,three);
        createChainReportsForSellB1
            ("2b",ACCOUNT,sExtraUserID,sExtraUserID,one);

        createChainReportsForSellB1
            ("3a",null,null,sExtraUserID,two);
        createChainReportsForSellB1
            ("3b",null,null,sExtraUserID,one);

        createChainReportsForSellB1
            ("4a",oAccount,null,sExtraUserID,one);
        createChainReportsForSellB1
            ("4b",oAccount,null,sExtraUserID,one);

        Date after = new Date();

        assertThat(getPositions(after),
                   allOf(isOfSize(4),
                         hasEntry(pos("A",ACCOUNT,sViewerID),
                                  two.setScale(SCALE)),
                         hasEntry(pos("A",ACCOUNT,sExtraUserID),
                                  three.setScale(SCALE)),
                         hasEntry(pos("A",oAccount,sViewerID),
                                  four.setScale(SCALE)),
                         hasEntry(pos("A",oAccount,(String)null),
                                  five.setScale(SCALE))));
        assertThat(getPositions(after,sActor),
                   allOf(isOfSize(8),
                         hasEntry(pos("A",ACCOUNT,sViewerID),
                                  two.setScale(SCALE)),
                         hasEntry(pos("A",ACCOUNT,sExtraUserID),
                                  three.setScale(SCALE)),
                         hasEntry(pos("A",oAccount,sViewerID),
                                  four.setScale(SCALE)),
                         hasEntry(pos("A",oAccount,(String)null),
                                  five.setScale(SCALE)),
                         hasEntry(pos("B",null,sViewerID),
                                  five.negate().setScale(SCALE)),
                         hasEntry(pos("B",ACCOUNT,sExtraUserID),
                                  four.negate().setScale(SCALE)),
                         hasEntry(pos("B",null,(String)null),
                                  three.negate().setScale(SCALE)),
                         hasEntry(pos("B",oAccount,(String)null),
                                  two.negate().setScale(SCALE))));
    }

    @Test
    public void chainReportsInterLeaved() throws Exception {
        Date before = new Date();
        createChainReportsForBuyA();
        createChainReportsForSellB1();
        Date after1 = new Date();
        createChainReportsForSellA();
        createChainReportsForSellB2();
        Date after2 = new Date();
        createChainReportsForSellShortA();
        createChainReportsForBuyB1();
        Date after3 = new Date();
        createChainReportsForSellShortExemptA();
        createChainReportsForSellShortB();
        Date after4 = new Date();
        createChainReportsForBuyB2();
        Date after5 = new Date();

        assertBigDecimalEquals(BigDecimal.ZERO, getPosition(before, "A"));
        assertBigDecimalEquals(BigDecimal.ZERO, getPosition(before, "B"));
        assertThat(getPositions(before), isOfSize(0));

        BigDecimal positionA = POSITION_BUY_A;
        BigDecimal positionB = POSITION_SELL_B1.negate();
        assertBigDecimalEquals(positionA, getPosition(after1, "A"));
        assertBigDecimalEquals(positionB, getPosition(after1, "B"));
        assertThat(getPositions(after1), allOf(isOfSize(2),
                hasEntry(pos("A"), positionA.setScale(SCALE)),
                hasEntry(pos("B"), positionB.setScale(SCALE))));

        positionA = POSITION_BUY_A.subtract(POSITION_SELL_A);
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2);
        assertBigDecimalEquals(positionA,
                getPosition(after2, "A"));
        assertBigDecimalEquals(positionB, getPosition(after2, "B"));
        assertThat(getPositions(after2), allOf(isOfSize(2),
                hasEntry(pos("A"), positionA.setScale(SCALE)),
                hasEntry(pos("B"), positionB.setScale(SCALE))));

        positionA = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A);
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1);
        assertBigDecimalEquals(positionA, getPosition(after3, "A"));
        assertBigDecimalEquals(positionB, getPosition(after3, "B"));
        assertThat(getPositions(after3), allOf(isOfSize(2),
                hasEntry(pos("A"), positionA.setScale(SCALE)),
                hasEntry(pos("B"), positionB.setScale(SCALE))));

        positionA = FINAL_POSITION_A;
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1).
                subtract(POSITION_SELL_SHORT_B);
        assertBigDecimalEquals(positionA,
                getPosition(after4, "A"));
        assertBigDecimalEquals(positionB,
                getPosition(after4, "B"));
        assertThat(getPositions(after4), allOf(isOfSize(2),
                hasEntry(pos("A"), positionA.setScale(SCALE)),
                hasEntry(pos("B"), positionB.setScale(SCALE))));

        positionA = FINAL_POSITION_A;
        positionB = FINAL_POSITION_B;
        assertBigDecimalEquals(positionA, getPosition(after5, "A"));
        assertBigDecimalEquals(positionB, getPosition(after5, "B"));
        assertThat(getPositions(after5), allOf(isOfSize(2),
                hasEntry(pos("A"), positionA.setScale(SCALE)),
                hasEntry(pos("B"), positionB.setScale(SCALE))));
    }

    private void verifyShortPosition(BigDecimal inValue,
                                     Date inBefore,
                                     Date inAfter) throws Exception {
        assertBigDecimalEquals(inValue.negate(),
                getPosition(inAfter, TEST_SYMBOL));
        assertThat(getPositions(inAfter), allOf(isOfSize(3),
                hasEntry(pos(TEST_SYMBOL), inValue.negate().setScale(SCALE)),
                hasAandB()));
        assertBigDecimalEquals(BigDecimal.ZERO,
                getPosition(inBefore, TEST_SYMBOL));
        assertThat(getPositions(inBefore), allOf(isOfSize(2), hasAandB()));
    }

    private static Matcher<Map<PositionKey, BigDecimal>> hasAandB() {
        return allOf(hasEntry(pos("A"), FINAL_POSITION_A.setScale(SCALE)),
                hasEntry(pos("B"), FINAL_POSITION_B.setScale(SCALE)));
    }

    private List<ExecutionReport> createChainReportsForBuyA()
        throws Exception
    {
        return createChainReportsForBuyA
            ("",ACCOUNT,sActorID,sViewerID,POSITION_BUY_A);
    }
    private List<ExecutionReport> createChainReportsForBuyA
        (String prefix,
         String inAccount,
         UserID inActorID,
         UserID inViewerID,
         BigDecimal finalPosition)
        throws Exception
    {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER(prefix+"a1", null, "A", Side.Buy, BigDecimal.ONE, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"a2", prefix+"a1", "A", Side.Buy, BigDecimal.TEN, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"a3", prefix+"a2", "A", Side.Buy, POSITION_SELL_SHORTE_A, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"a4", prefix+"a3", "A", Side.Buy, finalPosition, inAccount, inActorID, inViewerID));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForSellA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a5", null, "A", Side.Sell, BigDecimal.ONE));
        reports.add(createAndSaveER("a6", "a5", "A", Side.Sell, BigDecimal.TEN));
        reports.add(createAndSaveER("a7", "a6", "A", Side.Sell, POSITION_SELL_A));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForSellShortA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a8", null, "A", Side.SellShort, BigDecimal.ONE));
        reports.add(createAndSaveER("a9", "a8", "A", Side.SellShort, POSITION_SELL_SHORT_A));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForSellShortExemptA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a10", null, "A", Side.SellShortExempt, POSITION_SELL_SHORTE_A));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForSellB1() throws Exception {
        return createChainReportsForSellB1
            ("",ACCOUNT,sActorID,sViewerID,POSITION_SELL_B1);
    }
    private List<ExecutionReport> createChainReportsForSellB1
        (String prefix,
         String inAccount,
         UserID inActorID,
         UserID inViewerID,
         BigDecimal finalPosition)
        throws Exception
    {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER(prefix+"b1", null, "B", Side.Sell, BigDecimal.ONE, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"b2", prefix+"b1", "B", Side.Sell, BigDecimal.TEN, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"b3", prefix+"b2", "B", Side.Sell, finalPosition, inAccount, inActorID, inViewerID));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForSellB2() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b4", null, "B", Side.Sell, new BigDecimal("23.34")));
        reports.add(createAndSaveER("b5", "b4", "B", Side.Sell, new BigDecimal("34.56")));
        reports.add(createAndSaveER("b6", "b5", "B", Side.Sell, POSITION_SELL_B2));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForBuyB1() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b7", null, "B", Side.Buy, POSITION_BUY_B1));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForBuyB2() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b8", null, "B", Side.Buy, new BigDecimal("38")));
        reports.add(createAndSaveER("b9", "b8", "B", Side.Buy, POSITION_BUY_B2));
        return reports;
    }
    private List<ExecutionReport> createChainReportsForSellShortB() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b10", null, "B", Side.SellShort, new BigDecimal("23.34")));
        reports.add(createAndSaveER("b11", "b10", "B", Side.SellShort, new BigDecimal("34.56")));
        reports.add(createAndSaveER("b12", "b11", "B", Side.SellShort, new BigDecimal("67.45")));
        reports.add(createAndSaveER("b13", "b12", "B", Side.SellShort, new BigDecimal("95.34")));
        reports.add(createAndSaveER("b14", "b13", "B", Side.SellShort, POSITION_SELL_SHORT_B));
        return reports;
    }
    private void createBackgroundNoise() throws Exception {
        //Create all the reports for symbols A and B
        createChainReportsForBuyA();
        createChainReportsForBuyB1();
        createChainReportsForBuyB2();
        createChainReportsForSellA();
        createChainReportsForSellB1();
        createChainReportsForSellB2();
        createChainReportsForSellShortA();
        createChainReportsForSellShortB();
        createChainReportsForSellShortExemptA();
    }

    private static final BigDecimal POSITION_BUY_A = new BigDecimal("246.82");
    private static final BigDecimal POSITION_SELL_A = new BigDecimal("23.54");
    private static final BigDecimal POSITION_SELL_SHORT_A = new BigDecimal("89.45");
    private static final BigDecimal POSITION_SELL_SHORTE_A = new BigDecimal("43.54");
    private static final BigDecimal POSITION_SELL_B1 = new BigDecimal("8795.45");
    private static final BigDecimal POSITION_SELL_B2 = new BigDecimal("238.34");
    private static final BigDecimal POSITION_BUY_B1 = new BigDecimal("900");
    private static final BigDecimal POSITION_BUY_B2 = new BigDecimal("743.43");
    private static final BigDecimal POSITION_SELL_SHORT_B = new BigDecimal("280.101");
    private static final BigDecimal FINAL_POSITION_A = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A).subtract(POSITION_SELL_SHORTE_A);
    private static final BigDecimal FINAL_POSITION_B = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1).
                subtract(POSITION_SELL_SHORT_B).add(POSITION_BUY_B2);
    private static final String TEST_SYMBOL = "s1";
}
