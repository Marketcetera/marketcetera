package org.marketcetera.webui.view;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

/* $License$ */

/**
 * Main application view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringView(name=MainView.NAME)
public class MainView
        extends HorizontalLayout
        implements View
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        setSizeFull();
        mainLayout = new CssLayout();
        mainLayout.setSizeFull();
        addComponent(mainLayout);
    }
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
    }
    /**
     * main layout component of this view
     */
    private Layout mainLayout;
    /**
     * view identifier
     */
    public static final String NAME = "";
    private static final long serialVersionUID = -4998089295932260796L;
}
