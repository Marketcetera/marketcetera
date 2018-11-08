package com.marketcetera.ors.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.marketcetera.admin.service.AuthorizationService;
import com.marketcetera.ors.TradingPermissions;
import com.marketcetera.ors.dao.OrderStatusDao;
import com.marketcetera.ors.dao.OrderStatusService;
import com.marketcetera.ors.history.OrderStatus;
import com.marketcetera.ors.history.PersistentOrderStatus;
import com.marketcetera.ors.history.PersistentReport;
import com.marketcetera.ors.history.QPersistentOrderStatus;
import com.marketcetera.ors.security.SimpleUser;
import com.querydsl.core.BooleanBuilder;

/* $License$ */

/**
 * Provides order status services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
public class OrderStatusServiceImpl
        implements OrderStatusService
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findByReportId(long)
     */
    @Override
    public PersistentOrderStatus findByReportId(long inReportId)
    {
        return orderStatusDao.findByReportId(inReportId);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findMostRecentByRootOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public PersistentOrderStatus findMostRecentByRootOrderId(OrderID inRootOrderId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentOrderStatus.persistentOrderStatus.rootOrderId.eq(inRootOrderId));
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,
                                            QPersistentOrderStatus.persistentOrderStatus.id.getMetadata().getName()));
        Pageable pageRequest = new PageRequest(0,
                                               1,
                                               sort);
        Page<PersistentOrderStatus> results = orderStatusDao.findAll(where,
                                                                     pageRequest);
        if(!results.hasContent()) {
            return null;
        }
        return results.getContent().get(0);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findMostRecentExecutionByRootOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderStatus findMostRecentExecutionByRootOrderId(OrderID inRootOrderId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentOrderStatus.persistentOrderStatus.rootOrderId.eq(inRootOrderId));
        where = where.and(QPersistentOrderStatus.persistentOrderStatus.securityType.isNotNull());
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,
                                            QPersistentOrderStatus.persistentOrderStatus.id.getMetadata().getName()));
        Pageable pageRequest = new PageRequest(0,
                                               1,
                                               sort);
        Page<PersistentOrderStatus> results = orderStatusDao.findAll(where,
                                                                     pageRequest);
        if(!results.hasContent()) {
            return null;
        }
        return results.getContent().get(0);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findFirstByRootOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderStatus findFirstByRootOrderId(OrderID inRootOrderId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentOrderStatus.persistentOrderStatus.rootOrderId.eq(inRootOrderId));
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC,
                                            QPersistentOrderStatus.persistentOrderStatus.id.getMetadata().getName()));
        Pageable pageRequest = new PageRequest(0,
                                               1,
                                               sort);
        Page<PersistentOrderStatus> results = orderStatusDao.findAll(where,
                                                                     pageRequest);
        if(!results.hasContent()) {
            return null;
        }
        return results.getContent().get(0);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findReportByOrderStatusIn(com.marketcetera.admin.User, java.util.Set)
     */
    @Override
    public List<PersistentReport> findReportByOrderStatusIn(SimpleUser inViewer,
                                                            Set<org.marketcetera.trade.OrderStatus> inOrderStatusValues)
    {
        QPersistentOrderStatus r = QPersistentOrderStatus.persistentOrderStatus;
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(r.orderStatus.in(inOrderStatusValues));
        Set<SimpleUser> subjectUsers = authzService.getSubjectUsersFor(inViewer,
                                                                       TradingPermissions.ViewReportAction.name());
        // show the report if the given user is this user or has supervisor permission over the report (the report permission has already been checked for the "same user" case)
        if(!subjectUsers.isEmpty()) {
            where = where.and(r.viewer.in(subjectUsers)).or(r.viewer.eq(inViewer));
        } else {
            where = where.and(r.viewer.eq(inViewer));
        }
        Sort sort = new Sort(Sort.Direction.DESC,
                             r.sendingTime.getMetadata().getName());
        // can expose the page and page size to allow paging through the api interfaces
        PageRequest page = new PageRequest(0,
                                           Integer.MAX_VALUE,
                                           sort);
        Iterable<PersistentOrderStatus> orderStatusIterable = orderStatusDao.findAll(where,
                                                                                     page);
        List<PersistentReport> reports = Lists.newArrayList();
        for(PersistentOrderStatus orderStatus : orderStatusIterable) {
            reports.add(orderStatus.getReport());
        }
        return reports;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findByRootOrderIdAndOrderId(org.marketcetera.trade.OrderID, org.marketcetera.trade.OrderID)
     */
    @Override
    public PersistentOrderStatus findByRootOrderIdAndOrderId(OrderID inRootID,
                                                             OrderID inOrderID)
    {
        return orderStatusDao.findByRootOrderIdAndOrderId(inRootID,
                                                          inOrderID);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findByOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public List<OrderStatus> findByOrderId(OrderID inOrderId)
    {
        List<PersistentOrderStatus> results = orderStatusDao.findByOrderId(inOrderId);
        List<OrderStatus> actualResults = new ArrayList<>();
        if(results != null) {
            actualResults.addAll(results);
        }
        return actualResults;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findOpenOrders(int, int)
     */
    @Override
    public Page<PersistentOrderStatus> findOpenOrders(int inPageNumber,
                                                      int inPageSize)
    {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC,
                                            QPersistentOrderStatus.persistentOrderStatus.sendingTime.getMetadata().getName()));
        Pageable pageRequest = new PageRequest(inPageNumber,
                                               inPageSize,
                                               sort);
        return orderStatusDao.findOpenOrders(org.marketcetera.trade.OrderStatus.openOrderStatuses,
                                             pageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#update(com.marketcetera.ors.history.OrderStatus, com.marketcetera.ors.history.PersistentReport, org.marketcetera.trade.ReportBase)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public OrderStatus update(OrderStatus inOrderStatus,
                              PersistentReport inReport,
                              ReportBase inReportBase)
    {
        PersistentOrderStatus orderStatus;
        if(inOrderStatus instanceof PersistentOrderStatus) {
            orderStatus = (PersistentOrderStatus)inOrderStatus;
        } else {
            orderStatus = orderStatusDao.findByRootOrderIdAndOrderId(inOrderStatus.getRootOrderId(),
                                                                     inOrderStatus.getOrderId());
            if(orderStatus == null) {
                return null;
            }
        }
        orderStatus.update(inReport,
                           inReportBase);
        orderStatus = orderStatusDao.save(orderStatus);
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#save(com.marketcetera.ors.history.OrderStatus)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentOrderStatus save(OrderStatus inOrderStatus)
    {
        PersistentOrderStatus orderStatus;
        if(inOrderStatus instanceof PersistentOrderStatus) {
            orderStatus = (PersistentOrderStatus)inOrderStatus;
        } else {
            orderStatus = new PersistentOrderStatus(inOrderStatus);
        }
        return orderStatusDao.save(orderStatus);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#delete(com.marketcetera.ors.history.OrderStatus)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(OrderStatus inOrderStatus)
    {
        PersistentOrderStatus orderStatus;
        if(inOrderStatus instanceof PersistentOrderStatus) {
            orderStatus = (PersistentOrderStatus)inOrderStatus;
        } else {
            orderStatus = orderStatusDao.findByRootOrderIdAndOrderId(inOrderStatus.getRootOrderId(),
                                                                     inOrderStatus.getOrderId());
            if(orderStatus == null) {
                return;
            }
        }
        orderStatusDao.delete(orderStatus);
    }
    /**
     * provides access to the order status data store
     */
    @Autowired
    private OrderStatusDao orderStatusDao;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}
