package org.marketcetera.persist;

/* $License$ */

/**
 * Describes an element of an overall sort directive.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class Sort
{
    /**
     * Create a new Sort instance.
     */
    public Sort() {}
    /**
     * Create a new Sort instance.
     *
     * @param inProperty a <code>String</code> value
     * @param inDirection a <code>SortDirection</code> value
     */
    public Sort(String inProperty,
                SortDirection inDirection)
    {
        property = inProperty;
        direction = inDirection;
    }
    /**
     * Get the property value.
     *
     * @return a <code>String</code> value
     */
    public String getProperty()
    {
        return property;
    }
    /**
     * Sets the property value.
     *
     * @param inProperty a <code>String</code> value
     */
    public void setProperty(String inProperty)
    {
        property = inProperty;
    }
    /**
     * Get the direction value.
     *
     * @return a <code>SortDirection</code> value
     */
    public SortDirection getDirection()
    {
        return direction;
    }
    /**
     * Sets the direction value.
     *
     * @param inDirection a <code>SortDirection</code> value
     */
    public void setDirection(SortDirection inDirection)
    {
        direction = inDirection;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Sort [").append(property).append(" ").append(direction).append("]");
        return builder.toString();
    }
    /**
     * property to sort
     */
    private String property;
    /**
     * direction of the sort
     */
    private SortDirection direction;
}
