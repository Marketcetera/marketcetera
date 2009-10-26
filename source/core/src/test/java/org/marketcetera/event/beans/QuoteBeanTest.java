package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link QuoteBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class QuoteBeanTest
        extends MarketDataBeanTest
{
    /**
     * Tests {@link MarketDataBean#copy(MarketDataBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        MarketDataBeanTest.doCopyTest(new QuoteBean());
        doCopyTest(new QuoteBean());
    }
    /**
     * Tests {@link QuoteBean#getQuoteBeanFromEvent(org.marketcetera.event.QuoteEvent, QuoteAction)} and
     * {@link QuoteBean#getQuoteBeanFromEvent(org.marketcetera.event.QuoteEvent, java.util.Date, java.math.BigDecimal, QuoteAction).
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getQuoteBeanFromEvent()
            throws Exception
    {
        Instrument equity = new Equity("METC");
        Date timestamp = new Date();
        BigDecimal size = BigDecimal.ONE;
        QuoteAction action = QuoteAction.CHANGE;
        final QuoteEventBuilder<AskEvent> equityAskBuilder = QuoteEventBuilder.askEvent(equity);
        equityAskBuilder.withExchange("exchange")
                        .withPrice(BigDecimal.ONE)
                        .withSize(BigDecimal.TEN)
                        .withQuoteDate(DateUtils.dateToString(new Date()));
        // signature 1
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                QuoteBean.getQuoteBeanFromEvent(null,
                                                new Date(),
                                                BigDecimal.ZERO,
                                                QuoteAction.ADD);
            }
        };
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                AskEvent ask = equityAskBuilder.create();
                QuoteBean.getQuoteBeanFromEvent(ask,
                                                null,
                                                BigDecimal.ZERO,
                                                QuoteAction.ADD);
            }
        };
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                AskEvent ask = equityAskBuilder.create();
                QuoteBean.getQuoteBeanFromEvent(ask,
                                                new Date(),
                                                null,
                                                QuoteAction.ADD);
            }
        };
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                AskEvent ask = equityAskBuilder.create();
                QuoteBean.getQuoteBeanFromEvent(ask,
                                                new Date(),
                                                BigDecimal.ZERO,
                                                null);
            }
        };
        // signature 2
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                QuoteBean.getQuoteBeanFromEvent(null,
                                                QuoteAction.ADD);
            }
        };
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                AskEvent ask = equityAskBuilder.create();
                QuoteBean.getQuoteBeanFromEvent(ask,
                                                null);
            }
        };
        // valid tests
        AskEvent ask = equityAskBuilder.create();
        // start with null source
        assertNull(ask.getSource());
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        // change to non-null source
        ask.setSource(this);
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        // use odd timestamp
        timestamp = new Date(-1);
        assertTrue(timestamp.getTime() < 0);
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            timestamp,
                                                            size,
                                                            action),
                            ask.getMessageId(),
                            timestamp,
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            size,
                            action);
        timestamp = new Date();
        // use negative size
        size = new BigDecimal("-10");
        assertTrue(size.intValue() < 0);
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            timestamp,
                                                            size,
                                                            action),
                            ask.getMessageId(),
                            timestamp,
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            size,
                            action);
        // use zero size
        size = BigDecimal.ZERO;
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            timestamp,
                                                            size,
                                                            action),
                            ask.getMessageId(),
                            timestamp,
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            size,
                            action);
        // now try varying the inputs to the event itself
        // some can't be done (it might look like we're testing the event impl validation here, but
        //  what's really being accomplished is verifying that behavior the quotebean depends on remains
        //  in the event impls.  if that behavior changes, this test will fail and the quotebean constructors
        //  will have to be re-evaluated)
        equityAskBuilder.withMessageId(-1);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_INVALID_MESSAGEID.getText(-1)){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        // this will get a messageId assigned
        equityAskBuilder.withMessageId(Long.MIN_VALUE);
        // test the instrument
        equityAskBuilder.withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withInstrument(equity);
        // exchange
        equityAskBuilder.withExchange(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withExchange("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withExchange("test exchange");
        // price
        equityAskBuilder.withPrice(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_PRICE.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withPrice(BigDecimal.ONE);
        // size
        equityAskBuilder.withSize(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_SIZE.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withSize(BigDecimal.TEN);
        // exchange timestamp
        equityAskBuilder.withQuoteDate(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE_TIMESTAMP.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withQuoteDate("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE_TIMESTAMP.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                equityAskBuilder.create();
            }
        };
        equityAskBuilder.withQuoteDate(DateUtils.dateToString(new Date()));
        // now test valid cases
        // event price
        equityAskBuilder.withPrice(new BigDecimal("-10"));
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        equityAskBuilder.withPrice(BigDecimal.ZERO);
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        equityAskBuilder.withPrice(BigDecimal.TEN);
        // event size
        equityAskBuilder.withSize(new BigDecimal("-20"));
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        equityAskBuilder.withSize(BigDecimal.ZERO);
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        // this seems odd, but it is valid
        equityAskBuilder.withQuoteDate("this-is-not-a-date");
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(ask,
                                                            action),
                            ask.getMessageId(),
                            ask.getTimestamp(),
                            ask.getSource(),
                            ask.getExchange(),
                            ask.getExchangeTimestamp(),
                            ask.getInstrument(),
                            ask.getPrice(),
                            ask.getSize(),
                            action);
        // last, show that it doesn't have to be either an ask or an equity
        Instrument option = new Option("MSFT",
                                       "20100319",
                                       BigDecimal.ONE,
                                       OptionType.Call);
        final QuoteEventBuilder<BidEvent> optionBidBuilder = QuoteEventBuilder.bidEvent(option);
        // these values don't need to be as extensively tested because, while necessary to create
        //  an OptionEvent, they don't get transfered to the QuoteEvent
        optionBidBuilder.withExpirationType(ExpirationType.AMERICAN)
                        .withMultiplier(0)
                        .withUnderlyingInstrument(equity);
        // these values will get tested
        optionBidBuilder.withAction(QuoteAction.DELETE)
                        .withExchange("exchange")
                        .withPrice(BigDecimal.ONE)
                        .withQuoteDate(DateUtils.dateToString(new Date()))
                        .withSize(BigDecimal.TEN);
        BidEvent bid = optionBidBuilder.create();
        verifyQuoteBeanFull(QuoteBean.getQuoteBeanFromEvent(bid,
                                                            action),
                            bid.getMessageId(),
                            bid.getTimestamp(),
                            bid.getSource(),
                            bid.getExchange(),
                            bid.getExchangeTimestamp(),
                            bid.getInstrument(),
                            bid.getPrice(),
                            bid.getSize(),
                            action);
    }
    /**
     * Tests {@link QuoteBean#getAction()} and {@link QuoteBean#setAction(QuoteAction)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void action()
            throws Exception
    {
       QuoteBean bean = (QuoteBean)constructBean();
       assertNull(bean.getAction());
       QuoteAction action = QuoteAction.CHANGE;
       bean.setAction(action);
       assertEquals(action,
                    bean.getAction());
    }
    /**
     * Tests {@link MarketDataBean#hashCode()} and {@link MarketDataBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        QuoteBean bean1 = (QuoteBean)constructBean();
        QuoteBean bean2 = (QuoteBean)constructBean();
        QuoteBean bean3 = (QuoteBean)constructBean();
        assertNull(bean1.getAction());
        assertNull(bean2.getAction());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // test action
        assertNull(bean1.getAction());
        bean3.setAction(QuoteAction.DELETE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setAction(bean1.getAction());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.MarketDataBeanTest#constructBean()
     */
    @Override
    protected MarketDataBean constructBean()
    {
        return new QuoteBean();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.MarketDataBeanTest#doAdditionalValidationTest(org.marketcetera.event.beans.MarketDataBean)
     */
    @Override
    protected void doAdditionalValidationTest(MarketDataBean inBean)
            throws Exception
    {
        // do MarketDataBean-level validation tests
        super.doAdditionalValidationTest(inBean);
        final QuoteBean quote = (QuoteBean)inBean;
        assertNull(quote.getAction());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_QUOTE_ACTION.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                quote.validate();
            }
        };
        quote.setAction(QuoteAction.ADD);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#doAdditionalSetDefaultsTest(org.marketcetera.event.beans.EventBean)
     */
    @Override
    protected void doAdditionalSetDefaultsTest(MarketDataBean inBean)
            throws Exception
    {
        // perform MarketDataBean-level defaults test
        super.doAdditionalSetDefaultsTest(inBean);
        QuoteBean quote = (QuoteBean)inBean;
        quote.setAction(null);
        assertNull(quote.getAction());
        quote.setDefaults();
        assertEquals(QuoteAction.ADD,
                     quote.getAction());
        quote.setAction(QuoteAction.DELETE);
        quote.setDefaults();
        assertEquals(QuoteAction.DELETE,
                     quote.getAction());
    }
    /**
     * Tests {@link QuoteBean#copy(QuoteBean)}.
     *
     * @param inBean a <code>QuoteBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void doCopyTest(QuoteBean inBean)
            throws Exception
    {
        verifyQuoteBean(inBean,
                        null);
        QuoteBean newBean = QuoteBean.copy(inBean);
        verifyQuoteBean(newBean,
                        null);
        QuoteAction action = QuoteAction.CHANGE;
        inBean.setAction(action);
        verifyQuoteBean(inBean,
                        action);
        newBean = QuoteBean.copy(inBean);
        verifyQuoteBean(newBean,
                        action);
    }
    /**
     * Verifies that the given <code>QuoteBean</code> contains the given attributes.
     *
     * @param inBean a <code>QuoteBean</code> value
     * @param inExpectedAction a <code>QuoteAction</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyQuoteBean(QuoteBean inBean,
                                QuoteAction inExpectedAction)
            throws Exception
    {
        assertEquals(inExpectedAction,
                     inBean.getAction());
    }
    /**
     * Verifies that the given <code>QuoteBean</code> contains the given attributes.
     *
     * @param inBean a <code>QuoteBean</code> value
     * @param inExpectedMessageId a <code>long</code> value
     * @param inExpectedTimestamp a <code>Date</code> value
     * @param inExpectedSource an <code>Object</code> value
     * @param inExpectedExchange a <code>String</code> value
     * @param inExpectedExchangeTimestamp a <code>String</code> value
     * @param inExpectedInstrument an <code>Instrument</code> value
     * @param inExpectedPrice a <code>BigDecimal</code> value
     * @param inExpectedSize a <code>BigDecimal</code> value
     * @param inExpectedAction a <code>QuoteAction</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyQuoteBeanFull(QuoteBean inBean,
                                    long inExpectedMessageId,
                                    Date inExpectedTimestamp,
                                    Object inExpectedSource,
                                    String inExpectedExchange,
                                    String inExpectedExchangeTimestamp,
                                    Instrument inExpectedInstrument,
                                    BigDecimal inExpectedPrice,
                                    BigDecimal inExpectedSize,
                                    QuoteAction inExpectedAction)
            throws Exception
    {
        verifyQuoteBean(inBean,
                        inExpectedAction);
        verifyMarketDataBean(inBean,
                             inExpectedExchange,
                             inExpectedExchangeTimestamp,
                             inExpectedInstrument,
                             inExpectedPrice,
                             inExpectedSize);
        EventBeanTest.verifyEventBean(inBean,
                                      inExpectedMessageId,
                                      inExpectedTimestamp,
                                      inExpectedSource);
    }
}
