package org.marketcetera.web.trade.event;

import java.util.Properties;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.trade.fixmessagedetails.view.FixMessageDetailsViewFactory;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Indicates that the given FIX message is to be displayed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FixMessageDetailsViewEvent
        implements HasFIXMessage,NewWindowEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getWindowTitle()
     */
    @Override
    public String getWindowTitle()
    {
        return "View FIX Message Details";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getViewFactory()
     */
    @Override
    public ContentViewFactory getViewFactory()
    {
        return applicationContext.getBean(FixMessageDetailsViewFactory.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getProperties()
     */
    @Override
    public Properties getProperties()
    {
        return windowProperties;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return hasFixMessage.getMessage();
    }
    /**
     * Create a new FixMessageDetailsViewEvent instance.
     *
     * @param inHasFixMessage a <code>HasFIXMessage</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public FixMessageDetailsViewEvent(HasFIXMessage inHasFixMessage,
                                      Properties inProperties)
    {
        hasFixMessage = inHasFixMessage;
        windowProperties = inProperties;
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * FIX message holder value
     */
    private HasFIXMessage hasFixMessage;
    /**
     * properties with which to seed the window
     */
    private Properties windowProperties;
}
