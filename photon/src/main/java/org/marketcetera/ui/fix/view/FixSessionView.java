package org.marketcetera.ui.fix.view;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.Pair;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
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
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
        implements ContentView,BrokerStatusListener
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getScene()
     */
    @Override
    public Scene getScene()
    {
        return scene;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} receiveBrokerStatus: {}",
                               PlatformServices.getServiceName(getClass()),
                               inActiveFixSession);
        updateSessions();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose(javafx.stage.WindowEvent)
     */
    @Override
    public void onClose(WindowEvent inEvent)
    {
        try {
            fixAdminClient.removeBrokerStatusListener(this);
        } catch (Exception ignored) {}
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
     * Initialize and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.trace(this,
                               "{} {} start",
                               PlatformServices.getServiceName(getClass()),
                               hashCode());
        fixAdminClient = serviceManager.getService(AdminClientService.class);
        fixAdminClient.addBrokerStatusListener(this);
        rootLayout = new VBox();
        initializeTable();
        rootLayout.getChildren().add(fixSessionsTable);
        scene = new Scene(rootLayout);
        updateSessions();
    }
    /**
     * Create a new OrderTicketView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public FixSessionView(Stage inParent,
                           NewWindowEvent inEvent,
                           Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
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
                    switch(inNewValue.getStatus()) {
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
        brokerIdTableColumn = new TableColumn<>("BrokerId");
        brokerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("brokerId"));
        hostIdTableColumn = new TableColumn<>("HostId");
        hostIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("hostId"));
        statusTableColumn = new TableColumn<>("Status");
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        senderSequenceNumberTableColumn = new TableColumn<>("SenderSeqNum");
        senderSequenceNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("senderSeqNum"));
        targetSequenceNumberTableColumn = new TableColumn<>("TargetSeqNum");
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
                    inAction.accept(inSelectedItem.getName());
                    SLF4JLoggerProxy.info(FixSessionView.this,
                                          "{} on {} succeeded",
                                          inTitle,
                                          inSelectedItem);
                    updateSessions();
                    webMessageService.post(new NotificationEvent(inTitle,
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
                    webMessageService.post(new NotificationEvent(inTitle,
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
                                 "Enable " + selectedItem.getName(),
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
                                 "Disable " + selectedItem.getName(),
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
                                 "Start " + selectedItem.getName(),
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
                                 "Stop " + selectedItem.getName(),
                                 fixAdminClient::stopSession);
        });
        editSessionContextMenuItem = new MenuItem("Edit");
        editSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            updateFixSession(selectedItem);
        });
        deleteSessionContextMenuItem = new MenuItem("Delete");
        deleteSessionContextMenuItem.setOnAction(event -> {
            DisplayFixSession selectedItem = fixSessionsTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            performSessionAction(selectedItem,
                                 "Delete Session",
                                 "Delete " + selectedItem.getName(),
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
     * @param inSelectedItem a <code>DisplayFixSession</code> value
     */
    private void updateFixSession(DisplayFixSession inSelectedItem)
    {
        throw new UnsupportedOperationException(); // TODO
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
        dialog.setHeaderText("Update Session Sequence Numbers for " + inSession.getName());
        ButtonType updateButtonType = new ButtonType("Update",
                                                     ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType,
                                                       ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));
        ValidatingTextField senderSequenceNumberTextField = new ValidatingTextField(input -> input.matches("^\\d+$"));
        senderSequenceNumberTextField.textProperty().set(String.valueOf(inSession.getSenderSeqNum()));
        ValidatingTextField targetSequenceNumberTextField = new ValidatingTextField(input -> input.matches("^\\d+$"));
        targetSequenceNumberTextField.textProperty().set(String.valueOf(inSession.getTargetSeqNum()));
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
                fixAdminClient.updateSequenceNumbers(inSession.getName(),
                                                     newSenderSequenceNumber,
                                                     newTargetSequenceNumber);
                updateSessions();
                webMessageService.post(new NotificationEvent("Update Sequence Numbers",
                                                             "Update sequence numbers on " + inSession.getName() + " succeeded",
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                String message = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(FixSessionView.this,
                                      e,
                                      "Unable to update sequence numbers on {}: {}",
                                      inSession,
                                      message);
                webMessageService.post(new NotificationEvent("Update Sequence Numbers",
                                                             "Update sequence numbers on " + inSession.getName() + " failed: " + message,
                                                             AlertType.ERROR));
            }
        });
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
    private Scene scene;
    private VBox rootLayout;
    private TableView<DisplayFixSession> fixSessionsTable;
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
