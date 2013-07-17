package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * A query that fetches multiple instances of
 * {@link org.marketcetera.persist.NDEntityBase}
 *
 * The query provides filters to filter the query results by
 * name and description.
 *
 * The query provides Entity Orders to order the query results
 * by {@link #BY_NAME name} and {@link #BY_DESCRIPTION description}.
 *
 * By default the query is has no filters or orders set.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MultiNDQuery
        extends MultipleEntityQuery {
    /**
     * Ordering that will order the results in ascending order
     * by the entity name.
     */
    public static final EntityOrder BY_NAME =
            new SimpleEntityOrder(NDEntityBase.ATTRIBUTE_NAME);
    /**
     * Ordering that will order the results in ascending order
     * by the entity description.
     */
    public static final EntityOrder BY_DESCRIPTION =
            new SimpleEntityOrder(NDEntityBase.ATTRIBUTE_DESCRIPTION);
    private static final long serialVersionUID = -4689112134559808505L;

    /**
     * Returns the name filter that should be applied to the query.
     * If the value is null, the query results are not filtered by name.
     *
     * @return the name filter.
     */
    public StringFilter getNameFilter() {
        return nameFilter;
    }

    /**
     * Sets the name filter for the query.
     *
     * @param nameFilter the namefilter, can be null.
     */
    public void setNameFilter(StringFilter nameFilter) {
        this.nameFilter = nameFilter;
    }

    /**
     * Returns the description filter that should be applied to the query
     * If the value is null, the query results are not filtered by description
     *
     * @return the description filter
     */
    public StringFilter getDescriptionFilter() {
        return descriptionFilter;
    }

    /**
     * Sets the description filter for the query.
     * 
     * @param descriptionFilter the description filter, can be null.
     */
    public void setDescriptionFilter(StringFilter descriptionFilter) {
        this.descriptionFilter = descriptionFilter;
    }

    /**
     * Constructs an instance.
     *
     * @param fromClause the initial JPQL from clause
     * @param whereClause the JPQL where clause
     */
    protected MultiNDQuery(String fromClause,
                           String whereClause) {
        super(fromClause, whereClause);
    }

    @Override
    protected void addWhereClauses(StringBuilder queryString) {
        super.addWhereClauses(queryString);
        addFilterIfNotNull(queryString,
                NDEntityBase.ATTRIBUTE_NAME,getNameFilter());
        addFilterIfNotNull(queryString,
                NDEntityBase.ATTRIBUTE_DESCRIPTION,getDescriptionFilter());
    }

    private StringFilter nameFilter;
    private StringFilter descriptionFilter;
}
