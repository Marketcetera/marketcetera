package org.marketcetera.core.notifications;

/* $License$ */

/**
 * Provides an {@link INotification} implementation for PagerDuty.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PagerDutyNotification
        extends INotification
{
    /**
     * Get the Pager Duty URL value.
     *
     * @return a <code>String</code> value
     */
    String getPagerDutyUrl();
    /**
     * Get the Pager Duty service key value.
     *
     * @return a <code>String</code> value
     */
    String getServiceKey();
    /**
     * Get the Pager Duty incident key value.
     *
     * @return a <code>String</code> value
     */
    String getIncidentKey();
    /**
     * Get the Pager Duty event type value.
     *
     * @return a <code>PagerDutyEventType</code> value
     */
    PagerDutyEventType getEventType();
    /**
     * Indicate if the Pager Duty notification should be sent.
     *
     * @return a <code>boolean</cod> value
     */
    boolean shouldPagerDuty();
}
