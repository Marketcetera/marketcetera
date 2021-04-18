package org.marketcetera.web.view;

import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.font.MarketceteraFont;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Provides common behavior for views that are based around a data grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractGridView<DataClazz,DataContainerClazz extends GridDataContainer<DataClazz>>
        extends AbstractContentView
{
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} enter: columns are {}",
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
        aboveTheGridLayout = new CssLayout();
        aboveTheGridLayout.setWidth("100%");
        belowTheGridLayout = new CssLayout();
        belowTheGridLayout.setWidth("100%");
        gridLayout = new VerticalLayout();
        gridLayout.setMargin(true);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("75%");
        grid = new Grid();
        gridLayout.addComponents(grid);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setHeightMode(HeightMode.CSS);
        grid.setSizeFull();
        dataContainer = createDataContainer();
        dataContainer.configure();
        grid.setContainerDataSource(dataContainer);
        grid.setColumnReorderingAllowed(true);
        grid.setColumnResizeMode(ColumnResizeMode.ANIMATED);
        setGridColumns();
        SLF4JLoggerProxy.debug(this,
                               "{} attach: columns are {}",
                               getViewName(),
                               grid.getColumns(),
                               grid.getContainerDataSource().getContainerPropertyIds());
        actionSelect = new ComboBox("Actions");
        actionSelect.setTextInputAllowed(false);
        actionSelect.setNewItemsAllowed(false);
        actionSelect.setNullSelectionAllowed(false);
        actionSelect.setStyleName(ValoTheme.COMBOBOX_SMALL);
        addActions(actionSelect);
        actionSelect.addValueChangeListener(getActionValueChangeListener());
        createNewButton = new Button("Create New " + getViewSubjectName());
        createNewButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        createNewButton.setIcon(MarketceteraFont.Plus);
        createNewButton.addClickListener(getCreateNewClickListener());
        aboveTheGridLayout.addComponents(actionSelect,
                                         createNewButton);
        addComponents(aboveTheGridLayout,
                      gridLayout,
                      belowTheGridLayout);
    }
    /**
     * Get the selected item.
     *
     * @return a <code>DataClazz</code> or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    protected DataClazz getSelectedItem()
    {
        Object rawRow = getGrid().getSelectedRow();
        if(rawRow == null) {
            return null;
        }
        return (DataClazz)rawRow;
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
     * Get the data container type class value.
     *
     * @return a <code>Class&lt;DataContainerClazz&gt;</code> value
     */
    protected abstract Class<? extends DataContainerClazz> getDataContainerType();
    /**
     * Create a new data container.
     *
     * @return a <code>DataContainerClazz</code> value
     */
    protected DataContainerClazz createDataContainer()
    {
        return applicationContext.getBean(getDataContainerType(),
                                          this);
    }
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
     * @return a <code>DataContainerClazz</code> value
     */
    protected DataContainerClazz getDataContainer()
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
     * Get the aboveTheGridLayout value.
     *
     * @return a <code>CssLayout</code> value
     */
    protected CssLayout getAboveTheGridLayout()
    {
        return aboveTheGridLayout;
    }
    /**
     * Get the belowTheGridLayout value.
     *
     * @return a <code>CssLayout</code> value
     */
    protected CssLayout getBelowTheGridLayout()
    {
        return belowTheGridLayout;
    }
    /**
     * Create a new AbstractGridView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractGridView(Window inParentWindow,
                               NewWindowEvent inEvent,
                               Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /**
     * layout above the grid
     */
    private CssLayout aboveTheGridLayout;
    /**
     * layout below the grid
     */
    private CssLayout belowTheGridLayout;
    /**
     * grid layout value
     */
    private VerticalLayout gridLayout;
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
    private DataContainerClazz dataContainer;
    private static final long serialVersionUID = -7412752686777864724L;
}
