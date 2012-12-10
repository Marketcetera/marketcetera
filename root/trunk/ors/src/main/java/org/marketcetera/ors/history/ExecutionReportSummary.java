package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import org.marketcetera.trade.Currency;

import javax.annotation.Nullable;
import javax.persistence.*;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.persist.PersistContext;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.persist.Transaction;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Maintains a summary of fields of an ExecutionReport
 * to aid Position calculations. The lifecycle of this object
 * is controlled by {@link PersistentReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
@Entity
@Table(name="execreports")

@NamedQuery(name = "rootIDForOrderID",
        query = "select e.rootID from ExecutionReportSummary e " +
                "where e.orderID = :orderID")

@SqlResultSetMappings({
    @SqlResultSetMapping(name = "positionForSymbol",
            columns = {@ColumnResult(name = "position")}),
    @SqlResultSetMapping(name = "eqAllPositions",
            columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                    }),
    @SqlResultSetMapping(name = "crAllPositions",
           columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                   }),
   @SqlResultSetMapping(name = "futAllPositions",
                        columns = {
           @ColumnResult(name = "symbol"),
           @ColumnResult(name = "expiry"),
           @ColumnResult(name = "account"),
           @ColumnResult(name = "actor"),
           @ColumnResult(name = "position")
   }),
    @SqlResultSetMapping(name = "optAllPositions",
            columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "expiry"),
                @ColumnResult(name = "strikePrice"),
                @ColumnResult(name = "optionType"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                    })
        })
// CD 26-Apr-2012 ORS-84
// The position queries should ignore PENDING ERs. This is done by excluding ORS with particular order status values.
// Hibernate maps enums to 0-based index values, so 7, 11, and 15 map to values in the OrderStatus enum, the PENDING values.
@NamedNativeQueries({
    @NamedNativeQuery(name = "eqPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "eqAllPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "eqAllPositions"),
    @NamedNativeQuery(name = "crPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "crAllPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
             resultSetMapping = "crAllPositions"),    
    @NamedNativeQuery(name = "futPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and e.securityType = :securityType " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "futAllPositions",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "futAllPositions"),
    @NamedNativeQuery(name = "optPositionForTuple",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and e.securityType = :securityType " +
            "and e.expiry = :expiry " +
            "and e.strikePrice = :strikePrice " +
            "and e.optionType = :optionType " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "optAllPositions",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.strikePrice as strikePrice, e.optionType as optionType, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, expiry, strikePrice, optionType, account, actor having position <> 0",
            resultSetMapping = "optAllPositions"),
    @NamedNativeQuery(name = "optPositionsForRoots",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.strikePrice as strikePrice, e.optionType as optionType, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and e.symbol in (:symbols) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, expiry, strikePrice, optionType, account, actor having position <> 0",
            resultSetMapping = "optAllPositions")
        })

class ExecutionReportSummary extends EntityBase {

    /**
     * Gets the current aggregate position for the equity based on
     * execution reports received on or before the supplied time, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the time. execution reports with sending time values less
     * than or equal to this time are included in this calculation.
     * @param inEquity the equity for which this position needs to be computed
     *
     * @return the aggregate position for the equity.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position.
     */
    static BigDecimal getEquityPositionAsOf
        (final SimpleUser inUser,
         final Date inDate,
         final Equity inEquity)
        throws PersistenceException
    {
        BigDecimal position = executeRemote(new Transaction<BigDecimal>() {
            private static final long serialVersionUID = 1L;

            @Override
            public BigDecimal execute(EntityManager em, PersistContext context) {
                Query query = em.createNamedQuery(
                        "eqPositionForSymbol");  //$NON-NLS-1$

                query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
                query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
                query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
                query.setParameter("symbol", inEquity.getSymbol());  //$NON-NLS-1$
                query.setParameter("securityType", SecurityType.CommonStock.ordinal());  //$NON-NLS-1$
                query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                        TemporalType.TIMESTAMP);
                return (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
            }
        }, null);
        return position == null? BigDecimal.ZERO: position;

    }
    
