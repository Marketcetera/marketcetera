package org.marketcetera.ui.strategy.view;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.marketcetera.admin.User;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.strategy.FileUploadStatus;
import org.marketcetera.strategy.SimpleFileUploadRequest;
import org.marketcetera.strategy.StrategyEventListener;
import org.marketcetera.strategy.StrategyInstance;
import org.marketcetera.strategy.StrategyMessage;
import org.marketcetera.strategy.StrategyPermissions;
import org.marketcetera.strategy.StrategyStatus;
import org.marketcetera.strategy.events.StrategyEvent;
import org.marketcetera.strategy.events.StrategyMessageEvent;
import org.marketcetera.strategy.events.StrategyStartFailedEvent;
import org.marketcetera.strategy.events.StrategyStartedEvent;
import org.marketcetera.strategy.events.StrategyStatusChangedEvent;
import org.marketcetera.strategy.events.StrategyStoppedEvent;
import org.marketcetera.strategy.events.StrategyUnloadedEvent;
import org.marketcetera.strategy.events.StrategyUploadFailedEvent;
import org.marketcetera.strategy.events.StrategyUploadSucceededEvent;
import org.marketcetera.ui.PhotonApp;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.strategy.service.StrategyClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import info.schnatterer.mobynamesgenerator.MobyNamesGenerator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

/* $License$ */

