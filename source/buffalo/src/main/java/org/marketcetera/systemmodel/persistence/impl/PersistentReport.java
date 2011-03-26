package org.marketcetera.systemmodel.persistence.impl;

import java.util.Date;

import javax.persistence.*;

import org.marketcetera.ors.history.ReportType;
import org.marketcetera.systemmodel.*;
import org.marketcetera.systemmodel.persistence.PersistentEntity;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name = "reports")
@ClassVersion("$Id$")
class PersistentReport
        implements Report, PersistentEntity
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getID()
     */
    @Override
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    public long getID()
    {
        return id;
    }
    /**
     * 
     *
     *
     * @param inId
     */
    @SuppressWarnings("unused")
    private void setID(long inId)
    {
        id = inId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getLastUpdated()
     */
    @Override
    @Temporal(TemporalType.TIMESTAMP) //i18n_datetime when storing to / retrieving from database
    public Date getLastUpdated()
    {
        return lastUpdated;
    }
    /**
     * 
     *
     *
     * @param inLastUpdated
     */
    @SuppressWarnings("unused")
    private void setLastUpdated(Date inLastUpdated)
    {
        lastUpdated = inLastUpdated;
    }
    /*
     * (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getUpdateCount()
     */
    @Override
    @Version
    public int getUpdateCount()
    {
        return updateCount;
    }
    /**
     * 
     *
     *
     * @param inUpdateCount
     */
    @SuppressWarnings("unused")
    private void setUpdateCount(int inUpdateCount)
    {
        updateCount = inUpdateCount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getDestinationID()
     */
    @Override
    @Transient
    public OrderDestinationID getDestinationID()
    {
        return orderDestinationID;
    }
    /**
     * 
     *
     *
     * @param inOrderDestinationId
     */
    private void setDestinationID(OrderDestinationID inOrderDestinationId)
    {
        orderDestinationID = inOrderDestinationId;
    }
    /**
     * 
     *
     *
     * @return
     */
    @SuppressWarnings("unused")
    @Column(name = "destinationID", nullable = false)
    private String getDestinationIdAsString()
    {
        return getDestinationID() == null ? null : getDestinationID().toString();
    }
    /**
     * 
     *
     *
     * @param inValue
     */
    @SuppressWarnings("unused")
    private void setDestinationIdAsString(String inValue)
    {
        setDestinationID(inValue == null ? null : orderDestinationIdFactory.create(inValue));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getFixMessage()
     */
    @Override
    @Lob
    @Column(nullable = true)
    public String getRawMessage()
    {
        return rawMessage;
    }
    /**
     * 
     *
     *
     * @param inRawMessage
     */
    private void setRawMessage(String inRawMessage)
    {
        rawMessage = inRawMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getReportType()
     */
    @Override
    @Column(nullable = false)
    public ReportType getReportType()
    {
        return reportType;
    }
    /**
     * 
     *
     *
     * @param inReportType
     */
    private void setReportType(ReportType inReportType)
    {
        reportType = inReportType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getSendingTime()
     */
    @Override
    @Column(nullable = false)
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /**
     * 
     *
     *
     * @param inSendingTime
     */
    private void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getOrderID()
     */
    @Override
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name="value", column = @Column(name = "orderID", nullable = false))})
    public OrderID getOrderID()
    {
        return orderID;
    }
    /**
     * 
     *
     *
     * @param inOrderID
     */
    private void setOrderID(OrderID inOrderID)
    {
        orderID = inOrderID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getOwner()
     */
    @Override
    @ManyToOne
    public PersistentUser getOwner()
    {
        return owner;
    }
    /**
     * 
     *
     *
     * @param inOwner
     */
    private void setOwner(PersistentUser inOwner)
    {
        owner = inOwner;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getSummary()
     */
    @Override
    @Transient
    public PersistentReportSummary getSummary()
    {
        return summary;
    }
    /**
     * 
     *
     *
     * @param inSummary
     */
    private void setSummary(PersistentReportSummary inSummary)
    {
        summary = inSummary;
    }
    /**
     * Create a new PersistentReport instance.
     *
     * @param inReport
     */
    PersistentReport(Report inReport)
    {
        setOrderID(inReport.getOrderID());
        setDestinationID(inReport.getDestinationID());
        setRawMessage(inReport.getRawMessage());
        setSendingTime(inReport.getSendingTime());
        setReportType(inReport.getReportType());
        User owner = inReport.getOwner();
        if(owner != null) {
            if(owner instanceof PersistentUser) {
                setOwner((PersistentUser)owner);
            } else {
                setOwner(new PersistentUser(owner));
            }
        }
        ReportSummary summary = inReport.getSummary();
        if(summary != null) {
            if(summary instanceof PersistentReportSummary) {
                setSummary((PersistentReportSummary)summary);
            } else {
                setSummary(new PersistentReportSummary(summary));
            }
        }
    }
    /**
     * 
     */
    private long id;
    /**
     * 
     */
    private int updateCount;
    /**
     * 
     */
    private Date lastUpdated;
    /**
     * 
     */
    private OrderID orderID;
    /**
     * 
     */
    private PersistentUser owner;
    /**
     * 
     */
    private OrderDestinationID orderDestinationID;
    /**
     * 
     */
    private String rawMessage;
    /**
     * 
     */
    private Date sendingTime;
    /**
     * 
     */
    private ReportType reportType;
    /**
     * 
     */
    private PersistentReportSummary summary;
    /**
     * 
     */
    @Autowired
    private OrderDestinationIdFactory orderDestinationIdFactory;
    private static final long serialVersionUID = 1;
}
