package org.marketcetera.persistence.hibernate;

import org.apache.commons.lang.Validate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.marketcetera.systemmodel.Report;
import org.marketcetera.systemmodel.ReportSummary;
import org.marketcetera.systemmodel.persistence.ReportDao;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Repository
@ClassVersion("$Id$")
public class HibernateReportDao
        implements ReportDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.ReportDao#write(org.marketcetera.systemmodel.Report)
     */
    @Override
    public void write(Report inReport)
    {
        PersistentReport pReport;
        if(inReport instanceof PersistentReport) {
            pReport = (PersistentReport)inReport;
        } else {
            pReport = new PersistentReport(inReport);
        }
        currentSession().saveOrUpdate(pReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.ReportDao#write(org.marketcetera.systemmodel.ReportSummary)
     */
    @Override
    public void write(ReportSummary inReportSummary)
    {
        PersistentReportSummary pReportSummary;
        if(inReportSummary instanceof PersistentReportSummary) {
            pReportSummary = (PersistentReportSummary)inReportSummary;
        } else {
            pReportSummary = new PersistentReportSummary(inReportSummary);
        }
        currentSession().saveOrUpdate(pReportSummary);
    }
    /**
     * Create a new HibernateReportDao instance.
     *
     * @param inSessionFactory
     */
    @Autowired
    public HibernateReportDao(SessionFactory inSessionFactory)
    {
        Validate.notNull(inSessionFactory,
                         "Session factory missing");
        sessionFactory = inSessionFactory;
    }
    /**
     *
     *
     *
     * @return
     */
    private Session currentSession()
    {
        return sessionFactory.getCurrentSession();
    }
    /**
     * 
     */
    private final SessionFactory sessionFactory;
}
