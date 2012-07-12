package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.JPQLConstants.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.persistence.Query;
import javax.persistence.EntityManager;

/* $License$ */
/**
 * A query processor that runs the query and returns
 * the count of number of results that it would return.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CountQueryProcessor extends QueryProcessor<Long> {
    private static final long serialVersionUID = 8533947556425281342L;

    /**
     * Creates an instance.
     *
     */
    protected CountQueryProcessor() {
        super(false);
    }

    /**
     * Over-ridden to prefix the generated query with <code>select count(*)</code>
     * @param queryString the query string
     * @param queryBase the query instance
     */
    @Override
    protected void preGenerate(StringBuilder queryString, QueryBase queryBase) {
        queryString.append(SELECT).append(S).append(COUNT_ALL);
    }

    public QueryResults<Long> process(EntityManager em, Query q)
            throws PersistenceException {
        final Long result = (Long) q.getSingleResult();
        SLF4JLoggerProxy.debug(this,"Fetched Count is {}",result); //$NON-NLS-1$
        return new SingleResult<Long>(result);
    }
}
