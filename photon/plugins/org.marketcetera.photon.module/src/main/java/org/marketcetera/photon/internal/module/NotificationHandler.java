package org.marketcetera.photon.internal.module;

import java.util.Date;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.module.IDataFlowLabelProvider;
import org.marketcetera.photon.module.SinkDataHandler;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;
import com.google.inject.Provider;

/* $License$ */

/**
 * Simple handler that forwards notifications to the core notification manager.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class NotificationHandler extends SinkDataHandler {

    private final Provider<IDataFlowLabelProvider> mLabelProvider;
    private final INotificationManager mNotificationManager;

    /**
     * Constructor.
     * 
     * @param notificationManager
     *            the notification manager
     * @param labelProvider
     *            provides custom labels, can be null
     * @throws IllegalArgumentException
     *             if notificationManager is null
     */
    @Inject
    public NotificationHandler(INotificationManager notificationManager,
            Provider<IDataFlowLabelProvider> labelProvider) {
        Validate.notNull(notificationManager, "notificationManager"); //$NON-NLS-1$
        mNotificationManager = notificationManager;
        mLabelProvider = labelProvider;
    }

    @Override
    public void receivedData(DataFlowID inFlowID, Object inData) {
        INotification original = (INotification) inData;
        String enhancedSubject = Messages.NOTIFICATION_HANDLER_ENHANCED_SUBJECT_FORMAT
                .getText(getLabel(inFlowID), original.getSubject());
        mNotificationManager.publish(new ModuleNotification(enhancedSubject,
                original.getBody(), original.getDate(), original.getSeverity(),
                original.getOriginator()));
    }

    private String getLabel(DataFlowID dataFlowId) {
        if (mLabelProvider != null) {
            IDataFlowLabelProvider service = mLabelProvider.get();
            if (service != null) {
                String label = service.getLabel(dataFlowId);
                if (label != null) {
                    return label;
                }
            }
        }
        return dataFlowId.getValue();
    }

    /**
     * A notification that originated from a module in the module framework.
     */
    @ClassVersion("$Id$")
    private class ModuleNotification extends Notification {

        private static final long serialVersionUID = 1L;

        /**
         * Constructor.
         */
        public ModuleNotification(String inSubject, String inBody, Date inDate,
                Severity inSeverity, String inOriginator) {
            super(inSubject, inBody, inDate, inSeverity, inOriginator);
        }
    }
}
