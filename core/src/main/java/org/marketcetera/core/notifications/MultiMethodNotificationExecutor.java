package org.marketcetera.core.notifications;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

/* $License$ */

/**
 * Provides a {@link NotificationExecutor} implementation that allows notifications via multiple methods.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class MultiMethodNotificationExecutor
        extends AbstractNotificationExecutor
        implements NotificationExecutor
{
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.strategy.NotificationExecutor#notify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    public void notify(INotification inNotification)
    {
        super.notify(inNotification);
        for(NotificationExecutorMethod executor : executorMethods) {
            executor.notify(inNotification);
        }
    }
    /**
     * Get the executorMethods value.
     *
     * @return a <code>List&lt;NotificationExecutorMethod&gt;</code> value
     */
    public List<NotificationExecutorMethod> getExecutorMethods()
    {
        return executorMethods;
    }
    /**
     * Sets the executorMethods value.
     *
     * @param a <code>List&lt;NotificationExecutorMethod&gt;</code> value
     */
    public void setExecutorMethods(List<NotificationExecutorMethod> inExecutorMethods)
    {
        executorMethods.clear();
        if(inExecutorMethods != null) {
            executorMethods.addAll(inExecutorMethods);
        }
    }
    /**
     * collection of executors to use for notification
     */
    private final List<NotificationExecutorMethod> executorMethods = new ArrayList<>();
}
