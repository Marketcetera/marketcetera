package org.marketcetera.ui.fix.view;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.cluster.MutableClusterData;
import org.marketcetera.cluster.SimpleClusterData;
import org.marketcetera.core.Pair;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionDay;
import org.marketcetera.fix.FixSessionInstanceData;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableActiveFixSession;
import org.marketcetera.fix.impl.SimpleActiveFixSession;
import org.marketcetera.fix.impl.SimpleFixSession;
import org.marketcetera.fix.impl.SimpleFixSessionAttributeDescriptor;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.ui.PhotonApp;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.AuthorizationHelperService;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.ValidatingTextField;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* $License$ */

/**
 * Provides a view for FIX Sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FixSessionView
        extends AbstractContentView
        implements ContentView
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getMainLayout()
     */
    @Override
    public Region getMainLayout()
    {
        return rootLayout;
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onBrokerStatusChange(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    protected void onBrokerStatusChange(ActiveFixSession inActiveFixSession)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} receiveBrokerStatus: {}",
                               PlatformServices.getServiceName(getClass()),
                               inActiveFixSession);
        String fixSessionName = inActiveFixSession.getFixSession().getName();
        DisplayFixSession fixSessionToUpdate = null;
        for(DisplayFixSession fixSession : fixSessionsTable.getItems()) {
            if(fixSession.nameProperty().get().equals(fixSessionName)) {
                fixSessionToUpdate = fixSession;
                break;
            }
        }
        if(fixSessionToUpdate != null) {
            final DisplayFixSession displayFixSession = fixSessionToUpdate;
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    displayFixSession.statusProperty().set(inActiveFixSession.getStatus());
                    displayFixSession.senderSeqNumProperty().set((inActiveFixSession.getSenderSequenceNumber()));
                    displayFixSession.targetSeqNumProperty().set(inActiveFixSession.getTargetSequenceNumber());
                }}
            );
        } else {
            updateSessions();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
      return NAME;
    }
    /**
     * Create a new FixSessionView instance.
     *
     * @param inParent a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public FixSessionView(Region inParent,
                          NewWindowEvent inEvent,
                          Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        fixAdminClient = serviceManager.getService(AdminClientService.class);
        rootLayout = new VBox();
        buttonLayout = new HBox();
        rootLayout.setPadding(new Insets(10));
        addFixSessionButton = new Button("Add FIX Session");
        addFixSessionButton.setOnAction(inEvent -> {
            MutableClusterData clusterData = new SimpleClusterData();
            FixSession newFixSession = new SimpleFixSession();
            MutableActiveFixSession newActiveFixSession = new SimpleActiveFixSession(newFixSession,
                                                                                     clusterData,
                                                                                     FixSessionStatus.UNKNOWN,
                                                                                     null);
            DisplayFixSession selectedItem = new DisplayFixSession(newActiveFixSession);
            updateFixSession(selectedItem,
                             true);
        });
        buttonLayout.getChildren().add(addFixSessionButton);
        buttonLayout.setPadding(new Insets(5));
        addFixSessionButton.visibleProperty().set(authzHelperService.hasPermission(AdminPermissions.AddSessionAction));
        initializeTable();
        rootLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
        rootLayout.getChildren().addAll(fixSessionsTable,
                                        buttonLayout);
        updateSessions();
    }
    /**
     * Initialize the table widget.
     */
    private void initializeTable()
    {
        fixSessionsTable = new TableView<>();
        initializeColumns();
        initializeContextMenu();
        fixSessionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fixSessionsTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<DisplayFixSession>) (inObservable,inOldValue,inNewValue) -> {
            if(inNewValue == null) {
                return;
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    for(MenuItem contextMenuItem : fixSessionsTable.getContextMenu().getItems()) {
                        contextMenuItem.setDisable(true);
                    }
                    switch(inNewValue.statusProperty().get()) {
                        case AFFINITY_MISMATCH:
                        case BACKUP:
                        case DELETED:
                        case UNKNOWN:
                            break;
                        case CONNECTED:
                        case DISCONNECTED:
                        case NOT_CONNECTED:
                            stopSessionContextMenuItem.setDisable(false);
                            break;
                        case DISABLED:
                            enableSessionContextMenuItem.setDisable(false);
                            sequenceSessionContextMenuItem.setDisable(false);
                            editSessionContextMenuItem.setDisable(false);
                            deleteSessionContextMenuItem.setDisable(false);
                            break;
                        case STOPPED:
                            startSessionContextMenuItem.setDisable(false);
                            disableSessionContextMenuItem.setDisable(false);
                            sequenceSessionContextMenuItem.setDisable(false);
                            break;
                        default:
                            break;
                     }
                }}
            );
        });
        fixSessionsTable.prefWidthProperty().bind(getParentWindow().widthProperty());
        fixSessionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    /**
     * Update the sessions displayed in the table.
     */
    private void updateSessions()
    {
        CollectionPageResponse<ActiveFixSession> response = fixAdminClient.getFixSessions(PageRequest.ALL);
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                fixSessionsTable.getItems().clear();
                for(ActiveFixSession activeFixSession : response.getElements()) {
                    fixSessionsTable.getItems().add(new DisplayFixSession(activeFixSession));
                }
            }}
        );
    }
    /**
     * Initialize the FIX table columns.
     */
    private void initializeColumns()
    {
        nameTableColumn = new TableColumn<>("Name");
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        brokerIdTableColumn = new TableColumn<>("Broker Id");
        brokerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("brokerId"));
        hostIdTableColumn = new TableColumn<>("Host Id");
        hostIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("hostId"));
        statusTableColumn = new TableColumn<>("Status");
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        senderSequenceNumberTableColumn = new TableColumn<>("Sender Seq Num");
        senderSequenceNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("senderSeqNum"));
        targetSequenceNumberTableColumn = new TableColumn<>("Target Seq Num");
        targetSequenceNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("targetSeqNum"));
        fixSessionsTable.getColumns().add(nameTableColumn);
        fixSessionsTable.getColumns().add(brokerIdTableColumn);
        fixSessionsTable.getColumns().add(hostIdTableColumn);
        fixSessionsTable.getColumns().add(statusTableColumn);
        fixSessionsTable.getColumns().add(senderSequenceNumberTableColumn);
        fixSessionsTable.getColumns().add(targetSequenceNumberTableColumn);
    }
    /**
     * Perform the context menu session action according to the given parameters.
     *
     * @param inSelectedItem a <code>DisplayFixSession</code> value
     * @param inTitle a <code>String</code> value
     * @param inContent a <code>String</code> value
     * @param inAction a <code>Consumer&lt;String&gt;</code> value
     */
    private void performSessionAction(DisplayFixSession inSelectedItem,
                                      String inTitle,
                                      String inContent,
                                      Consumer<String> inAction)
    {
        if(inSelectedItem == null) {
            return;
        }
        Alert alert = PhotonServices.generateAlert(inTitle,
                                                   inContent + "?",
                                                   AlertType.CONFIRMATION);
        ButtonType okButton = new ButtonType("Ok",
                                             ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel",
                                                 ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton,
                                      cancelButton);
        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                try {
                    SLF4JLoggerProxy.info(FixSessionView.this,
                                          "{} performing {} on {}",
                                          SessionUser.getCurrent(),
                                          inTitle,
                                          inSelectedItem);
                    inAction.accept(inSelectedItem.nameProperty().get());
                    SLF4JLoggerProxy.info(FixSessionView.this,
                                          "{} on {} succeeded",
                                          inTitle,
                                          inSelectedItem);
                    updateSessions();
                    uiMessageService.post(new NotificationEvent(inTitle,
                                                                 inContent + " succeeded",
                                                                 AlertType.INFORMATION));
                } catch (Exception e) {
                    String message = PlatformServices.getMessage(e);
                    SLF4JLoggerProxy.warn(FixSessionView.this,
                                          e,
                                          "Unable to perform {} on {}: {}",
                                          inTitle,
                                          inSelectedItem,
                                          message);
                    uiMessageService.post(new NotificationEvent(inTitle,
                                                                 inContent + " failed: " + message,
                                                                 AlertType.ERROR));
                }
            } else {
                return;
            }
        });
    }
    /**
     * Initialize the FIX sessions context menu.
     */
    private void initializeContextMenu()
    {
        fixSessionsContextMenu = new ContextMenu();
        fixSessionsTable.setContextMenu(fixSessionsContextMenu);
        enableSessionContextMenuItem = new MenuItem("Enable");
        enableSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            performSessionAction(selectedItem,
                                 "Enable Session",
                                 "Enable " + selectedItem.nameProperty().get(),
                                 fixAdminClient::enableSession);
        });
        disableSessionContextMenuItem = new MenuItem("Disable");
        disableSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            performSessionAction(selectedItem,
                                 "Disable Session",
                                 "Disable " + selectedItem.nameProperty().get(),
                                 fixAdminClient::disableSession);
        });
        startSessionContextMenuItem = new MenuItem("Start");
        startSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            performSessionAction(selectedItem,
                                 "Start Session",
                                 "Start " + selectedItem.nameProperty().get(),
                                 fixAdminClient::startSession);
        });
        stopSessionContextMenuItem = new MenuItem("Stop");
        stopSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            performSessionAction(selectedItem,
                                 "Stop Session",
                                 "Stop " + selectedItem.nameProperty().get(),
                                 fixAdminClient::stopSession);
        });
        editSessionContextMenuItem = new MenuItem("Edit");
        editSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            updateFixSession(selectedItem,
                             false);
        });
        deleteSessionContextMenuItem = new MenuItem("Delete");
        deleteSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            performSessionAction(selectedItem,
                                 "Delete Session",
                                 "Delete " + selectedItem.nameProperty().get(),
                                 fixAdminClient::deleteSession);
        });
        sequenceSessionContextMenuItem = new MenuItem("Update Sequence Numbers");
        sequenceSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            doUpdateSessionSequenceNumbers(selectedItem);
            updateSessions();
        });
        if(authzHelperService.hasPermission(AdminPermissions.EnableSessionAction)) {
            fixSessionsContextMenu.getItems().add(enableSessionContextMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.DisableSessionAction)) {
            fixSessionsContextMenu.getItems().add(disableSessionContextMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.StartSessionAction)) {
            fixSessionsContextMenu.getItems().add(startSessionContextMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.StopSessionAction)) {
            fixSessionsContextMenu.getItems().add(stopSessionContextMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.EditSessionAction)) {
            fixSessionsContextMenu.getItems().add(editSessionContextMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.DeleteSessionAction)) {
            fixSessionsContextMenu.getItems().add(deleteSessionContextMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.UpdateSequenceAction)) {
            fixSessionsContextMenu.getItems().add(sequenceSessionContextMenuItem);
        }
        for(MenuItem contextMenuItem : fixSessionsContextMenu.getItems()) {
            contextMenuItem.setDisable(true);
        }
    }
    /**
     * Gathers the information necessary to update the given FIX session and performs the update.
     *
     * @param inFixSession a <code>DisplayFixSession</code> value
     */
    private void updateFixSession(DisplayFixSession inFixSession,
                                  boolean inIsNew)
    {
        final String acceptorString = "Acceptor";
        final String initiatorString = "Initiator";
        final String incomingFixSessionName = inFixSession.nameProperty().get();
        Wizard wizard = new Wizard(PhotonApp.getPrimaryStage());
        wizard.setTitle((inIsNew ? "Add" : "Edit") + " Session");
        final ComboBox<String> connectionTypeComboBox = new ComboBox<>();
        connectionTypeComboBox.getItems().addAll(acceptorString,
                                                 initiatorString);
        connectionTypeComboBox.setId("sessionType.connectionType");
        connectionTypeComboBox.setTooltip(new Tooltip("Indicates whether the session will receive orders (acceptor) or send them (initiator)"));
        final ValidatingTextField affinityTextField = new ValidatingTextField(input -> input.matches("^\\d+$"));
        affinityTextField.setId("sessionType.affinity");
        affinityTextField.setTooltip(new Tooltip("Indicates which cluster instance will host this session, if unsure, leave as 1"));
        final Label adviceLabel = new Label();
        final BooleanProperty sessionTypeValid = new SimpleBooleanProperty(false);
        connectionTypeComboBox.valueProperty().addListener((observable,oldValue,newValue) -> {
            sessionTypeValid.set(!(connectionTypeComboBox.getValue() != null && affinityTextField.isValidProperty().get()));
        });
        affinityTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            sessionTypeValid.set(!(connectionTypeComboBox.getValue() != null && affinityTextField.isValidProperty().get()));
        });
        affinityTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                affinityTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.setText("");
                adviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                affinityTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.setText("affinity must be an integer value");
                adviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        WizardPane sessionTypePane = new WizardPane() {
            /* (non-Javadoc)
             * @see org.controlsfx.dialog.WizardPane#onEnteringPage(org.controlsfx.dialog.Wizard)
             */
            @Override
            public void onEnteringPage(Wizard inWizard)
            {
                super.onEnteringPage(inWizard);
                connectionTypeComboBox.setValue(inIsNew ? "Initiator" : inFixSession.sourceProperty().get().getFixSession().isAcceptor() ? acceptorString:initiatorString);
                affinityTextField.setText(inIsNew ? "1" : String.valueOf(inFixSession.sourceProperty().get().getFixSession().getAffinity()));
                inWizard.invalidProperty().bind(sessionTypeValid);
            }
            /* (non-Javadoc)
             * @see org.controlsfx.dialog.WizardPane#onExitingPage(org.controlsfx.dialog.Wizard)
             */
            @Override
            public void onExitingPage(Wizard inWizard)
            {
                inFixSession.sourceProperty().get().getFixSession().getMutableView().setIsAcceptor(connectionTypeComboBox.getValue().equals(acceptorString));
                inFixSession.sourceProperty().get().getFixSession().getMutableView().setAffinity(Integer.parseInt(affinityTextField.getText()));
                super.onExitingPage(inWizard);
            }
        };
        sessionTypePane.setId("sessionType");
        GridPane sessionTypeLayout = new GridPane();
        sessionTypeLayout.setHgap(10);
        sessionTypeLayout.setVgap(10);
        sessionTypeLayout.setPadding(new Insets(20,150,10,10));
        sessionTypePane.setHeaderText("Session Type");
        sessionTypeLayout.add(new Label("Connection Type"),0,0);
        sessionTypeLayout.add(connectionTypeComboBox,1,0);
        sessionTypeLayout.add(new Label("Affinity"),0,1);
        sessionTypeLayout.add(affinityTextField,1,1);
        sessionTypeLayout.add(adviceLabel,0,2,2,1);
        sessionTypePane.setContent(sessionTypeLayout);
        // network settings
        final BooleanProperty networkInvalid = new SimpleBooleanProperty(false);
        ValidatingTextField hostnameTextField = new ValidatingTextField(input -> PhotonServices.isValidHostNameSyntax(input));
        hostnameTextField.setId("network.hostname");
        hostnameTextField.setPromptText("fix gateway host or ip address");
        ValidatingTextField portTextField = new ValidatingTextField(input -> input.matches("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$"));
        portTextField.setId("network.port");
        portTextField.setPromptText("fix gateway port");
        hostnameTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            networkInvalid.set(!(hostnameTextField.isValidProperty().get() && portTextField.isValidProperty().get()));
        });
        portTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            networkInvalid.set(!(hostnameTextField.isValidProperty().get() && portTextField.isValidProperty().get()));
        });
        // add test connection button and label and add to validators
        Button testConnectionButton = new Button("Test Connection");
        Label testConnectionLabel = new Label();
        networkInvalid.addListener((observable,oldValue,newValue) -> {
            if(inFixSession.sourceProperty().get().getFixSession().isAcceptor()) {
                testConnectionButton.setDisable(true);
                testConnectionButton.setVisible(false);
                testConnectionLabel.setDisable(true);
                testConnectionLabel.setVisible(false);
            } else {
                testConnectionButton.setDisable(newValue);
                testConnectionButton.setVisible(!newValue);
                testConnectionLabel.setDisable(newValue);
                testConnectionLabel.setVisible(!newValue);
            }
        });
        hostnameTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                hostnameTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.setText("");
                adviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                hostnameTextField.setStyle(PhotonServices.errorStyle);
                adviceLabel.setText("hostname must be a valid host specification");
                adviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        portTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                portTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.setText("");
                adviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                portTextField.setStyle(PhotonServices.errorStyle);
                adviceLabel.setText("port must be a valid port");
                adviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        WizardPane networkPane = new WizardPane() {
            /* (non-Javadoc)
             * @see org.controlsfx.dialog.WizardPane#onEnteringPage(org.controlsfx.dialog.Wizard)
             */
            @Override
            public void onEnteringPage(Wizard inWizard)
            {
                super.onEnteringPage(inWizard);
                if(inFixSession.sourceProperty().get().getFixSession().isAcceptor()) {
                    hostnameTextField.setTooltip(new Tooltip("The acceptor hostname is determined by the server and is not modifiable"));
                    hostnameTextField.setDisable(true);
                    portTextField.setTooltip(new Tooltip("The acceptor port is determined by the server cluster framework and is not modifiable"));
                    portTextField.setDisable(true);
                    testConnectionButton.setDisable(true);
                    testConnectionButton.setVisible(false);
                    testConnectionLabel.setDisable(true);
                    testConnectionLabel.setVisible(false);
                    FixSessionInstanceData instanceData = AdminClientService.getInstance().getFixSessionInstanceData(inFixSession.sourceProperty().get().getFixSession().getAffinity());
                    hostnameTextField.setText(instanceData.getHostname());
                    portTextField.setText(String.valueOf(instanceData.getPort()));
                } else {
                    hostnameTextField.setTooltip(new Tooltip("Hostname of the FIX gateway to connect to"));
                    hostnameTextField.setDisable(false);
                    portTextField.setTooltip(new Tooltip("Port of the FIX gateway to connect to"));
                    portTextField.setDisable(false);
                    testConnectionButton.setDisable(networkInvalid.get());
                    testConnectionButton.setVisible(!networkInvalid.get());
                    testConnectionLabel.setDisable(networkInvalid.get());
                    testConnectionLabel.setVisible(!networkInvalid.get());
                    if(inIsNew) {
                        hostnameTextField.setText("exchange.marketcetera.com");
                        portTextField.setText("7004");
                    } else {
                        hostnameTextField.setText(inFixSession.sourceProperty().get().getFixSession().getHost());
                        portTextField.setText(String.valueOf(inFixSession.sourceProperty().get().getFixSession().getPort()));
                    }
                }
                inWizard.invalidProperty().bind(networkInvalid);
            }
            /* (non-Javadoc)
             * @see org.controlsfx.dialog.WizardPane#onExitingPage(org.controlsfx.dialog.Wizard)
             */
            @Override
            public void onExitingPage(Wizard inWizard)
            {
                if(inFixSession.sourceProperty().get().getFixSession().isAcceptor()) {
                    
                } else {
                    inFixSession.sourceProperty().get().getFixSession().getMutableView().setHost(hostnameTextField.getText());
                    inFixSession.sourceProperty().get().getFixSession().getMutableView().setPort(Integer.parseInt(portTextField.getText()));
                }
                super.onExitingPage(inWizard);
            }
        };
        GridPane networkLayout = new GridPane();
        networkLayout.setHgap(10);
        networkLayout.setVgap(10);
        networkLayout.setPadding(new Insets(20,150,10,10));
        networkPane.setHeaderText("Network");
        networkLayout.add(new Label("Hostname"),0,0);
        networkLayout.add(hostnameTextField,1,0);
        networkLayout.add(new Label("Port"),0,1);
        networkLayout.add(portTextField,1,1);
        networkLayout.add(adviceLabel,0,2,2,1);
        networkLayout.add(testConnectionButton,0,3);
        networkLayout.add(testConnectionLabel,1,3);
        networkPane.setContent(networkLayout);
        testConnectionButton.setOnAction(event -> {
            testConnectionLabel.setText("");
            testConnectionLabel.setStyle(PhotonServices.successMessage);
            String hostname = hostnameTextField.getText();
            int port = Integer.parseInt(portTextField.getText());
            SocketAddress socketAddress = new InetSocketAddress(hostname,
                                                                port);
            Socket socket = new Socket();
            // Timeout required - it's in milliseconds
            int timeout = 2000;
            try {
                socket.connect(socketAddress,
                               timeout);
                socket.close();
                testConnectionLabel.setText("Test connection succeeded");
                testConnectionLabel.setStyle(PhotonServices.successMessage);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(PhotonServices.class,
                                      "Unable to connect to {}:{} -> " + e.getMessage(),
                                      hostname,
                                      port,
                                      ExceptionUtils.getRootCauseMessage(e));
                testConnectionLabel.setText("Test connection failed: " + ExceptionUtils.getRootCauseMessage(e));
                testConnectionLabel.setStyle(PhotonServices.errorMessage);
            } finally {
                if(networkLayout.getScene() != null) {
                    ((Stage)networkLayout.getScene().getWindow()).sizeToScene();
                }
            }
        });
        wizard.setFlow(new LinearFlow(sessionTypePane,
                                      networkPane,
                                      initializeSessionIdentityPane(inFixSession,inIsNew,adviceLabel),
                                      new SessionTimesPane(inFixSession,inIsNew).generateWizardPane("Session Times"),
                                      new SessionSettingsPane(inFixSession,inIsNew).generateWizardPane("Settings")));
        // show wizard and wait for response
        PhotonServices.style(sessionTypePane.getScene());
        wizard.showAndWait().ifPresent(result -> {
            if(result == ButtonType.FINISH) {
                try {
                    // fix session should now be updated
                    if(inIsNew) {
                        SLF4JLoggerProxy.info(this,
                                              "{} creating {}",
                                              SessionUser.getCurrent(),
                                              inFixSession);
                        fixAdminClient.createFixSession(inFixSession.sourceProperty().get().getFixSession());
                        uiMessageService.post(new NotificationEvent("Create FIX Session",
                                                                     "Create FIX Session '" + inFixSession.nameProperty().get() + "' succeeded",
                                                                     AlertType.INFORMATION));
                    } else {
                        SLF4JLoggerProxy.info(this,
                                              "{} updating {} -> {}",
                                              SessionUser.getCurrent(),
                                              incomingFixSessionName,
                                              inFixSession);
                        fixAdminClient.updateFixSession(incomingFixSessionName,
                                                        inFixSession.sourceProperty().get().getFixSession());
                        uiMessageService.post(new NotificationEvent("Update FIX Session",
                                                                     "Update FIX Session '" + inFixSession.nameProperty().get() + "' succeeded",
                                                                     AlertType.INFORMATION));
                    }
                } catch (Exception e) {
                    String message = PlatformServices.getMessage(e);
                    SLF4JLoggerProxy.warn(FixSessionView.this,
                                          e,
                                          "Unable to create or update FIX session {}: {}",
                                          inFixSession,
                                          message);
                    uiMessageService.post(new NotificationEvent("Create or Update FIX Session",
                                                                 "Create or update FIX sesssion '" + inFixSession.nameProperty().get() + " 'failed: " + message,
                                                                 AlertType.ERROR));
                }
            }
        });
    }
    private WizardPane initializeSessionIdentityPane(DisplayFixSession inFixSession,
                                                     boolean inIsNew,
                                                     Label inAdviceLabel)
    {
        ValidatingTextField sessionNameTextField;
        ValidatingTextField sessionDescriptionTextField;
        ValidatingTextField sessionBrokerIdTextField;
        ComboBox<FIXVersion> fixVersionComboBox;
        ValidatingTextField senderCompIdTextField;
        ValidatingTextField targetCompIdTextField;
        final Collection<ActiveFixSession> existingSessions = AdminClientService.getInstance().getFixSessions();
        final SortedMap<String,DecoratedDescriptor> sortedDescriptors = new TreeMap<>();
        final Collection<FixSessionAttributeDescriptor> descriptors = AdminClientService.getInstance().getFixSessionAttributeDescriptors();
        for(FixSessionAttributeDescriptor descriptor : descriptors) {
            DecoratedDescriptor actualDescriptor = new DecoratedDescriptor(descriptor);
            sortedDescriptors.put(descriptor.getName(),
                                  actualDescriptor);
        }
        final BooleanProperty sessionIdentityInvalid = new SimpleBooleanProperty(false);
        Predicate<String> sessionNameValidator = new Predicate<>() {
            @Override
            public boolean test(String inValue)
            {
                String computedValue = StringUtils.trimToNull(inValue);
                if(computedValue == null) {
                    return false;
                }
                if(computedValue.length() > 255) {
                    inAdviceLabel.setText("Name may contain up to 255 characters");
                    return false;
                }
                for(ActiveFixSession existingSession : existingSessions) {
                    if(inIsNew && existingSession.getFixSession().getName().equals(computedValue)) {
                        inAdviceLabel.setText("Name is already in use");
                        return false;
                    }
                }
                return true;
            }
        };
        sessionNameTextField = new ValidatingTextField(sessionNameValidator);
        sessionNameTextField.setId("sesssionIdentity.sessionName");
        sessionNameTextField.setPromptText("fix session name");
        sessionNameTextField.setTooltip(new Tooltip("Unique human-readable name of the session"));
        sessionDescriptionTextField = new ValidatingTextField(inValue -> {
            String computedValue = StringUtils.trimToNull(inValue);
            if(computedValue == null) {
                return false;
            }
            if(computedValue.length() > 255) {
                inAdviceLabel.setText("Description may contain up to 255 characters");
                return false;
            }
            return true;
        });
        sessionDescriptionTextField.setId("sesssionIdentity.sessionDescription");
        sessionDescriptionTextField.setPromptText("fix session description");
        sessionDescriptionTextField.setTooltip(new Tooltip("Optional description of the session"));
        sessionBrokerIdTextField = new ValidatingTextField(new Predicate<String>() {
            @Override
            public boolean test(String inValue)
            {
                String computedValue = StringUtils.trimToNull(inValue);
                if(computedValue == null) {
                    return false;
                }
                if(computedValue.length() > 255) {
                    inAdviceLabel.setText("Broker Id may contain up to 255 characters");
                    return false;
                }
                for(ActiveFixSession existingSession : existingSessions) {
                    if(inIsNew && existingSession.getFixSession().getBrokerId().equals(computedValue)) {
                        inAdviceLabel.setText("Broker Id is already in use");
                        return false;
                    }
                }
                return true;
            }}
        );
        sessionBrokerIdTextField.setId("sesssionIdentity.brokerId");
        sessionBrokerIdTextField.setPromptText("fix session broker id");
        sessionBrokerIdTextField.setTooltip(new Tooltip("Unique system identifier for this FIX session used to target orders, pick something short and descriptive"));
        fixVersionComboBox = new ComboBox<>();
        fixVersionComboBox.setId("sesssionIdentity.fixVersion");
        for(FIXVersion fixVersion : FIXVersion.values()) {
            if(fixVersion == FIXVersion.FIX_SYSTEM) {
                continue;
            }
            fixVersionComboBox.getItems().add(fixVersion);
        }
        senderCompIdTextField = new ValidatingTextField(inValue -> {
            String computedValue = StringUtils.trimToNull(inValue);
            if(computedValue == null) {
                return false;
            }
            if(computedValue.length() > 255) {
                inAdviceLabel.setText("Broker Id may contain up to 255 characters");
                return false;
            }
            return true;
        });
        senderCompIdTextField.setId("sesssionIdentity.senderCompId");
        senderCompIdTextField.setPromptText("sender comp id");
        senderCompIdTextField.setTooltip(new Tooltip("Sender Comp Id of the session"));
        targetCompIdTextField = new ValidatingTextField(inValue -> {
            String computedValue = StringUtils.trimToNull(inValue);
            if(computedValue == null) {
                return false;
            }
            if(computedValue.length() > 255) {
                inAdviceLabel.setText("Broker Id may contain up to 255 characters");
                return false;
            }
            return true;
        });
        targetCompIdTextField.setId("sesssionIdentity.targetCompId");
        targetCompIdTextField.setPromptText("target comp id");
        targetCompIdTextField.setTooltip(new Tooltip("Target Comp Id of the session"));
        sessionNameTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            sessionIdentityInvalid.set(!(sessionNameTextField.isValidProperty().get() &&
                    sessionDescriptionTextField.isValidProperty().get() &&
                    fixVersionComboBox.getValue() != null &&
                    sessionBrokerIdTextField.isValidProperty().get() &&
                    senderCompIdTextField.isValidProperty().get() &&
                    targetCompIdTextField.isValidProperty().get()));
        });
        sessionNameTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                sessionNameTextField.setStyle(PhotonServices.successStyle);
                inAdviceLabel.setText("");
                inAdviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                sessionNameTextField.setStyle(PhotonServices.errorStyle);
                inAdviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        sessionDescriptionTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            sessionIdentityInvalid.set(!(sessionNameTextField.isValidProperty().get() &&
                    sessionDescriptionTextField.isValidProperty().get() &&
                    fixVersionComboBox.getValue() != null &&
                    sessionBrokerIdTextField.isValidProperty().get() &&
                    senderCompIdTextField.isValidProperty().get() &&
                    targetCompIdTextField.isValidProperty().get()));
        });
        sessionDescriptionTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                sessionNameTextField.setStyle(PhotonServices.successStyle);
                inAdviceLabel.setText("");
                inAdviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                sessionNameTextField.setStyle(PhotonServices.errorStyle);
                inAdviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        fixVersionComboBox.valueProperty().addListener((observable,oldValue,newValue) -> {
            sessionIdentityInvalid.set(!(sessionNameTextField.isValidProperty().get() &&
                    sessionDescriptionTextField.isValidProperty().get() &&
                    fixVersionComboBox.getValue() != null &&
                    sessionBrokerIdTextField.isValidProperty().get() &&
                    senderCompIdTextField.isValidProperty().get() &&
                    targetCompIdTextField.isValidProperty().get()));
        });
        sessionBrokerIdTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            sessionIdentityInvalid.set(!(sessionNameTextField.isValidProperty().get() &&
                    sessionDescriptionTextField.isValidProperty().get() &&
                    fixVersionComboBox.getValue() != null &&
                    sessionBrokerIdTextField.isValidProperty().get() &&
                    senderCompIdTextField.isValidProperty().get() &&
                    targetCompIdTextField.isValidProperty().get()));
        });
        sessionBrokerIdTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                sessionBrokerIdTextField.setStyle(PhotonServices.successStyle);
                inAdviceLabel.setText("");
                inAdviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                sessionBrokerIdTextField.setStyle(PhotonServices.errorStyle);
                inAdviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        senderCompIdTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            sessionIdentityInvalid.set(!(sessionNameTextField.isValidProperty().get() &&
                    sessionDescriptionTextField.isValidProperty().get() &&
                    fixVersionComboBox.getValue() != null &&
                    sessionBrokerIdTextField.isValidProperty().get() &&
                    senderCompIdTextField.isValidProperty().get() &&
                    targetCompIdTextField.isValidProperty().get()));
        });
        senderCompIdTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                senderCompIdTextField.setStyle(PhotonServices.successStyle);
                inAdviceLabel.setText("");
                inAdviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                senderCompIdTextField.setStyle(PhotonServices.errorStyle);
                inAdviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        targetCompIdTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            sessionIdentityInvalid.set(!(sessionNameTextField.isValidProperty().get() &&
                    sessionDescriptionTextField.isValidProperty().get() &&
                    fixVersionComboBox.getValue() != null &&
                    sessionBrokerIdTextField.isValidProperty().get() &&
                    senderCompIdTextField.isValidProperty().get() &&
                    targetCompIdTextField.isValidProperty().get()));
        });
        targetCompIdTextField.isValidProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue) {
                targetCompIdTextField.setStyle(PhotonServices.successStyle);
                inAdviceLabel.setText("");
                inAdviceLabel.setStyle(PhotonServices.successMessage);
            } else {
                targetCompIdTextField.setStyle(PhotonServices.errorStyle);
                inAdviceLabel.setStyle(PhotonServices.errorMessage);
            }
        });
        // TODO factor this in, somehow, probably using chained predicates from a list
        // validate than a session id is new
        Predicate<String> sessionIdValidator = new Predicate<>() {
            @Override
            public boolean test(String value)
            {
                String fixVersionValue = fixVersionComboBox.getValue() == null ? null : String.valueOf(fixVersionComboBox.getValue());
                quickfix.SessionID sessionId = new quickfix.SessionID(fixVersionValue,
                                                                      senderCompIdTextField.getText(),
                                                                      targetCompIdTextField.getText());
                for(ActiveFixSession existingSession : existingSessions) {
                    if(inIsNew && existingSession.getFixSession().getSessionId().equals(sessionId.toString())) {
                        inAdviceLabel.setText("'"+existingSession.getFixSession().getSessionId() + "' is already in use");
                        return false;
                    }
                }
                return true;
            }
        };
        WizardPane sessionIdentityPane = new WizardPane() {
            /* (non-Javadoc)
             * @see org.controlsfx.dialog.WizardPane#onEnteringPage(org.controlsfx.dialog.Wizard)
             */
            @Override
            public void onEnteringPage(Wizard inWizard)
            {
                super.onEnteringPage(inWizard);
                sessionNameTextField.setText(inFixSession.sourceProperty().get().getFixSession().getName());
                sessionDescriptionTextField.setText(inFixSession.sourceProperty().get().getFixSession().getDescription());
                sessionBrokerIdTextField.setText(inFixSession.sourceProperty().get().getFixSession().getBrokerId());
                if(inFixSession.sourceProperty().get().getFixSession().getSessionId() != null) {
                    quickfix.SessionID sessionId = new quickfix.SessionID(inFixSession.sourceProperty().get().getFixSession().getSessionId());
                    if(sessionId.isFIXT()) {
                        String defaultApplVerId = inFixSession.sourceProperty().get().getFixSession().getSessionSettings().get(quickfix.Session.SETTING_DEFAULT_APPL_VER_ID);
                        if(defaultApplVerId != null) {
                            fixVersionComboBox.setValue(FIXVersion.getFIXVersion(new quickfix.field.ApplVerID(defaultApplVerId)));
                        }
                    } else {
                        fixVersionComboBox.setValue(FIXVersion.getFIXVersion(sessionId));
                    }
                    senderCompIdTextField.setText(sessionId.getSenderCompID());
                    targetCompIdTextField.setText(sessionId.getTargetCompID());
                } else {
                    fixVersionComboBox.setValue(FIXVersion.FIX42);
                }
                inWizard.invalidProperty().bind(sessionIdentityInvalid);
            }
            /* (non-Javadoc)
             * @see org.controlsfx.dialog.WizardPane#onExitingPage(org.controlsfx.dialog.Wizard)
             */
            @Override
            public void onExitingPage(Wizard inWizard)
            {
                inFixSession.sourceProperty().get().getFixSession().getMutableView().setName(sessionNameTextField.getText());
                inFixSession.sourceProperty().get().getFixSession().getMutableView().setDescription(sessionDescriptionTextField.getText());
                inFixSession.sourceProperty().get().getFixSession().getMutableView().setBrokerId(sessionBrokerIdTextField.getText());
                String fixVersionValue = fixVersionComboBox.getValue() == null ? null : String.valueOf(fixVersionComboBox.getValue());
                if(fixVersionValue != null) {
                    FIXVersion fixVersion = FIXVersion.getFIXVersion(fixVersionValue);
                    if(fixVersion.isFixT()) {
                        fixVersionValue = fixVersion.getApplicationVersion();
                        inFixSession.sourceProperty().get().getFixSession().getSessionSettings().put(quickfix.Session.SETTING_DEFAULT_APPL_VER_ID,
                                                                                          fixVersionValue);
                        DecoratedDescriptor defaultApplVerId = sortedDescriptors.get(quickfix.Session.SETTING_DEFAULT_APPL_VER_ID);
                        defaultApplVerId.setValue(fixVersionValue);
                        fixVersionValue = quickfix.FixVersions.BEGINSTRING_FIXT11;
                    }
                    quickfix.SessionID sessionId = new quickfix.SessionID(fixVersionValue,
                                                                          senderCompIdTextField.getText(),
                                                                          targetCompIdTextField.getText());
                    inFixSession.sourceProperty().get().getFixSession().getMutableView().setSessionId(sessionId.toString());
                }
            }
        };
        GridPane sessionIdentityLayout = new GridPane();
        sessionIdentityLayout.setHgap(10);
        sessionIdentityLayout.setVgap(10);
        sessionIdentityLayout.setPadding(new Insets(20,150,10,10));
        sessionIdentityPane.setHeaderText("Session Identity");
        sessionIdentityLayout.add(new Label("Name"),0,0);
        sessionIdentityLayout.add(sessionNameTextField,1,0);
        sessionIdentityLayout.add(new Label("Description"),0,1);
        sessionIdentityLayout.add(sessionDescriptionTextField,1,1);
        sessionIdentityLayout.add(new Label("Broker ID"),0,2);
        sessionIdentityLayout.add(sessionBrokerIdTextField,1,2);
        sessionIdentityLayout.add(new Label("Version"),0,3);
        sessionIdentityLayout.add(fixVersionComboBox,1,3);
        sessionIdentityLayout.add(new Label("Sender Comp Id"),0,4);
        sessionIdentityLayout.add(senderCompIdTextField,1,4);
        sessionIdentityLayout.add(new Label("Target Comp Id"),0,5);
        sessionIdentityLayout.add(targetCompIdTextField,1,5);
        sessionIdentityLayout.add(inAdviceLabel,0,6,2,1);
        sessionIdentityPane.setContent(sessionIdentityLayout);
        return sessionIdentityPane;
    }
    /**
     * Gathers the information needed and executions an optional sequence number update for the given session.
     *
     * @param inSession a <code>DisplayFixSession</code> value
     */
    private void doUpdateSessionSequenceNumbers(DisplayFixSession inSession)
    {
        Dialog<Pair<Integer,Integer>> dialog = new Dialog<>();
        dialog.setTitle("Update Sequence Numbers");
        dialog.setHeaderText("Update Session Sequence Numbers for " + inSession.nameProperty().get());
        ButtonType updateButtonType = new ButtonType("Update",
                                                     ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType,
                                                       ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));
        ValidatingTextField senderSequenceNumberTextField = new ValidatingTextField(input -> input.matches("^\\d+$"));
        senderSequenceNumberTextField.textProperty().set(String.valueOf(inSession.senderSeqNumProperty().get()));
        ValidatingTextField targetSequenceNumberTextField = new ValidatingTextField(input -> input.matches("^\\d+$"));
        targetSequenceNumberTextField.textProperty().set(String.valueOf(inSession.targetSeqNumProperty().get()));
        grid.add(new Label("Sender Sequence Number:"),0,0);
        grid.add(senderSequenceNumberTextField,1,0);
        grid.add(new Label("Target Sequence Number:"),0,1);
        grid.add(targetSequenceNumberTextField,1,1);
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(false);
        senderSequenceNumberTextField.isValidProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                senderSequenceNumberTextField.setStyle(PhotonServices.successStyle);
            } else {
                senderSequenceNumberTextField.setStyle(PhotonServices.errorStyle);
            }
        });
        targetSequenceNumberTextField.isValidProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                targetSequenceNumberTextField.setStyle(PhotonServices.successStyle);
            } else {
                targetSequenceNumberTextField.setStyle(PhotonServices.errorStyle);
            }
        });
        senderSequenceNumberTextField.textProperty().addListener((observable,oldValue,newvalue) -> {
            String senderFieldValue = StringUtils.trimToNull(senderSequenceNumberTextField.textProperty().get());
            String targetFieldValue = StringUtils.trimToNull(targetSequenceNumberTextField.textProperty().get());
            updateButton.setDisable(senderFieldValue == null ||
                                    targetFieldValue == null || 
                                    !senderSequenceNumberTextField.isValidProperty().get() ||
                                    !targetSequenceNumberTextField.isValidProperty().get());
        });
        targetSequenceNumberTextField.textProperty().addListener((observable,oldValue,newvalue) -> {
            String senderFieldValue = StringUtils.trimToNull(senderSequenceNumberTextField.textProperty().get());
            String targetFieldValue = StringUtils.trimToNull(targetSequenceNumberTextField.textProperty().get());
            updateButton.setDisable(senderFieldValue == null ||
                                    targetFieldValue == null || 
                                    !senderSequenceNumberTextField.isValidProperty().get() ||
                                    !targetSequenceNumberTextField.isValidProperty().get());
        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> senderSequenceNumberTextField.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == updateButtonType) {
                return Pair.create(Integer.parseInt(senderSequenceNumberTextField.getText()),
                                   Integer.parseInt(targetSequenceNumberTextField.getText()));
            }
            return null;
        });
        PhotonServices.styleDialog(dialog);
        Optional<Pair<Integer,Integer>> result = dialog.showAndWait();
        result.ifPresent(sequenceNumberPairs -> {
            int newSenderSequenceNumber = sequenceNumberPairs.getFirstMember();
            int newTargetSequenceNumber = sequenceNumberPairs.getSecondMember();
            try {
                SLF4JLoggerProxy.info(FixSessionView.this,
                                      "{} updating sequence numbers for {} to {},{}",
                                      SessionUser.getCurrent(),
                                      inSession,
                                      newSenderSequenceNumber,
                                      newTargetSequenceNumber);
                fixAdminClient.updateSequenceNumbers(inSession.nameProperty().get(),
                                                     newSenderSequenceNumber,
                                                     newTargetSequenceNumber);
                updateSessions();
                uiMessageService.post(new NotificationEvent("Update Sequence Numbers",
                                                             "Update sequence numbers on " + inSession.nameProperty().get() + " succeeded",
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                String message = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(FixSessionView.this,
                                      e,
                                      "Unable to update sequence numbers on {}: {}",
                                      inSession,
                                      message);
                uiMessageService.post(new NotificationEvent("Update Sequence Numbers",
                                                             "Update sequence numbers on " + inSession.nameProperty().get() + " failed: " + message,
                                                             AlertType.ERROR));
            }
        });
    }
    /**
     * Provides common behaviors for wizard pane objects.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static abstract class AbstractWizardPane
    {
        /**
         * Generate the wizard pane with the given header text.
         *
         * @param inHeaderText a <code>String</code> value
         * @return a <code>WizarPane</code> value
         */
        protected WizardPane generateWizardPane(String inHeaderText)
        {
            WizardPane wizardPane = new WizardPane() {
                /* (non-Javadoc)
                 * @see org.controlsfx.dialog.WizardPane#onEnteringPage(org.controlsfx.dialog.Wizard)
                 */
                @Override
                public void onEnteringPage(Wizard inWizard)
                {
                    doOnEnteringPage(inWizard);
                    inWizard.invalidProperty().bind(fieldsInvalid);
                }
                /* (non-Javadoc)
                 * @see org.controlsfx.dialog.WizardPane#onExitingPage(org.controlsfx.dialog.Wizard)
                 */
                @Override
                public void onExitingPage(Wizard inWizard)
                {
                    doOnExitingPage(inWizard);
                }
            };
            paneLayout.setHgap(10);
            paneLayout.setVgap(10);
            paneLayout.setPadding(new Insets(20,150,10,10));
            wizardPane.setHeaderText(inHeaderText);
            wizardPane.setId(getPaneName());
            mainLayout.getChildren().add(paneLayout);
            setFieldsInGrid();
            wizardPane.setContent(mainLayout);
            return wizardPane;
        }
        /**
         * Get the name of the pane for internal use.
         *
         * @return a <code>String</code> value
         */
        protected String getPaneName()
        {
            return getClass().getSimpleName();
        }
        /**
         * Establish the fields in the dialog grid.
         */
        protected void setFieldsInGrid() {}
        /**
         * Steps to take upon entering the page.
         *
         * @param inWizard a <code>Wizard</code> value
         */
        protected void doOnEnteringPage(Wizard inWizard) {}
        /**
         * Steps to take before exiting the page.
         *
         * @param inWizard a <code>Wizard</code> value
         */
        protected void doOnExitingPage(Wizard inWizard) {}
        /**
         * Create a new AbstractWizardPane instance.
         *
         * @param inFixSession a <code>DisplayFixSession</code> value
         * @param inIsNew a <code>boolean</code> value
         */
        protected AbstractWizardPane(DisplayFixSession inFixSession,
                                     boolean inIsNew)
        {
            fixSession = inFixSession;
            isNew = inIsNew;
            adviceLabel = new Label();
            paneLayout = new GridPane();
            fieldsInvalid = new SimpleBooleanProperty(true);
            mainLayout = new FlowPane();
        }
        /**
         * main layout of the page
         */
        protected final FlowPane mainLayout;
        /**
         * indicates if the fields are currently in an invalid state
         */
        protected final BooleanProperty fieldsInvalid;
        /**
         * FIX session to be modified
         */
        protected final DisplayFixSession fixSession;
        /**
         * indicates if the action is adding a new FIX session or modifying an existing one
         */
        protected final boolean isNew;
        /**
         * holds error messages to be displayed if a field is invalid
         */
        protected final Label adviceLabel;
        /**
         * the main layout of the page
         */
        protected final GridPane paneLayout;
    }
    /**
     * Presents the FIX session times settings.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class SessionTimesPane
            extends AbstractWizardPane
    {
        /* (non-Javadoc)
         * @see org.marketcetera.ui.fix.view.FixSessionView.AbstractFieldSet#setFieldsInGrid()
         */
        @Override
        protected void setFieldsInGrid()
        {
            paneLayout.getChildren().clear();
            int rowIndex = 0;
            paneLayout.add(new Label("Type"),0,rowIndex);
            paneLayout.add(sessionTypeComboBox,1,rowIndex);
            String value = String.valueOf(sessionTypeComboBox.getValue());
            switch(value) {
                case CONTINUOUS:
                    break;
                case DAILY:
                    paneLayout.add(new Label("Start Time"),0,++rowIndex);
                    paneLayout.add(startTimeField,1,rowIndex);
                    paneLayout.add(new Label("End Time"),0,++rowIndex);
                    paneLayout.add(endTimeField,1,rowIndex);
                    paneLayout.add(new Label("Time Zone"),0,++rowIndex);
                    paneLayout.add(timeZoneComboBox,1,rowIndex);
                    break;
                case WEEKLY:
                    paneLayout.add(new Label("Start Time"),0,++rowIndex);
                    paneLayout.add(startTimeField,1,rowIndex);
                    paneLayout.add(new Label("End Time"),0,++rowIndex);
                    paneLayout.add(endTimeField,1,rowIndex);
                    paneLayout.add(new Label("Start Day"),0,++rowIndex);
                    paneLayout.add(startDayComboBox,1,rowIndex);
                    paneLayout.add(new Label("End Day"),0,++rowIndex);
                    paneLayout.add(endDayComboBox,1,rowIndex);
                    paneLayout.add(new Label("Time Zone"),0,++rowIndex);
                    paneLayout.add(timeZoneComboBox,1,rowIndex);
                    break;
            }
            paneLayout.add(adviceLabel,0,++rowIndex,2,1);
            if(paneLayout.getScene() != null) {
                ((Stage)paneLayout.getScene().getWindow()).sizeToScene();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.fix.view.FixSessionView.AbstractFieldSet#doOnEnteringPage(org.controlsfx.dialog.Wizard)
         */
        @Override
        protected void doOnEnteringPage(Wizard inWizard)
        {
            Map<String,String> settings = fixSession.sourceProperty().get().getFixSession().getSessionSettings();
            startTimeField.setText(settings.containsKey(quickfix.Session.SETTING_START_TIME)?settings.get(quickfix.Session.SETTING_START_TIME):"00:00:00");
            endTimeField.setText(settings.containsKey(quickfix.Session.SETTING_END_TIME)?settings.get(quickfix.Session.SETTING_END_TIME):"00:00:00");
            startDayComboBox.setValue(settings.containsKey(quickfix.Session.SETTING_START_DAY)?FixSessionDay.valueOf(settings.get(quickfix.Session.SETTING_START_DAY)):FixSessionDay.Monday);
            endDayComboBox.setValue(settings.containsKey(quickfix.Session.SETTING_END_DAY)?FixSessionDay.valueOf(settings.get(quickfix.Session.SETTING_END_DAY)):FixSessionDay.Friday);
            timeZoneComboBox.setValue(settings.containsKey(quickfix.Session.SETTING_TIMEZONE)?settings.get(quickfix.Session.SETTING_TIMEZONE):TimeZone.getDefault().getID());
            startTimeField.setVisible(true);
            endTimeField.setVisible(true);
            // now, finalize the setup based on the selected session type
            String value = settings.get(quickfix.Session.SETTING_NON_STOP_SESSION);
            if(YES.equals(value)) {
                // this is a non-stop session. hide everything but the select
                sessionTypeComboBox.setValue(CONTINUOUS);
            } else {
                // this is a weekly or daily session
                value = settings.get(quickfix.Session.SETTING_START_DAY);
                if(value != null) {
                    // this is a weekly session, nothing more needs to be done
                    sessionTypeComboBox.setValue(WEEKLY);
                } else {
                    // this is a daily session
                    sessionTypeComboBox.setValue(DAILY);
                }
            }
            updateFields();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.fix.view.FixSessionView.AbstractFieldSet#doOnExitingPage(org.controlsfx.dialog.Wizard)
         */
        @Override
        protected void doOnExitingPage(Wizard inWizard)
        {
            Map<String,String> settings = fixSession.sourceProperty().get().getFixSession().getSessionSettings();
            String value = String.valueOf(sessionTypeComboBox.getValue());
            switch(value) {
                case CONTINUOUS:
                    settings.remove(quickfix.Session.SETTING_START_TIME);
                    settings.remove(quickfix.Session.SETTING_END_TIME);
                    settings.remove(quickfix.Session.SETTING_START_DAY);
                    settings.remove(quickfix.Session.SETTING_END_DAY);
                    settings.remove(quickfix.Session.SETTING_TIMEZONE);
                    settings.put(quickfix.Session.SETTING_NON_STOP_SESSION,
                                 YES);
                    break;
                case DAILY:
                    settings.remove(quickfix.Session.SETTING_START_DAY);
                    settings.remove(quickfix.Session.SETTING_END_DAY);
                    settings.remove(quickfix.Session.SETTING_NON_STOP_SESSION);
                    settings.put(quickfix.Session.SETTING_START_TIME,
                                 startTimeField.getText());
                    settings.put(quickfix.Session.SETTING_END_TIME,
                                 endTimeField.getText());
                    settings.put(quickfix.Session.SETTING_TIMEZONE,
                                 String.valueOf(timeZoneComboBox.getValue()));
                    break;
                case WEEKLY:
                    settings.remove(quickfix.Session.SETTING_NON_STOP_SESSION);
                    settings.put(quickfix.Session.SETTING_START_TIME,
                                 startTimeField.getText());
                    settings.put(quickfix.Session.SETTING_END_TIME,
                                 endTimeField.getText());
                    settings.put(quickfix.Session.SETTING_TIMEZONE,
                                 String.valueOf(timeZoneComboBox.getValue()));
                    settings.put(quickfix.Session.SETTING_START_DAY,
                                 String.valueOf(startDayComboBox.getValue()));
                    settings.put(quickfix.Session.SETTING_END_DAY,
                                 String.valueOf(endDayComboBox.getValue()));
                    break;
            }
        }
        /**
         * Create a new SessionTimesPane instance.
         *
         * @param inFixSession a <code>DisplayFixSession</code> value
         * @param inIsNew a <code>boolean</code> value
         */
        private SessionTimesPane(DisplayFixSession inFixSession,
                                 boolean inIsNew)
        {
            super(inFixSession,
                  inIsNew);
            Predicate<String> timeFieldPredicate = value -> {
                return value.matches("^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$");
            };
            Function<Void,Boolean> invalidTestFunction = new Function<>() {
                @Override
                public Boolean apply(Void inIgnored)
                {
                    if(sessionTypeComboBox.getValue() == null) {
                        return true;
                    }
                    boolean isValid = false;
                    String value = String.valueOf(sessionTypeComboBox.getValue());
                    switch(value) {
                        case CONTINUOUS:
                            isValid = true;
                            break;
                        case DAILY:
                            isValid = startTimeField.isValidProperty().get() &&
                                      endTimeField.isValidProperty().get() &&
                                      timeZoneComboBox.getValue() != null;
                            break;
                        case WEEKLY:
                            isValid = startTimeField.isValidProperty().get() &&
                                      endTimeField.isValidProperty().get() &&
                                      startDayComboBox.getValue() != null &&
                                      endDayComboBox.getValue() != null &&
                                      timeZoneComboBox.getValue() != null;
                            break;
                    }
                    return !isValid;
                }
            };
            sessionTypeComboBox = new ComboBox<>();
            sessionTypeComboBox.setId("sessionType");
            sessionTypeComboBox.getItems().add(DAILY);
            sessionTypeComboBox.getItems().add(WEEKLY);
            sessionTypeComboBox.getItems().add(CONTINUOUS);
            startTimeField = new ValidatingTextField(timeFieldPredicate);
            startTimeField.setId("sessionStartTime");
            endTimeField = new ValidatingTextField(timeFieldPredicate);
            endTimeField.setId("sessionEndTime");
            timeZoneComboBox = new ComboBox<>();
            timeZoneComboBox.setId("sessionTimeZone");
            for(String timeZoneId : TimeZone.getAvailableIDs()) {
                timeZoneComboBox.getItems().add(timeZoneId);
            }
            startDayComboBox = new ComboBox<>();
            startDayComboBox.setId("sessionStartDay");
            endDayComboBox = new ComboBox<>();
            endDayComboBox.setId("sessionEndDay");
            sessionTypeComboBox.valueProperty().addListener((observableValue,oldValue,newValue) -> {
                fieldsInvalid.set(invalidTestFunction.apply(null));
                updateFields();
            });
            startTimeField.setId("sessionTimes.startTime");
            startTimeField.setPromptText("00:00:00");
            startTimeField.setTooltip(new Tooltip("Enter a time value in the form 00:00:00"));
            startTimeField.isValidProperty().addListener((observable,oldValue,newValue) -> {
                if(newValue) {
                    startTimeField.setStyle(PhotonServices.successStyle);
                    adviceLabel.setText("");
                    adviceLabel.setStyle(PhotonServices.successMessage);
                } else {
                    adviceLabel.setText("Start time must be in the form 00:00:00");
                    startTimeField.setStyle(PhotonServices.errorStyle);
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                }
            });
            startTimeField.textProperty().addListener((observableValue,oldValue,newValue) -> fieldsInvalid.set(invalidTestFunction.apply(null)));
            endTimeField.setId("sessionTimes.endTime");
            endTimeField.setPromptText("00:00:00");
            endTimeField.setTooltip(new Tooltip("Enter a time value in the form 00:00:00"));
            endTimeField.isValidProperty().addListener((observable,oldValue,newValue) -> {
                if(newValue) {
                    endTimeField.setStyle(PhotonServices.successStyle);
                    adviceLabel.setText("");
                    adviceLabel.setStyle(PhotonServices.successMessage);
                } else {
                    adviceLabel.setText("End time must be in the form 00:00:00");
                    endTimeField.setStyle(PhotonServices.errorStyle);
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                }
            });
            endTimeField.textProperty().addListener((observableValue,oldValue,newValue) -> fieldsInvalid.set(invalidTestFunction.apply(null)));
            startDayComboBox.getItems().addAll(FixSessionDay.values());
            endDayComboBox.getItems().addAll(FixSessionDay.values());
        }
        /**
         * Update the field widgets visible vs invisible.
         */
        private void updateFields()
        {
            String value = String.valueOf(sessionTypeComboBox.getValue());
            switch(value) {
                case CONTINUOUS:
                    startTimeField.setVisible(false);
                    endTimeField.setVisible(false);
                    timeZoneComboBox.setVisible(false);
                    startDayComboBox.setVisible(false);
                    endDayComboBox.setVisible(false);
                    break;
                case DAILY:
                    startTimeField.setVisible(true);
                    endTimeField.setVisible(true);
                    timeZoneComboBox.setVisible(true);
                    startDayComboBox.setVisible(false);
                    endDayComboBox.setVisible(false);
                    break;
                case WEEKLY:
                    startTimeField.setVisible(true);
                    endTimeField.setVisible(true);
                    timeZoneComboBox.setVisible(true);
                    startDayComboBox.setVisible(true);
                    endDayComboBox.setVisible(true);
                    break;
            }
            setFieldsInGrid();
        }
        /**
         * indicates continuous FIX sessions
         */
        private final String CONTINUOUS = "Continuous";
        /**
         * indicates daily FIX sessions
         */
        private final String DAILY = "Daily";
        /**
         * indicates weekly FIX sessions
         */
        private final String WEEKLY = "Weekly";
        /**
         * used to store continuous FIX sessions in the settings
         */
        private final String YES = "Y";
        /**
         * chooses what type of FIX session
         */
        private final ComboBox<String> sessionTypeComboBox;
        /**
         * daily/weekly FIX session start time
         */
        private final ValidatingTextField startTimeField;
        /**
         * daily/weekly FIX session end time
         */
        private final ValidatingTextField endTimeField;
        /**
         * daily/weekly FIX session time zone
         */
        private final ComboBox<String> timeZoneComboBox;
        /**
         * weekly FIX session start day
         */
        private final ComboBox<FixSessionDay> startDayComboBox;
        /**
         * weekly FIX session end day
         */
        private final ComboBox<FixSessionDay> endDayComboBox;
    }
    /**
     * Presents FIX session settings.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class SessionSettingsPane
            extends AbstractWizardPane
    {
        /**
         * Create a new SessionSettingsPane instance.
         *
         * @param inFixSession a <code>DisplayFixSession</code> value
         * @param inIsNew a <code>boolean</code> value
         */
        private SessionSettingsPane(DisplayFixSession inFixSession,
                                    boolean inIsNew)
        {
            super(inFixSession,
                  inIsNew);
            descriptorGrid = new TableView<>();
            descriptorGrid.setEditable(true);
            TableColumn<DecoratedDescriptor,String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameColumn.setEditable(false);
            TableColumn<DecoratedDescriptor,String> descriptionColumn = new TableColumn<>("Description");
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            descriptionColumn.setEditable(false);
            TableColumn<DecoratedDescriptor,String> defaultValueColumn = new TableColumn<>("Default");
            defaultValueColumn.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
            defaultValueColumn.setEditable(true);
            TableColumn<DecoratedDescriptor,String> valueColumn = new TableColumn<>("Value");
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
            valueColumn.setCellFactory(TextFieldTableCell.<DecoratedDescriptor>forTableColumn());
            valueColumn.setEditable(true);
            descriptorGrid.getColumns().add(nameColumn);
            descriptorGrid.getColumns().add(valueColumn);
            descriptorGrid.getColumns().add(defaultValueColumn);
            descriptorGrid.getColumns().add(descriptionColumn);
            fieldsInvalid.set(false);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.fix.view.FixSessionView.AbstractWizardPane#setFieldsInGrid()
         */
        @Override
        protected void setFieldsInGrid()
        {
            mainLayout.getChildren().add(descriptorGrid);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.fix.view.FixSessionView.AbstractWizardPane#doOnEnteringPage(org.controlsfx.dialog.Wizard)
         */
        @Override
        protected void doOnEnteringPage(Wizard inWizard)
        {
            descriptorGrid.getItems().clear();
            Map<String,String> sessionSettings = fixSession.sourceProperty().get().getFixSession().getSessionSettings();
            Collection<FixSessionAttributeDescriptor> attributeDescriptors = fixAdminClient.getFixSessionAttributeDescriptors();
            for(FixSessionAttributeDescriptor descriptor : attributeDescriptors) {
                DecoratedDescriptor newItem = new DecoratedDescriptor(descriptor);
                newItem.value.set(sessionSettings.get(newItem.getName()));
                descriptorGrid.getItems().add(newItem);
            }
            descriptorGrid.setPrefWidth(1024);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.fix.view.FixSessionView.AbstractWizardPane#doOnExitingPage(org.controlsfx.dialog.Wizard)
         */
        @Override
        protected void doOnExitingPage(Wizard inWizard)
        {
            Map<String,String> settings = fixSession.sourceProperty().get().getFixSession().getSessionSettings();
            for(DecoratedDescriptor decoratedDescriptor : descriptorGrid.getItems()) {
                String name = decoratedDescriptor.getName();
                String value = StringUtils.trimToNull(decoratedDescriptor.valueProperty().get());
                if(value == null) {
                    // if the descriptor does not have a value in the screen widget, it should be removed from the settings
                    settings.remove(name);
                } else {
                    // if the descriptor has a value in the screen widget, it needs to be written
                    settings.put(name,
                                 value);
                }
            }
        }
        /**
         * holds the FIX session settings
         */
        private final TableView<DecoratedDescriptor> descriptorGrid;
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
            value = new SimpleStringProperty();
        }
        /**
         * Get the value value.
         *
         * @return a <code>StringProperty</code> value
         */
        public StringProperty getValue()
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
            value.set(inValue);
        }
        /**
         * Reset the value to the default value.
         */
        public void reset()
        {
            value.set(getDefaultValue());
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
        public StringProperty valueProperty()
        {
            return value;
        }
        /**
         * value set by user
         */
        private final StringProperty value;
        private static final long serialVersionUID = 142085523837757672L;
    }
    private MenuItem enableSessionContextMenuItem;
    private MenuItem startSessionContextMenuItem;
    private MenuItem stopSessionContextMenuItem;
    private MenuItem editSessionContextMenuItem;
    private MenuItem deleteSessionContextMenuItem;
    private MenuItem sequenceSessionContextMenuItem;
    private MenuItem disableSessionContextMenuItem;
    private ContextMenu fixSessionsContextMenu;
    private TableColumn<DisplayFixSession,String> nameTableColumn;
    private TableColumn<DisplayFixSession,String> brokerIdTableColumn;
    private TableColumn<DisplayFixSession,String> hostIdTableColumn;
    private TableColumn<DisplayFixSession,FixSessionStatus> statusTableColumn;
    private TableColumn<DisplayFixSession,Integer> senderSequenceNumberTableColumn;
    private TableColumn<DisplayFixSession,Integer> targetSequenceNumberTableColumn;
    private AdminClientService fixAdminClient;
    private VBox rootLayout;
    private TableView<DisplayFixSession> fixSessionsTable;
    /**
     * button layout for action buttons
     */
    private HBox buttonLayout;
    /**
     * add FIX session button
     */
    private Button addFixSessionButton;
    /**
     * helps determine if authorization is granted for actions
     */
    @Autowired
    protected AuthorizationHelperService authzHelperService;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Session View";
}
