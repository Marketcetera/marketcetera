package org.marketcetera.core.notifications;

import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractNotificationExecutorMethod
        implements NotificationExecutorMethod
{
    /* (non-Javadoc)
     * @see com.marketcetera.cannonballtrading.notification.NotificationExecutor#notify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    public void notify(INotification inNotification)
    {
        if(verifySeverityThreshold(inNotification)) {
            try {
                doNotify(inNotification);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /**
     * Get the maximumThreshold value.
     *
     * @return an <code>INotification.Severity</code> value
     */
    public INotification.Severity getMaximumThreshold()
    {
        return maximumThreshold;
    }
    /**
     * Sets the maximumThreshold value.
     *
     * @param an <code>INotification.Severity</code> value
     */
    public void setMaximumThreshold(INotification.Severity inMaximumThreshold)
    {
        maximumThreshold = inMaximumThreshold;
    }
    /**
     * Get the threshold value.
     *
     * @return an <code>INotification.Severity</code> value
     */
    public INotification.Severity getMinimumThreshold()
    {
        return minimumThreshold;
    }
    /**
     * Sets the minimum threshold value.
     *
     * @param an <code>INotification.Severity</code> value
     */
    public void setMinimumThreshold(INotification.Severity inMinimumThreshold)
    {
        minimumThreshold = inMinimumThreshold;
    }
    /**
     * Executes the notification method of this executor.
     *
     * @param inNotification an <code>INotification</code> value
     * @throws Exception if an error occurs during notification
     */
    protected abstract void doNotify(INotification inNotification)
            throws Exception;
    /**
     * Gets the body of the notification.
     *
     * @param inNotification an <code>INotification</code> value
     * @return a <code>String</code> value
     */
    protected String getBody(INotification inNotification)
    {
        return inNotification.getBody();
    }
    /**
     * Gets the subject of the notification.
     *
     * @param inNotification an <code>INotification</code> value
     * @return a <code>String</code> value
     */
    protected String getSubject(INotification inNotification)
    {
        return inNotification.getSubject();
    }
    /**
     * Verifies that the given notification meets the minimum threshold for notification.
     *
     * @param inNotification an <code>INotification</code> value
     * @return a <code>boolean</code> value
     */
    protected boolean verifySeverityThreshold(INotification inNotification)
    {
        INotification.Severity calculatedMinimum = minimumThreshold == null ? INotification.Severity.LOW : minimumThreshold;
        INotification.Severity calculatedMaximum = maximumThreshold == null ? INotification.Severity.HIGH : maximumThreshold;
        INotification.Severity workingSeverity = inNotification.getSeverity() == null ? INotification.Severity.LOW : inNotification.getSeverity();
        return workingSeverity.ordinal() >= calculatedMinimum.ordinal() && workingSeverity.ordinal() <= calculatedMaximum.ordinal();
    }
    /**
     * threshold at or above which to notify
     */
    private INotification.Severity maximumThreshold = INotification.Severity.HIGH;
    /**
     * threshold at or above which to notify
     */
    private INotification.Severity minimumThreshold = INotification.Severity.LOW;
}
