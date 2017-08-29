package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasStatus
{
    boolean getFailed();
    void setFailed(boolean inFailed);
    String getMessage();
    void setMessage(String inMessage);
}
