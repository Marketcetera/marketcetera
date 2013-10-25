package org.marketcetera.ors;

import java.util.Date;

import javax.persistence.TemporalType;

import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.dao.UserService;
import org.springframework.context.support.AbstractApplicationContext;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistTestBase
{

    /**
     *
     *
     * @param inStrings
     * @return
     */
    public static AbstractApplicationContext springSetup(String[] inStrings)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /**
     *
     *
     * @param inSendingTime
     * @param inSendingTime2
     * @param inTimestamp
     */
    public static void assertCalendarEquals(Date inSendingTime,
                                            Date inSendingTime2,
                                            TemporalType inTimestamp)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /**
     *
     *
     * @return
     */
    public static String randomString()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    protected static UserService userService;
    protected static ReportService reportService;
}
