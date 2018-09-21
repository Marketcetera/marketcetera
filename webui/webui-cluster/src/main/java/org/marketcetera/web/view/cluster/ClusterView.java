package org.marketcetera.web.view.cluster;

import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.impl.SimpleActiveFixSession;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.services.WebMessageService;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.MenuContent;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/* $License$ */

/**
 * Provides a view for system Fix Sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class ClusterView
        extends AbstractGridView<ClusterMember>
        implements MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getActionSelect().setNullSelectionAllowed(false);
        getActionSelect().setReadOnly(true);
        getGrid().addSelectionListener(inEvent -> {
            ClusterMember selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionSelect().setReadOnly(false);
//                // adjust the available actions based on the status of the selected row
//                switch(selectedObject.getStatus()) {
//                    case CONNECTED:
//                    case DISCONNECTED:
//                    case NOT_CONNECTED:
//                        getActionSelect().addItems(ACTION_STOP);
//                        break;
//                    case DISABLED:
//                        getActionSelect().addItems(ACTION_ENABLE,
//                                                   ACTION_SEQUENCE,
//                                                   ACTION_EDIT,
//                                                   ACTION_DELETE);
//                        break;
//                    case STOPPED:
//                        getActionSelect().addItems(ACTION_START,
//                                                   ACTION_DISABLE,
//                                                   ACTION_SEQUENCE);
//                        break;
//                    case AFFINITY_MISMATCH:
//                    case BACKUP:
//                    case DELETED:
//                    case UNKNOWN:
//                    default:
//                        // nothing available, these are essentially weird statuses for display
//                        break;
//                }
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Cluster Data";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 50;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.SITEMAP;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                webMessageService.post(new NewWindowEvent() {
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Component getComponent()
                    {
                        return ClusterView.this;
                    }
                });
            }
            private static final long serialVersionUID = 49365592058433460L;
        };
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("name",
                             "sessionId",
                             "instance",
                             "status",
                             "senderSequenceNumber",
                             "targetSequenceNumber");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        ClusterMember selectedItem = getSelectedItem();
        if(selectedItem == null || inEvent.getProperty().getValue() == null) {
            return;
        }
        String action = String.valueOf(inEvent.getProperty().getValue());
        SLF4JLoggerProxy.info(this,
                              "{}: {} {} '{}'",
                              String.valueOf(VaadinSession.getCurrent().getAttribute(SessionUser.class)),
                              getViewName(),
                              action,
                              selectedItem);
//        AdminClientService adminClientService = AdminClientService.getInstance();
//        switch(action) {
//            case ACTION_START:
//                adminClientService.startSession(selectedItem.getFixSession().getName());
//                break;
//            case ACTION_STOP:
//                adminClientService.stopSession(selectedItem.getFixSession().getName());
//                break;
//            case ACTION_ENABLE:
//                adminClientService.enableSession(selectedItem.getFixSession().getName());
//                break;
//            case ACTION_DISABLE:
//                adminClientService.disableSession(selectedItem.getFixSession().getName());
//                break;
//            case ACTION_DELETE:
//                adminClientService.deleteSession(selectedItem.getFixSession().getName());
//                break;
//            case ACTION_SEQUENCE:
//                doUpdateSequenceNumbers(selectedItem);
//                break;
//            case ACTION_EDIT:
//                createOrEdit(selectedItem,
//                             false);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unsupported action: " + action);
//        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<ClusterMember> createDataContainer()
    {
        return new ClusterPagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Cluster Instances";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onCreateNew(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    protected void onCreateNew(ClickEvent inEvent)
    {
        // create a new FIX session object
        SimpleActiveFixSession newFixSession = new SimpleActiveFixSession();
        // set defaults for the new session
        newFixSession.getFixSession().getMutableView().setAffinity(1);
        newFixSession.getFixSession().getMutableView().setIsAcceptor(false);
        createOrEdit(newFixSession,
                     true);
    }
    /**
     * Allow the user to either create or edit the given session.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     * @param inIsNew a <code>boolean</code> value
     */
    private void createOrEdit(ActiveFixSession inFixSession,
                              boolean inIsNew)
    {
//        final VerticalLayout wizardLayout = new VerticalLayout();
//        wizardLayout.setSizeFull();
//        final Window subWindow = new Window((inIsNew?"Create":"Edit")+" FIX Session");
//        VerticalLayout content = new VerticalLayout();
//        content.setMargin(true);
//        subWindow.setContent(content);
//        subWindow.center();
//        subWindow.setModal(true);
//        subWindow.setSizeUndefined();
//        subWindow.setClosable(false);
//        subWindow.setDraggable(false);
//        subWindow.setResizable(false);
//        // prepare data
//        final SortedMap<String,DecoratedDescriptor> sortedDescriptors = new TreeMap<>();
//        final String incomingName = inFixSession.getFixSession().getName();
//        Collection<FixSessionAttributeDescriptor> descriptors = AdminClientService.getInstance().getFixSessionAttributeDescriptors();
//        for(FixSessionAttributeDescriptor descriptor : descriptors) {
//            DecoratedDescriptor actualDescriptor = new DecoratedDescriptor(descriptor);
//            sortedDescriptors.put(descriptor.getName(),
//                                  actualDescriptor);
//        }
//        if(!inIsNew) {
//            for(DecoratedDescriptor descriptor : sortedDescriptors.values()) {
//                String existingValue = inFixSession.getFixSession().getSessionSettings().get(descriptor.getName());
//                if(existingValue == null) {
//                    descriptor.setValue(null);
//                } else {
//                    descriptor.setValue(existingValue);
//                }
//            }
//        }
//        // create the wizard
//        Wizard fixSessionWizard = new Wizard();
//        fixSessionWizard.addListener(new WizardProgressListener() {
//            @Override
//            public void activeStepChanged(WizardStepActivationEvent inEvent)
//            {
//            }
//            @Override
//            public void stepSetChanged(WizardStepSetChangedEvent inEvent)
//            {
//            }
//            @Override
//            public void wizardCompleted(WizardCompletedEvent inEvent)
//            {
//                subWindow.close();
//            }
//            @Override
//            public void wizardCancelled(WizardCancelledEvent inEvent)
//            {
//                subWindow.close();
//            }
//        });
//        fixSessionWizard.addStep(new WizardStep() {
//            @Override
//            public String getCaption()
//            {
//                return "Type";
//            }
//            @Override
//            public Component getContent()
//            {
//                FormLayout formLayout = new FormLayout();
//                formLayout.setMargin(true);
//                formLayout.setSizeFull();
//                initializeFields();
//                connectionTypeOptionGroup.setMultiSelect(false);
//                connectionTypeOptionGroup.addItem(ACCEPTOR);
//                connectionTypeOptionGroup.addItem(INITIATOR);
//                connectionTypeOptionGroup.setDescription("Indicates whether the session will receive orders (acceptor) or send them (initiator)");
//                connectionTypeOptionGroup.setRequired(true);
//                connectionTypeOptionGroup.setRequiredError("Connection type required");
//                affinityTextField.setDescription("Indicates which cluster instance will host this session, if unsure, leave as 1");
//                affinityTextField.setRequired(true);
//                affinityTextField.setRequiredError("Affinity required");
//                affinityTextField.addValidator(inValue -> {
//                    try {
//                        Integer.parseInt(String.valueOf(inValue));
//                    } catch (Exception e) {
//                        throw new InvalidValueException(ExceptionUtils.getRootCauseMessage(e));
//                    }
//                });
//                updateFields();
//                formLayout.addComponents(connectionTypeOptionGroup,
//                                         affinityTextField);
//                return formLayout;
//            }
//            @Override
//            public boolean onAdvance()
//            {
//                updateFixSession();
//                return connectionTypeOptionGroup.isValid() && affinityTextField.isValid();
//            }
//            @Override
//            public boolean onBack()
//            {
//                return false;
//            }
//            /**
//             * Initialize the UI widgets.
//             */
//            private void initializeFields()
//            {
//                connectionTypeOptionGroup.setValue(inFixSession.getFixSession().isAcceptor()?ACCEPTOR:INITIATOR);
//                affinityTextField.setValue(String.valueOf(inFixSession.getFixSession().getAffinity()));
//                connectionTypeOptionGroup.setVisible(true);
//                affinityTextField.setVisible(true);
//            }
//            /**
//             * Update the UI fields from the FIX session variable.
//             */
//            private void updateFields()
//            {
//                connectionTypeOptionGroup.setValue(inFixSession.getFixSession().isAcceptor()?ACCEPTOR:INITIATOR);
//                affinityTextField.setValue(String.valueOf(inFixSession.getFixSession().getAffinity()));
//            }
//            /**
//             * Update the FIX session variable from the UI fields.
//             */
//            private void updateFixSession()
//            {
//                inFixSession.getFixSession().getMutableView().setIsAcceptor(ACCEPTOR.equals(connectionTypeOptionGroup.getValue()));
//                inFixSession.getFixSession().getMutableView().setAffinity(Integer.parseInt(affinityTextField.getValue()));
//            }
//            /**
//             * value for acceptor sessions
//             */
//            private final String ACCEPTOR = "Acceptor";
//            /**
//             * value for initiator sessions
//             */
//            private final String INITIATOR = "Initiator";
//            /**
//             * connection type UI widget
//             */
//            private OptionGroup connectionTypeOptionGroup = new OptionGroup("Connection Type");
//            /**
//             * affinity UI widget
//             */
//            private TextField affinityTextField = new TextField("Affinty");
//        });
//        fixSessionWizard.addStep(new WizardStep() {
//            @Override
//            public String getCaption()
//            {
//                return "Network";
//            }
//            @Override
//            public Component getContent()
//            {
//                FormLayout formLayout = new FormLayout();
//                formLayout.setMargin(true);
//                formLayout.setSizeFull();
//                testConnectionLabel.setVisible(false);
//                if(hostnameTextField.isEmpty()) {
//                    testConnectionButton.setReadOnly(true);
//                } else {
//                    testConnectionButton.setReadOnly(false);
//                }
//                testConnectionButton.addClickListener(new ClickListener() {
//                    @Override
//                    public void buttonClick(ClickEvent inEvent)
//                    {
//                        try(Socket s = new Socket(inFixSession.getFixSession().getHost(),
//                                                  inFixSession.getFixSession().getPort())) {
//                            testConnectionLabel.setValue("Test connection success");
//                            testConnectionLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
//                        } catch (Exception e) {
//                            testConnectionLabel.setValue("Test connection failed: " + ExceptionUtils.getRootCauseMessage(e));
//                            testConnectionLabel.setStyleName(ValoTheme.LABEL_FAILURE);
//                        }
//                        testConnectionLabel.setVisible(true);
//                    }
//                    private static final long serialVersionUID = -2219835238983724259L;
//                });
//                if(inFixSession.getFixSession().isAcceptor()) {
//                    InstanceData instanceData = AdminClientService.getInstance().getInstanceData(inFixSession.getFixSession().getAffinity());
//                    hostnameTextField.setValue(instanceData.getHostname());
//                    hostnameTextField.setReadOnly(true);
//                    hostnameTextField.setDescription("The acceptor hostname is determined by the server and is not modifiable");
//                    portTextField.setValue(String.valueOf(instanceData.getPort()));
//                    hostnameTextField.setDescription("The acceptor port is determined by the server cluster framework and is not modifiable");
//                    portTextField.setReadOnly(true);
//                    testConnectionButton.setVisible(false);
//                } else {
//                    hostnameTextField.setNullRepresentation("");
//                    hostnameTextField.setReadOnly(false);
//                    hostnameTextField.setDescription("Hostname of the FIX gateway to connect to");
//                    hostnameTextField.setValue(inFixSession.getFixSession().getHost()==null?"exchange.marketcetera.com":inFixSession.getFixSession().getHost());
//                    hostnameTextField.setRequired(true);
//                    hostnameTextField.setRequiredError("Hostname is required");
//                    hostnameTextField.addValueChangeListener(inEvent -> {
//                        inFixSession.getFixSession().getMutableView().setHost(String.valueOf(inEvent.getProperty().getValue()));
//                        if(!hostnameTextField.isEmpty()) {
//                            testConnectionButton.setReadOnly(false);
//                        }
//                    });
//                    portTextField.setReadOnly(false);
//                    portTextField.setDescription("Port of the FIX gateway to connect to");
//                    portTextField.setValue(inFixSession.getFixSession().getPort()==0?"7001":String.valueOf(inFixSession.getFixSession().getPort()));
//                    portTextField.setRequired(true);
//                    portTextField.setRequiredError("Port is required");
//                    portTextField.addValidator(inValue -> {
//                        try {
//                            Integer.parseInt(String.valueOf(inValue));
//                        } catch (Exception e) {
//                            throw new InvalidValueException("Port must be between 1 and 65535");
//                        }
//                    });
//                    portTextField.addValueChangeListener(inEvent -> {
//                        try {
//                            int value = Integer.parseInt(String.valueOf(inEvent.getProperty().getValue()));
//                            inFixSession.getFixSession().getMutableView().setPort(value);
//                        } catch (Exception ignored) {}
//                    });
//                    testConnectionButton.setVisible(true);
//                }
//                initializeFields();
//                updateFields();
//                formLayout.addComponents(hostnameTextField,
//                                         portTextField,
//                                         testConnectionButton,
//                                         testConnectionLabel);
//                formLayout.setComponentAlignment(testConnectionButton,
//                                                 Alignment.BOTTOM_RIGHT);
//                return formLayout;
//            }
//            @Override
//            public boolean onAdvance()
//            {
//                updateFixSession();
//                return hostnameTextField.isValid() && portTextField.isValid();
//            }
//            @Override
//            public boolean onBack()
//            {
//                return true;
//            }
//            /**
//             * Initialize the UI widgets.
//             */
//            private void initializeFields()
//            {
//                if(inFixSession.getFixSession().isAcceptor()) {
//                    InstanceData instanceData = AdminClientService.getInstance().getInstanceData(inFixSession.getFixSession().getAffinity());
//                    hostnameTextField.setValue(instanceData.getHostname());
//                    hostnameTextField.setReadOnly(true);
//                    hostnameTextField.setDescription("The acceptor hostname is determined by the server and is not modifiable");
//                    portTextField.setValue(String.valueOf(instanceData.getPort()));
//                    hostnameTextField.setDescription("The acceptor port is determined by the server cluster framework and is not modifiable");
//                    portTextField.setReadOnly(true);
//                    testConnectionButton.setVisible(false);
//                } else {
//                    hostnameTextField.setNullRepresentation("");
//                    hostnameTextField.setReadOnly(false);
//                    hostnameTextField.setDescription("Hostname of the FIX gateway to connect to");
//                    hostnameTextField.setValue(inFixSession.getFixSession().getHost()==null?"exchange.marketcetera.com":inFixSession.getFixSession().getHost());
//                    hostnameTextField.setRequired(true);
//                    hostnameTextField.setRequiredError("Hostname is required");
//                    hostnameTextField.addValueChangeListener(inEvent -> {
//                        inFixSession.getFixSession().getMutableView().setHost(String.valueOf(inEvent.getProperty().getValue()));
//                        if(!hostnameTextField.isEmpty()) {
//                            testConnectionButton.setReadOnly(false);
//                        }
//                    });
//                    portTextField.setReadOnly(false);
//                    portTextField.setDescription("Port of the FIX gateway to connect to");
//                    portTextField.setValue(inFixSession.getFixSession().getPort()==0?"7001":String.valueOf(inFixSession.getFixSession().getPort()));
//                    portTextField.setRequired(true);
//                    portTextField.setRequiredError("Port is required");
//                    portTextField.addValidator(inValue -> {
//                        try {
//                            Integer.parseInt(String.valueOf(inValue));
//                        } catch (Exception e) {
//                            throw new InvalidValueException("Port must be between 1 and 65535");
//                        }
//                    });
//                    portTextField.addValueChangeListener(inEvent -> {
//                        try {
//                            int value = Integer.parseInt(String.valueOf(inEvent.getProperty().getValue()));
//                            inFixSession.getFixSession().getMutableView().setPort(value);
//                        } catch (Exception ignored) {}
//                    });
//                    testConnectionButton.setVisible(true);
//                }
//            }
//            /**
//             * Update the UI fields from the FIX session variable.
//             */
//            private void updateFields()
//            {
//            }
//            /**
//             * Update the FIX session variable from the UI fields.
//             */
//            private void updateFixSession()
//            {
//                inFixSession.getFixSession().getMutableView().setHost(hostnameTextField.getValue());
//                inFixSession.getFixSession().getMutableView().setPort(Integer.parseInt(portTextField.getValue()));
//            }
//            /**
//             * hostname UI widget
//             */
//            private TextField hostnameTextField = new TextField("Hostname");
//            /**
//             * port UI widget
//             */
//            private TextField portTextField = new TextField("Port");
//            /**
//             * connection button widget
//             */
//            private Button testConnectionButton = new Button("Test Connection");
//            /**
//             * test connection results widget
//             */
//            private Label testConnectionLabel = new Label();
//        });
//        fixSessionWizard.addStep(new WizardStep() {
//            @Override
//            public String getCaption()
//            {
//                return "Identity";
//            }
//            @Override
//            public Component getContent()
//            {
//                // gather a list of the existing sessions, we'll use this to do some basic validation about session identity
//                final Collection<ActiveFixSession> existingSessions = AdminClientService.getInstance().getFixSessions();
//                FormLayout formLayout = new FormLayout();
//                formLayout.setMargin(true);
//                formLayout.setSizeFull();
//                nameTextField.setDescription("Unique human-readable name of the session");
//                nameTextField.addValidator(new Validator() {
//                    @Override
//                    public void validate(Object inValue)
//                            throws InvalidValueException
//                    {
//                        String computedValue = StringUtils.trimToNull(String.valueOf(inValue));
//                        for(ActiveFixSession existingSession : existingSessions) {
//                            if(inIsNew && existingSession.getFixSession().getName().equals(computedValue)) {
//                                throw new Validator.InvalidValueException("'"+existingSession.getFixSession().getName() + "' is already in use");
//                            }
//                        }
//                    }
//                    private static final long serialVersionUID = 3573400392332322865L;
//                });
//                nameTextField.addValidator(new NullValidator("Name is required",
//                                                             false));
//                nameTextField.addValidator(new StringLengthValidator("Name may contain up to 255 characters",
//                                                                     1,
//                                                                     255,
//                                                                     false));
//                nameTextField.addValidator(new RegexpValidator(NDEntityBase.namePattern.pattern(),
//                                                               "Names may contain up to 255 letters, numbers, spaces, or the dash char ('-')"));
//                nameTextField.setRequired(true);
//                nameTextField.setRequiredError("Name is required");
//                descriptionTextField.setDescription("Optional description of the session");
//                descriptionTextField.addValidator(new StringLengthValidator("Description may contain up to 255 characters",
//                                                                            0,
//                                                                            255,
//                                                                            true));
//                descriptionTextField.setNullRepresentation("");
//                brokerIdTextField.setDescription("Unique system identifier for this FIX session used to target orders, pick something short and descriptive");
//                brokerIdTextField.setRequired(true);
//                brokerIdTextField.setRequiredError("Broker Id required");
//                brokerIdTextField.addValidator(new NullValidator("Broker Id required",
//                                                                 false));
//                brokerIdTextField.addValidator(new Validator() {
//                    @Override
//                    public void validate(Object inValue)
//                            throws InvalidValueException
//                    {
//                        String computedValue = StringUtils.trimToNull(String.valueOf(inValue));
//                        for(ActiveFixSession existingSession : existingSessions) {
//                            if(inIsNew && existingSession.getFixSession().getBrokerId().equals(computedValue)) {
//                                throw new Validator.InvalidValueException("'"+existingSession.getFixSession().getBrokerId() + "' is already in use");
//                            }
//                        }
//                    }
//                    private static final long serialVersionUID = 5566487022666219128L;
//                });
//                // build the FIX Session ID from three components
//                fixVersionSelect.setDescription("FIX version of the session");
//                fixVersionSelect.setNullSelectionAllowed(false);
//                fixVersionSelect.setTextInputAllowed(false);
//                fixVersionSelect.setRequired(true);
//                fixVersionSelect.setRequiredError("FIX Version requried");
//                senderCompIdTextField.setDescription("Sender Comp Id of the session");
//                senderCompIdTextField.setRequired(true);
//                senderCompIdTextField.setRequiredError("Sender Comp Id required");
//                senderCompIdTextField.addValidator(new NullValidator("Sender Comp Id required",
//                                                                     false));
//                targetCompIdTextField.setDescription("Target Comp Id of the session");
//                targetCompIdTextField.setRequired(true);
//                targetCompIdTextField.setRequiredError("Target Comp Id required");
//                targetCompIdTextField.addValidator(new NullValidator("Target Comp Id required",
//                                                                     false));
//                for(FIXVersion fixVersion : FIXVersion.values()) {
//                    if(fixVersion == FIXVersion.FIX_SYSTEM) {
//                        continue;
//                    }
//                    fixVersionSelect.addItem(fixVersion.toString());
//                }
//                Validator sessionIdValidator = new Validator() {
//                    @Override
//                    public void validate(Object inValue)
//                            throws InvalidValueException
//                    {
//                        String fixVersionValue = fixVersionSelect.getValue() == null ? null : String.valueOf(fixVersionSelect.getValue());
//                        SessionID sessionId = new SessionID(fixVersionValue,
//                                                            senderCompIdTextField.getValue(),
//                                                            targetCompIdTextField.getValue());
//                        for(ActiveFixSession existingSession : existingSessions) {
//                            if(inIsNew && existingSession.getFixSession().getSessionId().equals(sessionId.toString())) {
//                                throw new Validator.InvalidValueException("'"+existingSession.getFixSession().getSessionId() + "' is already in use");
//                            }
//                        }
//                    }
//                    private static final long serialVersionUID = -1596729632204460961L;
//                };
//                fixVersionSelect.addValidator(sessionIdValidator);
//                senderCompIdTextField.addValidator(sessionIdValidator);
//                targetCompIdTextField.addValidator(sessionIdValidator);
//                initializeFields();
//                updateFields();
//                formLayout.addComponents(nameTextField,
//                                         descriptionTextField,
//                                         brokerIdTextField,
//                                         fixVersionSelect,
//                                         senderCompIdTextField,
//                                         targetCompIdTextField);
//                return formLayout;
//            }
//            @Override
//            public boolean onAdvance()
//            {
//                updateFixSession();
//                return nameTextField.isValid() && descriptionTextField.isValid() && brokerIdTextField.isValid() &&
//                        fixVersionSelect.isValid() && senderCompIdTextField.isValid() && targetCompIdTextField.isValid();
//            }
//            @Override
//            public boolean onBack()
//            {
//                return true;
//            }
//            /**
//             * Initialize the UI widgets.
//             */
//            private void initializeFields()
//            {
//                nameTextField.setValue(inFixSession.getFixSession().getName()==null?"New Session":inFixSession.getFixSession().getName());
//                descriptionTextField.setValue(inFixSession.getFixSession().getDescription());
//                brokerIdTextField.setValue(inFixSession.getFixSession().getBrokerId()==null?"new-broker":inFixSession.getFixSession().getBrokerId());
//                if(inFixSession.getFixSession().getSessionId() != null) {
//                    SessionID sessionId = new SessionID(inFixSession.getFixSession().getSessionId());
//                    if(sessionId.isFIXT()) {
//                        String defaultApplVerId = inFixSession.getFixSession().getSessionSettings().get(Session.SETTING_DEFAULT_APPL_VER_ID);
//                        fixVersionSelect.setValue(defaultApplVerId);
//                    } else {
//                        fixVersionSelect.setValue(sessionId.getBeginString());
//                    }
//                    senderCompIdTextField.setValue(sessionId.getSenderCompID());
//                    targetCompIdTextField.setValue(sessionId.getTargetCompID());
//                } else {
//                    senderCompIdTextField.setValue("MATP");
//                    targetCompIdTextField.setValue("MRKTC-EXCH");
//                    fixVersionSelect.setValue(FIXVersion.FIX42.toString());
//                }
//            }
//            /**
//             * Update the UI fields from the FIX session variable.
//             */
//            private void updateFields()
//            {
//            }
//            /**
//             * Update the FIX session variable from the UI fields.
//             */
//            private void updateFixSession()
//            {
//                inFixSession.getFixSession().getMutableView().setName(nameTextField.getValue());
//                inFixSession.getFixSession().getMutableView().setDescription(descriptionTextField.getValue());
//                inFixSession.getFixSession().getMutableView().setBrokerId(brokerIdTextField.getValue());
//                String fixVersionValue = fixVersionSelect.getValue() == null ? null : String.valueOf(fixVersionSelect.getValue());
//                FIXVersion fixVersion = FIXVersion.getFIXVersion(fixVersionValue);
//                if(fixVersion.isFixT()) {
//                    inFixSession.getFixSession().getSessionSettings().put(Session.SETTING_DEFAULT_APPL_VER_ID,
//                                                          fixVersionValue);
//                    DecoratedDescriptor defaultApplVerId = sortedDescriptors.get(Session.SETTING_DEFAULT_APPL_VER_ID);
//                    defaultApplVerId.setValue(fixVersionValue);
//                    fixVersionValue = FixVersions.BEGINSTRING_FIXT11;
//                }
//                SessionID sessionId = new SessionID(fixVersionValue,
//                                                    senderCompIdTextField.getValue(),
//                                                    targetCompIdTextField.getValue());
//                inFixSession.getFixSession().getMutableView().setSessionId(sessionId.toString());
//            }
//            /**
//             * session name UI widget
//             */
//            private TextField nameTextField = new TextField("Session Name");
//            /**
//             * session description UI widget
//             */
//            private TextField descriptionTextField = new TextField("Session Description");
//            /**
//             * session broker id UI widget
//             */
//            private TextField brokerIdTextField = new TextField("Broker Id");
//            /**
//             * session FIX version UI widget
//             */
//            private ComboBox fixVersionSelect = new ComboBox("FIX Version");
//            /**
//             * session sender comp id UI widget
//             */
//            private TextField senderCompIdTextField = new TextField("Sender Comp Id");
//            /**
//             * session target comp id UI widget
//             */
//            private TextField targetCompIdTextField = new TextField("Target Comp Id");
//        });
//        fixSessionWizard.addStep(new WizardStep() {
//            @Override
//            public String getCaption()
//            {
//                return "Start and End";
//            }
//            @Override
//            public Component getContent()
//            {
//                FormLayout formLayout = new FormLayout();
//                formLayout.setMargin(true);
//                formLayout.setSizeFull();
//                // set up widgets
//                // session type
//                sessionTypeSelect.addItem(DAILY);
//                sessionTypeSelect.addItem(WEEKLY);
//                sessionTypeSelect.addItem(CONTINUOUS);
//                sessionTypeSelect.setNewItemsAllowed(false);
//                sessionTypeSelect.setNullSelectionAllowed(false);
//                sessionTypeSelect.setTextInputAllowed(false);
//                // start and end time
//                startTimeField.addValidator(new RegexpValidator("^([01]\\d|2[0-3]):?([0-5]\\d):?[0-5]\\d$",
//                                                                "Enter a time value in the form 00:00:00"));
//                endTimeField.addValidator(new RegexpValidator("^([01]\\d|2[0-3]):?([0-5]\\d):?[0-5]\\d$",
//                                                              "Enter a time value in the form 00:00:00"));
//                // time zone
//                timeZoneSelect.setNewItemsAllowed(false);
//                timeZoneSelect.setNullSelectionAllowed(false);
//                timeZoneSelect.setTextInputAllowed(false);
//                for(String timeZoneId : TimeZone.getAvailableIDs()) {
//                    timeZoneSelect.addItem(timeZoneId);
//                }
//                // start and end day
//                startDaySelect.setNewItemsAllowed(false);
//                startDaySelect.setNullSelectionAllowed(false);
//                startDaySelect.setTextInputAllowed(false);
//                endDaySelect.setNewItemsAllowed(false);
//                endDaySelect.setNullSelectionAllowed(false);
//                endDaySelect.setTextInputAllowed(false);
//                for(FixSessionDay fixSessionDay : FixSessionDay.values()) {
//                    startDaySelect.addItem(fixSessionDay.name());
//                    endDaySelect.addItem(fixSessionDay.name());
//                }
//                sessionTypeSelect.addValueChangeListener(inEvent -> {
//                    ValueChangeEvent event = (ValueChangeEvent)inEvent;
//                    SLF4JLoggerProxy.debug(ClusterView.this,
//                                           "SessionTypeSelect value change: {}",
//                                           event);
//                    updateFields();
//                });
//                // set up data
//                initializeFields();
//                updateFields();
//                formLayout.addComponents(sessionTypeSelect,
//                                         startTimeField,
//                                         endTimeField,
//                                         startDaySelect,
//                                         endDaySelect,
//                                         timeZoneSelect);
//                return formLayout;
//            }
//            @Override
//            public boolean onAdvance()
//            {
//                updateFixSession();
//                if(isContinuousSession(inFixSession.getFixSession())) {
//                    return sessionTypeSelect.isValid();
//                }
//                if(isWeeklySession(inFixSession.getFixSession())) {
//                    return sessionTypeSelect.isValid() && startTimeField.isValid() && endTimeField.isValid() && timeZoneSelect.isValid() &&
//                            startDaySelect.isValid() && endDaySelect.isValid();
//                }
//                return sessionTypeSelect.isValid() && startTimeField.isValid() && endTimeField.isValid() && timeZoneSelect.isValid();
//            }
//            @Override
//            public boolean onBack()
//            {
//                return true;
//            }
//            /**
//             * Initialize the UI widgets.
//             */
//            private void initializeFields()
//            {
//                Map<String,String> settings = inFixSession.getFixSession().getSessionSettings();
//                startTimeField.setValue(settings.containsKey(Session.SETTING_START_TIME)?settings.get(Session.SETTING_START_TIME):"00:00:00");
//                endTimeField.setValue(settings.containsKey(Session.SETTING_END_TIME)?settings.get(Session.SETTING_END_TIME):"00:00:00");
//                startDaySelect.setValue(settings.containsKey(Session.SETTING_START_DAY)?settings.get(Session.SETTING_START_DAY):FixSessionDay.Monday.name());
//                endDaySelect.setValue(settings.containsKey(Session.SETTING_END_DAY)?settings.get(Session.SETTING_END_DAY):FixSessionDay.Friday.name());
//                timeZoneSelect.setValue(settings.containsKey(Session.SETTING_TIMEZONE)?settings.get(Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
//                startTimeField.setVisible(true);
//                endTimeField.setVisible(true);
//                timeZoneSelect.setVisible(true);
//                startDaySelect.setVisible(true);
//                endDaySelect.setVisible(true);
//                // set default values
//                // now, finalize the setup based on the selected session type
//                String value = settings.get(Session.SETTING_NON_STOP_SESSION);
//                if(YES.equals(value)) {
//                    // this is a non-stop session. hide everything but the select
//                    startTimeField.setVisible(false);
//                    endTimeField.setVisible(false);
//                    timeZoneSelect.setVisible(false);
//                    startDaySelect.setVisible(false);
//                    endDaySelect.setVisible(false);
//                    sessionTypeSelect.setValue(CONTINUOUS);
//                } else {
//                    // this is a weekly or daily session
//                    value = settings.get(Session.SETTING_START_DAY);
//                    if(value != null) {
//                        // this is a weekly session, nothing more needs to be done
//                        sessionTypeSelect.setValue(WEEKLY);
//                    } else {
//                        // this is a daily session, hide the weekly settings
//                        startDaySelect.setVisible(false);
//                        endDaySelect.setVisible(false);
//                        sessionTypeSelect.setValue(DAILY);
//                    }
//                }
//            }
//            /**
//             * Update the UI fields from the FIX session variable.
//             */
//            private void updateFields()
//            {
//                Map<String,String> settings = inFixSession.getFixSession().getSessionSettings();
//                String value = String.valueOf(sessionTypeSelect.getValue());
//                switch(value) {
//                    case CONTINUOUS:
//                        startTimeField.setVisible(false);
//                        endTimeField.setVisible(false);
//                        timeZoneSelect.setVisible(false);
//                        startDaySelect.setVisible(false);
//                        endDaySelect.setVisible(false);
//                        break;
//                    case DAILY:
//                        startTimeField.setVisible(true);
//                        startTimeField.setValue(settings.containsKey(Session.SETTING_START_TIME)?settings.get(Session.SETTING_START_TIME):"00:00:00");
//                        endTimeField.setVisible(true);
//                        endTimeField.setValue(settings.containsKey(Session.SETTING_END_TIME)?settings.get(Session.SETTING_END_TIME):"00:00:00");
//                        timeZoneSelect.setVisible(true);
//                        timeZoneSelect.setValue(settings.containsKey(Session.SETTING_TIMEZONE)?settings.get(Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
//                        startDaySelect.setVisible(false);
//                        endDaySelect.setVisible(false);
//                        break;
//                    case WEEKLY:
//                        startTimeField.setVisible(true);
//                        startTimeField.setValue(settings.containsKey(Session.SETTING_START_TIME)?settings.get(Session.SETTING_START_TIME):"00:00:00");
//                        endTimeField.setVisible(true);
//                        endTimeField.setValue(settings.containsKey(Session.SETTING_END_TIME)?settings.get(Session.SETTING_END_TIME):"00:00:00");
//                        timeZoneSelect.setVisible(true);
//                        timeZoneSelect.setValue(settings.containsKey(Session.SETTING_TIMEZONE)?settings.get(Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
//                        startDaySelect.setVisible(true);
//                        startDaySelect.setValue(settings.containsKey(Session.SETTING_START_DAY)?settings.get(Session.SETTING_START_DAY):FixSessionDay.Monday.name());
//                        endDaySelect.setVisible(true);
//                        endDaySelect.setValue(settings.containsKey(Session.SETTING_END_DAY)?settings.get(Session.SETTING_END_DAY):FixSessionDay.Friday.name());
//                        break;
//                }
//            }
//            /**
//             * Update the FIX session variable from the UI fields.
//             */
//            private void updateFixSession()
//            {
//                Map<String,String> settings = inFixSession.getFixSession().getSessionSettings();
//                String value = String.valueOf(sessionTypeSelect.getValue());
//                switch(value) {
//                    case CONTINUOUS:
//                        settings.remove(Session.SETTING_START_TIME);
//                        settings.remove(Session.SETTING_END_TIME);
//                        settings.remove(Session.SETTING_START_DAY);
//                        settings.remove(Session.SETTING_END_DAY);
//                        settings.remove(Session.SETTING_TIMEZONE);
//                        settings.put(Session.SETTING_NON_STOP_SESSION,
//                                     YES);
//                        break;
//                    case DAILY:
//                        settings.remove(Session.SETTING_START_DAY);
//                        settings.remove(Session.SETTING_END_DAY);
//                        settings.remove(Session.SETTING_NON_STOP_SESSION);
//                        settings.put(Session.SETTING_START_TIME,
//                                     startTimeField.getValue());
//                        settings.put(Session.SETTING_END_TIME,
//                                     endTimeField.getValue());
//                        settings.put(Session.SETTING_TIMEZONE,
//                                     String.valueOf(timeZoneSelect.getValue()));
//                        break;
//                    case WEEKLY:
//                        settings.remove(Session.SETTING_NON_STOP_SESSION);
//                        settings.put(Session.SETTING_START_TIME,
//                                     startTimeField.getValue());
//                        settings.put(Session.SETTING_END_TIME,
//                                     endTimeField.getValue());
//                        settings.put(Session.SETTING_TIMEZONE,
//                                     String.valueOf(timeZoneSelect.getValue()));
//                        settings.put(Session.SETTING_START_DAY,
//                                     String.valueOf(startDaySelect.getValue()));
//                        settings.put(Session.SETTING_END_DAY,
//                                     String.valueOf(endDaySelect.getValue()));
//                        break;
//                }
//            }
//            /**
//             * Indicates if the given session is a weekly session.
//             *
//             * @param inFixSession a <code>FixSession</code> value
//             * @return a <code>boolean</code> value
//             */
//            private boolean isWeeklySession(FixSession inFixSession)
//            {
//                return inFixSession.getSessionSettings().containsKey(Session.SETTING_START_DAY);
//            }
//            /**
//             * Indicates if the given session is a non-stop, continuous session.
//             *
//             * @param inFixSession a <code>FixSession</code> value
//             * @return a <code>boolean</code> value
//             */
//            private boolean isContinuousSession(FixSession inFixSession)
//            {
//                return YES.equals(inFixSession.getSessionSettings().get(Session.SETTING_NON_STOP_SESSION));
//            }
//            /**
//             * UI widget for session type
//             */
//            private ComboBox sessionTypeSelect = new ComboBox("Session Type");
//            /**
//             * UI widget for session start time
//             */
//            private TextField startTimeField = new TextField("Start Time");
//            /**
//             * UI widget for session end time
//             */
//            private TextField endTimeField = new TextField("End Time");
//            /**
//             * UI widget for time zone
//             */
//            private ComboBox timeZoneSelect = new ComboBox("Time Zone");
//            /**
//             * UI widget for start day
//             */
//            private ComboBox startDaySelect = new ComboBox("Start Day");
//            /**
//             * UI widget for end day
//             */
//            private ComboBox endDaySelect = new ComboBox("End Day");
//            /**
//             * value for continuous session
//             */
//            private final String CONTINUOUS = "Continuous";
//            /**
//             * value for daily session
//             */
//            private final String DAILY = "Daily";
//            /**
//             * value for weekly session
//             */
//            private final String WEEKLY = "Weekly";
//            /**
//             * value for turning a binary FIX session attribute on
//             */
//            private final String YES = "Y";
//        });
//        fixSessionWizard.addStep(new WizardStep() {
//            @Override
//            public String getCaption()
//            {
//                return "Settings";
//            }
//            @Override
//            public Component getContent()
//            {
//                VerticalLayout formLayout = new VerticalLayout();
//                formLayout.setMargin(true);
//                formLayout.setSizeFull();
//                final TextArea fieldDescription = new TextArea();
//                fieldDescription.setWidth("100%");
//                fieldDescription.setHeightUndefined();
//                fieldDescription.setWordwrap(true);
//                fieldDescription.setStyleName(ValoTheme.TEXTAREA_BORDERLESS);
//                final Grid descriptorGrid = new Grid();
//                descriptorGrid.setSizeFull();
//                final BeanItemContainer<DecoratedDescriptor> dataSource = new BeanItemContainer<>(DecoratedDescriptor.class,
//                                                                                                  sortedDescriptors.values());
//                descriptorGrid.setContainerDataSource(dataSource);
//                descriptorGrid.setColumns("name","value");
//                descriptorGrid.getColumn("name").setEditable(false);
//                descriptorGrid.getColumn("value").setEditable(true);
//                descriptorGrid.setEditorEnabled(true);
//                final DecoratedDescriptor selectedDescriptor = new DecoratedDescriptor();
//                descriptorGrid.addItemClickListener(inEvent -> {
//                    ItemClickEvent itemClickEvent = (ItemClickEvent)inEvent;
//                    Item clickItem = itemClickEvent.getItem();
//                    selectedDescriptor.setAdvice(String.valueOf(clickItem.getItemProperty("advice")));
//                    selectedDescriptor.setDefaultValue(String.valueOf(clickItem.getItemProperty("defaultValue")));
//                    selectedDescriptor.setDescription(String.valueOf(clickItem.getItemProperty("description")));
//                    selectedDescriptor.setName(String.valueOf(clickItem.getItemProperty("name")));
//                    selectedDescriptor.setPattern(String.valueOf(clickItem.getItemProperty("pattern")));
//                    selectedDescriptor.setRequired(Boolean.parseBoolean(String.valueOf(clickItem.getItemProperty("required"))));
//                    fieldDescription.setReadOnly(false);
//                    fieldDescription.setValue(selectedDescriptor.getDescription());
//                    fieldDescription.setReadOnly(true);
//                });
//                // create an editor specific to the selected row
//                descriptorGrid.setEditorFieldFactory(new FieldGroupFieldFactory() {
//                    @Override
//                    @SuppressWarnings("rawtypes")
//                    public <T extends Field> T createField(Class<?> inDataType,
//                                                           Class<T> inFieldType)
//                    {
//                        final TextField textField = new TextField();
//                        textField.setRequired(selectedDescriptor.isRequired());
//                        textField.setDescription(selectedDescriptor.getDescription());
//                        textField.setNullRepresentation("");
//                        textField.addValueChangeListener(inEvent -> {
//                            ValueChangeEvent valueChangeEvent = (ValueChangeEvent)inEvent;
//                            String value = String.valueOf(valueChangeEvent.getProperty().getValue());
//                            inFixSession.getFixSession().getSessionSettings().put(selectedDescriptor.getName(),
//                                                                   value);
//                        });
//                        String pattern = StringUtils.trimToNull(selectedDescriptor.getPattern());
//                        if(pattern != null) {
//                            String advice = StringUtils.trimToNull(selectedDescriptor.getAdvice());
//                            if(advice == null) {
//                                advice = "Does not match pattern " + pattern;
//                            }
//                            RegexpValidator validator = new RegexpValidator(pattern,
//                                                                            advice);
//                            validator.setErrorMessage(advice);
//                            textField.addValidator(validator);
//                        }
//                        return inFieldType.cast(textField);
//                    }
//                    private static final long serialVersionUID = -5965893350850268432L;
//                });
//                formLayout.addComponents(descriptorGrid,
//                                         fieldDescription);
//                return formLayout;
//            }
//            @Override
//            public boolean onAdvance()
//            {
//                for(DecoratedDescriptor descriptor : sortedDescriptors.values()) {
//                    if(StringUtils.trimToNull(descriptor.getValue()) != null) {
//                        inFixSession.getFixSession().getSessionSettings().put(descriptor.getName(),
//                                                              descriptor.getValue());
//                    }
//                }
//                try {
//                    if(inIsNew) {
//                        SLF4JLoggerProxy.debug(ClusterView.this,
//                                               "Submitting new fix session: {}",
//                                               inFixSession);
//                        AdminClientService.getInstance().createFixSession(inFixSession.getFixSession());
//                    } else {
//                        SLF4JLoggerProxy.debug(ClusterView.this,
//                                               "Submitting fix session for update: {}",
//                                               inFixSession);
//                        AdminClientService.getInstance().updateFixSession(incomingName,
//                                                                          inFixSession.getFixSession());
//                    }
//                } catch (Exception e) {
//                    Notification.show("Error Saving Session",
//                                      "A problem occurred saving the session: " + ExceptionUtils.getRootCauseMessage(e),
//                                      Type.ERROR_MESSAGE);
//                    return false;
//                }
//                return true;
//            }
//            @Override
//            public boolean onBack()
//            {
//                return true;
//            }
//        });
//        fixSessionWizard.setWidth("640px");
//        fixSessionWizard.setHeightUndefined();
//        wizardLayout.addComponent(fixSessionWizard);
//        wizardLayout.setComponentAlignment(fixSessionWizard,
//                                           Alignment.MIDDLE_CENTER);
//        content.addComponents(wizardLayout);
//        UI.getCurrent().addWindow(subWindow);
    }
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * global name of this view
     */
    private static final String NAME = "Cluster View";
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
    private static final long serialVersionUID = 1901286026590258969L;
}
