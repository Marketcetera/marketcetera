package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.util.List;

/* $License$ */
/**
 * Processes results of a {@link MultipleEntityQuery multi query}.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiQueryProcessor<R extends SummaryEntityBase>
        extends QueryProcessor<List<R>> {
    private static final long serialVersionUID = -2950510247621073215L;

    /**
     * Creates an instance.
     *
     * @param detail if the processor retrieves
     *               detailed results.
     */
    public MultiQueryProcessor(boolean detail) {
        super(detail);
    }

    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public QueryResults<List<R>> process(EntityManager em,
                                   Query q)
            throws PersistenceException {
        final List result = q.getResultList();
        SLF4JLoggerProxy.debug(this,"Fetched {} rows",result.size());  //$NON-NLS-1$
        return new MultipleResults<R>(result);
    }
}
