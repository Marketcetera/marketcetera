package org.marketcetera.webservices.systemmodel;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.api.systemmodel.MutableSystemObject;
import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Provides support for {@link SystemObject} implementations that are available via web services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class WebServicesSystemObject
        implements MutableSystemObject, Serializable
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.SystemObject#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableSystemObject#setId(long)
     */
    @Override
    public void setId(long inId)
    {
        id = inId;
    }
    /**
     * Sets this object's attributes to the matching attribute of the given object.
     *
     * @param inSystemObject a <code>SystemObject</code> value
     */
    protected void copyAttributes(SystemObject inSystemObject)
    {
        setId(inSystemObject.getId());
    }
    /**
     * unique identifier for all system objects
     */
    @XmlAttribute
    private long id;
    private static final long serialVersionUID = 1L;
}
