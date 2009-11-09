package org.marketcetera.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.Event;
import org.marketcetera.event.Messages;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link AbstractEventBuilderImpl} portion of an {@link EventBuilder}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractEventBuilderTestBase<E extends Event,B extends AbstractEventBuilderImpl<E>>
        implements Messages
{
    /**
     * Tests {@link AbstractEventBuilderImpl#withMessageId(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMessageId()
            throws Exception
    {
        B builder = getBuilder();
        setDefaults(builder);
        builder.withMessageId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE,
                     builder.getEvent().getMessageId());
        builder.withMessageId(-1);
        assertEquals(-1,
                     builder.getEvent().getMessageId());
        builder.withMessageId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE,
                     builder.getEvent().getMessageId());
        verify(builder);
    }
    /**
     * Tests {@link AbstractEventBuilderImpl#withTimestamp(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTimestamp()
            throws Exception
    {
        B builder = getBuilder();
        setDefaults(builder);
        // null timestamp
        builder.withTimestamp(null);
        assertEquals(null,
                     builder.getEvent().getTimestamp());
        // regular timestamp
        Date timestamp = new Date();
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.getEvent().getTimestamp());
        // make a weird timestamp
        timestamp = new Date(-1);
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.create().getTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link AbstractEventBuilderImpl#withSource(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSource()
            throws Exception
    {
        B builder = getBuilder();
        setDefaults(builder);
        // null source
        builder.withSource(null);
        assertEquals(null,
                     builder.getEvent().getSource());
        // non-null source
        builder.withSource(this);
        assertEquals(this,
                     builder.getEvent().getSource());
        verify(builder);
    }
    /**
     * Tests event <code>hashCode</code> and <code>equals</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        B builder = getBuilder();
        E event1 = setDefaults(builder).create();
        E event2 = builder.create();
        E event3 = setDefaults(builder).create();
        assertEquals(event1.getMessageId(),
                     event2.getMessageId());
        assertFalse(event2.getMessageId() == event3.getMessageId());
        EqualityAssert.assertEquality(event1,
                                      event2,
                                      event3,
                                      null,
                                      this);
    }
    /**
     * Tests basic event validation.
     *
     * <p>Subclasses may override this method but should call the parent
     * method.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validation()
            throws Exception
    {
        final B builder = setDefaults(getBuilder());
        // check messageId
        builder.withMessageId(-1);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_INVALID_MESSAGEID.getText(builder.getEvent().getMessageId())) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder);
        // 0 is ok
        builder.withMessageId(0);
        verify(builder);
        // minimum value is ok (asks for a generated id)
        setDefaults(builder).withMessageId(Long.MIN_VALUE);
        verify(builder);
        // maximum value is ok
        setDefaults(builder).withMessageId(Long.MAX_VALUE);
        verify(builder);
        // timestamp
        // negative timestamp ok (not even sure what this means, maybe 1ms before epoch?)
        builder.withTimestamp(new Date(-1));
        verify(builder);
       // 0 timestamp
        setDefaults(builder).withTimestamp(new Date(0));
        verify(builder);
        // null timestamp (requests a new timestamp)
        setDefaults(builder).withTimestamp(null);
        verify(builder);
        // normal timestamp
        setDefaults(builder).withTimestamp(new Date());
        verify(builder);
    }
    /**
     * Sets valid defaults in the given builder.
     * 
     * <p>Subclasses may override this method to set their own defaults
     * but should call the parent method.
     *
     * @param inBuilder a <code>B</code> value
     * @return a <code>B</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected B setDefaults(B inBuilder)
            throws Exception
    {
        inBuilder.withMessageId(System.nanoTime());
        inBuilder.withTimestamp(new Date());
        inBuilder.withSource(this);
        return inBuilder;
    }
    /**
     * Gets the builder to use for testing.
     *
     * @return a <code>B</code> value
     */
    protected abstract B getBuilder();
    /**
     * Verifies that the given builder can produce an event of the
     * correct type with the builder's attributes.
     * 
     * <p>Note that the builder is assumed to be in a state that
     * can produce an event without error.
     * 
     * <p>Subclasses should override this method to test the
     * attributes of the subclass and invoke the parent method.
     *
     * @param inBuilder a <code>B</code> value
     * @return an <code>E</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected E verify(B inBuilder)
            throws Exception
    {
        assertNotNull(inBuilder);
        assertNotNull(inBuilder.toString());
        E event = inBuilder.create();
        assertNotNull(event);
        assertNotNull(event.toString());
        EventBean eventBean = null;
        if(event instanceof HasEventBean) {
            eventBean = ((HasEventBean)event).getEventBean();
        }
        // there is a special case for messageId - if equal to Long.MIN_VALUE
        //  then it will be some value >= 0
        if(inBuilder.getEvent().getMessageId() == Long.MIN_VALUE) {
            assertTrue(event.getMessageId() >= 0);
            if(eventBean != null) {
                assertTrue(eventBean.getMessageId() >= 0);
            }
        } else {
            assertEquals(inBuilder.getEvent().getMessageId(),
                         event.getMessageId());
            if(eventBean != null) {
                assertEquals(inBuilder.getEvent().getMessageId(),
                             eventBean.getMessageId());
            }
        }
        // there's a special case for timestamp, too
        if(inBuilder.getEvent().getTimestamp() == null) {
            assertNotNull(event.getTimestamp());
            assertEquals(event.getTimestamp().getTime(),
                         event.getTimeMillis());
        } else {
            assertEquals(inBuilder.getEvent().getTimestamp(),
                         event.getTimestamp());
            assertEquals(inBuilder.getEvent().getTimestamp().getTime(),
                         event.getTimeMillis());
        }
        assertEquals(inBuilder.getEvent().getSource(),
                     event.getSource());
        Object newSource = new Object();
        event.setSource(newSource);
        assertEquals(newSource,
                     event.getSource());
        return event;
    }
}
