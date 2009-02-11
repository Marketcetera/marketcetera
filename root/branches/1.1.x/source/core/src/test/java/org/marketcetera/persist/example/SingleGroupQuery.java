package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.SingleFetchQuery;

/* $License$ */
/**
 * A query that fetches a single instance of the Group.
 * To be able to fetch multiple instances of groups, see
 * {@link org.marketcetera.persist.example.MultiGroupQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class SingleGroupQuery extends
        SingleFetchQuery<SummaryGroup,Group> {
    private static final long serialVersionUID = -8649037859166615973L;

    /**
     * Creates a query that will fetch the group given its ID.
     *
     * @param id The group ID.
     */
    public SingleGroupQuery(long id) {
        super(Group.ENTITY_NAME, id);
    }

    /**
     * Creates a query that will fetch the group given its name
     *
     * @param name the name of the group that needs to be looked up.
     */
    public SingleGroupQuery(String name) {
        super(Group.ENTITY_NAME, name);
    }

    protected String[] getFetchJoinAttributeNames() {
        return FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    static final String[] FETCH_JOIN_ATTRIBUTE_NAMES =
            new String[]{Group.ATTRIBUTE_USER,Group.ATTRIBUTE_AUTH};

}
