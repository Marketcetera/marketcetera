package org.marketcetera.ors.dao;

import org.marketcetera.ors.Principals;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import org.marketcetera.ors.history.PersistentReport;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 10/21/13
 * Time: 10:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PersistentReportDao {

    void save(ReportBase inReport) throws PersistenceException;

    Principals getPrincipals(OrderID orderID);

    PersistentReport findReportForOrder(OrderID orderID);

    List<PersistentReport> findReportForOrderBefore(Date inPurgeDate);

}
