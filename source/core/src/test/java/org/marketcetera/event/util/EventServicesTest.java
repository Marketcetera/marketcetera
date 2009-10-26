package org.marketcetera.event.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.event.Messages;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link EventServices}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventServicesTest
        implements Messages
{
    /**
     * Tests {@link EventServices#error(org.marketcetera.util.log.I18NBoundMessage)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void error()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventServices.error(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_QUOTE_ACTION.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                EventServices.error(VALIDATION_NULL_QUOTE_ACTION);
            }
        };
    }
    /**
     * Tests {@link EventServices#eventEquals(Object, Object)} and {@link EventServices#eventHashCode(org.marketcetera.event.beans.HasEventBean)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void eventEquals()
            throws Exception
    {
        // this bean has an eventbean with a messageid of 1
        HasEventBean e1 = new HasEventBean() {
            private final EventBean bean = new EventBean();
            @Override
            public EventBean getEventBean()
            {
                bean.setMessageId(1);
                return bean;
            }
        };
        // this bean has an eventbean also with a messageid of 1
        HasEventBean e2 = new HasEventBean() {
            private final EventBean bean = new EventBean();
            @Override
            public EventBean getEventBean()
            {
                bean.setMessageId(1);
                return bean;
            }
        };
        // this bean has an eventbean also with a messageid of 2
        HasEventBean e3 = new HasEventBean() {
            private final EventBean bean = new EventBean();
            @Override
            public EventBean getEventBean()
            {
                bean.setMessageId(2);
                return bean;
            }
        };
        // this bean does not have an event bean (it lies)
        HasEventBean e4 = new HasEventBean() {
            @Override
            public EventBean getEventBean()
            {
                return null;
            }
        };
        // this bean also oes not have an event bean (it lies like e4, but is not e4)
        HasEventBean e5 = new HasEventBean() {
            @Override
            public EventBean getEventBean()
            {
                return null;
            }
        };
        assertTrue(EventServices.eventEquals(null,
                                             null));
        assertTrue(EventServices.eventEquals(e1,
                                             e1));
        assertTrue(EventServices.eventEquals(e1,
                                             e2));
        assertTrue(EventServices.eventEquals(e2,
                                             e1));
        assertTrue(EventServices.eventEquals(e4,
                                             e5));
        assertFalse(EventServices.eventEquals(e1,
                                              null));
        assertFalse(EventServices.eventEquals(null,
                                              e1));
        assertFalse(EventServices.eventEquals(e4,
                                              null));
        assertFalse(EventServices.eventEquals(null,
                                              e4));
        assertFalse(EventServices.eventEquals(e1,
                                              e3));
        assertFalse(EventServices.eventEquals(e3,
                                              e1));
        assertFalse(EventServices.eventEquals(e1,
                                              e4));
        assertFalse(EventServices.eventEquals(e4,
                                              e1));
        assertFalse(EventServices.eventEquals(e3,
                                              e4));
        assertFalse(EventServices.eventEquals(e4,
                                              e3));
        assertFalse(EventServices.eventEquals(e1,
                                              this));
        assertFalse(EventServices.eventEquals(this,
                                              e1));
        // test eventHashCode
        assertTrue(EventServices.eventHashCode(null) == EventServices.eventHashCode(null));
        assertTrue(EventServices.eventHashCode(e1) == EventServices.eventHashCode(e2));
        assertTrue(EventServices.eventHashCode(e4) == EventServices.eventHashCode(e5));
        assertTrue(EventServices.eventHashCode(e4) == EventServices.eventHashCode(null));
        assertFalse(EventServices.eventHashCode(e1) == EventServices.eventHashCode(null));
        assertFalse(EventServices.eventHashCode(e1) == EventServices.eventHashCode(e3));
        assertFalse(EventServices.eventHashCode(e1) == EventServices.eventHashCode(e4));
        assertFalse(EventServices.eventHashCode(e3) == EventServices.eventHashCode(e4));
    }
}
