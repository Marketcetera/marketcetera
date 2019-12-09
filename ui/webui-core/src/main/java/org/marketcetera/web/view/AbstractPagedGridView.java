package org.marketcetera.web.view;

import java.util.List;
import java.util.Properties;

import org.marketcetera.web.events.NewWindowEvent;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Provides common behaviors for {@link AbstractGridView} implementations with paged data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractPagedGridView<DataClazz>
        extends AbstractGridView<DataClazz,PagedDataContainer<DataClazz>>
        implements PagedViewProvider
{
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getGrid().addSortListener(inEvent -> {
            getDataContainer().update();
        });
        getDataContainer().setItemsPerPage(25); // TODO config
        getDataContainer().setCurrentPage(0);
        getDataContainer().start();
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
            getDataContainer().setItemsPerPage((Integer)inEvent.getProperty().getValue());
        });
        firstPageButton = new Button();
        firstPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        firstPageButton.setIcon(FontAwesome.FAST_BACKWARD);
        firstPageButton.addClickListener(inEvent -> {
            getDataContainer().firstPageClick();
        });
        prevPageButton = new Button();
        prevPageButton.setIcon(FontAwesome.BACKWARD);
        prevPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        prevPageButton.addClickListener(inEvent -> {
            getDataContainer().prevPageClick();
        });
        currentPageLabel = new Label();
        currentPageLabel.setWidthUndefined();
        nextPageButton = new Button();
        nextPageButton.setIcon(FontAwesome.FORWARD);
        nextPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        nextPageButton.addClickListener(inEvent -> {
            getDataContainer().nextPageClick();
        });
        lastPageButton = new Button();
        lastPageButton.setIcon(FontAwesome.FAST_FORWARD);
        lastPageButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        lastPageButton.addClickListener(inEvent -> {
            getDataContainer().lastPageClick();
        });
        totalItemsLabel = new Label();
        totalItemsLabel.setWidthUndefined();
        getBelowTheGridLayout().addComponents(pageSizeSelect,
                                              firstPageButton,
                                              prevPageButton,
                                              currentPageLabel,
                                              nextPageButton,
                                              lastPageButton,
                                              totalItemsLabel);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        super.detach();
        if(getDataContainer() != null) {
            getDataContainer().stop();
        }
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
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
        return getGrid().getSortOrder();
    }
    /**
     * Get the action value change listener to use when the action value changes.
     *
     * @return a <code>ValueChangeListener</code> value
     */
    protected ValueChangeListener getActionValueChangeListener()
    {
        try {
            return super.getActionValueChangeListener();
        } finally {
            getDataContainer().update();
        }
    }
    /**
     * Create a new AbstractPagedGridView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractPagedGridView(Window inParentWindow,
                                    NewWindowEvent inEvent,
                                    Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
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
    private static final long serialVersionUID = 202962881632363603L;
}
