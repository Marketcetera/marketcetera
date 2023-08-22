package org.marketcetera.ui.trade.event;

import java.util.Properties;

import org.marketcetera.core.Pair;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.fixmessagedetails.view.FixMessageDetailsViewFactory;
import org.marketcetera.ui.view.ContentViewFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Indicates that the given FIX message is to be displayed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
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
     * @see org.marketcetera.web.events.NewWindowEvent#getViewFactoryType()
     */
    @Override
    public Class<? extends ContentViewFactory> getViewFactoryType()
    {
        return FixMessageDetailsViewFactory.class;
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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.events.NewWindowEvent#getWindowSize()
     */
    @Override
    public Pair<Double,Double> getWindowSize()
    {
        return Pair.create(500.0,
                           400.0);
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
     * FIX message holder value
     */
    private HasFIXMessage hasFixMessage;
    /**
     * properties with which to seed the window
     */
    private Properties windowProperties;
}
