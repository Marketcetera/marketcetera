package org.marketcetera.ui.view;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.ClientStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.service.AuthorizationHelperService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.StyleService;
import org.marketcetera.ui.service.UiMessageService;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javafx.scene.layout.Region;

/* $License$ */

/**
 * Provides common behavior for {@link ContentView} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractContentView
        implements ContentView,ClientStatusListener
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        viewName = PlatformServices.getServiceName(getClass());
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              viewName);
        reconnectTimer = new Timer();
        adminClientService = serviceManager.getService(AdminClientService.class);
        adminClientService.addClientStatusListener(this);
        onStart();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientStatusListener#receiveClientStatus(boolean)
     */
    @Override
    public void receiveClientStatus(boolean inIsAvailable)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received client status available: {}",
                               viewName,
                               inIsAvailable);
        if(inIsAvailable) {
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    boolean succeeded = false;
                    do {
                        try {
                            onClientConnect();
                            succeeded = true;
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(this,
                                                  e);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {}
                        }
                    } while(!succeeded);
                    initializeBrokerStatusListener();
                }},500);
        } else {
            onClientDisconnect();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose()
     */
    @Override
    public void onClose()
    {
        if(brokerStatusListener != null) {
            try {
                adminClientService.removeBrokerStatusListener(brokerStatusListener);
            } catch (Exception ignored) {}
        }
        try {
            adminClientService.removeClientStatusListener(this);
        } catch (Exception ignored) {}
        SLF4JLoggerProxy.trace(this,
                               "{} close",
                               viewName);
    }
    /**
     * Create the broker status listener value.
     */
    protected void initializeBrokerStatusListener()
    {
        if(brokerStatusListener != null) {
            try {
                adminClientService.removeBrokerStatusListener(brokerStatusListener);
                brokerStatusListener = null;
            } catch (Exception ignored) {}
        }
        brokerStatusListener = new BrokerStatusListener() {
            @Override
            public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
            {
                onBrokerStatusChange(inActiveFixSession);
            }
        };
        serviceManager.getService(AdminClientService.class).addBrokerStatusListener(brokerStatusListener);
    }
    /**
     * Receive broker status changes.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     */
    protected void onBrokerStatusChange(ActiveFixSession inActiveFixSession) {}
    /**
     * Executed when the client connection is reestablished after a break.
     *
     * <p>If anything does not succeed, throw an exception and this method will be called
     * again until an exception is not thrown.</p>
     */
    protected void onClientConnect() {}
    /**
     * Executed when the client connection is lost.
     */
    protected void onClientDisconnect() {}
    /**
     * Executed when the view is started.
     */
    protected abstract void onStart();
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
     * @return a <code>Region</code> value
     */
    protected Region getParentWindow()
    {
        return parentWindow;
    }
    /**
     * Get the newWindowEvent value.
     *
     * @return a <code>NewWindowEvent</code> value
     */
    protected NewWindowEvent getNewWindowEvent()
    {
        return newWindowEvent;
    }
    /**
     * Create a new AbstractContentView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractContentView(Region inParentWindow,
                                  NewWindowEvent inEvent,
                                  Properties inViewProperties)
    {
        parentWindow = inParentWindow;
        newWindowEvent = inEvent;
        viewProperties = inViewProperties;
    }
    /**
     * provides access to admin services
     */
    protected AdminClientService adminClientService;
    /**
     * used to reconnect to the server after disconnection
     */
    private Timer reconnectTimer;
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
     * provides access to UI message services
     */
    @Autowired
    protected UiMessageService uiMessageService;
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
     * event which signaled the view to be opened
     */
    private final NewWindowEvent newWindowEvent;
    /**
     * parent window that owns the view
     */
    private final Region parentWindow;
    /**
     * properties used to seed the view
     */
    private final Properties viewProperties;
    /**
     * holds the name of this service
     */
    protected String viewName;
    /**
     * listens for broker status changes
     */
    private BrokerStatusListener brokerStatusListener;
}
