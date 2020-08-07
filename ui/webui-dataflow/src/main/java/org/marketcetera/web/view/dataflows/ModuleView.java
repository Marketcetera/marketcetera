package org.marketcetera.web.view.dataflows;

import java.util.Properties;

import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.view.AbstractPagedGridView;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view of modules within a Strategy Engine.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModuleView
        extends AbstractPagedGridView<DecoratedModuleInfo>
        implements ContentView
{
    /**
     * Create a new ModuleView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     * @param inStrategyEngine a <code>DecoratedStrategyEngine</code> value
     */
    public ModuleView(Window inParentWindow,
                      NewWindowEvent inEvent,
                      Properties inViewProperties,
                      DecoratedStrategyEngine inStrategyEngine)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
        strategyEngine = inStrategyEngine;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getGrid().addSelectionListener(inEvent -> {
            DecoratedModuleInfo selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionSelect().setReadOnly(false);
                switch(selectedObject.getState()) {
                    case STOPPING:
                    case STOPPED:
                    case START_FAILED:
                    case CREATED:
                        getActionSelect().addItems(ACTION_START,
                                                   ACTION_DELETE);
                        break;
                    case STARTING:
                    case STARTED:
                    case STOP_FAILED:
                        getActionSelect().addItems(ACTION_STOP);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported state: " + selectedObject.getState());
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("urn",
                             "state",
                             "initiatedDataFlows",
                             "participatingDataFlows");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Modules of " + strategyEngine.getName();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createDataContainer()
     */
    @Override
    protected PagedDataContainer<DecoratedModuleInfo> createDataContainer()
    {
        return applicationContext.getBean(ModuleInfoPagedDataContainer.class,
                                          this,
                                          strategyEngine);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#getDataContainerType()
     */
    @Override
    protected Class<ModuleInfoPagedDataContainer> getDataContainerType()
    {
        return ModuleInfoPagedDataContainer.class;
    }
    /**
     * start action
     */
    private static final String ACTION_START = "Start";
    /**
     * stop action
     */
    private static final String ACTION_STOP = "Stop";
    /**
     * delete action
     */
    private static final String ACTION_DELETE = "Delete";
    /**
     * currently selected strategy engine
     */
    private DecoratedStrategyEngine strategyEngine;
    /**
     * global name of this view
     */
    public static final String NAME = "ModuleView";
    private static final long serialVersionUID = -8021226875064644245L;
}
