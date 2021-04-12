package org.marketcetera.web.session.view;

import java.util.Map;
import java.util.Properties;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.FixAdminClientService;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.StyleService;
import org.marketcetera.web.view.ContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a widget that shows the status of broker sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionStatusView
        extends HorizontalLayout
        implements ContentView,BrokerStatusListener
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        FixAdminClientService adminClientService = serviceManager.getService(FixAdminClientService.class);
        synchronized(sessionRows) {
            adminClientService.getFixSessions().forEach(activeFixSession-> {
                handleFixSession(activeFixSession);}
            );
        }
        setId(getClass().getCanonicalName() + ".sessionStatusView");
        adminClientService.addBrokerStatusListener(this);
        styleService.addStyle(this);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        serviceManager.getService(FixAdminClientService.class).removeBrokerStatusListener(this);
        synchronized(sessionRows) {
            sessionRows.clear();
        }
    }
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
        synchronized(sessionRows) {
            handleFixSession(inActiveFixSession);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new SessionStatusView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public SessionStatusView(Window inParent,
                             NewWindowEvent inEvent,
                             Properties inProperties)
    {
        parent = inParent;
        event = inEvent;
        viewProperties = inProperties;
    }
    /**
     * Handle the update for the given FIX session.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     */
    private void handleFixSession(ActiveFixSession inActiveFixSession)
    {
        SLF4JLoggerProxy.debug(this,
                               "Incoming broker status: {}",
                               inActiveFixSession);
        try {
            SessionRow sessionRow = sessionRows.get(inActiveFixSession.getFixSession().getBrokerId());
            if(inActiveFixSession.getStatus() == FixSessionStatus.DELETED) {
                SLF4JLoggerProxy.debug(this,
                                       "Deleting: {}",
                                       sessionRow);
                if(sessionRow == null) {
                    // don't expect this, but, I guess nothing to do
                } else {
                    sessionRows.remove(inActiveFixSession.getFixSession().getBrokerId());
                    removeComponent(sessionRow.fixSessionLayout);
                }
            } else {
                if(sessionRow == null) {
                    sessionRow = new SessionRow(inActiveFixSession);
                    addComponent(sessionRow.fixSessionLayout);
                    sessionRows.put(inActiveFixSession.getFixSession().getBrokerId(),
                                    sessionRow);
                }
                sessionRow.setFixSession(inActiveFixSession);
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to handle update: {}",
                                  inActiveFixSession);
        }
    }
    /**
     * Holds the data for a session image displayed in the view.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class SessionRow
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("SessionRow [").append(fixSession.getFixSession().getName()).append(" ").append(fixSession.getStatus()).append("]");
            return builder.toString();
        }
        /**
         * Sets the fixSession value.
         *
         * @param inFixSession a <code>ActiveFixSession</code> value
         */
        private void setFixSession(ActiveFixSession inFixSession)
        {
            FixSessionStatus oldStatus = null;
            if(fixSession != null) {
                oldStatus = fixSession.getStatus();
            }
            fixSession = inFixSession;
            FixSessionStatus fixSessionStatus = fixSession.getStatus();
            if(fixSessionStatus != oldStatus) {
                UI currentUi = UI.getCurrent();
                if(currentUi == null) {
                    return;
                }
                currentUi.access(new Runnable() {
                    @Override
                    public void run()
                    {
                        try {
                            ThemeResource brokerImageResource;
                            switch(fixSessionStatus) {
                                case AFFINITY_MISMATCH:
                                case BACKUP:
                                case DELETED:
                                case STOPPED:
                                case UNKNOWN:
                                default:
                                    brokerImageResource = new ThemeResource("broker-red.png");
                                    break;
                                case CONNECTED:
                                    brokerImageResource = new ThemeResource("broker-green.png");
                                    break;
                                case DISABLED:
                                    brokerImageResource = new ThemeResource("broker-gray.png");
                                    break;
                                case DISCONNECTED:
                                    brokerImageResource = new ThemeResource("broker-yellow.png");
                                    break;
                                case NOT_CONNECTED:
                                    brokerImageResource = new ThemeResource("broker-orange.png");
                                    break;
                            }
                            fixSessionLayout.removeAllComponents();
                            brokerStatusImage = new Image(null,
                                                          brokerImageResource);
                            brokerStatusImage.setDescription(fixSessionStatus.name());
                            brokerStatusLabel.setCaption(fixSession.getFixSession().getName());
                            brokerStatusImage.setId(getClass().getCanonicalName() + ".brokerStatusImage");
                            styleService.addStyle(brokerStatusImage);
                            fixSessionLayout.addComponents(brokerStatusImage,
                                                           brokerStatusLabel);
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(SessionStatusView.this,
                                                  e,
                                                  "Unable to handle update: {}",
                                                  inFixSession);
                        }
                    }}
                );
            }
        }
        /**
         * Create a new SessionRow instance.
         *
         * @param inActiveFixSession an <code>ActiveFixSession</code> value
         */
        private SessionRow(ActiveFixSession inActiveFixSession)
        {
            fixSessionLayout = new VerticalLayout();
            fixSessionLayout.setId(getClass().getCanonicalName() + ".fixSessionLayout");
            styleService.addStyle(fixSessionLayout);
            brokerStatusLabel = new Label();
            brokerStatusLabel.setId(getClass().getCanonicalName() + ".brokerStatusLabel");
            styleService.addStyle(brokerStatusLabel);
            setFixSession(inActiveFixSession);
        }
        /**
         * layout for a single session
         */
        private final VerticalLayout fixSessionLayout;
        /**
         * image of the session
         */
        private Image brokerStatusImage;
        /**
         * label which holds the name of the session
         */
        private Label brokerStatusLabel;
        /**
         * fix session - should be up-to-date
         */
        private ActiveFixSession fixSession;
    }
    /**
     * holds the session row values - one per session currently being displayed
     */
    private final Map<String,SessionRow> sessionRows = Maps.newHashMap();
    /**
     * parent window opened for the content
     */
    @SuppressWarnings("unused")
    private final Window parent;
    /**
     * new window event that caused the view to be opened
     */
    @SuppressWarnings("unused")
    private final NewWindowEvent event;
    /**
     * properties that initialize this view
     */
    @SuppressWarnings("unused")
    private final Properties viewProperties;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Session Status View";
    /**
     * provides access to style services
     */
    @Autowired
    private StyleService styleService;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    private static final long serialVersionUID = -5731255009579917547L;
}
