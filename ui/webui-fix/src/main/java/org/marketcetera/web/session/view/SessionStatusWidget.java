package org.marketcetera.web.session.view;

import java.util.SortedMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.web.BrokerStatusLayoutProvider;
import org.marketcetera.web.WidgetProvider;
import org.marketcetera.web.events.LoginEvent;
import org.marketcetera.web.font.MarketceteraFont;
import org.marketcetera.web.service.FixAdminClientService;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;

/* $License$ */

/**
 *
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
    @PostConstruct
    public void start()
    {
        webMessageService.register(this);
        FixAdminClientService adminClientService = serviceManager.getService(FixAdminClientService.class);
        adminClientService.addBrokerStatusListener(this);
    }
    @PreDestroy
    public void stop()
    {
        webMessageService.unregister(this);
    }
    @Subscribe
    public void receiveLoginEvent(LoginEvent inLoginEvent)
    {
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
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
                    activeFixSessions.put(inActiveFixSession.getFixSession().getName(),
                                          inActiveFixSession);
                    for(ActiveFixSession activeFixSession : activeFixSessions.values()) {
                        Label brokerStatus = new Label();
                        brokerStatus.setValue(" ");
                        brokerStatus.setIcon(MarketceteraFont.Session_Status_Green);
                        brokerStatus.addStyleName("green");
                        brokerStatus.setDescription(activeFixSession.getFixSession().getName() + " " + activeFixSession.getStatus().name().toLowerCase());
                        brokerStatusLayout.addComponent(brokerStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}
        );
    }
    private SortedMap<String,ActiveFixSession> activeFixSessions = Maps.newTreeMap();
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private BrokerStatusLayoutProvider brokerStatusLayoutProvider;
    @Autowired
    private WebMessageService webMessageService;
}
