package org.marketcetera.web.trade.view;

import java.util.Map;
import java.util.Properties;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.StyleService;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.ContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
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
        extends VerticalLayout
        implements ContentView,BrokerStatusListener
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        synchronized(sessionRows) {
            adminClientService.getFixSessions().forEach(activeFixSession-> {
                handleFixSession(activeFixSession);}
            );
        }
        adminClientService.addBrokerStatusListener(this);
        styleService.addStyle(this);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        serviceManager.getService(AdminClientService.class).removeBrokerStatusListener(this);
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
    private void handleFixSession(ActiveFixSession inActiveFixSession)
    {
        SLF4JLoggerProxy.trace(this,
                               "Incoming broker status: {}",
                               inActiveFixSession);
        SessionRow sessionRow = sessionRows.get(inActiveFixSession.getFixSession().getBrokerId());
        if(sessionRow == null) {
            sessionRow = new SessionRow(inActiveFixSession);
            addComponent(sessionRow.fixSessionLayout);
            sessionRows.put(inActiveFixSession.getFixSession().getBrokerId(),
                            sessionRow);
        }
        sessionRow.setFixSession(inActiveFixSession);
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
    private class SessionRow
    {
        /**
         * Sets the fixSession value.
         *
         * @param inFixSession a <code>ActiveFixSession</code> value
         */
        private void setFixSession(ActiveFixSession inFixSession)
        {
            fixSession = inFixSession;
            FixSessionStatus fixSessionStatus = inFixSession.getStatus();
            statusLabel.setDescription(fixSessionStatus.name());
        }
        /**
         * Create a new SessionRow instance.
         *
         * @param inActiveFixSession an <code>ActiveFixSession</code> value
         */
        private SessionRow(ActiveFixSession inActiveFixSession)
        {
            fixSession = inActiveFixSession;
            statusGrid = new Grid();
            statusGrid.addColumn("status",Image.class);
            statusGrid.addColumn("name",String.class);
            styleService.addStyle(statusGrid);
//            fixSessionLabel = new Label(fixSession.getFixSession().getName());
//            statusLabel = new Image(null,
//                                    new ThemeResource("green.png"));
            fixSessionLayout = new HorizontalLayout();
//            fixSessionLayout.addComponents(fixSessionLabel,
//                                           statusLabel);
//            styleService.addStyle(fixSessionLabel);
//            styleService.addStyle(statusLabel);
            styleService.addStyle(fixSessionLayout);
            fixSessionLayout.addComponent(statusGrid);
        }
        private final Grid statusGrid;
        private final HorizontalLayout fixSessionLayout;
        private Label fixSessionLabel;
        private Image statusLabel;
        private ActiveFixSession fixSession;
    }
    private final Map<String,SessionRow> sessionRows = Maps.newHashMap();
    /**
     * parent window opened for the content
     */
    private final Window parent;
    /**
     * new window event that caused the view to be opened
     */
    private final NewWindowEvent event;
    /**
     * properties that initialize this view
     */
    private final Properties viewProperties;
    /**
     * global name of this view
     */
    private static final String NAME = "Session Status View";
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
