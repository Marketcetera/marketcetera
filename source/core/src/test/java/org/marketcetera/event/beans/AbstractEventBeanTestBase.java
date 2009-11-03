package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.Messages;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests common routines of objects that extend {@link EventBean}.
 * 
 * <p>Tests of subclasses of {@link EventBean} should extend this class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractEventBeanTestBase<E extends EventBean>
        implements Messages
{
    /**
     * Tests that the bean gets default values set correctly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public final void setDefaults()
        throws Exception
    {
        E bean = constructBean();
        assertEquals(Long.MIN_VALUE,
                     bean.getMessageId());
        assertNull(bean.getTimestamp());
        bean.setDefaults();
        assertTrue(bean.getMessageId() > 0);
        assertNotNull(bean.getTimestamp());
        doAdditionalSetDefaultsTest(bean);
    }
    /**
     * Tests the validation routine of the bean.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public final void validate()
            throws Exception
    {
        final E bean = constructBean();
        assertNotNull(bean.toString());
        // test common validation
        bean.setMessageId(1);
        assertNull(bean.getTimestamp());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_TIMESTAMP.getText()) {
            @Override
            protected void run()
                throws Exception
            {
                bean.validate();
            }
        };
        bean.setMessageId(Long.MIN_VALUE);
        bean.setTimestamp(new Date());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_INVALID_MESSAGEID.getText(Long.MIN_VALUE)) {
            @Override
            protected void run()
                throws Exception
            {
                bean.validate();
            }
        };
        bean.setMessageId(1);
        doAdditionalValidationTest(bean);
        bean.validate();
        assertNotNull(bean.toString());
    }
    /**
     * Tests <code>messageId</code> get and set methods.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public final void messageId()
            throws Exception
    {
        E bean = constructBean();
        assertEquals(Long.MIN_VALUE,
                     bean.getMessageId());
        bean.setMessageId(0);
        assertEquals(0,
                     bean.getMessageId());
        bean.setMessageId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE,
                     bean.getMessageId());
    }
    /**
     * Tests <code>timestamp</code> get and set methods.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public final void timestamp()
            throws Exception
    {
        final E bean = constructBean();
        assertEquals(null,
                     bean.getTimestamp());
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                throws Exception
            {
                bean.getTimeMillis();
            }
        };
        Date timestamp = new Date(-1);
        bean.setTimestamp(timestamp);
        assertEquals(timestamp,
                     bean.getTimestamp());
        assertEquals(timestamp.getTime(),
                     bean.getTimeMillis());
        timestamp = new Date(0);
        bean.setTimestamp(timestamp);
        assertEquals(timestamp,
                     bean.getTimestamp());
        assertEquals(timestamp.getTime(),
                     bean.getTimeMillis());
        timestamp = new Date();
        bean.setTimestamp(timestamp);
        assertEquals(timestamp,
                     bean.getTimestamp());
        assertEquals(timestamp.getTime(),
                     bean.getTimeMillis());
    }
    /**
     * Tests <code>source</code> get and set methods.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void source()
            throws Exception
    {
        E bean = constructBean();
        assertEquals(null,
                     bean.getSource());
        bean.setSource(this);
        assertEquals(this,
                     bean.getSource());
        bean.setSource(this.getClass());
        assertEquals(this.getClass(),
                     bean.getSource());
    }
    /**
     * Creates a new, empty bean of the appropriate type.
     *
     * <p>Subclasses must override this method.
     *
     * @return an <code>E</code> value
     */
    protected abstract E constructBean();
    /**
     * Tests <code>hashCode</code> and <code>equals</code> as appropriate.
     *
     * <p>Subclasses must override this method.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public abstract void hashCodeAndEquals()
            throws Exception;
    /**
     * Performs additional validation testing for the bean as appropriate.
     * 
     * <p>Subclasses should override this method to perform testing specific
     * to the bean.  The default implementation does nothing.  Overridden methods
     * should leave <code>E</code> in a valid state.
     * 
     * @param inBean an <code>E</code> value to test
     * @throws Exception if an unexpected error occurs
     */
    protected void doAdditionalValidationTest(E inBean)
            throws Exception
    {
    }
    /**
     * Performs additional defaults testing for the bean as appropriate.
     * 
     * <p>Subclasses should override this method to perform testing specific
     * to the bean.  The default implementation does nothing.
     *
     * @param inBean an <code>E</code> value to test
     * @throws Exception if an unexpected error occurs
     */
    protected void doAdditionalSetDefaultsTest(E inBean)
            throws Exception
    {
    }
}
