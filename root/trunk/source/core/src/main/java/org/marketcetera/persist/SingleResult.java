package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Encapsulates the result of execution of a
 * {@link SingleEntityQuery} 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SingleResult<R> extends QueryResults<R> {
    private static final long serialVersionUID = -6361499557273250428L;

    /**
     * Creates an instance
     * @param result the result of the query
     */
    public SingleResult(R result) {
        this.result = result;
    }

    /**
     * Returns the result of the query
     * @return the query's result
     */
    public R getResult() {
        return result;
    }

    private R result;
}
