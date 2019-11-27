package org.marketcetera.web.trade.fixmessagedetails.view;

import java.util.Properties;

import org.marketcetera.web.trade.openorders.view.OpenOrderView;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Creates {@link OpenOrderView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class FixMessageDetailsViewFactory
        implements ContentViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentViewFactory#create(com.vaadin.ui.Window, java.util.Properties)
     */
    @Override
    public ContentView create(Window inParent,
                              Properties inViewProperties)
    {
        return applicationContext.getBean(FixMessageDetailsView.class,
                                          inViewProperties);
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}
