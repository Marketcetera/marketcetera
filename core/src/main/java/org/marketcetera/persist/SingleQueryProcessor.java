package org.marketcetera.persist;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Processes results of a
 * {@link org.marketcetera.persist.SingleEntityQuery single query}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SingleQueryProcessor<R extends SummaryEntityBase>
        extends QueryProcessor<R> {
    private static final long serialVersionUID = -6996278361193329163L;

    /**
     * Creates an instance.
     *
     * @param detail if the processor retrieves
     *               detailed results.
     */
    public SingleQueryProcessor(boolean detail) {
        super(detail);
    }

    @SuppressWarnings("unchecked")
    public QueryResults<R> process(EntityManager em,
                                   Query q) {
        return new SingleResult<R>((R) q.getSingleResult());
    }
}
