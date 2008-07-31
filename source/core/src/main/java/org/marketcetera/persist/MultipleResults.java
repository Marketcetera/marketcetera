package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.util.List;

/* $License$ */
/**
 * Encapsulates results of a query that fetches
 * multiple objects.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultipleResults<R extends SummaryEntityBase>
        extends QueryResults<List<R>> {
    private static final long serialVersionUID = 3072234715355535996L;

    /**
     * Creates an instance that contains a list of results.
     *
     * @param result the list of results.
     */
    public MultipleResults(List<R> result) {
        this.result = result;
    }

    /**
     * Returns the list of results.
     * 
     * @return the list of results
     */
    public List<R> getResult() {
        return result;
    }

    private final List<R> result;
}
