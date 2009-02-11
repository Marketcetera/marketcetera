package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.SingleFetchQuery;

/* $License$ */
/**
 * A query that fetches a single instance of the User.
 * To be able to fetch multiple instances of users, see
 * {@link MultiUserQuery}
 * 
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class SingleUserQuery extends
        SingleFetchQuery<SummaryUser,User> {
    private static final long serialVersionUID = -4241121936568861031L;

    /**
     * Creates a query that will fetch the user given its ID.
     *
     * @param id The user ID.
     */
    public SingleUserQuery(long id) {
        super(User.ENTITY_NAME, id);
    }

    /**
     * Creates a query that will fetch the user given the name
     *
     * @param name the name of the user that needs to be looked up.
     */
    public SingleUserQuery(String name) {
        super(User.ENTITY_NAME, name);
    }

    protected String[] getFetchJoinAttributeNames() {
        return FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    static final String[] FETCH_JOIN_ATTRIBUTE_NAMES=
            new String[]{User.ATTRIBUTE_GROUPS,User.ATTRIBUTE_SETTING};
}
