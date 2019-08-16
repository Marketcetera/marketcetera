package org.marketcetera.core.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.core.notifications.INotification.Severity;

/* $License$ */

/**
 * Tests {@link Notification}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.8.0
 */
public class NotificationTest
{
    @Test
    public void staticConstructors()
        throws Exception
    {
        long beginTime = System.currentTimeMillis();
        Thread.sleep(100);
        // test debug
        String subject = "subject_" + System.nanoTime(); //$NON-NLS-1$
        String body = "body_" + System.nanoTime(); //$NON-NLS-1$
        String originator = this.getClass().toString();
        Notification debug = Notification.debug(subject,
                                                body,
                                                originator);
        assertNotNull(debug);
        assertEquals(subject,
                     debug.getSubject());
        assertEquals(body,
                     debug.getBody());
        assertEquals(originator,
                     debug.getOriginator());
        assertEquals(Severity.DEBUG,
                     debug.getSeverity());
        assertNotNull(debug.getTimestamp());
        assertTrue(debug.getTimestamp().getTime() > beginTime);
        // test info
        Thread.sleep(100);
        subject = "subject_" + System.nanoTime(); //$NON-NLS-1$
        body = "body_" + System.nanoTime(); //$NON-NLS-1$
        originator = Notification.class.toString();
        Notification info = Notification.info(subject,
                                              body,
                                              originator);
        assertNotNull(info);
        assertEquals(subject,
                     info.getSubject());
        assertEquals(body,
                     info.getBody());
        assertEquals(originator,
                     info.getOriginator());
        assertEquals(Severity.INFO,
                     info.getSeverity());
        assertNotNull(info.getTimestamp());
        assertTrue(info.getTimestamp().getTime() > debug.getTimestamp().getTime());
        // test warn
        Thread.sleep(100);
        subject = "subject_" + System.nanoTime(); //$NON-NLS-1$
        body = "body_" + System.nanoTime(); //$NON-NLS-1$
        originator = Notification.class.toString();
        Notification warn = Notification.warn(subject,
                                              body,
                                              originator);
        assertNotNull(warn);
        assertEquals(subject,
                     warn.getSubject());
        assertEquals(body,
                     warn.getBody());
        assertEquals(originator,
                     warn.getOriginator());
        assertEquals(Severity.WARN,
                     warn.getSeverity());
        assertNotNull(warn.getTimestamp());
        assertTrue(warn.getTimestamp().getTime() > info.getTimestamp().getTime());
        // test error
        Thread.sleep(100);
        subject = "subject_" + System.nanoTime(); //$NON-NLS-1$
        body = "body_" + System.nanoTime(); //$NON-NLS-1$
        originator = Notification.class.toString();
        Notification error = Notification.error(subject,
                                                body,
                                                originator);
        assertNotNull(error);
        assertEquals(subject,
                     error.getSubject());
        assertEquals(body,
                     error.getBody());
        assertEquals(originator,
                     error.getOriginator());
        assertEquals(Severity.ERROR,
                     error.getSeverity());
        assertNotNull(error.getTimestamp());
        assertTrue(error.getTimestamp().getTime() > warn.getTimestamp().getTime());
    }
}
