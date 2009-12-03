package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.security.SimpleUser;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

/* $License$ */
/**
 * Base class for testing position APIs for various instruments.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class PositionsTestBase<T extends Instrument> extends ReportsTestBase {
    @Test
    public void noRecords() throws Exception {
        assertBigDecimalEquals(BigDecimal.ZERO,
                getInstrumentPosition(new Date(), getInstrument()));
        Date date = new Date();
        assertThat(getInstrumentPositions(date), isOfSize(0));
    }

    @Test
    public void singleReportBuy() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, getInstrument(), Side.Buy, value, sExtraUserID);
        Date after = new Date();
        assertBigDecimalEquals(value, getInstrumentPosition(after, getInstrument(), sExtraUser));
        assertThat(getInstrumentPositions(after, sExtraUser), allOf(isOfSize(1),
                hasEntry(pos(getInstrument()), value.setScale(SCALE))));
        assertBigDecimalEquals(BigDecimal.ZERO, getInstrumentPosition(before, getInstrument(), sExtraUser));
        assertThat(getInstrumentPositions(before, sExtraUser), Matchers.allOf(isOfSize(0)));
    }

    @Test
    public void singleReportSell() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, getInstrument(), Side.Sell, value);
        Date after = new Date();
        verifyShortPosition(value, before, after);
    }

    @Test
    public void singleReportSellShort() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, getInstrument(), Side.SellShort, value);
        Date after = new Date();
        verifyShortPosition(value, before, after);
    }

    @Test
    public void singleReportSellShortExempt() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("135.79");
        Date before = new Date();
        createAndSaveER("o1", null, getInstrument(), Side.SellShortExempt, value);
        Date after = new Date();
        verifyShortPosition(value, before, after);
    }

    @Test
    public void positionExcludedWhenZero() throws Exception {
        createBackgroundNoise();
        BigDecimal value = new BigDecimal("34895.434");
        Date before = new Date();
        //Establish a long position
        createAndSaveER("o1", null, getInstrument(), Side.Buy, value);
        Date after1 = new Date();
        //Now cancel it out with a short position
        createAndSaveER("o2", null, getInstrument(), Side.Sell, value);
        Date after2 = new Date();
        //Verify initial position
        assertBigDecimalEquals(BigDecimal.ZERO, getInstrumentPosition(before, getInstrument()));
        assertThat(getInstrumentPositions(before), allOf(isOfSize(2), hasAandB()));
        //verify the long position
        assertBigDecimalEquals(value, getInstrumentPosition(after1, getInstrument()));
        assertThat(getInstrumentPositions(after1), allOf(isOfSize(3),
                hasEntry(pos(getInstrument()), value.setScale(SCALE)),
                hasAandB()));
        //verify that the final position zeros out and disappears
        assertBigDecimalEquals(BigDecimal.ZERO, getInstrumentPosition(after2, getInstrument()));
        assertThat(getInstrumentPositions(after2), allOf(isOfSize(2), hasAandB()));
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
        assertBigDecimalEquals(position, getInstrumentPosition(before, getInstrumentA()));
        assertThat(getInstrumentPositions(before), isOfSize(0));

        position = POSITION_BUY_A;
        assertBigDecimalEquals(position,
                getInstrumentPosition(after1, getInstrumentA()));
        assertThat(getInstrumentPositions(after1), allOf(isOfSize(1),
                hasEntry(pos(getInstrumentA()), position.setScale(SCALE))));

        position = POSITION_BUY_A.subtract(POSITION_SELL_A);
        assertBigDecimalEquals(position,
                getInstrumentPosition(after2, getInstrumentA()));
        assertThat(getInstrumentPositions(after2), allOf(isOfSize(1),
                hasEntry(pos(getInstrumentA()), position.setScale(SCALE))));

        position = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A);
        assertBigDecimalEquals(position, getInstrumentPosition(after3, getInstrumentA()));
        assertThat(getInstrumentPositions(after3), allOf(isOfSize(1),
                hasEntry(pos(getInstrumentA()), position.setScale(SCALE))));

        position = FINAL_POSITION_A;
        assertBigDecimalEquals(position,
                getInstrumentPosition(after4, getInstrumentA()));
        assertThat(getInstrumentPositions(after4), allOf(isOfSize(1),
                hasEntry(pos(getInstrumentA()), position.setScale(SCALE))));
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

        assertThat(getInstrumentPositions(after),
                   allOf(isOfSize(4),
                         hasEntry(pos(getInstrumentA(),ACCOUNT,sViewerID),
                                  two.setScale(SCALE)),
                         hasEntry(pos(getInstrumentA(),ACCOUNT,sExtraUserID),
                                  three.setScale(SCALE)),
                         hasEntry(pos(getInstrumentA(),oAccount,sViewerID),
                                  four.setScale(SCALE)),
                         hasEntry(pos(getInstrumentA(),oAccount,(String)null),
                                  five.setScale(SCALE))));
        assertThat(getInstrumentPositions(after, sActor),
                   allOf(isOfSize(8),
                         hasEntry(pos(getInstrumentA(),ACCOUNT,sViewerID),
                                  two.setScale(SCALE)),
                         hasEntry(pos(getInstrumentA(),ACCOUNT,sExtraUserID),
                                  three.setScale(SCALE)),
                         hasEntry(pos(getInstrumentA(),oAccount,sViewerID),
                                  four.setScale(SCALE)),
                         hasEntry(pos(getInstrumentA(),oAccount,(String)null),
                                  five.setScale(SCALE)),
                         hasEntry(pos(getInstrumentB(),null,sViewerID),
                                  five.negate().setScale(SCALE)),
                         hasEntry(pos(getInstrumentB(),ACCOUNT,sExtraUserID),
                                  four.negate().setScale(SCALE)),
                         hasEntry(pos(getInstrumentB(),null,(String)null),
                                  three.negate().setScale(SCALE)),
                         hasEntry(pos(getInstrumentB(),oAccount,(String)null),
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

        assertBigDecimalEquals(BigDecimal.ZERO, getInstrumentPosition(before, getInstrumentA()));
        assertBigDecimalEquals(BigDecimal.ZERO, getInstrumentPosition(before, getInstrumentB()));
        assertThat(getInstrumentPositions(before), isOfSize(0));

        BigDecimal positionA = POSITION_BUY_A;
        BigDecimal positionB = POSITION_SELL_B1.negate();
        assertBigDecimalEquals(positionA, getInstrumentPosition(after1, getInstrumentA()));
        assertBigDecimalEquals(positionB, getInstrumentPosition(after1, getInstrumentB()));
        assertThat(getInstrumentPositions(after1), allOf(isOfSize(2),
                hasEntry(pos(getInstrumentA()), positionA.setScale(SCALE)),
                hasEntry(pos(getInstrumentB()), positionB.setScale(SCALE))));

        positionA = POSITION_BUY_A.subtract(POSITION_SELL_A);
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2);
        assertBigDecimalEquals(positionA,
                getInstrumentPosition(after2, getInstrumentA()));
        assertBigDecimalEquals(positionB, getInstrumentPosition(after2, getInstrumentB()));
        assertThat(getInstrumentPositions(after2), allOf(isOfSize(2),
                hasEntry(pos(getInstrumentA()), positionA.setScale(SCALE)),
                hasEntry(pos(getInstrumentB()), positionB.setScale(SCALE))));

        positionA = POSITION_BUY_A.subtract(POSITION_SELL_A).
                subtract(POSITION_SELL_SHORT_A);
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1);
        assertBigDecimalEquals(positionA, getInstrumentPosition(after3, getInstrumentA()));
        assertBigDecimalEquals(positionB, getInstrumentPosition(after3, getInstrumentB()));
        assertThat(getInstrumentPositions(after3), allOf(isOfSize(2),
                hasEntry(pos(getInstrumentA()), positionA.setScale(SCALE)),
                hasEntry(pos(getInstrumentB()), positionB.setScale(SCALE))));

        positionA = FINAL_POSITION_A;
        positionB = POSITION_SELL_B1.negate().
                subtract(POSITION_SELL_B2).add(POSITION_BUY_B1).
                subtract(POSITION_SELL_SHORT_B);
        assertBigDecimalEquals(positionA,
                getInstrumentPosition(after4, getInstrumentA()));
        assertBigDecimalEquals(positionB,
                getInstrumentPosition(after4, getInstrumentB()));
        assertThat(getInstrumentPositions(after4), allOf(isOfSize(2),
                hasEntry(pos(getInstrumentA()), positionA.setScale(SCALE)),
                hasEntry(pos(getInstrumentB()), positionB.setScale(SCALE))));

        positionA = FINAL_POSITION_A;
        positionB = FINAL_POSITION_B;
        assertBigDecimalEquals(positionA, getInstrumentPosition(after5, getInstrumentA()));
        assertBigDecimalEquals(positionB, getInstrumentPosition(after5, getInstrumentB()));
        assertThat(getInstrumentPositions(after5), allOf(isOfSize(2),
                hasEntry(pos(getInstrumentA()), positionA.setScale(SCALE)),
                hasEntry(pos(getInstrumentB()), positionB.setScale(SCALE))));
    }

    /**
     * Returns the instrument.
     *
     * @return the instrument.
     */
    protected abstract T getInstrument();

    /**
     * Returns the first instrument.
     *
     * @return the first instrument.
     */
    protected abstract T getInstrumentA();

    /**
     * Returns the second instrument.
     *
     * @return the second instrument.
     */
    protected abstract T getInstrumentB();

    /**
     * Fetches the instrument's position.
     *
     * @param inDate reports received before this time will be used in position calculation.
     * @param inInstrument the instrument.
     *
     * @return the position
     *
     * @throws Exception if there were unexpected errors
     */
    protected abstract BigDecimal getInstrumentPosition(Date inDate,
                                                        T inInstrument)
            throws Exception;

    /**
     * Fetches the instrument's position.
     *
     * @param inDate reports received before this time will be used in position calculation.
     * @param inInstrument the instrument.
     * @param inUser the user querying the position
     *
     * @return the position
     * 
     * @throws Exception if there were unexpected errors
     */
    protected abstract BigDecimal getInstrumentPosition(Date inDate,
                                                        T inInstrument,
                                                        SimpleUser inUser) throws Exception;

    /**
     * Fetches the positions for all instruments with non-zero positions.
     *
     * @param inDate reports received before this time will be used in position calculation.
     *
     * @return all open positions for the instrument type.
     *
     * @throws Exception if there were unexpected errors
     */
    protected abstract Map<PositionKey<T>, BigDecimal> getInstrumentPositions(
            Date inDate)
            throws Exception;

    /**
     * Fetches the position for all instruments with non-zero positions as
     * the supplied user.
     *
     * @param inDate reports received before this time will be used in position calculation.
     * @param inUser the user querying the position
     *
     * @return all open positions visible to the querying user.
     *
     * @throws Exception if there were unexpected errors
     */
    protected abstract Map<PositionKey<T>, BigDecimal> getInstrumentPositions(
            Date inDate, SimpleUser inUser)
            throws Exception;

    private void verifyShortPosition(BigDecimal inValue,
                                     Date inBefore,
                                     Date inAfter) throws Exception {
        assertBigDecimalEquals(inValue.negate(),
                getInstrumentPosition(inAfter, getInstrument()));
        assertThat(getInstrumentPositions(inAfter), allOf(isOfSize(3),
                hasEntry(pos(getInstrument()), inValue.negate().setScale(SCALE)),
                hasAandB()));
        assertBigDecimalEquals(BigDecimal.ZERO,
                getInstrumentPosition(inBefore, getInstrument()));
        assertThat(getInstrumentPositions(inBefore), allOf(isOfSize(2), hasAandB()));
    }

    private Matcher<Map<PositionKey<T>, BigDecimal>> hasAandB() {
        return allOf(hasEntry(pos(getInstrumentA()), FINAL_POSITION_A.setScale(SCALE)),
                hasEntry(pos(getInstrumentB()), FINAL_POSITION_B.setScale(SCALE)));
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
        reports.add(createAndSaveER(prefix+"a1", null, getInstrumentA(), Side.Buy, BigDecimal.ONE, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"a2", prefix+"a1", getInstrumentA(), Side.Buy, BigDecimal.TEN, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"a3", prefix+"a2", getInstrumentA(), Side.Buy, POSITION_SELL_SHORTE_A, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"a4", prefix+"a3", getInstrumentA(), Side.Buy, finalPosition, inAccount, inActorID, inViewerID));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForSellA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a5", null, getInstrumentA(), Side.Sell, BigDecimal.ONE));
        reports.add(createAndSaveER("a6", "a5", getInstrumentA(), Side.Sell, BigDecimal.TEN));
        reports.add(createAndSaveER("a7", "a6", getInstrumentA(), Side.Sell, POSITION_SELL_A));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForSellShortA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a8", null, getInstrumentA(), Side.SellShort, BigDecimal.ONE));
        reports.add(createAndSaveER("a9", "a8", getInstrumentA(), Side.SellShort, POSITION_SELL_SHORT_A));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForSellShortExemptA() throws Exception {
        //A simple chain of buy orders for A
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("a10", null, getInstrumentA(), Side.SellShortExempt, POSITION_SELL_SHORTE_A));
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
        reports.add(createAndSaveER(prefix+"b1", null, getInstrumentB(), Side.Sell, BigDecimal.ONE, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"b2", prefix+"b1", getInstrumentB(), Side.Sell, BigDecimal.TEN, inAccount, inActorID, inViewerID));
        reports.add(createAndSaveER(prefix+"b3", prefix+"b2", getInstrumentB(), Side.Sell, finalPosition, inAccount, inActorID, inViewerID));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForSellB2() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b4", null, getInstrumentB(), Side.Sell, new BigDecimal("23.34")));
        reports.add(createAndSaveER("b5", "b4", getInstrumentB(), Side.Sell, new BigDecimal("34.56")));
        reports.add(createAndSaveER("b6", "b5", getInstrumentB(), Side.Sell, POSITION_SELL_B2));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForBuyB1() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b7", null, getInstrumentB(), Side.Buy, POSITION_BUY_B1));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForBuyB2() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b8", null, getInstrumentB(), Side.Buy, new BigDecimal("38")));
        reports.add(createAndSaveER("b9", "b8", getInstrumentB(), Side.Buy, POSITION_BUY_B2));
        return reports;
    }

    private List<ExecutionReport> createChainReportsForSellShortB() throws Exception {
        List<ExecutionReport> reports = new LinkedList<ExecutionReport>();
        reports.add(createAndSaveER("b10", null, getInstrumentB(), Side.SellShort, new BigDecimal("23.34")));
        reports.add(createAndSaveER("b11", "b10", getInstrumentB(), Side.SellShort, new BigDecimal("34.56")));
        reports.add(createAndSaveER("b12", "b11", getInstrumentB(), Side.SellShort, new BigDecimal("67.45")));
        reports.add(createAndSaveER("b13", "b12", getInstrumentB(), Side.SellShort, new BigDecimal("95.34")));
        reports.add(createAndSaveER("b14", "b13", getInstrumentB(), Side.SellShort, POSITION_SELL_SHORT_B));
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
}
