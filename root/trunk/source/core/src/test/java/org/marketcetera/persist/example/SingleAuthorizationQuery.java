package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.SingleFetchQuery;

/* $License$ */
/**
 * A query that fetches a single instance of the Authorization.
 * To be able to fetch multiple instances of authorizations, see
 * {@link org.marketcetera.persist.example.MultiAuthorizationQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class SingleAuthorizationQuery extends
        SingleFetchQuery<Authorization,Authorization> {
    private static final long serialVersionUID = -6790805174824618746L;

    /**
     * Creates a query that will fetch the authorization given its ID.
     *
     * @param id The user ID.
     */
    public SingleAuthorizationQuery(long id) {
        super(Authorization.ENTITY_NAME, id);
    }

    /**
     * Creates a query that will fetch the authorization given the name
     *
     * @param name the name of the authorization that needs to be looked up.
     */
    public SingleAuthorizationQuery(String name) {
        super(Authorization.ENTITY_NAME, name);
    }

    protected String[] getFetchJoinAttributeNames() {
        return FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    public static final String[] FETCH_JOIN_ATTRIBUTE_NAMES= new String[]{};
}
