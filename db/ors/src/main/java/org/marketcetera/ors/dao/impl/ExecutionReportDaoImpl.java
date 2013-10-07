package org.marketcetera.ors.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.ors.dao.ExecutionReportDao;
import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;

public class ExecutionReportDaoImpl implements ExecutionReportDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public int deleteReportsFor(PersistentReport report) {
        Query query = entityManager.createNamedQuery("deleteReportsFor");  //$NON-NLS-1$
        query.setParameter("id",report.getId());  //$NON-NLS-1$
        return query.executeUpdate();
	}
	
	public int deleteReportsIn(List<PersistentReport> inReports) {
        List<Long> ids = new ArrayList<Long>();
        if(inReports != null) {
            for(PersistentReport report : inReports) {
                ids.add(report.getId());
            }
        }
        return entityManager.createNativeQuery("DELETE FROM execreports WHERE report_id IN (:ids)").setParameter("ids",ids).executeUpdate();
	}

	public BigDecimal getEquityPositionAsOf (final SimpleUser inUser, final Date inDate,final Equity inEquity) {
        Query query = entityManager.createNamedQuery(
                "eqPositionForSymbol");

        query.setParameter("viewerID",inUser.getUserID().getValue());
        query.setParameter("allViewers",inUser.isSuperuser());
        query.setParameter("sideBuy", Side.Buy.ordinal());
        query.setParameter("symbol", inEquity.getSymbol());
        query.setParameter("securityType", SecurityType.CommonStock.ordinal());
        query.setParameter("sendingTime", inDate,
                TemporalType.TIMESTAMP);
        BigDecimal position = (BigDecimal) query.getSingleResult();
        return position == null? BigDecimal.ZERO: position;        
	}
	
	@SuppressWarnings("unchecked")
	public List<ExecutionReportSummary> getOpenOrders(SimpleUser inUser) {
        Query query = entityManager.createNamedQuery("openOrders");  //$NON-NLS-1$
        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        return (List<ExecutionReportSummary>)query.getResultList();
	}
	
	public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,Date inDate) {
        Query query = entityManager.createNamedQuery(
                "eqAllPositions");  //$NON-NLS-1$
        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.CommonStock.ordinal());  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        HashMap<PositionKey<Equity>, BigDecimal> map =
                new HashMap<PositionKey<Equity>, BigDecimal>();
        List<?> list = query.getResultList();
        Object[] columns;
        for(Object o: list) {
            columns = (Object[]) o;
            //4 columns
            if(columns.length > 1) {
                //first one is the symbol
                //second one is the account
                //third one is the actor ID
                //fourth one is the position
                map.put(PositionKeyFactory.createEquityKey
                        ((String)columns[0],
                         (String)columns[1],
                         ((columns[2]==null)?null:
                          ((BigInteger)columns[2]).toString())),
                         (BigDecimal)columns[3]);
            }
        }
        return map;		
	}
	
	public BigDecimal getCurrencyPositionAsOf(SimpleUser inUser, Date inDate, Currency inCurrency) {
        Query query = entityManager.createNamedQuery(	
                "crPositionForSymbol");  //$NON-NLS-1$

        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("symbol", inCurrency.getSymbol());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.Currency.ordinal());  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        return (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
	}
	
	public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser, Date inDate) {
        Query query = entityManager.createNamedQuery(
                "crAllPositions");  //$NON-NLS-1$
        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.Currency.ordinal());  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        HashMap<PositionKey<Currency>, BigDecimal> map =
                new HashMap<PositionKey<Currency>, BigDecimal>();
        List<?> list = query.getResultList();
        Object[] columns;
        for(Object o: list) {
            columns = (Object[]) o;
            //4 columns
            if(columns.length > 1) {
                //first one is the symbol
                //second one is the account
                //third one is the actor ID
                //fourth one is the position
                map.put(PositionKeyFactory.createCurrencyKey
                        ((String)columns[0],
                         (String)columns[1],
                         ((columns[2]==null)?null:
                          ((BigInteger)columns[2]).toString())),
                         (BigDecimal)columns[3]);
            }
        }
        return map;		
	}

	@Override
	public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(
			SimpleUser inUser, Date inDate) {
        Query query = entityManager.createNamedQuery("futAllPositions");  //$NON-NLS-1$
        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.Future.ordinal());  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        HashMap<PositionKey<Future>, BigDecimal> map =
                new HashMap<PositionKey<Future>, BigDecimal>();
        List<?> list = query.getResultList();
        Object[] columns;
        for(Object o: list) {
            columns = (Object[]) o;
            //5 columns
            if(columns.length > 1) {
                //first one is the symbol
                //second one is the expiry
                //third one is the account
                //fourth one is the actor ID
                //fifth one is the position
                map.put(PositionKeyFactory.createFutureKey((String)columns[0],
                                                           (String)columns[1],
                                                           (String)columns[2],
                                                           ((columns[3]==null)?null:
                          ((BigInteger)columns[3]).toString())),
                         (BigDecimal)columns[4]);
            }
        }
        return map;		
	}

	@Override
	public BigDecimal getFuturePositionAsOf(SimpleUser inUser, Date inDate,
			Future inFuture) {
        Query query = entityManager.createNamedQuery("futPositionForSymbol");  //$NON-NLS-1$
        query.setParameter("viewerID",  //$NON-NLS-1$
                           inUser.getUserID().getValue());
        query.setParameter("allViewers",  //$NON-NLS-1$
                           inUser.isSuperuser());
        query.setParameter("sideBuy",  //$NON-NLS-1$
                           Side.Buy.ordinal());
        query.setParameter("symbol",  //$NON-NLS-1$
                           inFuture.getSymbol());
        query.setParameter("securityType",  //$NON-NLS-1$
                           SecurityType.Future.ordinal());
        query.setParameter("sendingTime",  //$NON-NLS-1$
                           inDate,
                TemporalType.TIMESTAMP);
        return (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
	}

	@Override
	public BigDecimal getOptionPositionAsOf(SimpleUser inUser, Date inDate,
			Option inOption) {

        Query query = entityManager.createNamedQuery(
                "optPositionForTuple");  //$NON-NLS-1$

        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("symbol", inOption.getSymbol());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.Option.ordinal());  //$NON-NLS-1$
        query.setParameter("expiry", inOption.getExpiry());  //$NON-NLS-1$
        query.setParameter("strikePrice", inOption.getStrikePrice());  //$NON-NLS-1$
        query.setParameter("optionType", inOption.getType().ordinal());  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        BigDecimal position = (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
        return position == null? BigDecimal.ZERO: position;        
		
	}

	@Override
	public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(
			SimpleUser inUser, Date inDate) {
        Query query = entityManager.createNamedQuery(
                "optAllPositions");  //$NON-NLS-1$
        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.Option.ordinal());  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        HashMap<PositionKey<Option>, BigDecimal> map =
                new HashMap<PositionKey<Option>, BigDecimal>();
        List<?> list = query.getResultList();
        Object[] columns;
        for(Object o: list) {
            columns = (Object[]) o;
            //7 columns
            if(columns.length > 1) {
                //first one is the symbol
                //second one is the expiry
                //third one is the strikePrice
                //fourth one is the option type
                //fifth one is the account
                //sixth one is the actor ID
                //seventh one is the position
                map.put(PositionKeyFactory.createOptionKey
                        ((String)columns[0],
                         (String)columns[1],
                         (BigDecimal)columns[2],
                         OptionType.values()[(Integer)columns[3]],
                         (String)columns[4],
                         ((columns[5]==null)?null:
                          ((BigInteger)columns[5]).toString())),
                         (BigDecimal)columns[6]);
            }
        }
        return map;
    }

	@Override
	public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(
			SimpleUser inUser, Date inDate, String[] inSymbols) {
        Query query = entityManager.createNamedQuery("optPositionsForRoots");  //$NON-NLS-1$
        query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
        query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
        query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
        query.setParameter("securityType", SecurityType.Option.ordinal());  //$NON-NLS-1$
        query.setParameter("symbols", Arrays.asList(inRootSymbols));  //$NON-NLS-1$
        query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                TemporalType.TIMESTAMP);
        HashMap<PositionKey<Option>, BigDecimal> map =
                new HashMap<PositionKey<Option>, BigDecimal>();
        List<?> list = query.getResultList();
        Object[] columns;
        for(Object o: list) {
            columns = (Object[]) o;
            //7 columns
            if(columns.length > 1) {
                //first one is the symbol
                //second one is the expiry
                //third one is the strikePrice
                //fourth one is the optionType
                //fifth one is the account
                //sixth one is the actor ID
                //seventh one is the position
                map.put(PositionKeyFactory.createOptionKey
                        ((String)columns[0],
                         (String)columns[1],
                         (BigDecimal)columns[2],
                         OptionType.values()[(Integer)columns[3]],
                         (String)columns[4],
                         ((columns[5]==null)?null:
                          ((BigInteger)columns[5]).toString())),
                         (BigDecimal)columns[6]);
            }
        }
        return map;
		
	}
}
