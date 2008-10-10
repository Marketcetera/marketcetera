package org.marketcetera.photon.scripting;

import java.util.Date;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.notifications.Notification;

/* $License$ */

/**
 * <code>INotification</code> implementation used by {@link Strategy}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class StrategyNotification
        extends Notification
{
    /**
     * originating class
     */
    private static final Class<StrategyNotification> sOriginator = StrategyNotification.class;
    /**
     * Create a <code>StrategyNotification</code> object of severity <code>debug</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @return a <code>StrategyNotification</code> value
     */
    static StrategyNotification low(String inSubject,
                                    String inBody)
    {
        return new StrategyNotification(inSubject,
                                        inBody,
                                        Severity.LOW);
    }
    /**
     * Create a <code>StrategyNotification</code> object of severity <code>info</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @return a <code>StrategyNotification</code> value
     */
    static StrategyNotification medium(String inSubject,
                                       String inBody)
    {
        return new StrategyNotification(inSubject,
                                        inBody,
                                        Severity.MEDIUM);
    }
    /**
     * Create a <code>StrategyNotification</code> object of severity <code>error</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @return a <code>StrategyNotification</code> value
     */
    static StrategyNotification high(String inSubject,
                                     String inBody)
    {
        return new StrategyNotification(inSubject,
                                        inBody,
                                        Severity.HIGH);
    }
    /**
     * Create a new StrategyNotification instance.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inSeverity a <code>Severity</code> value
     */
    private StrategyNotification(String inSubject,
                                 String inBody,
                                 Severity inSeverity)
    {
        super(inSubject,
              inBody,
              new Date(),
              inSeverity,
              sOriginator);
    }
}
