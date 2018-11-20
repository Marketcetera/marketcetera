package org.marketcetera.core.notifications;

/**
 * Represents the event types available to PagerDuty.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.7.2
 */
public enum PagerDutyEventType
{
    Acknowledge,
    Resolve,
    Trigger;
}