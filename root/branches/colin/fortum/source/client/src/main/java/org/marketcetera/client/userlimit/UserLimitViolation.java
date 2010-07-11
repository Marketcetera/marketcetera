package org.marketcetera.client.userlimit;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserLimitViolation
        extends CoreException
{
    /**
     * Create a new UserLimitViolation instance.
     *
     * @param inNested
     */
    public UserLimitViolation(Throwable inNested)
    {
        super(inNested);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new UserLimitViolation instance.
     *
     * @param inMessage
     */
    public UserLimitViolation(I18NBoundMessage inMessage)
    {
        super(inMessage);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new UserLimitViolation instance.
     *
     * @param inNested
     * @param inMessage
     */
    public UserLimitViolation(Throwable inNested,
            I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
        // TODO Auto-generated constructor stub
    }
    private static final long serialVersionUID = 1L;
}
