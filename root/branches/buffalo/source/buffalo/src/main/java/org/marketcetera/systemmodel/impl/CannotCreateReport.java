package org.marketcetera.systemmodel.impl;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CannotCreateReport
        extends RuntimeException
{
    /**
     * Create a new CannotCreateReport instance.
     */
    public CannotCreateReport()
    {
        super();
    }
    /**
     * Create a new CannotCreateReport instance.
     *
     * @param inMessage
     */
    public CannotCreateReport(String inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new CannotCreateReport instance.
     *
     * @param inCause
     */
    public CannotCreateReport(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new CannotCreateReport instance.
     *
     * @param inMessage
     * @param inCause
     */
    public CannotCreateReport(String inMessage,
                              Throwable inCause)
    {
        super(inMessage,
              inCause);
    }
    private static final long serialVersionUID = 1L;
}
