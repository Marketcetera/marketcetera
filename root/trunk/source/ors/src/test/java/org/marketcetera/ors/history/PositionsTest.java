package org.marketcetera.ors.history;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.MSymbol;
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
                hasEntry(sym(TEST_SYMBOL), value.setScale(SCALE))));
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
                hasEntry(sym(TEST_SYMBOL), value.setScale(SCALE)),
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
                hasEntry(sym("A"), position.setScale(SCALE))));

        position = POSITION_BUY_A.subtract(POSITION_SELL_A);
        assertBigDecimalEquals(position,
                getPosition(after2, "A"));
        assertThat(getPositions(after2), allOf(isOfSize(1),
                hasEntry(sym("A"), position.setScale(SCALE))));

        position = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A);
        assertBigDecimalEquals(position, getPosition(after3, "A"));
        assertThat(getPositions(after3), allOf(isOfSize(1),
                hasEntry(sym("A"), position.setScale(SCALE))));

        position = FINAL_POSITION_A;
        assertBigDecimalEquals(position,
                getPosition(after4, "A"));
        assertThat(getPositions(after4), allOf(isOfSize(1),
                hasEntry(sym("A"), position.setScale(SCALE))));
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
                hasEntry(sym("A"), positionA.setScale(SCALE)),
                hasEntry(sym("B"), positionB.setScale(SCALE))));

        positionA = POSITION_BUY_A.subtract(POSITION_SELL_A);
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2);
        assertBigDecimalEquals(positionA,
                getPosition(after2, "A"));
        assertBigDecimalEquals(positionB, getPosition(after2, "B"));
        assertThat(getPositions(after2), allOf(isOfSize(2),
                hasEntry(sym("A"), positionA.setScale(SCALE)),
                hasEntry(sym("B"), positionB.setScale(SCALE))));

        positionA = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A);
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1);
        assertBigDecimalEquals(positionA, getPosition(after3, "A"));
        assertBigDecimalEquals(positionB, getPosition(after3, "B"));
        assertThat(getPositions(after3), allOf(isOfSize(2),
                hasEntry(sym("A"), positionA.setScale(SCALE)),
                hasEntry(sym("B"), positionB.setScale(SCALE))));

        positionA = FINAL_POSITION_A;
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1).
                subtract(POSITION_SELL_SHORT_B);
        assertBigDecimalEquals(positionA,
                getPosition(after4, "A"));
        assertBigDecimalEquals(positionB,
                getPosition(after4, "B"));
        assertThat(getPositions(after4), allOf(isOfSize(2),
                hasEntry(sym("A"), positionA.setScale(SCALE)),
                hasEntry(sym("B"), positionB.setScale(SCALE))));

        positionA = FINAL_POSITION_A;
        positionB = FINAL_POSITION_B;
        assertBigDecimalEquals(positionA, getPosition(after5, "A"));
        assertBigDecimalEquals(positionB, getPosition(after5, "B"));
        assertThat(getPositions(after5), allOf(isOfSize(2),
                hasEntry(sym("A"), positionA.setScale(SCALE)),
                hasEntry(sym("B"), positionB.setScale(SCALE))));
    }

    private void verifyShortPosition(BigDecimal inValue,
                                     Date inBefore,
                                     Date inAfter) throws Exception {
        assertBigDecimalEquals(inValue.negate(),
                getPosition(inAfter, TEST_SYMBOL));
        assertThat(getPositions(inAfter), allOf(isOfSize(3),
                hasEntry(sym(TEST_SYMBOL), inValue.negate().setScale(SCALE)),
                hasAandB()));
        assertBigDecimalEquals(BigDecimal.ZERO,
                getPosition(inBefore, TEST_SYMBOL));
        assertThat(getPositions(inBefore), allOf(isOfSize(2), hasAandB()));
    }

    private static Matcher<Map<MSymbol, BigDecimal>> hasAandB() {
        return allOf(hasEntry(sym("A"), FINAL_POSITION_A.setScale(SCALE)),
                hasEntry(sym("B"), FINAL_POSITION_B.setScale(SCALE)));
    }

    private List<ExecutionReport> createChainReportsForBuyA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a1", null, "A", Side.Buy, BigDecimal.ONE));
        reports.add(createAndSaveER("a2", "a1", "A", Side.Buy, BigDecimal.TEN));
        reports.add(createAndSaveER("a3", "a2", "A", Side.Buy, POSITION_SELL_SHORTE_A));
        reports.add(createAndSaveER("a4", "a3", "A", Side.Buy, POSITION_BUY_A));
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
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b1", null, "B", Side.Sell, BigDecimal.ONE));
        reports.add(createAndSaveER("b2", "b1", "B", Side.Sell, BigDecimal.TEN));
        reports.add(createAndSaveER("b3", "b2", "B", Side.Sell, POSITION_SELL_B1));
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
