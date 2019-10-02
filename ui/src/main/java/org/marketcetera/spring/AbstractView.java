package org.marketcetera.spring;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractView
        extends VerticalLayout
        implements BeforeEnterObserver
{
    /* (non-Javadoc)
     * @see com.vaadin.flow.router.internal.BeforeEnterHandler#beforeEnter(com.vaadin.flow.router.BeforeEnterEvent)
     */
    @Override
    public void beforeEnter(BeforeEnterEvent inEvent)
    {
        SLF4JLoggerProxy.warn(this,
                              "Coco: before enter {}",
                              inEvent);
        MetcSession session = UI.getCurrent().getSession().getAttribute(MetcSession.class);
        if(session == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No session, redirect to login view");
        } else {
            SLF4JLoggerProxy.warn(this,
                                  "Session: {}",
                                  session);
        }
        UI.getCurrent().navigate(LoginView.class);
    }
    private static final long serialVersionUID = 607774015598934869L;
}
