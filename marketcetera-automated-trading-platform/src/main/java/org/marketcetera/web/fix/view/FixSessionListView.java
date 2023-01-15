package org.marketcetera.web.fix.view;

import java.util.Collection;
import java.util.Map;

import javax.annotation.security.PermitAll;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.impl.SimpleActiveFixSession;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.fixadmin.FixAdminClientService;
import org.marketcetera.web.view.AbstractListView;
import org.marketcetera.webui.views.MainLayout;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;

@PermitAll
@PageTitle("FIX Sessions | MATP")
@Route(value="fix", layout = MainLayout.class) 
public class FixSessionListView
        extends AbstractListView<SimpleActiveFixSession,FixSessionListView.FixSessionForm>
        implements BrokerStatusListener
{
    /**
     * Create a new UserListView instance.
     */
    public FixSessionListView()
    {
        super(SimpleActiveFixSession.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
        getUi().access(new Command() {
            @Override
            public void execute()
            {
                try {
                    updateList();
                    getUi().push();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(FixSessionListView.this,
                                          e);
                }
            }
            private static final long serialVersionUID = 8842410404962613192L;
            }
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#setColumns(com.vaadin.flow.component.grid.Grid)
     */
    @Override
    protected void setColumns(Grid<SimpleActiveFixSession> inGrid)
    {
        inGrid.setColumns("fixSession.name",
                          "fixSession.description",
                          "fixSession.sessionId",
                          "fixSession.brokerId",
                          "fixSession.affinity",
                          "status",
                          "clusterData",
                          "senderSequenceNumber",
                          "targetSequenceNumber");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createNewValue()
     */
    @Override
    protected SimpleActiveFixSession createNewValue()
    {
        return new SimpleActiveFixSession();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getUpdatedList()
     */
    @Override
    protected Collection<SimpleActiveFixSession> getUpdatedList()
    {
        Collection<SimpleActiveFixSession> fixSessions = Lists.newArrayList();
        getServiceClient().getFixSessions().forEach(fixSession -> fixSessions.add((fixSession instanceof SimpleActiveFixSession ? (SimpleActiveFixSession)fixSession : new SimpleActiveFixSession(fixSession))));
        return fixSessions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doCreate(java.lang.Object)
     */
    @Override
    protected void doCreate(SimpleActiveFixSession inValue)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doUpdate(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doUpdate(SimpleActiveFixSession inValue,
                            Map<String,Object> inValueKeyData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doDelete(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doDelete(SimpleActiveFixSession inValue,
                            Map<String,Object> inValueKeyData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#registerInitialValue(java.lang.Object, java.util.Map)
     */
    @Override
    protected void registerInitialValue(SimpleActiveFixSession inValue,
                                        Map<String,Object> inOutValueKeyData)
    {
        inOutValueKeyData.put("name",
                              inValue.getFixSession().getName());
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.Component#onAttach(com.vaadin.flow.component.AttachEvent)
     */
    @Override
    protected void onAttach(AttachEvent inAttachEvent)
    {
        super.onAttach(inAttachEvent);
        getServiceClient().addBrokerStatusListener(this);
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.Component#onDetach(com.vaadin.flow.component.DetachEvent)
     */
    @Override
    protected void onDetach(DetachEvent inDetachEvent)
    {
        getServiceClient().removeBrokerStatusListener(this);
        super.onDetach(inDetachEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createForm()
     */
    @Override
    protected FixSessionForm createForm()
    {
        form = new FixSessionForm();
        return form;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getDataClazzName()
     */
    @Override
    protected String getDataClazzName()
    {
        return "FIX Session";
    }
    /**
     * Get the service client to use for this view.
     *
     * @return a <code>FixAdminClientService</code> value
     */
    private FixAdminClientService getServiceClient()
    {
        return ServiceManager.getInstance().getService(FixAdminClientService.class);
    }
    /**
     * Provides the create/edit subform for users.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    class FixSessionForm
            extends AbstractListView<SimpleActiveFixSession,FixSessionForm>.AbstractListForm
    {
        /**
         * Create a new UserForm instance.
         */
        private FixSessionForm()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.AbstractListView.AbstractListForm#createFormComponentLayout(com.vaadin.flow.data.binder.Binder)
         */
        @Override
        protected Component createFormComponentLayout(Binder<SimpleActiveFixSession> inBinder)
        {
            name = new TextField("Name"); 
            description = new TextField("Description");
            name.setEnabled(true);
            name.setReadOnly(false);
            description.setEnabled(true);
            description.setReadOnly(false);
            componentLayout = new VerticalLayout();
            componentLayout.add(name,
                                description);
            return componentLayout;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.AbstractListView.AbstractListForm#useBinder()
         */
        @Override
        protected boolean useBinder()
        {
            return false;
        }
        /**
         * name widget
         */
        private TextField name;
        /**
         * description widget
         */
        private TextField description;
        /**
         * editor components layout value
         */
        private VerticalLayout componentLayout;
        private static final long serialVersionUID = -1931251254078030265L;
    }
    /**
     * edit form instance
     */
    private FixSessionForm form;
    private static final long serialVersionUID = 516637620331918587L;
}
