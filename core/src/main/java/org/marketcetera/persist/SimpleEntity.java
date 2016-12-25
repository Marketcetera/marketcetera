package org.marketcetera.persist;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Provides common behavior for non-attached entities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="SimpleEntity")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class SimpleEntity
        implements SummaryEntityBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryEntityBase#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryEntityBase#getUpdateCount()
     */
    @Override
    public int getUpdateCount()
    {
        return updateCount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryEntityBase#getLastUpdated()
     */
    @Override
    public Date getLastUpdated()
    {
        return lastUpdated;
    }
    /**
     * Sets the id value.
     *
     * @param inId a <code>long</code> value
     */
    public void setId(long inId)
    {
        id = inId;
    }
    /**
     * Sets the updateCount value.
     *
     * @param inUpdateCount a <code>int</code> value
     */
    public void setUpdateCount(int inUpdateCount)
    {
        updateCount = inUpdateCount;
    }
    /**
     * Sets the lastUpdated value.
     *
     * @param inLastUpdated a <code>Date</code> value
     */
    public void setLastUpdated(Date inLastUpdated)
    {
        lastUpdated = inLastUpdated;
    }
    /**
     * unique identified
     */
    @XmlAttribute(name="id")
    private long id;
    /**
     * update count
     */
    @XmlAttribute(name="updateCount")
    private int updateCount;
    /**
     * last updated timestamp
     */
    @XmlAttribute(name="lastUpdated")
    private Date lastUpdated;
    private static final long serialVersionUID = 4059943927076504388L;
}
