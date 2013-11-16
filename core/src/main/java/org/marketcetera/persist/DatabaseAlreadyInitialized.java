package org.marketcetera.persist;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the database has already been initialized.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DatabaseAlreadyInitialized
        extends CoreException
{
    /**
     * Create a new DatabaseAlreadyInitialized instance.
     */
    public DatabaseAlreadyInitialized()
    {
    }
    /**
     * Create a new DatabaseAlreadyInitialized instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public DatabaseAlreadyInitialized(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new DatabaseAlreadyInitialized instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public DatabaseAlreadyInitialized(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new DatabaseAlreadyInitialized instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public DatabaseAlreadyInitialized(Throwable inNested,
                                      I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = -1258050676207452322L;
}
