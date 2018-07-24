package com.marketcetera.web.view;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Provides common behavior for views that are based around a data grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractGridView<Clazz>
        extends CssLayout
        implements ContentView,PagedViewProvider
{
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} columns are {}",
                               getViewName(),
                               grid.getContainerDataSource().getContainerPropertyIds());
        setGridColumns();
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        setSizeFull();
        CssLayout aboveTheGridLayout = new CssLayout();
        aboveTheGridLayout.setWidth("100%");
        CssLayout belowTheGridLayout = new CssLayout();
        belowTheGridLayout.setWidth("100%");
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setMargin(true);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("75%");
        grid = new Grid();
        gridLayout.addComponents(grid);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setHeightMode(HeightMode.CSS);
        grid.setSizeFull();
        grid.addSortListener(inEvent -> {
            dataContainer.update();
        });
        dataContainer = createDataContainer();
        dataContainer.setItemsPerPage(25);
        dataContainer.setCurrentPage(0);
        dataContainer.start();
        grid.setContainerDataSource(dataContainer);
        grid.setColumnReorderingAllowed(true);
//        grid.setColumnResizeMode(ColumnResizeMode.ANIMATED);
        actionSelect = new ComboBox("Actions");
        actionSelect.setTextInputAllowed(false);
        actionSelect.setNewItemsAllowed(false);
        actionSelect.setNullSelectionAllowed(false);
        actionSelect.setStyleName(ValoTheme.COMBOBOX_SMALL);
        addActions(actionSelect);
        actionSelect.addValueChangeListener(getActionValueChangeListener());
        createNewButton = new Button("Create New " + getViewSubjectName());
        createNewButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        createNewButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createNewButton.addClickListener(getCreateNewClickListener());
        aboveTheGridLayout.addComponents(actionSelect,
                                         createNewButton);
        pageSizeSelect = new ComboBox("Items per page");
        pageSizeSelect.setNullSelectionAllowed(false);
        pageSizeSelect.setNewItemsAllowed(true);
        pageSizeSelect.addItems(10,25,50);
        pageSizeSelect.setValue(25);
        pageSizeSelect.setNewItemHandler(inNewItemCaption -> {
            try {
                int newValue = Integer.parseInt(String.valueOf(inNewItemCaption));
                if(newValue > 0) {
                    pageSizeSelect.addItem(newValue);
                }
            } catch (Exception ignored) {
            }
        });
        pageSizeSelect.addValueChangeListener(inEvent -> {
            ValueChangeEvent event = (ValueChangeEvent)inEvent;
            dataContainer.setItemsPerPage((Integer)event.getProperty().getValue());
        });
        firstPageButton = new Button();
        firstPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        firstPageButton.setIcon(FontAwesome.FAST_BACKWARD);
        firstPageButton.addClickListener(inEvent -> {
            dataContainer.firstPageClick();
        });
        prevPageButton = new Button();
        prevPageButton.setIcon(FontAwesome.BACKWARD);
        prevPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        prevPageButton.addClickListener(inEvent -> {
            dataContainer.prevPageClick();
        });
        currentPageLabel = new Label();
        currentPageLabel.setWidthUndefined();
        nextPageButton = new Button();
        nextPageButton.setIcon(FontAwesome.FORWARD);
        nextPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        nextPageButton.addClickListener(inEvent -> {
            dataContainer.nextPageClick();
        });
        lastPageButton = new Button();
        lastPageButton.setIcon(FontAwesome.FAST_FORWARD);
        lastPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        lastPageButton.addClickListener(inEvent -> {
            dataContainer.lastPageClick();
        });
        totalItemsLabel = new Label();
        totalItemsLabel.setWidthUndefined();
        belowTheGridLayout.addComponents(pageSizeSelect,
                                         firstPageButton,
                                         prevPageButton,
                                         currentPageLabel,
                                         nextPageButton,
                                         lastPageButton,
                                         totalItemsLabel);
        addComponents(aboveTheGridLayout,
                      gridLayout,
                      belowTheGridLayout);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        super.detach();
        if(dataContainer != null) {
            dataContainer.stop();
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getPageSizeSelect()
     */
    @Override
    public ComboBox getPageSizeSelect()
    {
        return pageSizeSelect;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getFirstPageButton()
     */
    @Override
    public Button getFirstPageButton()
    {
        return firstPageButton;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getPrevPageButton()
     */
    @Override
    public Button getPrevPageButton()
    {
        return prevPageButton;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getNextPageButton()
     */
    @Override
    public Button getNextPageButton()
    {
        return nextPageButton;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getLastPageButton()
     */
    @Override
    public Button getLastPageButton()
    {
        return lastPageButton;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getCurrentPageLabel()
     */
    @Override
    public Label getCurrentPageLabel()
    {
        return currentPageLabel;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getTotalItemsLabel()
     */
    @Override
    public Label getTotalItemsLabel()
    {
        return totalItemsLabel;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedViewProvider#getSortOrder()
     */
    @Override
    public List<SortOrder> getSortOrder()
    {
        return grid.getSortOrder();
    }
    /**
     * Get the selected item.
     *
     * @return a <code>Clazz</code> or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    protected Clazz getSelectedItem()
    {
        Object rawRow = getGrid().getSelectedRow();
        if(rawRow == null) {
            return null;
        }
        return (Clazz)rawRow;
    }
    /**
     * Get the action value change listener to use when the action value changes.
     *
     * @return a <code>ValueChangeListener</code> value
     */
    protected ValueChangeListener getActionValueChangeListener()
    {
        return inEvent -> {
            try {
                onActionSelect(inEvent);
            } catch (Exception e) {
                String caption = inEvent.getProperty().getValue() + " Problem";
                String message = ExceptionUtils.getRootCauseMessage(e);
                SLF4JLoggerProxy.warn(AbstractGridView.this,
                                      e,
                                      "{}:{}",
                                      caption,
                                      message);
                Notification.show(caption,
                                  message,
                                  Type.ERROR_MESSAGE);
            } finally {
                getActionSelect().setValue(null);
                getDataContainer().update();
            }
        };
    }
    /**
     * Set the columns to display in the grid.
     */
    protected void setGridColumns()
    {
        getGrid().setColumns("name",
                             "description");
    }
    /**
     * Invoked when an action is selected from the action menu.
     *
     * @param inEvent a <code>ValueChangeEvent</code> value
     */
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
    }
    /**
     * Get the human-readable name of the view subject.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getViewSubjectName();
    /**
     * Create a new data container.
     *
     * @return a <code>PagedDataContainer&lt;Clazz&gt;</code> value
     */
    protected abstract PagedDataContainer<Clazz> createDataContainer();
    /**
     * Get the click listener to use when the create new button is invoked.
     *
     * @return a <code>ClickListener</code> value
     */
    protected ClickListener getCreateNewClickListener()
    {
        return new ClickListener() {
            @Override
            public void buttonClick(ClickEvent inEvent)
            {
                onCreateNew(inEvent);
            }
            private static final long serialVersionUID = -5150603461750104700L;
        };
    }
    /**
     * Invoked when a new object is created.
     * 
     * @param inEvent a <code>ClickEvent</code> value
     */
    protected void onCreateNew(ClickEvent inEvent)
    {
    }
    /**
     * Add available actions to the action select.
     *
     * @param inActionSelect a <code>ComboBox</code> value
     */
    protected void addActions(ComboBox inActionSelect)
    {
    }
    /**
     * Get the bean item container for the view grid.
     *
     * @return a <code>PagedDataContainer&lt;Clazz&gt;</code> value
     */
    protected final PagedDataContainer<Clazz> getDataContainer()
    {
        return dataContainer;
    }
    /**
     * Get the actionSelect value.
     *
     * @return a <code>ComboBox</code> value
     */
    protected final ComboBox getActionSelect()
    {
        return actionSelect;
    }
    /**
     * Get the createNewButton value.
     *
     * @return a <code>Button</code> value
     */
    protected final Button getCreateNewButton()
    {
        return createNewButton;
    }
    /**
     * Get the grid value.
     *
     * @return a <code>Grid</code> value
     */
    protected final Grid getGrid()
    {
        return grid;
    }
    /**
     * allows selection of the page size
     */
    private ComboBox pageSizeSelect;
    /**
     * changes the display to the first page
     */
    private Button firstPageButton;
    /**
     * changes the display to the previous page
     */
    private Button prevPageButton;
    /**
     * describes the current page
     */
    private Label currentPageLabel;
    /**
     * describes the total number of items
     */
    private Label totalItemsLabel;
    /**
     * changes the display to the next page
     */
    private Button nextPageButton;
    /**
     * changes the display to the last page
     */
    private Button lastPageButton;
    /**
     * creates new objects
     */
    private Button createNewButton;
    /**
     * select widget used to show actions
     */
    private ComboBox actionSelect;
    /**
     * grid used to display data
     */
    private Grid grid;
    /**
     * holds data for the view
     */
    private PagedDataContainer<Clazz> dataContainer;
    private static final long serialVersionUID = -7412752686777864724L;
}
