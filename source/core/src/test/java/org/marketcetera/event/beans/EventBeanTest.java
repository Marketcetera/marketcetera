package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link EventBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class EventBeanTest
        extends AbstractEventBeanTestBase<EventBean>
{
    /**
     * Tests {@link EventBean#copy(EventBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        doCopyTest(new EventBean());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#constructBean()
     */
    @Override
    protected EventBean constructBean()
    {
        return new EventBean();
    }
    /**
     * Tests {@link EventBean#hashCode()} and {@link EventBeanTest#equals(Object)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Override
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        EventBean bean1 = new EventBean();
        EventBean bean2 = new EventBean();
        EventBean bean3 = new EventBean();
        // verify that null attributes are still equal (mostly this is that equals/hashcode doesn't NPE with null attributes)
        assertNull(bean1.getTimestamp());
        assertNull(bean2.getTimestamp());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // test messageId
        bean1.setMessageId(1);
        bean2.setMessageId(bean1.getMessageId());
        assertFalse(bean1.getMessageId() == bean3.getMessageId());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      bean3);
        bean3.setMessageId(bean1.getMessageId());
        // test timestamp
        // set bean3 to non-null
        assertNull(bean1.getTimestamp());
        bean3.setTimestamp(new Date());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setTimestamp(bean1.getTimestamp());
        // test source
        // set bean3 to non-null
        assertNull(bean1.getSource());
        bean3.setSource(this);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
    /**
     * Tests {@link EventBean#copy(EventBean)}.
     *
     * @param inBean an <code>EventBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void doCopyTest(EventBean inBean)
            throws Exception
    {
        verifyEventBean(inBean,
                        Long.MIN_VALUE,
                        null,
                        null);
        EventBean newBean = EventBean.copy(inBean);
        verifyEventBean(newBean,
                        Long.MIN_VALUE,
                        null,
                        null);
        long messageId = System.nanoTime();
        Date timestamp = new Date();
        Object source = EventBeanTest.class;
        inBean.setMessageId(messageId);
        inBean.setTimestamp(timestamp);
        inBean.setSource(source);
        verifyEventBean(inBean,
                        messageId,
                        timestamp,
                        source);
        newBean = EventBean.copy(inBean);
        verifyEventBean(newBean,
                        messageId,
                        timestamp,
                        source);
    }
    /**
     * Verifies that the given <code>EventBean</code> contains the given attributes.
     *
     * @param inBean an <code>EventBean</code> value
     * @param inExpectedMessageId a <code>long</code> value
     * @param inExpectedTimestamp a <code>Date</code> value
     * @param inExpectedSource an <code>Object</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyEventBean(EventBean inBean,
                                long inExpectedMessageId,
                                Date inExpectedTimestamp,
                                Object inExpectedSource)
            throws Exception
    {
        assertEquals(inExpectedMessageId,
                     inBean.getMessageId());
        assertEquals(inExpectedTimestamp,
                     inBean.getTimestamp());
        assertEquals(inExpectedSource,
                     inBean.getSource());
    }
}
