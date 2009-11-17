package org.marketcetera.event.util;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.event.Event;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides event services. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@Immutable
@ClassVersion("$Id$")
public final class EventServices
{
    /**
     * Causes an unchecked exception of the appropriate type
     * and message to be thrown. 
     *
     * @param inErrorMessage an <code>I18NBoundMessage</code> value
     */
    public static void error(I18NBoundMessage inErrorMessage)
    {
        throw new IllegalArgumentException(inErrorMessage.getText());
    }
    /**
     * Provides equality test for the given <code>EventBean</code> objects
     * in the context of their use in events.
     * 
     * <p>This method will try to interpret the given objects as events, 
     * specifically, implementers of {@link HasEventBean}.  If successful,
     * the two objects are compared according to the contract specified
     * in {@link Event} for event implementers.  If unsuccessful, that is,
     * if either of the objects does not implement {@link HasEventBean},
     * a standard equality test is performed.
     *
     * @param inObject1 an <code>Object</code> value
     * @param inObject2 an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean eventEquals(Object inObject1,
                                      Object inObject2)
    {
        if(inObject1 == inObject2) {
            return true;
        }
        // objects are not equal (both cannot be null, but one or the other may yet be)
        if(inObject1 == null ||
           inObject2 == null) {
            return false;
        }
        // neither object is null, but both can yet be of any class
        if(inObject1 instanceof HasEventBean) {
            if(!(inObject2 instanceof HasEventBean)) {
                return false;
            }
        } else {
            if(inObject2 instanceof HasEventBean) {
                return false;
            }
        }
        // both events are of HasEventBean
        HasEventBean e1 = (HasEventBean)inObject1;
        HasEventBean e2 = (HasEventBean)inObject2;
        if(e1.getEventBean() == null) {
            return e2.getEventBean() == null;
        }
        // e1 eventBean is not null
        if(e2.getEventBean() == null) {
            return false;
        }
        // both e1 and e2 have non-null eventBeans
        return e1.getEventBean().getMessageId() == e2.getEventBean().getMessageId();
    }
    /**
     * Provides hash generation services for the given <code>EventBean</code> object
     * in the context of its use as an event. 
     *
     * @param inEventBean a <code>HasEventBean</code> value
     * @return an <code>int</code> value
     */
    public static int eventHashCode(HasEventBean inEventBean)
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((inEventBean == null || inEventBean.getEventBean() == null) ? 0 : (int)(inEventBean.getEventBean().getMessageId() ^ (inEventBean.getEventBean().getMessageId() >>> 32)));
        return result;
    }
    /**
     * Create a new EventValidationServices instance.
     */
    private EventServices()
    {
        throw new UnsupportedOperationException();
    }
}
