package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.JPQLConstants.*;

/* $License$ */

/**
 * Instances of this class represent single
 * column orderings that can applied to
 * {@link MultipleEntityQuery multi entity queries}
 * Each ordering has a name that has a value that is
 * the same as the name of the attribute that the
 * results are ordered by.
 * The ordering is ascending, unless the multi query has
 * the {@link MultipleEntityQuery#isReverseOrder() reverseOrder}
 * flag set to true, in which case its descending.
 * 
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SimpleEntityOrder extends EntityOrder {
    private static final long serialVersionUID = 8772824511315751514L;

    /**
     * Creates an order instance
     *
     * @param name The entity attribute that the results
     * need to be ordered by. Cannot be null
     */
    public SimpleEntityOrder(String name) {
        if(name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    /**
     * The name of the entity ordering. The value is the same
     * as the name of attribute that the results will be ordered
     * by.
     *
     * @return The name of the entity ordering.
     */
    public String getName() {
        return name;
    }

    /**
     * Applies the ordering to the supplied query string
     *
     * @param queryString the query string.
     * @param query The query that needs its results ordered.
     */
    protected void apply(StringBuilder queryString,
                         MultipleEntityQuery query) {
        queryString.append(S).append(ORDER_BY).append(S).append(
                query.getEntityAlias()).append(DOT).append(getName()).
                append(S).append(query.isReverseOrder()
                        ? DESC
                        : ASC);
    }

    private String name;
}
