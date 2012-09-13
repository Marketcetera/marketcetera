package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.api.systemmodel.MutableNamedObject;
import org.marketcetera.api.systemmodel.NamedObject;

/* $License$ */

/**
 * Provides support for {@link NamedObject} implementations that are available via web services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class WebServicesNamedObject
        extends WebServicesVersionedObject
        implements MutableNamedObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableNamedObject#setName(java.lang.String)
     */
    @Override
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableNamedObject#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * name of the named object
     */
    @XmlAttribute
    private String name;
    /**
     * description of the named object
     */
    @XmlAttribute
    private String description;
}
