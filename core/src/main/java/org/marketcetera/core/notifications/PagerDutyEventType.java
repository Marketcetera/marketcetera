package org.marketcetera.core.notifications;

/**
 * Represents the event types available to PagerDuty.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PagerDutyNotification.java 85003 2015-11-13 15:57:55Z colin $
 * @since 1.7.2
 */
public enum PagerDutyEventType
{
    Acknowledge,
    Resolve,
    Trigger;
}