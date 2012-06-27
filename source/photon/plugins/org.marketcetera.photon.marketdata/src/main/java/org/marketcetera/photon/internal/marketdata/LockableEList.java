package org.marketcetera.photon.internal.marketdata;

import java.util.concurrent.Callable;

import org.eclipse.emf.common.util.EList;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an interface to an <code>EList</code> object that permits locked read and write operations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.2.0
 */
@ClassVersion("$Id$")
public interface LockableEList<E>
        extends EList<E>
{
    /**
     * Executes the given operation as a locked read. 
     *
     * @param inOperation a <code>Callable&lt;Tgt;</code> value
     * @return a <code>T</code> value
     */
    public <T> T doReadOperation(Callable<T> inOperation);
    /**
     * Executes the given operation as a locked write. 
     *
     * @param inOperation a <code>Callable&lt;Tgt;</code> value
     * @return a <code>T</code> value
     */
    public <T> T doWriteOperation(Callable<T> inOperation);
}
