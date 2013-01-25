package org.marketcetera.newpersist;

import java.util.regex.Pattern;

import javax.persistence.*;

import org.marketcetera.core.ClassVersion;

/**
 * Base class for entities that have a name and description.
 * This class provides support for the name and description
 * properties.
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@ClassVersion("$Id: NDEntityBase.java 16461 2013-01-21 22:58:07Z colin $")
public abstract class NDEntityBase
        extends EntityBase
        implements SummaryNDEntityBase
{
    /**
     * Gets the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param a <code>String</code> value
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
     * Sets the description value.
     *
     * @param a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Validates that the values are within prescribed bounds.
     *
     * @throws PersistenceException if the attributes are not valid
     */
    @PrePersist
    @PreUpdate
    public void validate()
    {
        if(getName() == null || getName().trim().length() < 1) {
            throw new PersistenceException(Messages.UNSPECIFIED_NAME_ATTRIBUTE.getText());
        }
        if(getName().length() > 255) {
            throw new PersistenceException(Messages.NAME_ATTRIBUTE_TOO_LONG.getText(getName()));
        }
        if(!namePattern.matcher(getName()).matches()) {
            throw new PersistenceException(Messages.NAME_ATTRIBUTE_INVALID.getText(getName(),
                                                                                   namePattern.toString()));
        }
    }
    /**
     * attribute name of the name column
     */
    protected static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
    /**
     * attribute name of the description column
     */
    protected static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
    /**
     * The pattern for validating name attribute values
     */
    static final Pattern namePattern = Pattern.compile("^[\\p{L}\\p{N}- ]{1,255}$"); //$NON-NLS-1$
    /**
     * name value
     */
    @Column(name=ATTRIBUTE_NAME,length=255,nullable=false)
    private String name;
    /**
     * description value
     */
    @Column(name=ATTRIBUTE_DESCRIPTION,nullable=true)
    private String description;
    private static final long serialVersionUID = -4305155142443172457L;
}
