package org.marketcetera.web.session.view;

import java.util.SortedMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.web.BrokerStatusLayoutProvider;
import org.marketcetera.web.WidgetProvider;
import org.marketcetera.web.fonts.MarketceteraSessionStatusConnectedFont;
import org.marketcetera.web.fonts.MarketceteraSessionStatusDisabledFont;
import org.marketcetera.web.fonts.MarketceteraSessionStatusDisconnectedFont;
import org.marketcetera.web.fonts.MarketceteraSessionStatusNotConnectedFont;
import org.marketcetera.web.fonts.MarketceteraSessionStatusStoppedFont;
import org.marketcetera.web.fonts.MarketceteraSessionStatusUnknownFont;
import org.marketcetera.web.service.FixAdminClientService;
import org.marketcetera.web.service.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;

/* $License$ */

/**
 * Provides a session status display in the workspace footer.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionStatusWidget
        implements BrokerStatusListener, WidgetProvider
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        FixAdminClientService adminClientService = serviceManager.getService(FixAdminClientService.class);
        adminClientService.addBrokerStatusListener(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        FixAdminClientService adminClientService = serviceManager.getService(FixAdminClientService.class);
        adminClientService.removeBrokerStatusListener(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
        if(inActiveFixSession.getStatus() == FixSessionStatus.DELETED) {
            activeFixSessions.remove(inActiveFixSession.getFixSession().getName());
        } else {
            activeFixSessions.put(inActiveFixSession.getFixSession().getName(),
                                  inActiveFixSession);
        }
        UI currentUi = UI.getCurrent();
        if(currentUi == null || !currentUi.isAttached()) {
            return;
        }
        currentUi.access(new Runnable() {
            @Override
            public void run()
            {
                try {
                    Layout brokerStatusLayout = brokerStatusLayoutProvider.getBrokerStatusLayout();
                    brokerStatusLayout.removeAllComponents();
                    for(ActiveFixSession activeFixSession : activeFixSessions.values()) {
                        FixSessionStatus fixSessionStatus = activeFixSession.getStatus();
                        String description = activeFixSession.getFixSession().getName() + " " + activeFixSession.getStatus().getHumanReadable();
                        Label brokerStatusLabel = new Label();
                        brokerStatusLabel.setSizeUndefined();
                        brokerStatusLabel.setValue("");
                        Resource sessionStatusGlyph;
                        switch(fixSessionStatus) {
                            case AFFINITY_MISMATCH:
                            case BACKUP:
                            case DELETED:
                            case UNKNOWN:
                            default:
                                sessionStatusGlyph = MarketceteraSessionStatusUnknownFont.SESSION_STATUS_UNKNOWN;
                                break;
                            case STOPPED:
                                sessionStatusGlyph = MarketceteraSessionStatusStoppedFont.SESSION_STATUS_STOPPED;
                                break;
                            case CONNECTED:
                                sessionStatusGlyph = MarketceteraSessionStatusConnectedFont.SESSION_STATUS_CONNECTED;
                                break;
                            case DISABLED:
                                sessionStatusGlyph = MarketceteraSessionStatusDisabledFont.SESSION_STATUS_DISABLED;
                                break;
                            case DISCONNECTED:
                                sessionStatusGlyph = MarketceteraSessionStatusDisconnectedFont.SESSION_STATUS_DISCONNECTED;
                                break;
                            case NOT_CONNECTED:
                                sessionStatusGlyph = MarketceteraSessionStatusNotConnectedFont.SESSION_STATUS_NOT_CONNECTED;
                                break;
                        }
                        brokerStatusLabel.setIcon(sessionStatusGlyph);
                        brokerStatusLabel.setDescription(description);
                        brokerStatusLayout.addComponent(brokerStatusLabel);
                    }
                } catch (Exception ignored) {}
            }}
        );
    }
    private SortedMap<String,ActiveFixSession> activeFixSessions = Maps.newTreeMap();
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    /**
     * provides access to the broker status layout to place the widget
     */
    @Autowired
    private BrokerStatusLayoutProvider brokerStatusLayoutProvider;
}
