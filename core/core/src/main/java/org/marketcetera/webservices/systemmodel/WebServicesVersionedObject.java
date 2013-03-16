package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.marketcetera.api.systemmodel.MutableVersionedObject;
import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Provides support for {@link VersionedObject} implementations that are available via web services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class WebServicesVersionedObject
        extends WebServicesSystemObject
        implements MutableVersionedObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.VersionedObject#getVersion()
     */
    @Override
    public int getVersion()
    {
        return version;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableVersionedObject#setVersion(int)
     */
    @Override
    public void setVersion(int inVersion)
    {
        version = inVersion;
    }
    /**
     * Sets this object's attributes to the matching attribute of the given object.
     *
     * @param inVersionedObject a <code>VersionedObject</code> value
     */
    protected void copyAttributes(VersionedObject inVersionedObject)
    {
        super.copyAttributes(inVersionedObject);
        setVersion(inVersionedObject.getVersion());
    }
    /**
     * version value (not made available for web services)
     */
    @XmlTransient
    private int version;
    private static final long serialVersionUID = 1L;
}
