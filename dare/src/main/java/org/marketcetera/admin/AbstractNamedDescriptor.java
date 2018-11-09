package org.marketcetera.admin;

/* $License$ */

/**
 * Provides common behavior for POJOs with a name and description.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractNamedDescriptor.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public abstract class AbstractNamedDescriptor
{
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * description value
     */
    private String description;
    /**
     * name value
     */
    private String name;
}
