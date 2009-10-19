package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * A query that fetches a single instance of the DataTypes.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class SingleDataTypesQuery extends
        SingleFetchQuery<SummaryDataType,DataTypes> {
    private static final long serialVersionUID = 6901942525380122146L;

    /**
     * Creates a query that will fetch the authorization given its ID.
     *
     * @param id The user ID.
     */
    public SingleDataTypesQuery(long id) {
        super(ENTITY_NAME, id);
    }

    /**
     * Creates a query that will fetch the data types instance given the name
     *
     * @param name the name of the authorization that needs to be looked up.
     */
    public SingleDataTypesQuery(String name) {
        super(ENTITY_NAME, name);
    }

    protected String[] getFetchJoinAttributeNames() {
        return FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    private static final String ENTITY_NAME = "DataTypes"; //$NON-NLS-1$
    static final String[] FETCH_JOIN_ATTRIBUTE_NAMES= new String[]{};
}
