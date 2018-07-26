package org.marketcetera.web.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.UI;

/* $License$ */

/**
 * Provides a paged data container.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PagedDataContainer<Clazz>
        extends BeanItemContainer<Clazz>
{
    /**
     * Create a new PagedDataContainer instance.
     *
     * @param inType a <code>Class&lt;? super Clazz&gt;</code> value
     * @param inCollection a <code>Collection&lt;? extends Clazz&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public PagedDataContainer(Class<? super Clazz> inType,
                              Collection<? extends Clazz> inCollection,
                              PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(inType,
              inCollection);
        pagedViewProvider = inPagedViewProvider;
    }
    /**
     * Create a new PagedDataContainer instance.
     *
     * @param inType a <code>Class&lt;? super Clazz&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public PagedDataContainer(Class<? super Clazz> inType,
                              PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(inType);
        pagedViewProvider = inPagedViewProvider;
    }
    /**
     * Validate and start the object.
     */
    public void start()
    {
        synchronized(PagedDataContainer.class) {
            if(refreshService == null) {
                refreshService = Executors.newScheduledThreadPool(10); // TODO config
            }
        }
        refreshToken = refreshService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    SLF4JLoggerProxy.trace(PagedDataContainer.this,
                                           "Updating {}",
                                           getDescription());
                    UI.getCurrent().access(new Runnable() {
                        @Override
                        public void run()
                        {
                            update();
                        }
                    });
                } catch (Exception e) {
                    
                }
            }
        },1000,1000,TimeUnit.MILLISECONDS); // TODO config
    }
    /**
     * Stop the object.
     */
    public void stop()
    {
        if(refreshToken != null) {
            try  {
                refreshToken.cancel(true);
            } catch (Exception ignored) {}
            refreshToken = null;
        }
    }
    /**
     * Set the items per page value.
     *
     * @param inItemsPerPage an <code>int</code> value
     */
    public void setItemsPerPage(int inItemsPerPage)
    {
        itemsPerPage = inItemsPerPage;
        update();
    }
    /**
     * Get the items per page value.
     *
     * @return an <code>int</code> value
     */
    public int getItemsPerPage()
    {
        return itemsPerPage;
    }
    /**
     * Indicate that the first page button was clicked.
     */
    public void firstPageClick()
    {
        currentPage = 0;
        update();
    }
    /**
     * Indicate that the previous page button was clicked.
     */
    public void prevPageClick()
    {
        if(currentPage > 0) {
            currentPage -= 1;
        }
        update();
    }
    /**
     * Indicate that the next page button was clicked.
     */
    public void nextPageClick()
    {
        currentPage += 1;
        update();
    }
    /**
     * Indicate that the last page button was clicked.
     */
    public void lastPageClick()
    {
        currentPage = totalPages-1;
        update();
    }
    /**
     * Get the current page value.
     *
     * @return an <code>int</code> value
     */
    public int getCurrentPage()
    {
        return currentPage;
    }
    /**
     * Set the current page value.
     *
     * @param inCurrentPage an <code>int</code> value
     */
    public void setCurrentPage(int inCurrentPage)
    {
        currentPage = inCurrentPage;
    }
    /**
     * Get the total pages value.
     *
     * @return an <code>int</code> value
     */
    public int getTotalPages()
    {
        return totalPages;
    }
    /**
     * Get the total items value.
     *
     * @return a <code>long</code> value
     */
    public long getTotalItems()
    {
        return totalItems;
    }
    /**
     * Update the data container.
     */
    public void update()
    {
        synchronized(updateDataContainerLock) {
            CollectionPageResponse<Clazz> updatedDataPackage = getDataContainerContents(generatePageRequest());
            // TODO set sort order based on results
            currentPage = updatedDataPackage.getPageNumber();
            totalPages = updatedDataPackage.getTotalPages();
            totalItems = updatedDataPackage.getTotalSize();
            Collection<Clazz> newData = updatedDataPackage.getElements();
            List<Clazz> existingData = getItemIds();
            SLF4JLoggerProxy.trace(this,
                                   "Updating {} with: {}, existing data is {}",
                                   getDescription(),
                                   newData,
                                   existingData);
            Iterator<Clazz> newDataIterator = newData.iterator();
            Iterator<Clazz> existingDataIterator = existingData.iterator();
            List<Runnable> updateActions = Lists.newArrayList();
            while(existingDataIterator.hasNext()) {
                final Clazz existingDataItem = existingDataIterator.next();
                if(newDataIterator.hasNext()) {
                    final Clazz newDataItem = newDataIterator.next();
                    if(!isDeepEquals(existingDataItem,newDataItem)) {
                        updateActions.add(new Runnable() {
                            @Override
                            public void run()
                            {
                                addItemAfter(existingDataItem,
                                             newDataItem);
                                removeItem(existingDataItem);
                            }
                        });
                    }
                } else {
                    updateActions.add(new Runnable() {
                        @Override
                        public void run()
                        {
                            removeItem(existingDataItem);
                        }
                    });
                }
            }
            try {
                for(Runnable runnable : updateActions) {
                    runnable.run();
                }
                // anything left in new data is new
                while(newDataIterator.hasNext()) {
                    Clazz newDataItem = newDataIterator.next();
                    addItem(newDataItem);
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "An error occurred while trying to update the {} view, executing full refresh",
                                      getDescription());
                removeAllItems();
                addAll(newData);
            }
            if(pagedViewProvider.getPrevPageButton() != null) {
                pagedViewProvider.getPrevPageButton().setEnabled(getCurrentPage()!=0);
            }
            if(pagedViewProvider.getFirstPageButton() != null) {
                pagedViewProvider.getFirstPageButton().setEnabled(getCurrentPage()!=0);
            }
            if(pagedViewProvider.getNextPageButton() != null) {
                pagedViewProvider.getNextPageButton().setEnabled((getCurrentPage()+1)!=getTotalPages());
            }
            if(pagedViewProvider.getLastPageButton() != null) {
                pagedViewProvider.getLastPageButton().setEnabled((getCurrentPage()+1)!=getTotalPages());
            }
            if(pagedViewProvider.getCurrentPageLabel() != null) {
                pagedViewProvider.getCurrentPageLabel().setCaption(getCurrentPage()+1 + "/" + getTotalPages());
            }
            if(pagedViewProvider.getTotalItemsLabel() != null) {
                StringBuilder output = new StringBuilder();
                output.append(getTotalItems()).append(" total item");
                if(getTotalItems() != 1) {
                    output.append('s');
                }
                pagedViewProvider.getTotalItemsLabel().setCaption(output.toString());
            }
        }
    }
    /**
     * Generate the page request for the current state of the container.
     *
     * @return a <code>PageRequest</code> value
     */
    protected PageRequest generatePageRequest()
    {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNumber(currentPage);
        pageRequest.setPageSize(itemsPerPage);
        if(pagedViewProvider.getSortOrder() != null) {
            List<org.marketcetera.persist.Sort> pageRequestSortOrder = Lists.newArrayList();
            for(SortOrder sortOrder : pagedViewProvider.getSortOrder()) {
                SortDirection sortDirection = sortOrder.getDirection();
                String property = String.valueOf(sortOrder.getPropertyId());
                pageRequestSortOrder.add(new org.marketcetera.persist.Sort(property,
                                                                           sortDirection == SortDirection.ASCENDING?org.marketcetera.persist.SortDirection.ASCENDING:org.marketcetera.persist.SortDirection.DESCENDING));
            }
            pageRequest.setSortOrder(pageRequestSortOrder);
        }
        return pageRequest;
    }
    /**
     * Get the contents of the data container.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Clazz&gt;</code> value
     */
    protected abstract CollectionPageResponse<Clazz> getDataContainerContents(PageRequest inPageRequest);
    /**
     * Indicates if the two given objects are equal in all the displayed columns in the view.
     *
     * @param inO1 a <code>Clazz</code> value
     * @param inO2 a <code>Clazz</code> value
     * @return a <code>boolean</code> value
     */
    protected abstract boolean isDeepEquals(Clazz inO1,
                                            Clazz inO2);
    /**
     * Get a description of the data types in this container.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getDescription();
    /**
     * view which holds the paged view components
     */
    private final PagedViewProvider pagedViewProvider;
    /**
     * token for the scheduled job that refreshes the container
     */
    private Future<?> refreshToken;
    /**
     * number of items per page to display
     */
    private int itemsPerPage = 25;
    /**
     * current page being displayed
     */
    private int currentPage = 0;
    /**
     * total number of pages available to be displayed
     */
    private int totalPages;
    /**
     * total number of items on all pages
     */
    private long totalItems;
    /**
     * guards access to the data container
     */
    private final Object updateDataContainerLock = new Object();
    /**
     * common service for refreshing all data containers
     */
    private static ScheduledExecutorService refreshService;
    private static final long serialVersionUID = -4894838864066631094L;
}
