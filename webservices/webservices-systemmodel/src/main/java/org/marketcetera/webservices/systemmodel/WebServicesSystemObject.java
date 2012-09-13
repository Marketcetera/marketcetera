package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.api.systemmodel.MutableSystemObject;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class WebServicesSystemObject
        implements MutableSystemObject
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
     * unique identifier for all system objects
     */
    @XmlAttribute
    private long id;
}
