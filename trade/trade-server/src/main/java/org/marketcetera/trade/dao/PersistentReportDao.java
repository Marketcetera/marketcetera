package org.marketcetera.trade.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentReportDao.java 17339 2017-08-10 02:14:34Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: PersistentReportDao.java 17339 2017-08-10 02:14:34Z colin $")
public interface PersistentReportDao
        extends JpaRepository<PersistentReport,Long>,QuerydslPredicateExecutor<PersistentReport>
{
    /**
     * Finds the report with the given report id.
     *
     * @param inReportId a <code>ReportID</code> value
     * @return a <code>PersistentReport</code> value
     */
    PersistentReport findByReportID(ReportID inReportId);
    /**
     * Finds the reports with ids in the given collection.
     *
     * @param inIds a <code>Set&lt;Long&gt;</code> value
     * @param inPage a <code>Pageable</code> value
     * @return a <code>List&lt;PersistentReport&gt;</code> value
     */
    List<PersistentReport> findByIdIn(Set<Long> inIds,
                                      Pageable inPage);
    /**
     * Finds all reports since the given date.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>List&lt;PersistentReport</code> value
     */
    List<PersistentReport> findBySendingTimeBefore(Date inDate);
    /**
     * Get the number of executions for the given session since the given time.
     *
     * @param inSessionId a <code>String</code> value
     * @param inReportType a <code>ReportType</code> value
     * @param inSince a <code>Date</code> value
     * @return a <code>long</code> value
     */
    @Query("select count(r) FROM PersistentReport r WHERE r.sessionIdValue=?1 and r.mReportType=?2 and r.sendingTime >= ?3")
    long getExecutionCount(String inSessionId,
                           ReportType inReportType,
                           Date inSince);
    /**
     * Finds the ids of the incoming messages that do not have a corresponding report.
     *
     * @param inSessionId a <code>String</code> value
     * @param inMessageTypes a <code>Set&lt;String&gt;</code> value
     * @param inSince a <code>Date</code> value
     * @return a <code>List&lt;Long&gt;</code> value
     */
    @Query(value="select i.id from IncomingMessage i where not exists (select r.id from PersistentReport r where i.msgSeqNum=r.msgSeqNum and i.sessionId=r.sessionIdValue and i.sendingTime=r.sendingTime) and i.msgType in (?2) and i.sessionId=?1 and i.sendingTime >= ?3 order by id")
    List<Long> findUnhandledIncomingMessageIds(String inSessionId,
                                               Set<String> inMessageTypes,
                                               Date inSince);
}
