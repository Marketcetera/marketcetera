package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.Messages;
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
        implements Messages
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
    /**
     * Tests {@link DividendBean#validate()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validate()
        throws Exception
    {
        final DividendBean dividend = new DividendBean();
        // test superclass validation
        dividend.setMessageId(1);
        assertNull(dividend.getTimestamp());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_TIMESTAMP.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setTimestamp(new Date());
        // null equity
        assertNull(dividend.getEquity());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EQUITY.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setEquity(new Equity("METC"));
        // null amount
        assertNull(dividend.getAmount());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_AMOUNT.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setAmount(BigDecimal.TEN);
        // null currency
        assertNull(dividend.getCurrency());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_CURRENCY.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        // empty currency
        dividend.setCurrency("");
        assertTrue(dividend.getCurrency().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_CURRENCY.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setCurrency("US Dollars");
        // null declareDate
        assertNull(dividend.getDeclareDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_DECLARE_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        // empty declareDate
        dividend.setDeclareDate("");
        assertTrue(dividend.getDeclareDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_DECLARE_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setDeclareDate(DateUtils.dateToString(new Date()));
        // null executionDate
        assertNull(dividend.getExecutionDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXECUTION_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        // empty executionDate
        dividend.setExecutionDate("");
        assertTrue(dividend.getExecutionDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXECUTION_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setExecutionDate(DateUtils.dateToString(new Date()));
        // null paymentDate
        assertNull(dividend.getPaymentDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_PAYMENT_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        // empty paymentDate
        dividend.setPaymentDate("");
        assertTrue(dividend.getPaymentDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_PAYMENT_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setPaymentDate(DateUtils.dateToString(new Date()));
        // null recordDate
        assertNull(dividend.getRecordDate());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_RECORD_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        // empty recordDate
        dividend.setRecordDate("");
        assertTrue(dividend.getRecordDate().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_RECORD_DATE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setRecordDate(DateUtils.dateToString(new Date()));
        // null frequency
        assertNull(dividend.getFrequency());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_FREQUENCY.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setFrequency(DividendFrequency.MONTHLY);
        // null status
        assertNull(dividend.getStatus());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_STATUS.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setStatus(DividendStatus.UNOFFICIAL);
        // null type
        assertNull(dividend.getType());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_TYPE.getText()) {
            protected void run()
                throws Exception
            {
                dividend.validate();
            }
        };
        dividend.setType(DividendType.FUTURE);
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
        DividendBean bean1 = new DividendBean();
        DividendBean bean2 = new DividendBean();
        DividendBean bean3 = new DividendBean();
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
    }
}
