package org.marketcetera.trade.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.dao.OrderSummaryDao;
import org.marketcetera.trade.dao.PersistentOrderSummary;
import org.marketcetera.trade.dao.PersistentReport;
import org.marketcetera.trade.dao.QPersistentOrderSummary;
import org.marketcetera.trade.service.OrderSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;

/* $License$ */

/**
 * Provides order summary services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
public class OrderSummaryServiceImpl
        implements OrderSummaryService
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findByReportId(long)
     */
    @Override
    public PersistentOrderSummary findByReportId(long inReportId)
    {
        return orderStatusDao.findByReportId(inReportId);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findMostRecentByRootOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public PersistentOrderSummary findMostRecentByRootOrderId(OrderID inRootOrderId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentOrderSummary.persistentOrderSummary.rootOrderId.eq(inRootOrderId));
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,
                                           QPersistentOrderSummary.persistentOrderSummary.id.getMetadata().getName()));
        Pageable pageRequest = PageRequest.of(0,
                                              1,
                                              sort);
        Page<PersistentOrderSummary> results = orderStatusDao.findAll(where,
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
    public OrderSummary findMostRecentExecutionByRootOrderId(OrderID inRootOrderId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentOrderSummary.persistentOrderSummary.rootOrderId.eq(inRootOrderId));
        where = where.and(QPersistentOrderSummary.persistentOrderSummary.securityType.isNotNull());
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,
                                           QPersistentOrderSummary.persistentOrderSummary.id.getMetadata().getName()));
        Pageable pageRequest = PageRequest.of(0,
                                              1,
                                              sort);
        Page<PersistentOrderSummary> results = orderStatusDao.findAll(where,
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
    public OrderSummary findFirstByRootOrderId(OrderID inRootOrderId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentOrderSummary.persistentOrderSummary.rootOrderId.eq(inRootOrderId));
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                           QPersistentOrderSummary.persistentOrderSummary.id.getMetadata().getName()));
        Pageable pageRequest = PageRequest.of(0,
                                              1,
                                              sort);
        Page<PersistentOrderSummary> results = orderStatusDao.findAll(where,
                                                                     pageRequest);
        if(!results.hasContent()) {
            return null;
        }
        return results.getContent().get(0);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.OrderStatusService#findReportByOrderStatusIn(org.marketcetera.admin.User, java.util.Set)
     */
    @Override
    public List<Report> findReportByOrderStatusIn(User inViewer,
                                                  Set<OrderStatus> inOrderStatusValues)
    {
        PersistentUser persistentViewer = (PersistentUser)inViewer;
        QPersistentOrderSummary r = QPersistentOrderSummary.persistentOrderSummary;
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(r.orderStatus.in(inOrderStatusValues));
        Set<User> basicUsers = authzService.getSubjectUsersFor(inViewer,
                                                               TradePermissions.ViewReportAction.name());
        Set<PersistentUser> subjectUsers = Sets.newHashSet();
        for(User basicUser : basicUsers) {
            subjectUsers.add((PersistentUser)basicUser);
        }
        // show the report if the given user is this user or has supervisor permission over the report (the report permission has already been checked for the "same user" case)
        if(!subjectUsers.isEmpty()) {
            where = where.and(r.viewer.in(subjectUsers)).or(r.viewer.eq(persistentViewer));
        } else {
            where = where.and(r.viewer.eq(persistentViewer));
        }
        Sort sort = new Sort(Sort.Direction.DESC,
                             r.sendingTime.getMetadata().getName());
        // can expose the page and page size to allow paging through the api interfaces
        PageRequest page = PageRequest.of(0,
                                          Integer.MAX_VALUE,
                                          sort);
        Iterable<PersistentOrderSummary> orderStatusIterable = orderStatusDao.findAll(where,
                                                                                     page);
        List<Report> reports = Lists.newArrayList();
        for(PersistentOrderSummary orderStatus : orderStatusIterable) {
            reports.add(orderStatus.getReport());
        }
        return reports;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findByRootOrderIdAndOrderId(org.marketcetera.trade.OrderID, org.marketcetera.trade.OrderID)
     */
    @Override
    public PersistentOrderSummary findByRootOrderIdAndOrderId(OrderID inRootID,
                                                             OrderID inOrderID)
    {
        return orderStatusDao.findByRootOrderIdAndOrderId(inRootID,
                                                          inOrderID);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#findByOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public List<OrderSummary> findByOrderId(OrderID inOrderId)
    {
        List<PersistentOrderSummary> results = orderStatusDao.findByOrderId(inOrderId);
        List<OrderSummary> actualResults = new ArrayList<>();
        if(results != null) {
            actualResults.addAll(results);
        }
        return actualResults;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.OrderSummaryService#findOpenOrders(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<? extends OrderSummary> findOpenOrders(org.marketcetera.persist.PageRequest inPageRequest)
    {
        // TODO use the sort from the page request or this one if no sort specified
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                           QPersistentOrderSummary.persistentOrderSummary.sendingTime.getMetadata().getName()));
        Pageable pageRequest = PageRequest.of(inPageRequest.getPageNumber(),
                                              inPageRequest.getPageSize(),
                                              sort);
        Page<PersistentOrderSummary> pageResponse = orderStatusDao.findOpenOrders(OrderStatus.openOrderStatuses,
                                                                                  pageRequest);
        return new CollectionPageResponse<>(pageRequest,
                                            pageResponse);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.OrderStatusService#update(org.marketcetera.trade.OrderSummary, org.marketcetera.trade.Report, org.marketcetera.trade.ReportBase)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public OrderSummary update(OrderSummary inOrderStatus,
                               Report inReport,
                               ReportBase inReportBase)
    {
        PersistentOrderSummary orderStatus;
        if(inOrderStatus instanceof PersistentOrderSummary) {
            orderStatus = (PersistentOrderSummary)inOrderStatus;
        } else {
            orderStatus = orderStatusDao.findByRootOrderIdAndOrderId(inOrderStatus.getRootOrderId(),
                                                                     inOrderStatus.getOrderId());
            if(orderStatus == null) {
                return null;
            }
        }
        orderStatus.update((PersistentReport)inReport,
                           inReportBase);
        orderStatus = orderStatusDao.save(orderStatus);
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#save(com.marketcetera.ors.history.OrderStatus)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentOrderSummary save(OrderSummary inOrderStatus)
    {
        PersistentOrderSummary orderStatus;
        if(inOrderStatus instanceof PersistentOrderSummary) {
            orderStatus = (PersistentOrderSummary)inOrderStatus;
        } else {
            orderStatus = new PersistentOrderSummary(inOrderStatus);
        }
        return orderStatusDao.save(orderStatus);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.OrderStatusService#delete(com.marketcetera.ors.history.OrderStatus)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(OrderSummary inOrderStatus)
    {
        PersistentOrderSummary orderStatus;
        if(inOrderStatus instanceof PersistentOrderSummary) {
            orderStatus = (PersistentOrderSummary)inOrderStatus;
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
    private OrderSummaryDao orderStatusDao;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}