    /**
     * Gets the current aggregate position for the currency based on
     * execution reports received on or before the supplied time, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the time. execution reports with sending time values less
     * than or equal to this time are included in this calculation.
     * @param inCurrency the currency for which this position needs to be computed
     *
     * @return the aggregate position for the currency.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position.
     */
    static BigDecimal getCurrencyPositionAsOf
        (final SimpleUser inUser,
         final Date inDate,
         final Currency inCurrency)
        throws PersistenceException
    {
        BigDecimal position = executeRemote(new Transaction<BigDecimal>() {
            private static final long serialVersionUID = 1L;

            @Override
            public BigDecimal execute(EntityManager em, PersistContext context) {
                Query query = em.createNamedQuery(	
                        "crPositionForSymbol");  //$NON-NLS-1$									//ToDo Add Currency SQL

                query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
                query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
                query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
                query.setParameter("symbol", inCurrency.getSymbol());  //$NON-NLS-1$
                query.setParameter("securityType", SecurityType.Currency.ordinal());  //$NON-NLS-1$
                query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                        TemporalType.TIMESTAMP);
                return (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
            }
        }, null);
        return position == null? BigDecimal.ZERO: position;

    }
    
    /**
     * Returns the aggregate position of each (equity,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    static Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate)
        throws PersistenceException
    {
        return executeRemote(new Transaction<Map<PositionKey<Equity>, BigDecimal>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Map<PositionKey<Equity>, BigDecimal> execute(EntityManager em,
                                                    PersistContext context) {
                Query query = em.createNamedQuery(
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
        }, null);

    }    
    /**
     * Returns the aggregate position of each (currency,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    static Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate)
        throws PersistenceException
    {
        return executeRemote(new Transaction<Map<PositionKey<Currency>, BigDecimal>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Map<PositionKey<Currency>, BigDecimal> execute(EntityManager em,
                                                    PersistContext context) {
                Query query = em.createNamedQuery(
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
        }, null);

    }
    
    
    /**
     * Gets the current aggregate position for the future based on
     * execution reports received on or before the supplied time, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the time. execution reports with sending time values less
     * than or equal to this time are included in this calculation.
     * @param inFuture the future for which this position needs to be computed
     *
     * @return the aggregate position for the future.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position.
     */
    static BigDecimal getFuturePositionAsOf(final SimpleUser inUser,
                                            final Date inDate,
                                            final Future inFuture)
            throws PersistenceException
    {
        BigDecimal position = executeRemote(new Transaction<BigDecimal>() {
            private static final long serialVersionUID = 1L;
            @Override
            public BigDecimal execute(EntityManager em,
                                      PersistContext context)
            {
                Query query = em.createNamedQuery("futPositionForSymbol");  //$NON-NLS-1$
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
        }, null);
        return position == null? BigDecimal.ZERO: position;
    }
    /**
     * Returns the aggregate position of each (future,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    static Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(final SimpleUser inUser,
                                                                          final Date inDate)
            throws PersistenceException
    {
        return executeRemote(new Transaction<Map<PositionKey<Future>, BigDecimal>>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Map<PositionKey<Future>,BigDecimal> execute(EntityManager em,
                                                               PersistContext context)
            {
                Query query = em.createNamedQuery("futAllPositions");  //$NON-NLS-1$
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
        }, null);
    }
    /**
     * Gets the current aggregate position for the option tuple based on
     * execution reports received on or before the supplied time, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the time. execution reports with sending time values less
     * than or equal to this time are included in this calculation.
     * @param inOption option instrument
     *
     * @return the aggregate position for the symbol.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position.
     */
    static BigDecimal getOptionPositionAsOf
        (final SimpleUser inUser,
         final Date inDate,
         final Option inOption)
        throws PersistenceException {
        BigDecimal position = executeRemote(new Transaction<BigDecimal>() {
            private static final long serialVersionUID = 1L;

            @Override
            public BigDecimal execute(EntityManager em, PersistContext context) {
                Query query = em.createNamedQuery(
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
                return (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
            }
        }, null);
        return position == null? BigDecimal.ZERO: position;

    }

    /**
     * Returns the aggregate position of each option
     * (option,account,actor)
     * tuple based on all reports received for each option instrument on or before
     * the supplied date, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    static Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate)
        throws PersistenceException {
        return executeRemote(new Transaction<Map<PositionKey<Option>, BigDecimal>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Map<PositionKey<Option>, BigDecimal> execute(EntityManager em,
                                                    PersistContext context) {
                Query query = em.createNamedQuery(
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
        }, null);

    }

    /**
     * Returns the aggregate position of each option
     * (option,account,actor)
     * tuple based on all reports received for each option instrument on or before
     * the supplied date, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     * @param inRootSymbols the list of option roots.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    static Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate,
         final String... inRootSymbols)
        throws PersistenceException {
        return executeRemote(new Transaction<Map<PositionKey<Option>, BigDecimal>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Map<PositionKey<Option>, BigDecimal> execute(EntityManager em,
                                                    PersistContext context) {
                Query query = em.createNamedQuery("optPositionsForRoots");  //$NON-NLS-1$
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
        }, null);

    }

    /**
     * Creates an instance.
     *
     * @param inReport The original execution report message.
     * @param inSavedReport the saved persistent report.
     */
    ExecutionReportSummary(ExecutionReport inReport,
                           PersistentReport inSavedReport) {
        setReport(inSavedReport);
        mOrderID = inReport.getOrderID();
        mOrigOrderID = inReport.getOriginalOrderID();
        Instrument instrument = inReport.getInstrument();
        if (instrument != null) {
            mSecurityType = instrument.getSecurityType();
            mSymbol = instrument.getSymbol();
            InstrumentSummaryFields<?> summaryFields = InstrumentSummaryFields.SELECTOR.forInstrument(instrument);
            mOptionType = summaryFields.getOptionType(instrument);
            mStrikePrice = summaryFields.getStrikePrice(instrument);
            mExpiry = summaryFields.getExpiry(instrument);
        }
        mAccount = inReport.getAccount();
        mSide = inReport.getSide();
        mCumQuantity = inReport.getCumulativeQuantity();
        mAvgPrice = inReport.getAveragePrice();
        mLastQuantity = inReport.getLastQuantity();
        mLastPrice = inReport.getLastPrice();
        mOrderStatus = inReport.getOrderStatus();
        mSendingTime = inReport.getSendingTime();
        mViewer = inSavedReport.getViewer();
    }

    /**
     * Saves this instance within an existing transaction.
     *
     * @param inManager the entity manager instance
     * @param inContext the persistence context
     *
     * @throws PersistenceException if there were errors.
     */
    void localSave(EntityManager inManager,
                   PersistContext inContext)
            throws PersistenceException {
        super.saveLocal(inManager, inContext);
    }

    @Override
    protected void preSaveLocal(EntityManager em, PersistContext context)
            throws PersistenceException {
        super.preSaveLocal(em, context);
        // CD 17-Mar-2011 ORS-79
        // we need to find the correct root ID of the incoming ER. for cancels and cancel/replaces,
        //  this is easy - we can look up the root ID from the origOrderID. for a partial fill or fill
        //  of an original order, this is also easy - the rootID is just the orderID. the difficult case
        //  is a partial fill or fill of a replaced order. the origOrderID won't be present (not required)
        //  but there still exists an order chain to be respected or position reporting will be broken.
        //  therefore, the algorithm should be:
        // if the original orderID is present, use the root from that order
        // if it's not present, look for the rootID of an existing record with the same orderID
        Query query = em.createNamedQuery("rootIDForOrderID");  //$NON-NLS-1$
        SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
                               "Searching for rootID for {}",  //$NON-NLS-1$
                               getOrderID());
        if(getOrigOrderID() == null) {
            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
                                   "No origOrderID present, using orderID for query");  //$NON-NLS-1$
            query.setParameter("orderID",  //$NON-NLS-1$
                               getOrderID());
        } else {
            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
                                   "Using origOrderID {} for query",  //$NON-NLS-1$
                                   getOrigOrderID());
            query.setParameter("orderID",  //$NON-NLS-1$
                               getOrigOrderID());
        }
        List<?> list = query.getResultList();
        if(list.isEmpty()) {
            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
                                   "No other orders match this orderID - this must be the first in the order chain");  //$NON-NLS-1$
            // this is the first order in this chain
            setRootID(getOrderID());
        } else {
            OrderID rootID = (OrderID)list.get(0);
            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
                                   "Using {} for rootID",  //$NON-NLS-1$
                                   rootID);
            setRootID(rootID);
        }
    }

    @OneToOne(optional = false)
    PersistentReport getReport() {
        return mReport;
    }
    
    private void setReport(PersistentReport inReport) {
        mReport = inReport;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "rootID", nullable = false))})
    @Column(nullable = false)
    OrderID getRootID() {
        return mRootID;
    }

    private void setRootID(OrderID inRootID) {
        mRootID = inRootID;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "orderID", nullable = false))})
    OrderID getOrderID() {
        return mOrderID;
    }

    private void setOrderID(OrderID inOrderID) {
        mOrderID = inOrderID;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "origOrderID"))})
    OrderID getOrigOrderID() {
        return mOrigOrderID;
    }

    private void setOrigOrderID(OrderID inOrigOrderID) {
        mOrigOrderID = inOrigOrderID;
    }

    SecurityType getSecurityType() {
        return mSecurityType;
    }

    private void setSecurityType(SecurityType inSecurityType) {
        mSecurityType = inSecurityType;
    }

    @Column(nullable = false)
    String getSymbol() {
        return mSymbol;
    }

    private void setSymbol(String inSymbol) {
        mSymbol = inSymbol;
    }

    String getExpiry() {
        return mExpiry;
    }

    private void setExpiry(String inExpiry) {
        mExpiry = inExpiry;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = true)
    BigDecimal getStrikePrice() {
        return mStrikePrice;
    }

    private void setStrikePrice(BigDecimal inStrikePrice) {
        mStrikePrice = inStrikePrice;
    }

    OptionType getOptionType() {
        return mOptionType;
    }

    private void setOptionType(OptionType inOptionType) {
        mOptionType = inOptionType;
    }

    String getAccount() {
        return mAccount;
    }

    private void setAccount(String inAccount) {
        mAccount = inAccount;
    }

    @Column(nullable = false)
    Side getSide() {
        return mSide;
    }

    private void setSide(Side inSide) {
        mSide = inSide;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    BigDecimal getCumQuantity() {
        return mCumQuantity;
    }

    private void setCumQuantity(BigDecimal inCumQuantity) {
        mCumQuantity = inCumQuantity;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    BigDecimal getAvgPrice() {
        return mAvgPrice;
    }

    private void setAvgPrice(BigDecimal inAvgPrice) {
        mAvgPrice = inAvgPrice;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    BigDecimal getLastQuantity() {
        return mLastQuantity;
    }

    private void setLastQuantity(BigDecimal inLastQuantity) {
        mLastQuantity = inLastQuantity;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    BigDecimal getLastPrice() {
        return mLastPrice;
    }

    private void setLastPrice(BigDecimal inLastPrice) {
        mLastPrice = inLastPrice;
    }

    @Column(nullable = false)
    OrderStatus getOrderStatus() {
        return mOrderStatus;
    }

    private void setOrderStatus(OrderStatus inOrderStatus) {
        mOrderStatus = inOrderStatus;
    }

    @Column(nullable = false)
    Date getSendingTime() {
        return mSendingTime;
    }

    private void setSendingTime(Date inSendingTime) {
        mSendingTime = inSendingTime;
    }

    @ManyToOne
    public SimpleUser getViewer() {
        return mViewer;
    }

    private void setViewer(SimpleUser inViewer) {
        mViewer = inViewer;
    }

    @Transient
    UserID getViewerID() {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }

    /**
     * Defined to get JPA to work.
     */
    ExecutionReportSummary() {
    }

    private OrderID mRootID;
    private OrderID mOrderID;
    private OrderID mOrigOrderID;
    private SecurityType mSecurityType;
    private String mSymbol;
    private String mExpiry;
    private BigDecimal mStrikePrice;
    private OptionType mOptionType;
    private String mAccount;
    private Side mSide;
    private BigDecimal mCumQuantity;
    private BigDecimal mAvgPrice;
    private BigDecimal mLastQuantity;
    private BigDecimal mLastPrice;
    private OrderStatus mOrderStatus;
    private Date mSendingTime;
    private SimpleUser mViewer; 
    private PersistentReport mReport;
    /**
     * The attribute viewer used in JPQL queries
     */
    static final String ATTRIBUTE_VIEWER = "viewer";  //$NON-NLS-1$
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = ExecutionReportSummary.class.getSimpleName();
    /**
     * The scale used for storing all decimal values.
     */
    static final int DECIMAL_SCALE = 5;
    /**
     * The precision used for storing all decimal values.
     */
    static final int DECIMAL_PRECISION = 15;
    private static final long serialVersionUID = 1L;
}
