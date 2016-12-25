package org.marketcetera.persist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Provides common behavior for non-attached named entiies.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="SimpleNamedEntity")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class SimpleNamedEntity
        extends SimpleEntity
        implements SummaryNDEntityBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryNDEntityBase#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryNDEntityBase#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
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
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * name value
     */
    @XmlAttribute(name="name")
    private String name;
    /**
     * description value
     */
    @XmlAttribute(name="description")
    private String description;
    private static final long serialVersionUID = 4888831811689983374L;
}
