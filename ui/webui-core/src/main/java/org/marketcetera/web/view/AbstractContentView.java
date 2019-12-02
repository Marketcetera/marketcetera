package org.marketcetera.web.view;

import java.util.Properties;

import org.marketcetera.core.XmlService;
import org.marketcetera.web.service.AuthorizationHelperService;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.StyleService;
import org.marketcetera.web.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides common behavior for {@link ContentView} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractContentView
        extends CssLayout
        implements ContentView
{
    /**
     * Get the viewProperties value.
     *
     * @return a <code>Properties</code> value
     */
    protected Properties getViewProperties()
    {
        return viewProperties;
    }
    /**
     * Get the parentWindow value.
     *
     * @return a <code>Window</code> value
     */
    protected Window getParentWindow()
    {
        return parentWindow;
    }
    /**
     * Create a new AbstractContentView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractContentView(Window inParentWindow,
                                  Properties inViewProperties)
    {
        parentWindow = inParentWindow;
        viewProperties = inViewProperties;
    }
    /**
     * provides access to style services
     */
    @Autowired
    protected StyleService styleService;
    /**
     * provides access to XML services
     */
    @Autowired
    protected XmlService xmlService;
    /**
     * provides access to the application context
     */
    @Autowired
    protected ApplicationContext applicationContext;
    /**
     * provides access to web message services
     */
    @Autowired
    protected WebMessageService webMessageService;
    /**
     * provides access to client services
     */
    @Autowired
    protected ServiceManager serviceManager;
    /**
     * helps determine if authorization is granted for actions
     */
    @Autowired
    protected AuthorizationHelperService authzHelperService;
    /**
     * parent window that owns the view
     */
    private final Window parentWindow;
    /**
     * properties used to seed the view
     */
    private final Properties viewProperties;
    private static final long serialVersionUID = 1612276342925869107L;
}
