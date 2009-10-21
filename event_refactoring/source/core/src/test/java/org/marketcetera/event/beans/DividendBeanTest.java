package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.util.DividendFrequency;
import org.marketcetera.event.util.DividendStatus;
import org.marketcetera.event.util.DividendType;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link DividendBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DividendBeanTest
        extends AbstractEventBeanTestBase<DividendBean>
{
    /**
     * Tests {@link DividendBean#getEquity()} and {@link DividendBean#setEquity(org.marketcetera.trade.Equity)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void equity()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getEquity());
        bean.setEquity(null);
        assertNull(bean.getEquity());
        Equity metc = new Equity("METC");
        bean.setEquity(metc);
        assertEquals(metc,
                     bean.getEquity());
    }
    /**
     * Tests {@link DividendBean#getAmount()} and {@link DividendBean#setAmount(org.marketcetera.trade.Amount)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void amount()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getAmount());
        bean.setAmount(null);
        assertNull(bean.getAmount());
        BigDecimal amount = BigDecimal.TEN;
        bean.setAmount(amount);
        assertEquals(amount,
                     bean.getAmount());
    }
    /**
     * Tests {@link DividendBean#getCurrency()} and {@link DividendBean#setCurrency(org.marketcetera.trade.Currency)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void currency()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getCurrency());
        bean.setCurrency(null);
        assertNull(bean.getCurrency());
        String currency = "US Dollars";
        bean.setCurrency(currency);
        assertEquals(currency,
                     bean.getCurrency());
    }
    /**
     * Tests {@link DividendBean#getDeclareDate()} and {@link DividendBean#setDeclareDate(org.marketcetera.trade.DeclareDate)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void declareDate()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getDeclareDate());
        bean.setDeclareDate(null);
        assertNull(bean.getDeclareDate());
        bean.setDeclareDate("");
        assertEquals("",
                     bean.getDeclareDate());
        String declareDate = DateUtils.dateToString(new Date());
        bean.setDeclareDate(declareDate);
        assertEquals(declareDate,
                     bean.getDeclareDate());
    }
    /**
     * Tests {@link DividendBean#getExecutionDate()} and {@link DividendBean#setExecutionDate(org.marketcetera.trade.ExecutionDate)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void executionDate()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getExecutionDate());
        bean.setExecutionDate(null);
        assertNull(bean.getExecutionDate());
        bean.setExecutionDate("");
        assertEquals("",
                     bean.getExecutionDate());
        String ExecutionDate = DateUtils.dateToString(new Date());
        bean.setExecutionDate(ExecutionDate);
        assertEquals(ExecutionDate,
                     bean.getExecutionDate());
    }
    /**
     * Tests {@link DividendBean#getPaymentDate()} and {@link DividendBean#setPaymentDate(org.marketcetera.trade.PaymentDate)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void paymentDate()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getPaymentDate());
        bean.setPaymentDate(null);
        assertNull(bean.getPaymentDate());
        bean.setPaymentDate("");
        assertEquals("",
                     bean.getPaymentDate());
        String PaymentDate = DateUtils.dateToString(new Date());
        bean.setPaymentDate(PaymentDate);
        assertEquals(PaymentDate,
                     bean.getPaymentDate());
    }
    /**
     * Tests {@link DividendBean#getRecordDate()} and {@link DividendBean#setRecordDate(org.marketcetera.trade.RecordDate)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void recordDate()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getRecordDate());
        bean.setRecordDate(null);
        assertNull(bean.getRecordDate());
        bean.setRecordDate("");
        assertEquals("",
                     bean.getRecordDate());
        String recordDate = DateUtils.dateToString(new Date());
        bean.setRecordDate(recordDate);
        assertEquals(recordDate,
                     bean.getRecordDate());
    }
    /**
     * Tests {@link DividendBean#getFrequency()} and {@link DividendBean#setFrequency(org.marketcetera.trade.Frequency)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void frequency()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getFrequency());
        bean.setFrequency(null);
        assertNull(bean.getFrequency());
        DividendFrequency frequency = DividendFrequency.ANNUALLY;
        bean.setFrequency(frequency);
        assertEquals(frequency,
                     bean.getFrequency());
    }
    /**
     * Tests {@link DividendBean#getStatus()} and {@link DividendBean#setStatus(org.marketcetera.trade.Status)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void status()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getStatus());
        bean.setStatus(null);
        assertNull(bean.getStatus());
        DividendStatus status = DividendStatus.OFFICIAL;
        bean.setStatus(status);
        assertEquals(status,
                     bean.getStatus());
    }
    /**
     * Tests {@link DividendBean#getType()} and {@link DividendBean#setType(org.marketcetera.trade.Type)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void type()
            throws Exception
    {
        DividendBean bean = new DividendBean();
        assertNull(bean.getType());
        bean.setType(null);
        assertNull(bean.getType());
        DividendType type = DividendType.CURRENT;
        bean.setType(type);
        assertEquals(type,
                     bean.getType());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#doAdditionalValidationTest(DividendBean)
     */
    @Override
    protected void doAdditionalValidationTest(final DividendBean inBean)
            throws Exception
    {
        // null equity
        assertNull(inBean.getEquity());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EQUITY.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setEquity(new Equity("METC"));
        // null amount
        assertNull(inBean.getAmount());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_AMOUNT.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setAmount(BigDecimal.TEN);
        // null currency
        assertNull(inBean.getCurrency());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_CURRENCY.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        // empty currency
        inBean.setCurrency("");
        assertTrue(inBean.getCurrency().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_CURRENCY.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setCurrency("US Dollars");
        // null declareDate
        assertNull(inBean.getDeclareDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_DECLARE_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        // empty declareDate
        inBean.setDeclareDate("");
        assertTrue(inBean.getDeclareDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_DECLARE_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setDeclareDate(DateUtils.dateToString(new Date()));
        // null executionDate
        assertNull(inBean.getExecutionDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXECUTION_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        // empty executionDate
        inBean.setExecutionDate("");
        assertTrue(inBean.getExecutionDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXECUTION_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setExecutionDate(DateUtils.dateToString(new Date()));
        // null paymentDate
        assertNull(inBean.getPaymentDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_PAYMENT_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        // empty paymentDate
        inBean.setPaymentDate("");
        assertTrue(inBean.getPaymentDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_PAYMENT_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setPaymentDate(DateUtils.dateToString(new Date()));
        // null recordDate
        assertNull(inBean.getRecordDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_RECORD_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        // empty recordDate
        inBean.setRecordDate("");
        assertTrue(inBean.getRecordDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_RECORD_DATE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setRecordDate(DateUtils.dateToString(new Date()));
        // null frequency
        assertNull(inBean.getFrequency());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_FREQUENCY.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setFrequency(DividendFrequency.MONTHLY);
        // null status
        assertNull(inBean.getStatus());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_STATUS.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setStatus(DividendStatus.UNOFFICIAL);
        // null type
        assertNull(inBean.getType());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_TYPE.getText()) {
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setType(DividendType.FUTURE);
    }
    /**
     * Tests {@link DividendBean#hashCode()} and {@link DividendBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        DividendBean bean1 = new DividendBean();
        DividendBean bean2 = new DividendBean();
        DividendBean bean3 = new DividendBean();
        // verify that null attributes are still equal (mostly this is that equals/hashcode doesn't NPE with null attributes)
        assertNull(bean1.getAmount());
        assertNull(bean2.getAmount());
        assertNull(bean1.getCurrency());
        assertNull(bean2.getCurrency());
        assertNull(bean1.getDeclareDate());
        assertNull(bean2.getDeclareDate());
        assertNull(bean1.getEquity());
        assertNull(bean2.getEquity());
        assertNull(bean1.getExecutionDate());
        assertNull(bean2.getExecutionDate());
        assertNull(bean1.getFrequency());
        assertNull(bean2.getFrequency());
        assertNull(bean1.getPaymentDate());
        assertNull(bean2.getPaymentDate());
        assertNull(bean1.getRecordDate());
        assertNull(bean2.getRecordDate());
        assertNull(bean1.getStatus());
        assertNull(bean2.getStatus());
        assertNull(bean1.getType());
        assertNull(bean2.getType());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // differs by a superclass attribute
        bean1.setMessageId(1);
        bean2.setMessageId(bean1.getMessageId());
        assertFalse(bean1.getMessageId() == bean3.getMessageId());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setMessageId(bean1.getMessageId());
        assertEquals(bean1.getTimestamp(),
                     bean3.getTimestamp());
        // test amount
        // set bean3 amount to non-null
        assertNull(bean1.getAmount());
        bean3.setAmount(BigDecimal.TEN);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setAmount(bean1.getAmount());
        // test currency
        // set bean3 currency to non-null
        assertNull(bean1.getCurrency());
        bean3.setCurrency("CA Dollars");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setCurrency(bean1.getCurrency());
        // test declareDate
        // set bean3 to non-null
        assertNull(bean1.getDeclareDate());
        bean3.setDeclareDate(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setDeclareDate(bean1.getDeclareDate());
        // test equity
        // set bean3 to non-null
        assertNull(bean1.getEquity());
        bean3.setEquity(new Equity("METC"));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setEquity(bean1.getEquity());
        // test executionDate
        // set bean3 to non-null
        assertNull(bean1.getExecutionDate());
        bean3.setExecutionDate(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setExecutionDate(bean1.getExecutionDate());
        // test frequency
        // set bean3 to non-null
        assertNull(bean1.getFrequency());
        bean3.setFrequency(DividendFrequency.QUARTERLY);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setFrequency(bean1.getFrequency());
        // test paymentDate
        // set bean3 to non-null
        assertNull(bean1.getPaymentDate());
        bean3.setPaymentDate(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setPaymentDate(bean1.getPaymentDate());
        // test recordDate
        // set bean3 to non-null
        assertNull(bean1.getRecordDate());
        bean3.setRecordDate(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setRecordDate(bean1.getRecordDate());
        // test status
        // set bean3 to non-null
        assertNull(bean1.getStatus());
        bean3.setStatus(DividendStatus.OFFICIAL);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setStatus(bean1.getStatus());
        // test type
        // set bean3 to non-null
        assertNull(bean1.getType());
        bean3.setType(DividendType.SPECIAL);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#constructBean()
     */
    @Override
    protected DividendBean constructBean()
    {
        return new DividendBean();
    }
}
