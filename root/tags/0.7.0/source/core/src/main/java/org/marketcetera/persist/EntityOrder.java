package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.io.Serializable;

/* $License$ */
/**
 * Instances of this class represent ordering that can
 * be applied on the results of
 * {@link org.marketcetera.persist.MultipleEntityQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class EntityOrder implements Serializable {
    private static final long serialVersionUID = -27321569406013113L;

    /**
     * Returns the name of ordering.
     * @return the ordering name.
     */
    public abstract String getName();

    /**
     * Applies the ordering represented by this instance to the
     * supplied query. Only one ordering can be applied to
     * a query at a time. 
     * @param queryString the query string.
     * @param query the query instance.
     */
    protected abstract void apply(StringBuilder queryString,
                                  MultipleEntityQuery query);

}
