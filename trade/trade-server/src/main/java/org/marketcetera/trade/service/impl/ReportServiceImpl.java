package org.marketcetera.trade.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.admin.User;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.IncomingMessage;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.HasMutableReportID;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.service.ReportService;
import org.springframework.data.domain.Page;

/**
 * Stub class temporarily created for Spring Boot 3 migration.
 * Original implementation required QueryDSL, which isn't compatible with Jakarta EE.
 * 
 * See ReportServiceImpl.java.disabled for the original implementation.
 */
@Service
public class ReportServiceImpl
        implements ReportService
{
    public ReportServiceImpl()
    {
        SLF4JLoggerProxy.warn(this, "This is a stub implementation for Spring Boot 3 migration");
    }

    @Override
    public Report save(Report inReport) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - save(Report) not implemented");
        return inReport;
    }

    @Override
    public Report getReportFor(ReportID inReportId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getReportFor not implemented");
        return null;
    }

    @Override
    public OrderStatus getOrderStatusForOrderChain(OrderID inOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getOrderStatusForOrderChain not implemented");
        return null;
    }

    @Override
    public CollectionPageResponse<Report> getReports(PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getReports not implemented");
        return new CollectionPageResponse<>();
    }

    @Override
    public CollectionPageResponse<ExecutionReportSummary> getFills(PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getFills not implemented");
        return new CollectionPageResponse<>();
    }

    @Override
    public Optional<ExecutionReport> getLatestExecutionReportForOrderChain(OrderID inOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getLatestExecutionReportForOrderChain not implemented");
        return Optional.empty();
    }

    @Override
    public Optional<ExecutionReport> getExecutionReport(OrderID inRootOrderId, OrderID inOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getExecutionReport not implemented");
        return Optional.empty();
    }

    @Override
    public int purgeReportsBefore(Date inPurgeDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - purgeReportsBefore not implemented");
        return 0;
    }

    @Override
    public List<ReportBase> getReportsSince(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getReportsSince not implemented");
        return Collections.emptyList();
    }

    @Override
    public BigDecimal getPositionAsOf(User inUser, Date inDate, Instrument inInstrument) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getPositionAsOf not implemented");
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getEquityPositionAsOf(User inUser, Date inDate, Equity inEquity) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getEquityPositionAsOf not implemented");
        return BigDecimal.ZERO;
    }

    @Override
    public List<ReportBaseImpl> getOpenOrders(User inUser) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getOpenOrders not implemented");
        return Collections.emptyList();
    }

    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAllEquityPositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public BigDecimal getCurrencyPositionAsOf(User inUser, Date inDate, Currency inCurrency) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getCurrencyPositionAsOf not implemented");
        return BigDecimal.ZERO;
    }

    @Override
    public Map<PositionKey<? extends Instrument>, BigDecimal> getAllPositionsAsOf(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAllPositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAllCurrencyPositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public Map<PositionKey<ConvertibleBond>, BigDecimal> getAllConvertibleBondPositionsAsOf(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAllConvertibleBondPositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAllFuturePositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public BigDecimal getFuturePositionAsOf(User inUser, Date inDate, Future inFuture) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getFuturePositionAsOf not implemented");
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getConvertibleBondPositionAsOf(User inUser, Date inDate, ConvertibleBond inConvertibleBond) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getConvertibleBondPositionAsOf not implemented");
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getOptionPositionAsOf(User inUser, Date inDate, Option inOption) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getOptionPositionAsOf not implemented");
        return BigDecimal.ZERO;
    }

    @Override
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(User inUser, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAllOptionPositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(User inUser, Date inDate, String[] inSymbols) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getOptionPositionsAsOf not implemented");
        return Collections.emptyMap();
    }

    @Override
    public Report save(ReportBase inReport) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - save(ReportBase) not implemented");
        return null;
    }

    @Override
    public void delete(ReportID inReportId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - delete not implemented");
    }

    @Override
    public OrderID getRootOrderIdFor(OrderID inOrderID) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getRootOrderIdFor not implemented");
        return null;
    }

    @Override
    public int findLastSequenceNumberFor(quickfix.SessionID inSessionId, Date inDate) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findLastSequenceNumberFor not implemented");
        return 0;
    }

    @Override
    public List<Long> findUnhandledIncomingMessageIds(quickfix.SessionID inSessionId, Set<String> inMessageTypes, Date inSince) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findUnhandledIncomingMessageIds not implemented");
        return Collections.emptyList();
    }

    @Override
    public List<IncomingMessage> findIncomingMessagesForIdIn(Set<Long> inIds) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findIncomingMessagesForIdIn not implemented");
        return Collections.emptyList();
    }

    @Override
    public Page<? extends ExecutionReportSummary> getExecutions(int inPageNumber, int inPageSize) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getExecutions not implemented");
        return Page.empty();
    }

    @Override
    public void assignReportId(HasMutableReportID inReport) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - assignReportId not implemented");
    }

    @Override
    public void addReport(HasFIXMessage inMessage, BrokerID inBrokerID, UserID inUserId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - addReport not implemented");
    }

    @Override
    public CollectionPageResponse<AverageFillPrice> getAverageFillPrices(PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getAverageFillPrices not implemented");
        return new CollectionPageResponse<>();
    }

    @Override
    public OrderID findRootIDForOrderID(OrderID inOrderID) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findRootIDForOrderID not implemented");
        return null;
    }
}