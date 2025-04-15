package org.marketcetera.trade.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.admin.User;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.service.OrderSummaryService;

/**
 * Stub class temporarily created for Spring Boot 3 migration.
 * Original implementation required QueryDSL, which isn't compatible with Jakarta EE.
 * 
 * See OrderSummaryServiceImpl.java.disabled for the original implementation.
 */
@Service
public class OrderSummaryServiceImpl
        implements OrderSummaryService
{
    public OrderSummaryServiceImpl()
    {
        SLF4JLoggerProxy.warn(this, "This is a stub implementation for Spring Boot 3 migration");
    }

    @Override
    public OrderSummary findByReportId(long inReportId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findByReportId not implemented");
        return null;
    }

    @Override
    public Optional<? extends OrderSummary> findByOrderId(OrderID inOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findByOrderId not implemented");
        return Optional.empty();
    }

    @Override
    public OrderSummary findMostRecentByRootOrderId(OrderID inRootOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findMostRecentByRootOrderId not implemented");
        return null;
    }

    @Override
    public OrderSummary findMostRecentExecutionByRootOrderId(OrderID inRootOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findMostRecentExecutionByRootOrderId not implemented");
        return null;
    }

    @Override
    public OrderSummary save(OrderSummary inOrderStatus) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - save not implemented");
        return inOrderStatus;
    }

    @Override
    public void delete(OrderSummary inOrderStatus) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - delete not implemented");
    }

    @Override
    public OrderSummary findFirstByRootOrderId(OrderID inRootOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFirstByRootOrderId not implemented");
        return null;
    }

    @Override
    public List<Report> findReportByOrderStatusIn(User inViewer, Set<OrderStatus> inOrderStatusValues) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findReportByOrderStatusIn not implemented");
        return Collections.emptyList();
    }

    @Override
    public OrderSummary findByRootOrderIdAndOrderId(OrderID inRootID, OrderID inOrderID) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findByRootOrderIdAndOrderId not implemented");
        return null;
    }

    @Override
    public CollectionPageResponse<OrderSummary> findOpenOrders(PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findOpenOrders not implemented");
        return new CollectionPageResponse<>();
    }

    @Override
    public OrderSummary update(OrderSummary inOrderStatus, Report inReport, ReportBase inReportBase) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - update not implemented");
        return inOrderStatus;
    }

    @Override
    public List<OrderSummary> findByRootOrderId(OrderID inOrderId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findByRootOrderId not implemented");
        return Collections.emptyList();
    }
}