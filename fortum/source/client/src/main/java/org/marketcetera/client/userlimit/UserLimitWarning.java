package org.marketcetera.client.userlimit;

import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserLimitWarning
        extends UserLimitViolation
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new UserLimitWarning instance.
     *
     * @param inNested
     */
    public UserLimitWarning(Throwable inNested)
    {
        super(inNested);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new UserLimitWarning instance.
     *
     * @param inMessage
     */
    public UserLimitWarning(I18NBoundMessage inMessage)
    {
        super(inMessage);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new UserLimitWarning instance.
     *
     * @param inNested
     * @param inMessage
     */
    public UserLimitWarning(Throwable inNested,
            I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
        // TODO Auto-generated constructor stub
    }

}