/**
 * Displays loaded strategies and events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@AutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StrategyView
        extends AbstractContentView
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        strategyClient = serviceManager.getService(StrategyClientService.class);
        mainLayout = new VBox(10);
        initializeStrategyTable();
        initializeEventTable();
        eventTableCurrentPage = 0;
        eventTablePageSize = 20;
        eventTablePagination = new Pagination();
        eventTablePagination.setPageCount(1);
        eventTablePagination.setCurrentPageIndex(eventTableCurrentPage);
        eventTablePagination.setMaxPageIndicatorCount(1);
        eventTablePagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> inObservable,
                                Number inOldValue,
                                Number inNewValue)
            {
                eventTableCurrentPage = inNewValue.intValue();
                updateEvents();
            }}
        );
        strategyNameComboBox = new ComboBox<>();
        strategyNameComboBox.getItems().add(ALL_STRATEGIES);
        strategyNameComboBox.valueProperty().addListener((observableValue,oldValue,newValue) -> updateEvents());
        severityComboBox = new ComboBox<>();
        severityComboBox.getItems().addAll(Severity.values());
        severityComboBox.setValue(Severity.INFO);
        severityComboBox.valueProperty().addListener((observableValue,oldValue,newValue) -> updateEvents());
        filterLayout = new GridPane();
        filterLayout.setHgap(10);
        filterLayout.setVgap(10);
        filterLayout.setPadding(new Insets(10,10,10,10));
        int rowCount = 0;
        int colCount = 0;
        filterLayout.add(new Label("Strategy Id"),colCount,rowCount);
        filterLayout.add(strategyNameComboBox,++colCount,rowCount);
        filterLayout.add(new Label("Severity"),++colCount,rowCount);
        filterLayout.add(severityComboBox,++colCount,rowCount);
        loadStrategyButton = new Button("Load Strategy");
        loadStrategyButton.setDisable(!authzHelperService.hasPermission(StrategyPermissions.LoadStrategyAction));
        loadStrategyButton.setOnAction(event -> loadStrategy());
        buttonLayout = new HBox(10);
        buttonLayout.getChildren().add(loadStrategyButton);
        strategyTable.prefWidthProperty().bind(getParentWindow().widthProperty());
        eventTable.prefWidthProperty().bind(getParentWindow().widthProperty());
        strategyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mainLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
        mainLayout.getChildren().addAll(strategyTable,
                                        new Separator(Orientation.HORIZONTAL),
                                        buttonLayout,
                                        new Separator(Orientation.HORIZONTAL),
                                        filterLayout,
                                        eventTable,
                                        eventTablePagination);
        updateStrategies();
        updateEvents();
        initializeStrategyEventListener();
        strategyRuntimeUpdateTimer = new Timer();
        strategyRuntimeUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                try {
                    updateStrategyRuntime();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(StrategyView.this,
                                          e);
                }
            }},new Date(System.currentTimeMillis() + strategyRuntimeUpdateInterval),strategyRuntimeUpdateInterval);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose()
     */
    @Override
    public void onClose()
    {
        try {
            strategyRuntimeUpdateTimer.cancel();
        } catch (Exception ignored) {}
        if(strategyEventListener != null) {
            try {
                strategyClient.removeStrategyEventListener(strategyEventListener);
                strategyEventListener = null;
            } catch (Exception ignored) {}
        }
        super.onClose();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getMainLayout()
     */
    @Override
    public Region getMainLayout()
    {
        return mainLayout;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientConnect()
     */
    @Override
    protected void onClientConnect()
    {
        updateStrategies();
        updateEvents();
        initializeStrategyEventListener();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientDisconnect()
     */
    @Override
    protected void onClientDisconnect()
    {
        Platform.runLater(() -> {
            strategyTable.getItems().clear();
            eventTable.getItems().clear();
        });
    }
    /**
     * Create a new StrategyView instance.
     *
     * @param inParent a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public StrategyView(Region inParent,
                        NewWindowEvent inEvent,
                        Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
    }
    /**
     * Set up the strategy event listener.
     */
    private void initializeStrategyEventListener()
    {
        if(strategyEventListener != null) {
            try {
                strategyClient.removeStrategyEventListener(strategyEventListener);
                strategyEventListener = null;
            } catch (Exception ignored) {}
        }
        strategyEventListener = new StrategyEventListener() {
            /* (non-Javadoc)
             * @see org.marketcetera.strategy.StrategyEventListener#receiveStrategyEvent(org.marketcetera.strategy.events.StrategyEvent)
             */
            @Override
            public void receiveStrategyEvent(StrategyEvent inEvent)
            {
                SLF4JLoggerProxy.trace(StrategyView.this,
                                       "Received {}",
                                       inEvent);
                String instanceName = inEvent.getStrategyInstance().getName();
                DisplayStrategyInstance strategyToUpdate = null;
                for(DisplayStrategyInstance strategy : strategyTable.getItems()) {
                    if(strategy.strategyNameProperty().get().equals(instanceName)) {
                        strategyToUpdate = strategy;
                        break;
                    }
                }
                if(strategyToUpdate != null) {
                    final DisplayStrategyInstance displayStrategyInstance = strategyToUpdate;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            if(inEvent instanceof StrategyStoppedEvent) {
                                displayStrategyInstance.startedProperty().set(null);
                            } else if(inEvent instanceof StrategyStartedEvent) {
                                displayStrategyInstance.startedProperty().set(inEvent.getStrategyInstance().getStarted());
                            } else if(inEvent instanceof StrategyUploadFailedEvent) {
                            } else if(inEvent instanceof StrategyUploadSucceededEvent) {
                            } else if(inEvent instanceof StrategyStartFailedEvent) {
                                StrategyStartFailedEvent event = (StrategyStartFailedEvent)inEvent;
                                SLF4JLoggerProxy.warn(this,
                                                      "Received strategy start failed event: {}",
                                                      inEvent);
                                uiMessageService.post(new NotificationEvent("Start Strategy",
                                                                            "Strategy strategy failed: " + event.getErrorMessage(),
                                                                            AlertType.INFORMATION));
                            } else if(inEvent instanceof StrategyStatusChangedEvent) {
                                StrategyStatusChangedEvent event = (StrategyStatusChangedEvent)inEvent;
                                displayStrategyInstance.strategyStatusProperty().set(event.getNewValue());
                            } else if(inEvent instanceof StrategyUnloadedEvent) {
                                strategyTable.getItems().remove(displayStrategyInstance);
                            } else if(inEvent instanceof StrategyMessageEvent) {
                                updateEvents();
                            }
                        }}
                    );
                } else {
                    updateStrategies();
                    updateEvents();
                }
            }
        };
        strategyClient.addStrategyEventListener(strategyEventListener);
    }
    /**
     * Update the strategy runtime property.
     */
    private void updateStrategyRuntime()
    {
        Platform.runLater(() -> {
            for(DisplayStrategyInstance displayStrategyInstance : strategyTable.getItems()) {
                displayStrategyInstance.updateRunningProperty();
            }
        });
    }
    /**
     * Update the strategy events table.
     */
    private void updateEvents()
    {
        PageRequest pageRequest = new PageRequest(eventTableCurrentPage,
                                                  eventTablePageSize);
        String selectedStrategyName = strategyNameComboBox.valueProperty().get();
        CollectionPageResponse<? extends StrategyMessage> response = strategyClient.getStrategyMessages(selectedStrategyName == null || ALL_STRATEGIES.equals(selectedStrategyName) ? null : selectedStrategyName,
                                                                                                        severityComboBox.getValue(),
                                                                                                        pageRequest);
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                eventTablePagination.setPageCount(response.getTotalPages());
                eventTablePagination.setCurrentPageIndex(eventTableCurrentPage);
                eventTable.getItems().clear();
                for(StrategyMessage strategyMessage : response.getElements()) {
                    eventTable.getItems().add(new DisplayStrategyMessage(strategyMessage));
                }
            }}
        );
    }
    /**
     * Load the chosen strategy.
     */
    private void loadStrategy()
    {
        Optional<User> ownerOption = PhotonServices.getCurrentUser();
        if(ownerOption.isEmpty()) {
            SLF4JLoggerProxy.warn(this,
                                  "Cannot load a strategy because the current user cannot be determined");
            uiMessageService.post(new NotificationEvent("Load Strategy",
                                                        "Cannot load a new strategy because the current user cannot be determined",
                                                        AlertType.ERROR));
            return;
        }
        User owner = ownerOption.get();
        FileChooser strategyFileChooser = new FileChooser();
        strategyFileChooser.setTitle("Choose the Strategy JAR File");
        strategyFileChooser.getExtensionFilters().add(new ExtensionFilter("JAR Files",
                                                                          "*.jar"));
        File result = strategyFileChooser.showOpenDialog(PhotonApp.getPrimaryStage());
        if(result != null) {
            if(!(result.exists() && result.canRead())) {
                uiMessageService.post(new NotificationEvent("Load Strategy",
                                                            "File '" + result.getAbsolutePath() + "' could not be read",
                                                            AlertType.WARNING));
                return;
            }
            String name = MobyNamesGenerator.getRandomName();
            Dialog<String> nameConfirmationDialog = new Dialog<>();
            GridPane nameConfirmationGrid = new GridPane();
            nameConfirmationGrid.setHgap(10);
            nameConfirmationGrid.setVgap(10);
            nameConfirmationGrid.setPadding(new Insets(20,150,10,10));
            TextField nameField = new TextField();
            Label adviceLabel = new Label();
            int rowCount = 0,colCount = 0;
            nameConfirmationGrid.add(new Label("Strategy Name"),colCount,rowCount);
            nameConfirmationGrid.add(nameField,++colCount,rowCount); colCount = 0;
            nameConfirmationGrid.add(adviceLabel,colCount,++rowCount,2,1); colCount = 0;
            final BooleanProperty disableOkButton = new SimpleBooleanProperty(false);
            ButtonType okButtonType = new ButtonType("Ok",
                                                     ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel",
                                                     ButtonBar.ButtonData.CANCEL_CLOSE);
            nameField.textProperty().addListener((observableValue,oldValue,newValue) -> {
                String value = StringUtils.trimToNull(newValue);
                adviceLabel.textProperty().set("");
                adviceLabel.setStyle(PhotonServices.successMessage);
                nameField.setStyle(PhotonServices.successStyle);
                if(value == null) {
                    adviceLabel.textProperty().set("Unique name required");
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                    nameField.setStyle(PhotonServices.errorStyle);
                    disableOkButton.set(true);
                    return;
                }
                Optional<? extends StrategyInstance> strategyInstanceOption = strategyClient.findByName(value);
                if(strategyInstanceOption.isPresent()) {
                    adviceLabel.textProperty().set("Unique name required");
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                    nameField.setStyle(PhotonServices.errorStyle);
                    disableOkButton.set(true);
                } else {
                    adviceLabel.textProperty().set("");
                    adviceLabel.setStyle(PhotonServices.successMessage);
                    nameField.setStyle(PhotonServices.successStyle);
                    disableOkButton.set(false);
                }
            });
            nameField.textProperty().set(name);
            DialogPane nameConfirmationDialogPane = new DialogPane();
            nameConfirmationDialogPane.setContent(nameConfirmationGrid);
            nameConfirmationDialog.dialogPaneProperty().set(nameConfirmationDialogPane);
            nameConfirmationDialog.setTitle("Confirm Strategy Name");
            nameConfirmationDialogPane.getButtonTypes().setAll(okButtonType,
                                                               cancelButton);
            nameConfirmationDialog.getDialogPane().lookupButton(okButtonType).disableProperty().bind(disableOkButton);
            PhotonServices.styleDialog(nameConfirmationDialog);
            nameConfirmationDialog.initModality(Modality.APPLICATION_MODAL);
            nameConfirmationDialog.setResultConverter(dialogButton -> {
                if(dialogButton == okButtonType) {
                    return StringUtils.trimToNull(nameField.getText());
                }
                return null;
            });
            Optional<String> nameOption = nameConfirmationDialog.showAndWait();
            if(nameOption.isEmpty()) {
                uiMessageService.post(new NotificationEvent("Load Strategy",
                                                            "Strategy load canceled",
                                                            AlertType.INFORMATION));
                return;
            }
            name = nameOption.get();
            String nonce = UUID.randomUUID().toString();
            final DisplayStrategyInstance newItem = new DisplayStrategyInstance(name,
                                                                                owner.getName());
            strategyTable.getItems().add(newItem);
            try {
                getMainLayout().setCursor(Cursor.WAIT);
                // TODO transfer file - this will block? need to use a callback instead?
                SimpleFileUploadRequest uploadRequest = new SimpleFileUploadRequest(name,
                                                                                    nonce,
                                                                                    result.getAbsolutePath(),
                                                                                    owner) {
                    /* (non-Javadoc)
                     * @see org.marketcetera.strategy.FileUploadRequest#onProgress(double)
                     */
                    @Override
                    public void onProgress(double inPercentComplete)
                    {
                        SLF4JLoggerProxy.trace(StrategyView.class,
                                               "Reporting file upload progress: {}",
                                               inPercentComplete);
                        // TODO update progress bar in strategy table
                        // TODO we've shoehorned in a new value that won't display if we refresh strategies - need to factor that in
                        Platform.runLater(() -> newItem.uploadProgressProperty().set(inPercentComplete));
                    }
                    /* (non-Javadoc)
                     * @see org.marketcetera.strategy.FileUploadRequest#onStatus(org.marketcetera.strategy.FileUploadStatus)
                     */
                    @Override
                    public void onStatus(FileUploadStatus inStatus)
                    {
                        SLF4JLoggerProxy.trace(StrategyView.class,
                                               "Reporting file upload status: {}",
                                               inStatus);
                        // TODO
                        switch(inStatus) {
                            case SUCCESS:
                            case FAILED:
                                break;
                            case IN_PROGRESS:
                            case PENDING:
                                break;
                            default:
                                throw new UnsupportedOperationException("Unexpected file upload status: " + inStatus);
                        }
                    }
                    /* (non-Javadoc)
                     * @see org.marketcetera.strategy.FileUploadRequest#onError(java.lang.Throwable)
                     */
                    @Override
                    public void onError(Throwable inThrowable)
                    {
                        SLF4JLoggerProxy.trace(StrategyView.class,
                                               inThrowable,
                                               "Reporting file upload error");
                        // TODO update item, display error
                    }
                };
                strategyClient.uploadFile(uploadRequest);
                updateStrategies();
                uiMessageService.post(new NotificationEvent("Load Strategy",
                                                            "Strategy '" + name + "' loaded",
                                                            AlertType.INFORMATION));
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to create '{}'",
                                      name);
                uiMessageService.post(new NotificationEvent("Load Strategy",
                                                            "File '" + result.getAbsolutePath() + "' could not be read",
                                                            AlertType.WARNING));
            } finally {
                getMainLayout().setCursor(Cursor.DEFAULT);
            }
        }
    }
    /**
     * Unload the given strategy.
     *
     * @param inSelectedItem a <code>DisplayStrategyInstance</code> value
     */
    private void unloadStrategy(DisplayStrategyInstance inSelectedItem)
    {
        if(inSelectedItem == null) {
            return;
        }
        SLF4JLoggerProxy.info(this,
                              "{} unloading strategy instance {}",
                              SessionUser.getCurrent().getUsername(),
                              inSelectedItem.strategyNameProperty().get());
        strategyClient.unloadStrategyInstance(inSelectedItem.strategyNameProperty().get());
        updateStrategies();
    }
    /**
     * Cancel the upload of the given strategy instance.
     *
     * @param inSelectedItem a <code>DisplayStrategyInstance</code> value
     */
    private void cancelStrategyUpload(DisplayStrategyInstance inSelectedItem)
    {
        // TODO
    }
    /**
     * Update the strategies table.
     */
    private void updateStrategies()
    {
        Platform.runLater(() -> {
            String selectedStrategyName = strategyNameComboBox.valueProperty().get();
            strategyNameComboBox.getItems().clear();
            strategyTable.getItems().clear();
            Collection<? extends StrategyInstance> results = strategyClient.getStrategyInstances();
            if(results == null) {
                return;
            }
            List<DisplayStrategyInstance> displayStrategies = Lists.newArrayList();
            List<String> strategyNames = Lists.newArrayList();
            results.forEach(result -> {
                displayStrategies.add(new DisplayStrategyInstance(result));
                strategyNames.add(result.getName());
            });
            Collections.sort(strategyNames);
            strategyNameComboBox.getItems().add(ALL_STRATEGIES);
            strategyNameComboBox.getItems().addAll(strategyNames);
            strategyTable.getItems().addAll(displayStrategies);
            if(strategyNameComboBox.getItems().contains(selectedStrategyName)) {
                strategyNameComboBox.valueProperty().set(selectedStrategyName);
            } else {
                strategyNameComboBox.valueProperty().set(ALL_STRATEGIES);
            }
        });
    }
    /**
     * Initialize the strategy table.
     */
    private void initializeStrategyTable()
    {
        strategyTable = new TableView<>();
        strategyTable.setPlaceholder(new Label("no loaded strategies"));
        initializeStrategyTableColumns();
        initializeStrategyContextMenu();
        strategyTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    /**
     * Initialize the event table.
     */
    private void initializeEventTable()
    {
        eventTable = new TableView<>();
        eventTable.setPlaceholder(new Label("no strategy events to display"));
        initializeEventTableColumns();
        initializeEventContextMenu();
        eventTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    /**
     * Initialize the event table columns.
     */
    private void initializeEventTableColumns()
    {
        eventStrategyNameColumn = new TableColumn<>("Strategy");
        eventStrategyNameColumn.setCellValueFactory(new PropertyValueFactory<>("strategyName"));
        eventTimestampColumn = new TableColumn<>("Timestamp");
        eventTimestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        eventTimestampColumn.setCellFactory(tableColumn -> PhotonServices.renderDateTimeCell(tableColumn));
        eventSeverityColumn = new TableColumn<>("Severity");
        eventSeverityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        eventMessageColumn = new TableColumn<>("Message");
        eventMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        eventTable.getColumns().add(eventStrategyNameColumn);
        eventTable.getColumns().add(eventTimestampColumn);
        eventTable.getColumns().add(eventSeverityColumn);
        eventTable.getColumns().add(eventMessageColumn);
    }
    /**
     * Initialize the strategy table columns.
     */
    private void initializeStrategyTableColumns()
    {
        strategyNameColumn = new TableColumn<>("Name");
        strategyNameColumn.setCellValueFactory(new PropertyValueFactory<>("strategyName"));
        strategyStatusColumn = new TableColumn<>("Status");
        strategyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("strategyStatus"));
        strategyUptimeColumn = new TableColumn<>("Uptime");
        strategyUptimeColumn.setCellValueFactory(new PropertyValueFactory<>("uptime"));
        strategyUptimeColumn.setCellFactory(tableColumn -> PhotonServices.renderPeriodCell(tableColumn));
        strategyOwnerColumn = new TableColumn<>("Owner");
        strategyOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        strategyTable.getColumns().add(strategyNameColumn);
        strategyTable.getColumns().add(strategyStatusColumn);
        strategyTable.getColumns().add(strategyUptimeColumn);
        strategyTable.getColumns().add(strategyOwnerColumn);
        strategyTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<DisplayStrategyInstance>) (inObservable,inOldValue,inNewValue) -> {
            enableStrategyContextMenuItems(inNewValue);
        });
    }
    /**
     * Get the selected strategy messages.
     *
     * @return a <code>Collection&lt;DisplayStrategyMessage&gt;</code> value
     */
    private Collection<DisplayStrategyMessage> getSelectedMessages()
    {
        return eventTable.getSelectionModel().getSelectedItems();
    }
    /**
     * Create a human-readable representation of the given strategy messages.
     *
     * @param inStrategyMessages a <code>Collection&lt;DisplayStrategymessage&gt;</code> value
     * @return a <code>String</code> value
     */
    private String renderStrategyMessages(Collection<DisplayStrategyMessage> inStrategyMessages)
    {
        Table table = new Table(4,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell("Strategy Messages",
                      PlatformServices.cellStyle,
                      4);
        table.addCell("Timestamp",
                      PlatformServices.cellStyle);
        table.addCell("Strategy",
                      PlatformServices.cellStyle);
        table.addCell("Severity",
                      PlatformServices.cellStyle);
        table.addCell("Message",
                      PlatformServices.cellStyle);
        for(DisplayStrategyMessage message : inStrategyMessages) {
            table.addCell(String.valueOf(message.timestampProperty().get()));
            table.addCell(message.strategyNameProperty().get());
            table.addCell(message.severityProperty().get().name());
            table.addCell(message.messageProperty().get());
        }
        return table.render();
    }
    /**
     * Initialize the event context menu.
     */
    private void initializeEventContextMenu()
    {
        eventTableContextMenu = new ContextMenu();
        copyStrategyEventMenuItem = new MenuItem("Copy");
        copyStrategyEventMenuItem.setOnAction(event -> {
            Collection<DisplayStrategyMessage> selectedItems = getSelectedMessages();
            if(selectedItems == null || selectedItems.isEmpty()) {
                return;
            }
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(renderStrategyMessages(selectedItems));
            clipboard.setContent(clipboardContent);
        });
        deleteStrategyEventMenuItem = new MenuItem("Delete");
        deleteStrategyEventMenuItem.setOnAction(event -> {
            Collection<DisplayStrategyMessage> selectedItems = getSelectedMessages();
            if(selectedItems == null || selectedItems.isEmpty()) {
                return;
            }
            for(DisplayStrategyMessage message : selectedItems) {
                try {
                    strategyClient.deleteStrategyMessage(message.strategyIdProperty().get());
                    uiMessageService.post(new NotificationEvent("Delete Strategy",
                                                                "Strategy message deleted",
                                                                AlertType.INFORMATION));
                    updateEvents();
                } catch (Exception e) {
                    String errorMessage = PlatformServices.getMessage(e);
                    uiMessageService.post(new NotificationEvent("Delete Strategy Message",
                                                                "Unable to delete strategy message: " + errorMessage,
                                                                AlertType.ERROR));
                }
            }
        });
        eventTableContextMenu.getItems().addAll(copyStrategyEventMenuItem,
                                                new SeparatorMenuItem(),
                                                deleteStrategyEventMenuItem);
        eventTable.setContextMenu(eventTableContextMenu);
    }
    /**
     * Enable the strategy context menu items based on the selected value.
     *
     * @param inNewValue a <code>DisplayStrategyInstance</code> value
     */
    private void enableStrategyContextMenuItems(DisplayStrategyInstance inNewValue)
    {
        if(inNewValue == null) {
            startStrategyMenuItem.setDisable(true);
            stopStrategyMenuItem.setDisable(true);
            unloadStrategyMenuItem.setDisable(true);
            clearEventsMenuItem.setDisable(true);
            return;
        }
        StrategyStatus status = inNewValue.strategyStatusProperty().get();
        switch(status) {
            case ERROR:
                startStrategyMenuItem.setDisable(false);
                stopStrategyMenuItem.setDisable(true);
                unloadStrategyMenuItem.setDisable(false);
                clearEventsMenuItem.setDisable(false);
                cancelStrategyUploadMenuItem.setDisable(true);
                break;
            case RUNNING:
                startStrategyMenuItem.setDisable(true);
                stopStrategyMenuItem.setDisable(false);
                unloadStrategyMenuItem.setDisable(true);
                clearEventsMenuItem.setDisable(false);
                cancelStrategyUploadMenuItem.setDisable(true);
                break;
            case STOPPED:
                startStrategyMenuItem.setDisable(false);
                stopStrategyMenuItem.setDisable(true);
                unloadStrategyMenuItem.setDisable(false);
                clearEventsMenuItem.setDisable(false);
                cancelStrategyUploadMenuItem.setDisable(true);
                break;
            case LOADING:
            case PREPARING:
                startStrategyMenuItem.setDisable(true);
                stopStrategyMenuItem.setDisable(true);
                unloadStrategyMenuItem.setDisable(true);
                clearEventsMenuItem.setDisable(true);
                cancelStrategyUploadMenuItem.setDisable(false);
                break;
            default:
                throw new UnsupportedOperationException("Unexpected strategy status: " + status);
        }
    }
    /**
     * Initialize the strategy context menu.
     */
    private void initializeStrategyContextMenu()
    {
        strategyTableContextMenu = new ContextMenu();
        startStrategyMenuItem = new MenuItem("Start");
        stopStrategyMenuItem = new MenuItem("Stop");
        unloadStrategyMenuItem = new MenuItem("Unload");
        clearEventsMenuItem = new MenuItem("Clear Events");
        cancelStrategyUploadMenuItem = new MenuItem("Cancel Upload");
        unloadStrategyMenuItem.setOnAction(event -> {
            DisplayStrategyInstance selectedStrategy = strategyTable.getSelectionModel().getSelectedItem();
            if(selectedStrategy == null) {
                return;
            }
            unloadStrategy(selectedStrategy);
        });
        cancelStrategyUploadMenuItem.setOnAction(event -> {
            DisplayStrategyInstance selectedStrategy = strategyTable.getSelectionModel().getSelectedItem();
            if(selectedStrategy == null) {
                return;
            }
            cancelStrategyUpload(selectedStrategy);
        });
        startStrategyMenuItem.setOnAction(event -> {
            DisplayStrategyInstance selectedStrategy = strategyTable.getSelectionModel().getSelectedItem();
            if(selectedStrategy == null) {
                return;
            }
            startStrategy(selectedStrategy);
        });
        stopStrategyMenuItem.setOnAction(event -> {
            DisplayStrategyInstance selectedStrategy = strategyTable.getSelectionModel().getSelectedItem();
            if(selectedStrategy == null) {
                return;
            }
            stopStrategy(selectedStrategy);
        });
        clearEventsMenuItem.setOnAction(event -> {
            DisplayStrategyInstance selectedStrategy = strategyTable.getSelectionModel().getSelectedItem();
            if(selectedStrategy == null) {
                return;
            }
            try {
                strategyClient.deleteAllStrategyMessages(selectedStrategy.strategyNameProperty().get());
                uiMessageService.post(new NotificationEvent("Delete All Strategy Messages",
                                                            "Strategy messages deleted",
                                                            AlertType.INFORMATION));
                updateEvents();
            } catch (Exception e) {
                String errorMessage = PlatformServices.getMessage(e);
                uiMessageService.post(new NotificationEvent("Delete Strategy All Messages",
                                                            "Unable to delete strategy messages: " + errorMessage,
                                                            AlertType.ERROR));
            }
        });
        boolean firstGroup = false;
        if(authzHelperService.hasPermission(StrategyPermissions.StartStrategyAction)) {
            firstGroup = true;
            strategyTableContextMenu.getItems().add(startStrategyMenuItem);
        }
        if(authzHelperService.hasPermission(StrategyPermissions.StopStrategyAction)) {
            firstGroup = true;
            strategyTableContextMenu.getItems().add(stopStrategyMenuItem);
        }
        if(authzHelperService.hasPermission(StrategyPermissions.UnloadStrategyAction)) {
            firstGroup = true;
            strategyTableContextMenu.getItems().add(unloadStrategyMenuItem);
        }
        if(authzHelperService.hasPermission(StrategyPermissions.CancelStrategyUploadAction)) {
            firstGroup = true;
            strategyTableContextMenu.getItems().add(cancelStrategyUploadMenuItem);
        }
        if(authzHelperService.hasPermission(StrategyPermissions.ClearStrategyEventsAction)) {
            if(firstGroup) {
                strategyTableContextMenu.getItems().add(new SeparatorMenuItem());
            }
            strategyTableContextMenu.getItems().add(clearEventsMenuItem);
        }
        strategyTable.setContextMenu(strategyTableContextMenu);
    }
    /**
     * Stops the given strategy.
     *
     * @param inSelectedStrategy a <code>DisplayStrategy</code> value
     */
    private void stopStrategy(DisplayStrategyInstance inSelectedStrategy)
    {
        SLF4JLoggerProxy.info(this,
                              "{} stopping '{}'",
                              SessionUser.getCurrent().getUsername(),
                              inSelectedStrategy.strategyNameProperty().get());
        try {
            strategyClient.stopStrategyInstance(inSelectedStrategy.strategyNameProperty().get());
            uiMessageService.post(new NotificationEvent("Stop Strategy",
                                                         "Strategy '" + inSelectedStrategy.strategyNameProperty().get() + " stopped",
                                                         AlertType.INFORMATION));
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            uiMessageService.post(new NotificationEvent("Stop Strategy",
                                                         "Strategy '" + inSelectedStrategy.strategyNameProperty().get() + " stop failed: " + PlatformServices.getMessage(e),
                                                         AlertType.ERROR));
        }
        updateStrategies();
    }
    /**
     * Starts the given strategy.
     *
     * @param inSelectedStrategy a <code>DisplayStrategy</code> value
     */
    private void startStrategy(DisplayStrategyInstance inSelectedStrategy)
    {
        SLF4JLoggerProxy.info(this,
                              "{} starting '{}'",
                              SessionUser.getCurrent().getUsername(),
                              inSelectedStrategy.strategyNameProperty().get());
        try {
            strategyClient.startStrategyInstance(inSelectedStrategy.strategyNameProperty().get());
            uiMessageService.post(new NotificationEvent("Start Strategy",
                                                         "Strategy '" + inSelectedStrategy.strategyNameProperty().get() + " started",
                                                         AlertType.INFORMATION));
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            uiMessageService.post(new NotificationEvent("Start Strategy",
                                                         "Strategy '" + inSelectedStrategy.strategyNameProperty().get() + " start failed: " + PlatformServices.getMessage(e),
                                                         AlertType.ERROR));
        }
        updateStrategies();
    }
    /**
     * listens for strategy events
     */
    private StrategyEventListener strategyEventListener;
    /**
     * interval at which runtime update events will be sent out
     */
    @Value("${metc.strategy.runtime.update.interval:1000}")
    private long strategyRuntimeUpdateInterval;
    /**
     * used to trigger updates to the strategy runtime values
     */
    private Timer strategyRuntimeUpdateTimer;
    /**
     * wrench value used to indicate selection of all strategies
     */
    private final String ALL_STRATEGIES = "<all strategies>";
    /**
     * event table pagination page number
     */
    private int eventTableCurrentPage;
    /**
     * event table pagination page size
     */
    private int eventTablePageSize;
    /**
     * event table pagination widget
     */
    private Pagination eventTablePagination;
    /**
     * start strategy menu item
     */
    private MenuItem startStrategyMenuItem;
    /**
     * stop strategy menu item
     */
    private MenuItem stopStrategyMenuItem;
    /**
     * unload strategy menu item
     */
    private MenuItem unloadStrategyMenuItem;
    /**
     * clear strategy events menu item
     */
    private MenuItem clearEventsMenuItem;
    /**
     * copy strategy event menu item
     */
    private MenuItem copyStrategyEventMenuItem;
    /**
     * delete strategy event menu item
     */
    private MenuItem deleteStrategyEventMenuItem;
    /**
     * cancel strategy upload menu item
     */
    private MenuItem cancelStrategyUploadMenuItem;
    /**
     * strategy table context menu
     */
    private ContextMenu strategyTableContextMenu;
    /**
     * strategy event context menu
     */
    private ContextMenu eventTableContextMenu;
    /**
     * strategy name selection widget
     */
    private ComboBox<String> strategyNameComboBox;
    /**
     * strategy severity selection widget
     */
    private ComboBox<Severity> severityComboBox;
    /**
     * strategy event filter layout
     */
    private GridPane filterLayout;
    /**
     * main view layout
     */
    private VBox mainLayout;
    /**
     * strategy button layout
     */
    private HBox buttonLayout;
    /**
     * strategy table name column
     */
    private TableColumn<DisplayStrategyInstance,String> strategyNameColumn;
    /**
     * strategy table status column
     */
    private TableColumn<DisplayStrategyInstance,StrategyStatus> strategyStatusColumn;
    /**
     * strategy table uptime column
     */
    private TableColumn<DisplayStrategyInstance,Period> strategyUptimeColumn;
    /**
     * strategy table owner column
     */
    private TableColumn<DisplayStrategyInstance,String> strategyOwnerColumn;
    /**
     * event table strategy name column
     */
    private TableColumn<DisplayStrategyMessage,String> eventStrategyNameColumn;
    /**
     * event table timestamp column
     */
    private TableColumn<DisplayStrategyMessage,DateTime> eventTimestampColumn;
    /**
     * event severity table column
     */
    private TableColumn<DisplayStrategyMessage,Severity> eventSeverityColumn;
    /**
     * event message table column
     */
    private TableColumn<DisplayStrategyMessage,String> eventMessageColumn;
    /**
     * load strategy widget
     */
    private Button loadStrategyButton;
    /**
     * strategy table
     */
    private TableView<DisplayStrategyInstance> strategyTable;
    /**
     * strategy event table
     */
    private TableView<DisplayStrategyMessage> eventTable;
    /**
     * provides access to strategy services
     */
    private StrategyClientService strategyClient;
    /**
     * global name of the strategy
     */
    private static final String NAME = "Strategy View";
}
