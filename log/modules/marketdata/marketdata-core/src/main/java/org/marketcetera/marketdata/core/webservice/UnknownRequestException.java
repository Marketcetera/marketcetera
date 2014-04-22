package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that a follow-up market data service call was made for an unknown request id.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class UnknownRequestException
        extends CoreException
{
    /**
     * Create a new UnknownRequestException instance.
     *
     * @param inId a <code>long</code> value
     */
    public UnknownRequestException(long inId)
    {
        id = inId;
    }
    /**
     * Create a new UnknownRequestException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public UnknownRequestException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new UnknownRequestException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public UnknownRequestException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new UnknownRequestException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public UnknownRequestException(Throwable inNested,
                                   I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId()
    {
        return id;
    }
    /**
     * Sets the id value.
     *
     * @param inId a <code>long</code> value
     */
    public void setId(long inId)
    {
        id = inId;
    }
    /**
     * submitted request id
     */
    private long id;
    private static final long serialVersionUID = 1L;
}
