package org.marketcetera.web.fix.view;

import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.annotation.security.PermitAll;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionDay;
import org.marketcetera.fix.FixSessionInstanceData;
import org.marketcetera.fix.impl.SimpleFixSessionAttributeDescriptor;
import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.fixadmin.FixAdminClientService;
import org.marketcetera.web.view.AbstractListView;
import org.marketcetera.webui.views.MainLayout;
import org.vaadin.teemu.wizards.AbstractWizardProgressListener;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;

import quickfix.FixVersions;
import quickfix.Session;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a FIX Session view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * 
 * TODO stopping session doesn't send status update from server
 * TODO status update from server makes edit form disappear
 * TODO sequence numbers don't update
 * TODO shouldn't enable edit unless session is disabled
 * TODO no sequence number update yet
 */
@PermitAll
@PageTitle("FIX Sessions | MATP")
@Route(value="fix", layout = MainLayout.class) 
public class FixSessionListView
        extends AbstractListView<DisplayFixSession,FixSessionListView.FixSessionForm>
        implements BrokerStatusListener
{
    /**
     * Create a new UserListView instance.
     */
    public FixSessionListView()
    {
        super(DisplayFixSession.class);
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
                    // not ideal, but, calling updateList causes the edit form to disappear
                    if(!form.isVisible()) {
                        updateList();
                        getUi().push();
                    }
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
    protected void setColumns(Grid<DisplayFixSession> inGrid)
    {
        inGrid.setColumns("name",
                          "description",
                          "sessionId",
                          "brokerId",
                          "affinity",
                          "status",
                          "clusterData",
                          "senderSequenceNumber",
                          "targetSequenceNumber");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createNewValue()
     */
    @Override
    protected DisplayFixSession createNewValue()
    {
        return new DisplayFixSession();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getUpdatedList()
     */
    @Override
    protected Collection<DisplayFixSession> getUpdatedList()
    {
        Collection<DisplayFixSession> fixSessions = Lists.newArrayList();
        getServiceClient().getFixSessions().forEach(fixSession -> fixSessions.add(DisplayFixSession.create(fixSession)));
        return fixSessions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doCreate(java.lang.Object)
     */
    @Override
    protected void doCreate(DisplayFixSession inValue)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doUpdate(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doUpdate(DisplayFixSession inValue,
                            Map<String,Object> inValueKeyData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doDelete(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doDelete(DisplayFixSession inValue,
                            Map<String,Object> inValueKeyData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#registerInitialValue(java.lang.Object, java.util.Map)
     */
    @Override
    protected void registerInitialValue(DisplayFixSession inValue,
                                        Map<String,Object> inOutValueKeyData)
    {
        inOutValueKeyData.put("name",
                              inValue.getName());
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.Component#onAttach(com.vaadin.flow.component.AttachEvent)
     */
    @Override
    protected void onAttach(AttachEvent inAttachEvent)
    {
        super.onAttach(inAttachEvent);
        getServiceClient().addBrokerStatusListener(this);
        getGrid().addSelectionListener(inEvent -> {
            DisplayFixSession selectedObject = getSelectedItem();
            getActionComboBox().setItems(Lists.newArrayList());
            if(selectedObject == null) {
                getActionComboBox().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionComboBox().setReadOnly(false);
                // adjust the available actions based on the status of the selected row
                switch(selectedObject.getStatus()) {
                    case CONNECTED:
                    case DISCONNECTED:
                    case NOT_CONNECTED:
                        getActionComboBox().setItems(Lists.newArrayList(ACTION_STOP));
                        break;
                    case DISABLED:
                        getActionComboBox().setItems(Lists.newArrayList(ACTION_ENABLE,
                                                                        ACTION_SEQUENCE,
                                                                        ACTION_EDIT,
                                                                        ACTION_DELETE));
                        break;
                    case STOPPED:
                        getActionComboBox().setItems(Lists.newArrayList(ACTION_START,
                                                                        ACTION_DISABLE,
                                                                        ACTION_SEQUENCE));
                        break;
                    case AFFINITY_MISMATCH:
                    case BACKUP:
                    case DELETED:
                    case UNKNOWN:
                    default:
                        // nothing available, these are essentially weird statuses for display
                        break;
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractListView#actionValueChanged(com.vaadin.flow.component.HasValue.ValueChangeEvent)
     */
    @Override
    protected void actionValueChanged(ValueChangeEvent<String> inEvent)
    {
        DisplayFixSession target = getGrid().asSingleSelect().getValue();
        String action = inEvent.getValue();
        if(target == null || action == null) {
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Action '{}' invoked on {}",
                               inEvent.getValue(),
                               target);
        switch(action) {
            case ACTION_STOP:
                getServiceClient().stopSession(target.getName());
                updateList();
                break;
            case ACTION_START:
                getServiceClient().startSession(target.getName());
                updateList();
                break;
            case ACTION_DISABLE:
                getServiceClient().disableSession(target.getName());
                updateList();
                break;
            case ACTION_ENABLE:
                getServiceClient().enableSession(target.getName());
                updateList();
                break;
            case ACTION_EDIT:
                addOrEditFormValue(target,
                                   form,
                                   false);
                break;
            case ACTION_DELETE:
                getServiceClient().deleteSession(target.getName());
                updateList();
                break;
            case ACTION_SEQUENCE:
                updateSequenceNumbers(target);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
        super.actionValueChanged(inEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractListView#addGridValueChangeListener(com.vaadin.flow.component.grid.Grid)
     */
    @Override
    protected void addGridValueChangeListener(Grid<DisplayFixSession> inGrid)
    {
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
     * Update the sequence numbers for the given session.
     *
     * @param inFixSession a <code>DisplayFixSession</code> value
     */
    private void updateSequenceNumbers(DisplayFixSession inFixSession)
    {
        Binder<DisplayFixSession> sequenceBinder = new BeanValidationBinder<>(DisplayFixSession.class);
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        TextField senderSequenceNumber = new TextField("Sender Sequence Number");
        TextField targetSequenceNumber = new TextField("Target Sequence Number");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        senderSequenceNumber.setTooltipText("The sequence number of the next sender message");
        targetSequenceNumber.setTooltipText("The sequence number of the next target message");
        senderSequenceNumber.addBlurListener(event -> {
            okButton.setEnabled(!senderSequenceNumber.isInvalid() && !targetSequenceNumber.isInvalid());
        });
        targetSequenceNumber.addBlurListener(event -> {
            okButton.setEnabled(!senderSequenceNumber.isInvalid() && !targetSequenceNumber.isInvalid());
        });
        sequenceBinder.forField(senderSequenceNumber).asRequired("Sender Sequence Number Required").withValidator((senderValue,inContext) -> {
            try {
                Integer.parseInt(String.valueOf(senderValue));
            } catch (Exception e) {
                return ValidationResult.error(ExceptionUtils.getRootCauseMessage(e));
            }
            return ValidationResult.ok();
        }).withConverter(new Converter<String,Integer>(){
            private static final long serialVersionUID = 2261243139927742849L;
            @Override
            public Result<Integer> convertToModel(String inValue,
                                                  ValueContext inContext)
            {
                try {
                    return Result.ok(Integer.parseInt(String.valueOf(inValue)));
                } catch (Exception e) {
                    return Result.error(ExceptionUtils.getRootCauseMessage(e));
                }
            }
            @Override
            public String convertToPresentation(Integer inValue,
                                                ValueContext inContext)
            {
                return String.valueOf(inValue);
                
            }}).bind("senderSequenceNumber");
        sequenceBinder.forField(targetSequenceNumber).asRequired("Target Sequence Number Required").withValidator((targetValue,inContext) -> {
            try {
                Integer.parseInt(String.valueOf(targetValue));
            } catch (Exception e) {
                return ValidationResult.error(ExceptionUtils.getRootCauseMessage(e));
            }
            return ValidationResult.ok();
        }).withConverter(new Converter<String,Integer>(){
            private static final long serialVersionUID = 2261243139927742849L;
            @Override
            public Result<Integer> convertToModel(String inValue,
                                                  ValueContext inContext)
            {
                try {
                    return Result.ok(Integer.parseInt(String.valueOf(inValue)));
                } catch (Exception e) {
                    return Result.error(ExceptionUtils.getRootCauseMessage(e));
                }
            }
            @Override
            public String convertToPresentation(Integer inValue,
                                                ValueContext inContext)
            {
                return String.valueOf(inValue);
                
            }}).bind("targetSequenceNumber");
        sequenceBinder.readBean(inFixSession);
        sequenceBinder.bindInstanceFields(inFixSession);
        okButton.setEnabled(false);
        buttonLayout.add(cancelButton,
                         okButton);
        layout.add(senderSequenceNumber,
                   targetSequenceNumber,
                   buttonLayout);
        Dialog displayWindow = new Dialog();
        okButton.addClickListener(event -> {
            try {
                sequenceBinder.writeBean(inFixSession);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            getServiceClient().updateSequenceNumbers(inFixSession.getName(),
                                                     inFixSession.getSenderSequenceNumber(),
                                                     inFixSession.getTargetSequenceNumber());
            displayWindow.close();
            updateList();
        });
        cancelButton.addClickListener(event -> {
            displayWindow.close();
        });
        displayWindow.setCloseOnEsc(true);
        displayWindow.setCloseOnOutsideClick(false);
        displayWindow.setDraggable(true);
        displayWindow.setHeaderTitle("Set Sequence Numbers");
        displayWindow.setModal(true);
        displayWindow.setResizable(true);
        displayWindow.add(layout);
        displayWindow.open();
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
            extends AbstractListView<DisplayFixSession,FixSessionForm>.AbstractListForm
    {
        /**
         * Create a new UserForm instance.
         */
        private FixSessionForm()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.AbstractListView.AbstractListForm#useDefaultButtons()
         */
        @Override
        protected boolean useDefaultButtons()
        {
            return false;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.AbstractListView.AbstractListForm#setValue(java.lang.Object, boolean)
         */
        @Override
        protected void setValue(DisplayFixSession inValue,
                                boolean inIsAdd)
        {
            fixSessionValue = inValue;
            super.setValue(inValue,
                           inIsAdd);
            if(fixSessionValue != null) {
                setupWizard(inIsAdd);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.AbstractListView.AbstractListForm#createFormComponentLayout(com.vaadin.flow.data.binder.Binder)
         */
        @Override
        protected Component createFormComponentLayout(Binder<DisplayFixSession> inBinder)
        {
            componentLayout = new VerticalLayout();
            return componentLayout;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.AbstractListView.AbstractListForm#useBinder()
         */
        @Override
        protected boolean useBinder()
        {
            return true;
        }
        private void setupWizard(boolean inIsNew)
        {
            final SortedMap<String,DecoratedDescriptor> sortedDescriptors = new TreeMap<>();
            final String incomingName = fixSessionValue.getName();
            Collection<FixSessionAttributeDescriptor> descriptors = getServiceClient().getFixSessionAttributeDescriptors();
            for(FixSessionAttributeDescriptor descriptor : descriptors) {
                DecoratedDescriptor actualDescriptor = new DecoratedDescriptor(descriptor);
                sortedDescriptors.put(descriptor.getName(),
                                      actualDescriptor);
            }
            if(!inIsNew) {
                for(DecoratedDescriptor descriptor : sortedDescriptors.values()) {
                    String existingValue = fixSessionValue.getSessionSettings().get(descriptor.getName());
                    if(existingValue == null) {
                        descriptor.setValue(null);
                    } else {
                        descriptor.setValue(existingValue);
                    }
                }
            }
            Wizard fixSessionWizard = new Wizard();
            fixSessionWizard.addListener(new AbstractWizardProgressListener() {
                public void activeStepChanged(WizardStepActivationEvent inEvent)
                {
                }
                public void stepSetChanged(WizardStepSetChangedEvent inEvent)
                {
                }
                public void wizardCompleted(WizardCompletedEvent inEvent)
                {
                }
                public void wizardCancelled(WizardCancelledEvent inEvent)
                {
                    closeEditor();
                    updateList();
                }
                private static final long serialVersionUID = 7914059294191603115L;
            });
            fixSessionWizard.addStep(new WizardStep() {
                @Override
                public String getCaption()
                {
                    return "Type";
                }
                @Override
                public Component getContent()
                {
                    FormLayout formLayout = new FormLayout();
                    formLayout.setSizeFull();
                    initializeFields();
                    connectionType.setItems(DisplayFixSession.ACCEPTOR,
                                            DisplayFixSession.INITIATOR);
                    connectionType.setTooltipText("Indicates whether the session will receive orders (acceptor) or send them (initiator)");
                    getBinder().forField(connectionType).asRequired("Connection type required").bind("connectionType");
                    affinity.setTooltipText("Indicates which cluster instance will host this session, if unsure, leave as 1");
                    getBinder().forField(affinity).asRequired("Affinity Required").withValidator((affinityValue,inContext) -> {
                        try {
                            Integer.parseInt(String.valueOf(affinityValue));
                        } catch (Exception e) {
                            return ValidationResult.error(ExceptionUtils.getRootCauseMessage(e));
                        }
                        return ValidationResult.ok();
                    }).withConverter(new Converter<String,Integer>(){
                        private static final long serialVersionUID = 2261243139927742849L;
                        @Override
                        public Result<Integer> convertToModel(String inValue,
                                                              ValueContext inContext)
                        {
                            try {
                                return Result.ok(Integer.parseInt(String.valueOf(inValue)));
                            } catch (Exception e) {
                                return Result.error(ExceptionUtils.getRootCauseMessage(e));
                            }
                        }
                        @Override
                        public String convertToPresentation(Integer inValue,
                                                            ValueContext inContext)
                        {
                            return String.valueOf(inValue);
                            
                        }}).bind("affinity");
                    updateFields();
                    formLayout.add(connectionType,
                                   affinity);
                    return formLayout;
                }
                @Override
                public boolean onAdvance()
                {
                    updateFixSession();
                    return !connectionType.isInvalid() && !affinity.isInvalid();
                }
                @Override
                public boolean onBack()
                {
                    return false;
                }
                /**
                 * Initialize the UI widgets.
                 */
                private void initializeFields()
                {
                    if(inIsNew) {
                        fixSessionValue.setConnectionType(DisplayFixSession.INITIATOR);
                        fixSessionValue.setAffinity(1);
                    } else {
                        if(fixSessionValue.getConnectionType() != null) {
                            connectionType.setValue(fixSessionValue.getConnectionType());
                        }
                        affinity.setValue(String.valueOf(fixSessionValue.getAffinity()));
                    }
                    connectionType.setVisible(true);
                    affinity.setVisible(true);
                }
                /**
                 * Update the UI fields from the FIX session variable.
                 */
                private void updateFields()
                {
                    connectionType.setValue(fixSessionValue.getConnectionType());
                    affinity.setValue(String.valueOf(fixSessionValue.getAffinity()));
                }
                /**
                 * Update the FIX session variable from the UI fields.
                 */
                private void updateFixSession()
                {
                    fixSessionValue.setConnectionType(connectionType.getValue());
                    fixSessionValue.setAffinity(Integer.parseInt(affinity.getValue()));
                }
            });
            fixSessionWizard.addStep(new WizardStep() {
                @Override
                public String getCaption()
                {
                    return "Network";
                }
                @Override
                public Component getContent()
                {
                    FormLayout formLayout = new FormLayout();
                    formLayout.setSizeFull();
                    testConnectionLabel.setVisible(false);
                    if(hostname.isEmpty()) {
                        testConnectionButton.setEnabled(true);
                    } else {
                        testConnectionButton.setEnabled(false);
                    }
                    testConnectionButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                        @Override
                        public void onComponentEvent(ClickEvent<Button> inEvent)
                        {
                            testConnectionLabel.setText("");
                            testConnectionLabel.setClassName(ButtonVariant.LUMO_SUCCESS.getVariantName());
                            testConnectionLabel.setVisible(false);
                            try(Socket s = new Socket(StringUtils.trimToNull(hostname.getValue()),
                                                      Integer.parseInt(StringUtils.trim(port.getValue())))) {
                                testConnectionLabel.setText("Test connection success");
                                testConnectionLabel.setClassName(ButtonVariant.LUMO_SUCCESS.getVariantName());
                            } catch (Exception e) {
                                testConnectionLabel.setText("Test connection failed: " + ExceptionUtils.getRootCauseMessage(e));
                                testConnectionLabel.setClassName(ButtonVariant.LUMO_ERROR.getVariantName());
                            }
                            testConnectionLabel.setVisible(true);
                        }
                        private static final long serialVersionUID = -6990373053475044911L;
                    });
                    if(fixSessionValue.isAcceptor()) {
                        FixSessionInstanceData instanceData = getServiceClient().getFixSessionInstanceData(fixSessionValue.getAffinity());
                        hostname.setValue(instanceData.getHostname());
                        hostname.setReadOnly(true);
                        hostname.setTooltipText("The acceptor hostname is determined by the server and is not modifiable");
                        port.setValue(String.valueOf(instanceData.getPort()));
                        port.setTooltipText("The acceptor port is determined by the server cluster framework and is not modifiable");
                        port.setReadOnly(true);
                        testConnectionButton.setVisible(false);
                    } else {
                        hostname.setReadOnly(false);
                        hostname.setTooltipText("Hostname of the FIX gateway to connect to");
                        hostname.setValue(fixSessionValue.getHostname()==null?"exchange.marketcetera.com":fixSessionValue.getHostname());
                        getBinder().forField(hostname).asRequired("Hostname Required").withValidator((hostnameValue,inContext) -> {
                            return ValidationResult.ok();
                        }).bind("hostname");
                        hostname.addValueChangeListener(inEvent -> {
                            if(!hostname.isEmpty()) {
                                testConnectionButton.setEnabled(false);
                            }
                        });
                        port.setReadOnly(false);
                        port.setTooltipText("Port of the FIX gateway to connect to");
                        port.setValue(fixSessionValue.getPort()==0?"7001":String.valueOf(fixSessionValue.getPort()));
                        getBinder().forField(port).asRequired("Port is required").withValidator((portValue,inContext) -> {
                            try {
                                Integer.parseInt(String.valueOf(portValue));
                            } catch (Exception e) {
                                return ValidationResult.error(ExceptionUtils.getRootCauseMessage(e));
                            }
                            return ValidationResult.ok();
                        }).withConverter(new Converter<String,Integer>(){
                            private static final long serialVersionUID = 2261243139927742849L;
                            @Override
                            public Result<Integer> convertToModel(String inValue,
                                                                  ValueContext inContext)
                            {
                                try {
                                    return Result.ok(Integer.parseInt(String.valueOf(inValue)));
                                } catch (Exception e) {
                                    return Result.error(ExceptionUtils.getRootCauseMessage(e));
                                }
                            }
                            @Override
                            public String convertToPresentation(Integer inValue,
                                                                ValueContext inContext)
                            {
                                return String.valueOf(inValue);
                                
                            }}).bind("port");
                        testConnectionButton.setVisible(true);
                    }
                    initializeFields();
                    updateFields();
                    formLayout.add(hostname,
                                   port,
                                   testConnectionButton,
                                   testConnectionLabel);
                    return formLayout;
                }
                @Override
                public boolean onAdvance()
                {
                    updateFixSession();
                    return !hostname.isInvalid() && !port.isInvalid();
                }
                @Override
                public boolean onBack()
                {
                    return true;
                }
                /**
                 * Initialize the UI widgets.
                 */
                private void initializeFields()
                {
                    if(fixSessionValue.isAcceptor()) {
                        FixSessionInstanceData instanceData = getServiceClient().getFixSessionInstanceData(fixSessionValue.getAffinity());
                        hostname.setValue(instanceData.getHostname());
                        hostname.setReadOnly(true);
                        hostname.setTooltipText("The acceptor hostname is determined by the server and is not modifiable");
                        port.setValue(String.valueOf(instanceData.getPort()));
                        port.setTooltipText("The acceptor port is determined by the server cluster framework and is not modifiable");
                        port.setReadOnly(true);
                        testConnectionButton.setVisible(false);
                    } else {
                        hostname.setReadOnly(false);
                        hostname.setTooltipText("Hostname of the FIX gateway to connect to");
                        hostname.setValue(fixSessionValue.getHostname()==null?"exchange.marketcetera.com":fixSessionValue.getHostname());
                        getBinder().forField(hostname).asRequired("Hostname is required").withValidator((hostnameValue,inContext) -> {
                            hostnameValue = StringUtils.trimToNull(hostnameValue);
                            if(hostnameValue == null) {
                                return ValidationResult.error("Hostname is required");
                            } else {
                                return ValidationResult.ok();
                            }
                        }).bind("hostname");
                        hostname.addValueChangeListener(inEvent -> {
                            if(!hostname.isEmpty()) {
                                testConnectionButton.setEnabled(false);
                            }
                        });
                        port.setReadOnly(false);
                        port.setTooltipText("Port of the FIX gateway to connect to");
                        port.setValue(fixSessionValue.getPort()==0?"7001":String.valueOf(fixSessionValue.getPort()));
                        port.setRequired(true);
                        getBinder().forField(port).asRequired("Port is required").withValidator((portValue,inContext) -> {
                            try {
                                Integer.parseInt(String.valueOf(portValue));
                            } catch (Exception e) {
                                return ValidationResult.error(ExceptionUtils.getRootCauseMessage(e));
                            }
                            return ValidationResult.ok();
                        }).withConverter(new Converter<String,Integer>(){
                            private static final long serialVersionUID = 2261243139927742849L;
                            @Override
                            public Result<Integer> convertToModel(String inValue,
                                                                  ValueContext inContext)
                            {
                                try {
                                    return Result.ok(Integer.parseInt(String.valueOf(inValue)));
                                } catch (Exception e) {
                                    return Result.error(ExceptionUtils.getRootCauseMessage(e));
                                }
                            }
                            @Override
                            public String convertToPresentation(Integer inValue,
                                                                ValueContext inContext)
                            {
                                return String.valueOf(inValue);
                                
                            }}).bind("port");
                        testConnectionButton.setVisible(true);
                    }
                }
                /**
                 * Update the UI fields from the FIX session variable.
                 */
                private void updateFields()
                {
                }
                /**
                 * Update the FIX session variable from the UI fields.
                 */
                private void updateFixSession()
                {
                    fixSessionValue.setHostname(hostname.getValue());
                    fixSessionValue.setPort(Integer.parseInt(port.getValue()));
                }
                /**
                 * connection button widget
                 */
                private Button testConnectionButton = new Button("Test Connection");
                /**
                 * test connection results widget
                 */
                private Label testConnectionLabel = new Label();
            });
            fixSessionWizard.addStep(new WizardStep() {
                @Override
                public String getCaption()
                {
                    return "Identity";
                }
                @Override
                public Component getContent()
                {
                    // gather a list of the existing sessions, we'll use this to do some basic validation about session identity
                    final Collection<ActiveFixSession> existingSessions = FixAdminClientService.getInstance().getFixSessions();
                    FormLayout formLayout = new FormLayout();
                    formLayout.setSizeFull();
                    name.setTooltipText("Unique human-readable name of the session");
                    getBinder().forField(name).asRequired("Name is required").withValidator((nameValue,inContext) -> {
                        String computedValue = StringUtils.trimToNull(String.valueOf(nameValue));
                        for(ActiveFixSession existingSession : existingSessions) {
                            if(inIsNew && existingSession.getFixSession().getName().equals(computedValue)) {
                                return ValidationResult.error("'"+existingSession.getFixSession().getName() + "' is already in use");
                            }
                        }
                        if(computedValue.length() > 255) {
                            return ValidationResult.error("Name may contain up to 255 characters");
                        }
                        if(!NDEntityBase.namePattern.matcher(computedValue).matches()) {
                            return ValidationResult.error("Names may contain up to 255 letters, numbers, spaces, or the dash char ('-')");
                        }
                        return ValidationResult.ok();
                    }).bind("name");
                    description.setTooltipText("Optional description of the session");
                    getBinder().forField(description).withValidator((descriptionValue,inContext) -> {
                        String computedValue = StringUtils.trimToNull(String.valueOf(descriptionValue));
                        if(computedValue != null && computedValue.length() > 255) {
                            return ValidationResult.error("Description may contain up to 255 characters");
                        }
                        return ValidationResult.ok();
                    }).bind("description");
                    brokerId.setTooltipText("Unique system identifier for this FIX session used to target orders, pick something short and descriptive");
                    brokerId.setRequired(true);
                    getBinder().forField(brokerId).asRequired("Broker Id required").withValidator((brokerIdValue,inContext) -> {
                        String computedValue = StringUtils.trimToNull(String.valueOf(brokerIdValue));
                        for(ActiveFixSession existingSession : existingSessions) {
                            if(inIsNew && existingSession.getFixSession().getBrokerId().equals(computedValue)) {
                                return ValidationResult.error("'"+existingSession.getFixSession().getBrokerId() + "' is already in use");
                            }
                        }
                        return ValidationResult.ok();
                    }).bind("brokerId");
                    // build the FIX Session ID from three components
                    fixVersion.setTooltipText("FIX version of the session");
                    fixVersion.setAllowCustomValue(false);
                    fixVersion.setItemLabelGenerator(inItem -> inItem.name());
                    getBinder().forField(fixVersion).asRequired("FIX Version required").withValidator((inValue,inContext) -> {
                        String fixVersionValue = inValue == null ? null : inValue.name();
                        SessionID sessionId = new SessionID(fixVersionValue,
                                                            senderCompIdTextField.getValue(),
                                                            targetCompIdTextField.getValue());
                        for(ActiveFixSession existingSession : existingSessions) {
                            if(inIsNew && existingSession.getFixSession().getSessionId().equals(sessionId.toString())) {
                                return ValidationResult.error("'"+existingSession.getFixSession().getSessionId() + "' is already in use");
                            }
                        }
                        return ValidationResult.ok();
                    }).bind("fixVersion");
                    senderCompIdTextField.setTooltipText("Sender Comp Id of the session");
                    senderCompIdTextField.setRequired(true);
                    senderCompIdTextField.setErrorMessage("Sender Comp Id required");
                    targetCompIdTextField.setTooltipText("Target Comp Id of the session");
                    targetCompIdTextField.setRequired(true);
                    targetCompIdTextField.setErrorMessage("Target Comp Id required");
                    List<FIXVersion> fixVersions = Lists.newArrayList();
                    for(FIXVersion fixVersion : FIXVersion.values()) {
                        if(fixVersion == FIXVersion.FIX_SYSTEM) {
                            continue;
                        }
                        fixVersions.add(fixVersion);
                    }
                    fixVersion.setItems(fixVersions);
                    initializeFields();
                    updateFields();
                    formLayout.add(name,
                                   description,
                                   brokerId,
                                   fixVersion,
                                   senderCompIdTextField,
                                   targetCompIdTextField);
                    return formLayout;
                }
                @Override
                public boolean onAdvance()
                {
                    updateFixSession();
                    return !name.isInvalid() && !description.isInvalid() && !brokerId.isInvalid() &&
                            !fixVersion.isInvalid() && !senderCompIdTextField.isInvalid() && !targetCompIdTextField.isInvalid();
                }
                @Override
                public boolean onBack()
                {
                    return true;
                }
                /**
                 * Initialize the UI widgets.
                 */
                private void initializeFields()
                {
                    name.setValue(fixSessionValue.getName()==null?"New Session":fixSessionValue.getName());
                    description.setValue(fixSessionValue.getDescription()==null?"New Session Description":fixSessionValue.getDescription());
                    brokerId.setValue(fixSessionValue.getBrokerId()==null?"new-broker":fixSessionValue.getBrokerId());
                    if(fixSessionValue.getSessionId() != null) {
                        SessionID sessionId = new quickfix.SessionID(fixSessionValue.getSessionId());
                        if(sessionId.isFIXT()) {
                            String defaultApplVerId = fixSessionValue.getSessionSettings().get(Session.SETTING_DEFAULT_APPL_VER_ID);
                            fixVersion.setValue(FIXVersion.getFIXVersion(new quickfix.field.ApplVerID(defaultApplVerId)));
                        } else {
                            fixVersion.setValue(FIXVersion.getFIXVersion(sessionId));
                        }
                        senderCompIdTextField.setValue(sessionId.getSenderCompID());
                        targetCompIdTextField.setValue(sessionId.getTargetCompID());
                    } else {
                        senderCompIdTextField.setValue("MATP");
                        targetCompIdTextField.setValue("MRKTC-EXCH");
                        fixVersion.setValue(FIXVersion.FIX42);
                    }
                }
                /**
                 * Update the UI fields from the FIX session variable.
                 */
                private void updateFields()
                {
                }
                /**
                 * Update the FIX session variable from the UI fields.
                 */
                private void updateFixSession()
                {
                    fixSessionValue.setName(name.getValue());
                    fixSessionValue.setDescription(description.getValue());
                    fixSessionValue.setBrokerId(brokerId.getValue());
                    String fixVersionValue = fixVersion.getValue() == null ? null : String.valueOf(fixVersion.getValue());
                    FIXVersion fixVersion = FIXVersion.getFIXVersion(fixVersionValue);
                    if(fixVersion.isFixT()) {
                        fixSessionValue.getSessionSettings().put(Session.SETTING_DEFAULT_APPL_VER_ID,
                                                              fixVersionValue);
                        DecoratedDescriptor defaultApplVerId = sortedDescriptors.get(Session.SETTING_DEFAULT_APPL_VER_ID);
                        defaultApplVerId.setValue(fixVersionValue);
                        fixVersionValue = FixVersions.BEGINSTRING_FIXT11;
                    }
                    SessionID sessionId = new SessionID(fixVersionValue,
                                                        senderCompIdTextField.getValue(),
                                                        targetCompIdTextField.getValue());
                    fixSessionValue.setSessionId(sessionId.toString());
                }
                /**
                 * session name UI widget
                 */
                private TextField name = new TextField("Session Name");
                /**
                 * session description UI widget
                 */
                private TextField description = new TextField("Session Description");
                /**
                 * session broker id UI widget
                 */
                private TextField brokerId = new TextField("Broker Id");
                /**
                 * session FIX version UI widget
                 */
                private ComboBox<FIXVersion> fixVersion = new ComboBox<>("FIX Version");
                /**
                 * session sender comp id UI widget
                 */
                private TextField senderCompIdTextField = new TextField("Sender Comp Id");
                /**
                 * session target comp id UI widget
                 */
                private TextField targetCompIdTextField = new TextField("Target Comp Id");
            });
            fixSessionWizard.addStep(new WizardStep() {
                @Override
                public String getCaption()
                {
                    return "Start and End";
                }
                @Override
                public Component getContent()
                {
                    FormLayout formLayout = new FormLayout();
                    formLayout.setSizeFull();
                    // set up widgets
                    // session type
                    sessionType.setItems(Lists.newArrayList(DisplayFixSession.DAILY,DisplayFixSession.WEEKLY,DisplayFixSession.CONTINUOUS));
                    sessionType.setAllowCustomValue(false);
                    // time zone
                    timezone.setAllowCustomValue(false);
                    timezone.setItems(TimeZone.getAvailableIDs());
                    // start and end day
                    startDay.setAllowCustomValue(false);
                    endDay.setAllowCustomValue(false);
                    startDay.setItems(FixSessionDay.values());
                    endDay.setItems(FixSessionDay.values());
                    startDay.setItemLabelGenerator(item -> item.name());
                    endDay.setItemLabelGenerator(item -> item.name());
                    sessionType.addValueChangeListener(inEvent -> {
                        updateFields();
                    });
                    // set up data
                    initializeFields();
                    updateFields();
                    // start and end time
                    getBinder().forField(startTime).withValidator(new RegexpValidator("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
                                                                                      "Enter a time value in the form 00:00:00")).bind("startTime");
                    getBinder().forField(endTime).withValidator(new RegexpValidator("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
                                                                                    "Enter a time value in the form 00:00:00")).bind("endTime");
                    formLayout.add(sessionType,
                                   startTime,
                                   endTime,
                                   startDay,
                                   endDay,
                                   timezone);
                    return formLayout;
                }
                @Override
                public boolean onAdvance()
                {
                    updateFixSession();
                    if(isContinuousSession(fixSessionValue)) {
                        return !sessionType.isInvalid();
                    }
                    if(isWeeklySession(fixSessionValue)) {
                        return !sessionType.isInvalid() && !startTime.isInvalid() && !endTime.isInvalid() && !timezone.isInvalid() &&
                                !startDay.isInvalid() && !endDay.isInvalid();
                    }
                    return !sessionType.isInvalid() && !startTime.isInvalid() && !endTime.isInvalid() && !timezone.isInvalid();
                }
                @Override
                public boolean onBack()
                {
                    return true;
                }
                /**
                 * Initialize the UI widgets.
                 */
                private void initializeFields()
                {
                    Map<String,String> settings = fixSessionValue.getSessionSettings();
                    startTime.setValue(settings.containsKey(Session.SETTING_START_TIME)?settings.get(Session.SETTING_START_TIME):"00:00:00");
                    endTime.setValue(settings.containsKey(Session.SETTING_END_TIME)?settings.get(Session.SETTING_END_TIME):"00:00:00");
                    startDay.setValue(settings.containsKey(Session.SETTING_START_DAY)?FixSessionDay.valueOf(settings.get(Session.SETTING_START_DAY)):FixSessionDay.Monday);
                    endDay.setValue(settings.containsKey(Session.SETTING_END_DAY)?FixSessionDay.valueOf(settings.get(Session.SETTING_END_DAY)):FixSessionDay.Friday);
                    timezone.setValue(settings.containsKey(Session.SETTING_TIMEZONE)?settings.get(Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
                    startTime.setVisible(true);
                    endTime.setVisible(true);
                    timezone.setVisible(true);
                    startDay.setVisible(true);
                    endDay.setVisible(true);
                    // set default values
                    // now, finalize the setup based on the selected session type
                    String value = settings.get(Session.SETTING_NON_STOP_SESSION);
                    if(DisplayFixSession.YES.equals(value)) {
                        // this is a non-stop session. hide everything but the select
                        startTime.setVisible(false);
                        endTime.setVisible(false);
                        timezone.setVisible(false);
                        startDay.setVisible(false);
                        endDay.setVisible(false);
                        sessionType.setValue(DisplayFixSession.CONTINUOUS);
                    } else {
                        // this is a weekly or daily session
                        value = settings.get(Session.SETTING_START_DAY);
                        if(value != null) {
                            // this is a weekly session, nothing more needs to be done
                            sessionType.setValue(DisplayFixSession.WEEKLY);
                        } else {
                            // this is a daily session, hide the weekly settings
                            startDay.setVisible(false);
                            endDay.setVisible(false);
                            sessionType.setValue(DisplayFixSession.DAILY);
                        }
                    }
                }
                /**
                 * Update the UI fields from the FIX session variable.
                 */
                private void updateFields()
                {
                    Map<String,String> settings = fixSessionValue.getSessionSettings();
                    String value = String.valueOf(sessionType.getValue());
                    switch(value) {
                        case DisplayFixSession.CONTINUOUS:
                            startTime.setVisible(false);
                            endTime.setVisible(false);
                            timezone.setVisible(false);
                            startDay.setVisible(false);
                            endDay.setVisible(false);
                            break;
                        case DisplayFixSession.DAILY:
                            startTime.setVisible(true);
                            startTime.setValue(settings.containsKey(Session.SETTING_START_TIME)?settings.get(Session.SETTING_START_TIME):"00:00:00");
                            endTime.setVisible(true);
                            endTime.setValue(settings.containsKey(Session.SETTING_END_TIME)?settings.get(Session.SETTING_END_TIME):"00:00:00");
                            timezone.setVisible(true);
                            timezone.setValue(settings.containsKey(Session.SETTING_TIMEZONE)?settings.get(Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
                            startDay.setVisible(false);
                            endDay.setVisible(false);
                            break;
                        case DisplayFixSession.WEEKLY:
                            startTime.setVisible(true);
                            startTime.setValue(settings.containsKey(Session.SETTING_START_TIME)?settings.get(Session.SETTING_START_TIME):"00:00:00");
                            endTime.setVisible(true);
                            endTime.setValue(settings.containsKey(Session.SETTING_END_TIME)?settings.get(Session.SETTING_END_TIME):"00:00:00");
                            timezone.setVisible(true);
                            timezone.setValue(settings.containsKey(Session.SETTING_TIMEZONE)?settings.get(Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
                            startDay.setVisible(true);
                            startDay.setValue(settings.containsKey(Session.SETTING_START_DAY)?FixSessionDay.valueOf(settings.get(Session.SETTING_START_DAY)):FixSessionDay.Monday);
                            endDay.setVisible(true);
                            endDay.setValue(settings.containsKey(Session.SETTING_END_DAY)?FixSessionDay.valueOf(settings.get(Session.SETTING_END_DAY)):FixSessionDay.Friday);
                            break;
                    }
                }
                /**
                 * Update the FIX session variable from the UI fields.
                 */
                private void updateFixSession()
                {
                    Map<String,String> settings = fixSessionValue.getSessionSettings();
                    String value = String.valueOf(sessionType.getValue());
                    switch(value) {
                        case DisplayFixSession.CONTINUOUS:
                            settings.remove(Session.SETTING_START_TIME);
                            settings.remove(Session.SETTING_END_TIME);
                            settings.remove(Session.SETTING_START_DAY);
                            settings.remove(Session.SETTING_END_DAY);
                            settings.remove(Session.SETTING_TIMEZONE);
                            settings.put(Session.SETTING_NON_STOP_SESSION,
                                         DisplayFixSession.YES);
                            break;
                        case DisplayFixSession.DAILY:
                            settings.remove(Session.SETTING_START_DAY);
                            settings.remove(Session.SETTING_END_DAY);
                            settings.remove(Session.SETTING_NON_STOP_SESSION);
                            settings.put(Session.SETTING_START_TIME,
                                         startTime.getValue());
                            settings.put(Session.SETTING_END_TIME,
                                         endTime.getValue());
                            settings.put(Session.SETTING_TIMEZONE,
                                         String.valueOf(timezone.getValue()));
                            break;
                        case DisplayFixSession.WEEKLY:
                            settings.remove(Session.SETTING_NON_STOP_SESSION);
                            settings.put(Session.SETTING_START_TIME,
                                         startTime.getValue());
                            settings.put(Session.SETTING_END_TIME,
                                         endTime.getValue());
                            settings.put(Session.SETTING_TIMEZONE,
                                         String.valueOf(timezone.getValue()));
                            settings.put(Session.SETTING_START_DAY,
                                         String.valueOf(startDay.getValue()));
                            settings.put(Session.SETTING_END_DAY,
                                         String.valueOf(endDay.getValue()));
                            break;
                    }
                }
                /**
                 * Indicates if the given session is a weekly session.
                 *
                 * @param inFixSession a <code>DisplayFixSession</code> value
                 * @return a <code>boolean</code> value
                 */
                private boolean isWeeklySession(DisplayFixSession inFixSession)
                {
                    return inFixSession.getSessionSettings().containsKey(Session.SETTING_START_DAY);
                }
                /**
                 * Indicates if the given session is a non-stop, continuous session.
                 *
                 * @param inFixSession a <code>DisplayFixSession</code> value
                 * @return a <code>boolean</code> value
                 */
                private boolean isContinuousSession(DisplayFixSession inFixSession)
                {
                    return DisplayFixSession.YES.equals(inFixSession.getSessionSettings().get(Session.SETTING_NON_STOP_SESSION));
                }
                /**
                 * UI widget for session type
                 */
                private ComboBox<String> sessionType = new ComboBox<>("Session Type");
                /**
                 * UI widget for session start time
                 */
                private TextField startTime = new TextField("Start Time");
                /**
                 * UI widget for session end time
                 */
                private TextField endTime = new TextField("End Time");
                /**
                 * UI widget for time zone
                 */
                private ComboBox<String> timezone = new ComboBox<>("Time Zone");
                /**
                 * UI widget for start day
                 */
                private ComboBox<FixSessionDay> startDay = new ComboBox<>("Start Day");
                /**
                 * UI widget for end day
                 */
                private ComboBox<FixSessionDay> endDay = new ComboBox<>("End Day");
            });
            fixSessionWizard.addStep(new WizardStep() {
                @Override
                public String getCaption()
                {
                    return "Settings";
                }
                @Override
                public Component getContent()
                {
                    VerticalLayout formLayout = new VerticalLayout();
                    formLayout.setMargin(true);
                    formLayout.setSizeFull();
                    final Grid<DecoratedDescriptor> descriptorGrid = new Grid<>(DecoratedDescriptor.class,
                                                                                false);
                    Column<DecoratedDescriptor> nameColumn = descriptorGrid.addColumn(DecoratedDescriptor::getName).setHeader("Name");
                    Column<DecoratedDescriptor> valueColumn = descriptorGrid.addColumn(DecoratedDescriptor::getValue).setHeader("Value");
                    nameColumn.setTooltipGenerator(selectedDescriptor -> selectedDescriptor.getDescription());
                    valueColumn.setTooltipGenerator(selectedDescriptor -> selectedDescriptor.getDescription());
                    descriptorGrid.setItems(sortedDescriptors.values());
                    descriptorGrid.setWidth("1024px");
                    descriptorGrid.setHeight("640px");
                    Binder<DecoratedDescriptor> gridBinder = new Binder<>(DecoratedDescriptor.class);
                    Editor<DecoratedDescriptor> editor = descriptorGrid.getEditor();
                    editor.setBinder(gridBinder);
                    TextField valueField = new TextField();
                    valueField.setWidthFull();
//                    addCloseHandler(valueField,
//                                    editor);
                    gridBinder.forField(valueField)
//                            .withStatusLabel(firstNameValidationMessage)
                            .bind(DecoratedDescriptor::getValue, DecoratedDescriptor::setValue);
                    valueColumn.setEditorComponent(valueField);
                    descriptorGrid.addItemDoubleClickListener(e -> {
                        editor.editItem(e.getItem());
                        Component editorComponent = e.getColumn().getEditorComponent();
                        if (editorComponent instanceof Focusable) {
                            ((Focusable<?>)editorComponent).focus();
                        }
                    });
                    final DecoratedDescriptor selectedDescriptor = new DecoratedDescriptor();
                    descriptorGrid.addItemClickListener(inEvent -> {
                        ItemClickEvent<DecoratedDescriptor> itemClickEvent = (ItemClickEvent<DecoratedDescriptor>)inEvent;
                        DecoratedDescriptor clickItem = itemClickEvent.getItem();
                        selectedDescriptor.setAdvice(String.valueOf(clickItem.getAdvice()));
                        selectedDescriptor.setDefaultValue(String.valueOf(clickItem.getDefaultValue()));
                        selectedDescriptor.setDescription(String.valueOf(clickItem.getDescription()));
                        selectedDescriptor.setName(String.valueOf(clickItem.getName()));
                        selectedDescriptor.setPattern(String.valueOf(clickItem.getPattern()));
                        selectedDescriptor.setRequired(Boolean.parseBoolean(String.valueOf(clickItem.isRequired())));
                    });
//                    // create an editor specific to the selected row
//                    descriptorGrid.setEditorFieldFactory(new FieldGroupFieldFactory() {
//                        @Override
//                        @SuppressWarnings("rawtypes")
//                        public <T extends Field> T createField(Class<?> inDataType,
//                                                               Class<T> inFieldType)
//                        {
//                            final TextField textField = new TextField();
//                            textField.setRequired(selectedDescriptor.isRequired());
//                            textField.setDescription(selectedDescriptor.getDescription());
//                            textField.setNullRepresentation("");
//                            textField.addValueChangeListener(inEvent -> {
//                                ValueChangeEvent valueChangeEvent = (ValueChangeEvent)inEvent;
//                                String value = String.valueOf(valueChangeEvent.getProperty().getValue());
//                                fixSessionValue.getSessionSettings().put(selectedDescriptor.getName(),
//                                                                       value);
//                            });
//                            String pattern = StringUtils.trimToNull(selectedDescriptor.getPattern());
//                            if(pattern != null) {
//                                String advice = StringUtils.trimToNull(selectedDescriptor.getAdvice());
//                                if(advice == null) {
//                                    advice = "Does not match pattern " + pattern;
//                                }
//                                RegexpValidator validator = new RegexpValidator(pattern,
//                                                                                advice);
//                                validator.setErrorMessage(advice);
//                                textField.addValidator(validator);
//                            }
//                            return inFieldType.cast(textField);
//                        }
//                        private static final long serialVersionUID = -5965893350850268432L;
//                    });
                    formLayout.add(descriptorGrid);
                    return formLayout;
                }
                @Override
                public boolean onAdvance()
                {
                    for(DecoratedDescriptor descriptor : sortedDescriptors.values()) {
                        if(StringUtils.trimToNull(descriptor.getValue()) != null) {
                            fixSessionValue.getSessionSettings().put(descriptor.getName(),
                                                                     descriptor.getValue());
                        }
                    }
                    try {
                        if(inIsNew) {
                            SLF4JLoggerProxy.debug(FixSessionListView.this,
                                                   "Submitting new fix session: {}",
                                                   fixSessionValue);
                            getServiceClient().createFixSession(fixSessionValue);
                        } else {
                            SLF4JLoggerProxy.debug(FixSessionListView.this,
                                                   "Submitting fix session for update: {}",
                                                   fixSessionValue);
                            getServiceClient().updateFixSession(incomingName,
                                                                fixSessionValue);
                        }
                        closeEditor();
                        updateList();
                    } catch (Exception e) {
                        Notification notification = new Notification();
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        Div text = new Div(new Text("A problem occurred saving the session: " + ExceptionUtils.getRootCauseMessage(e)));
                        Button closeButton = new Button(new Icon("lumo",
                                                                 "cross"));
                        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                        closeButton.getElement().setAttribute("aria-label", "Close");
                        closeButton.addClickListener(event -> {
                            notification.close();
                        });
                        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
                        layout.setAlignItems(Alignment.CENTER);
                        notification.add(layout);
                        notification.open();
                        return false;
                    }
                    return true;
                }
                @Override
                public boolean onBack()
                {
                    return true;
                }
            });
            fixSessionWizard.setSizeFull();
            fixSessionWizard.setWidth("640px");
            componentLayout.setSizeFull();
            componentLayout.removeAll();
            componentLayout.add(fixSessionWizard);
        }
        /**
         * connection type UI widget
         */
        private RadioButtonGroup<String> connectionType = new RadioButtonGroup<>("Connection Type");
        /**
         * affinity UI widget
         */
        private TextField affinity = new TextField("Affinty");
        /**
         * hostname UI widget
         */
        private TextField hostname = new TextField("Hostname");
        /**
         * port UI widget
         */
        private TextField port = new TextField("Port");
        /**
         * editor components layout value
         */
        private VerticalLayout componentLayout;
        private DisplayFixSession fixSessionValue;
        private static final long serialVersionUID = -1931251254078030265L;
    }
    /**
     * Provides a <code>FixSessionAttributeDescriptor</code> that supports setting a value.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class DecoratedDescriptor
            extends SimpleFixSessionAttributeDescriptor
    {
        /**
         * Create a new DecoratedDescriptor instance.
         *
         * @param inDescriptor a <code>FixSessionAttributeDescriptor</code> value
         */
        public DecoratedDescriptor(FixSessionAttributeDescriptor inDescriptor)
        {
            setAdvice(inDescriptor.getAdvice());
            setDefaultValue(inDescriptor.getDefaultValue());
            setDescription(inDescriptor.getDescription());
            setName(inDescriptor.getName());
            setPattern(inDescriptor.getPattern());
            setRequired(inDescriptor.isRequired());
            setValue(getDefaultValue());
        }
        /**
         * Create a new DecoratedDescriptor instance.
         */
        public DecoratedDescriptor() {}
        /**
         * Get the value value.
         *
         * @return a <code>String</code> value
         */
        public String getValue()
        {
            return value;
        }
        /**
         * Sets the value value.
         *
         * @param inValue a <code>String</code> value
         */
        public void setValue(String inValue)
        {
            value = inValue;
        }
        /**
         * Reset the value to the default value.
         */
        public void reset()
        {
            value = getDefaultValue();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DecoratedDescriptor [").append(getName()).append("=").append(value).append("]");
            return builder.toString();
        }
        /**
         * value set by user
         */
        private String value;
        private static final long serialVersionUID = 142085523837757672L;
    }
    /**
     * edit form instance
     */
    private FixSessionForm form;
    /**
     * edit action label
     */
    private final String ACTION_EDIT = "Edit";
    /**
     * start action label
     */
    private final String ACTION_START = "Start";
    /**
     * stop action label
     */
    private final String ACTION_STOP = "Stop";
    /**
     * enable action label
     */
    private final String ACTION_ENABLE = "Enable";
    /**
     * disable action label
     */
    private final String ACTION_DISABLE = "Disable";
    /**
     * delete action label
     */
    private final String ACTION_DELETE = "Delete";
    /**
     * edit sequence numbers label
     */
    private final String ACTION_SEQUENCE = "Update Sequence Numbers";
    private static final long serialVersionUID = 516637620331918587L;
}
