package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.io.Serializable;

/* $License$ */
/**
 * An abstraction that represents the results of a query.
 * This class only serves as a common type for all query
 * results. Concrete Subclasses of this class carry the
 * actual result 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class QueryResults<R> implements Serializable {
    private static final long serialVersionUID = 342561755118459113L;

    /**
     * The result of the query
     * @return the query result value
     */
    public abstract R getResult();
}
