package org.marketcetera.ors.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.marketcetera.ors.Principals;
import org.marketcetera.ors.dao.PersistentReportDao;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.springframework.stereotype.Repository;

@Repository
public class PersistentReportDaoImpl implements PersistentReportDao {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void save(ReportBase inReport) throws PersistenceException {
        PersistentReport report = new PersistentReport(inReport);
        entityManager.persist(report);
	}

	@Override
	public Principals getPrincipals(OrderID orderID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersistentReport findReportForOrder(OrderID orderID) {
        Query query = entityManager.createNamedQuery("forOrderID"); //$NON-NLS-1$
        query.setParameter("orderID",
        		orderID); //$NON-NLS-1$
        List<?>list = query.getResultList();
        if(list.isEmpty()) {
            return null;
        }
        return (PersistentReport)list.get(0);
	}

	@Override
	public List<PersistentReport> findReportForOrderBefore(Date inPurgeDate) {
        Query query = entityManager.createNamedQuery("since"); //$NON-NLS-1$
        query.setParameter("target", //$NON-NLS-1$
                           inPurgeDate);
        @SuppressWarnings("unchecked")
		List<PersistentReport> list = (List<PersistentReport>)query.getResultList();
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list;
	}
	

}
