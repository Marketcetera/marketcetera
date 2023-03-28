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
import org.assertj.core.util.Lists;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
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
        strategyIdComboBox = new ComboBox<>();
        strategyIdComboBox.getItems().add(ALL_STRATEGIES);
        strategyIdComboBox.valueProperty().addListener((observableValue,oldValue,newValue) -> updateEvents());
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
        filterLayout.add(strategyIdComboBox,++colCount,rowCount);
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
                                        filterLayout,
                                        eventTable,
                                        eventTablePagination,
                                        new Separator(Orientation.HORIZONTAL),
                                        buttonLayout);
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
    private void updateStrategyRuntime()
    {
        Platform.runLater(() -> {
            for(DisplayStrategyInstance displayStrategyInstance : strategyTable.getItems()) {
                displayStrategyInstance.updateRunningProperty();
            }
        });
    }
    private void updateEvents()
    {
        PageRequest pageRequest = new PageRequest(eventTableCurrentPage,
                                                  eventTablePageSize);
        String selectedStrategyName = strategyIdComboBox.valueProperty().get();
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
    private void cancelStrategyUpload(DisplayStrategyInstance inSelectedItem)
    {
        // TODO
    }
    private void updateStrategies()
    {
        Platform.runLater(() -> {
            String selectedStrategyName = strategyIdComboBox.valueProperty().get();
            strategyIdComboBox.getItems().clear();
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
            strategyIdComboBox.getItems().add(ALL_STRATEGIES);
            strategyIdComboBox.getItems().addAll(strategyNames);
            strategyTable.getItems().addAll(displayStrategies);
            if(strategyIdComboBox.getItems().contains(selectedStrategyName)) {
                strategyIdComboBox.valueProperty().set(selectedStrategyName);
            } else {
                strategyIdComboBox.valueProperty().set(ALL_STRATEGIES);
            }
        });
    }
    private void initializeStrategyTable()
    {
        strategyTable = new TableView<>();
        strategyTable.setPlaceholder(new Label("no loaded strategies"));
        initializeStrategyTableColumns();
        initializeStrategyContextMenu();
        strategyTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    private void initializeEventTable()
    {
        eventTable = new TableView<>();
        eventTable.setPlaceholder(new Label("no strategy events to display"));
        initializeEventTableColumns();
        initializeEventContextMenu();
        eventTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    private void initializeEventTableColumns()
    {
        eventStrategyNameColumn = new TableColumn<>("Strategy");
        eventStrategyNameColumn.setCellValueFactory(new PropertyValueFactory<>("strategyId"));
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
        strategyProgressColumn = new TableColumn<>("Upload Progress");
        strategyProgressColumn.setCellValueFactory(new PropertyValueFactory<>("uploadProgress"));
        strategyProgressColumn.setCellFactory(tableColumn -> new TableCell<DisplayStrategyInstance,Double>() {});
        strategyProgressColumn.setCellFactory(ProgressBarTableCell.<DisplayStrategyInstance> forTableColumn());
        strategyTable.getColumns().add(strategyNameColumn);
        strategyTable.getColumns().add(strategyStatusColumn);
        strategyTable.getColumns().add(strategyUptimeColumn);
        strategyTable.getColumns().add(strategyOwnerColumn);
        strategyTable.getColumns().add(strategyProgressColumn);
        strategyTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<DisplayStrategyInstance>) (inObservable,inOldValue,inNewValue) -> {
            enableStrategyContextMenuItems(inNewValue);
        });
    }
    private void initializeEventContextMenu()
    {
        eventTableContextMenu = new ContextMenu();
        copyStrategyEventMenuItem = new MenuItem("Copy");
        deleteStrategyEventMenuItem = new MenuItem("Delete");
        eventTableContextMenu.getItems().addAll(copyStrategyEventMenuItem,
                                                new SeparatorMenuItem(),
                                                deleteStrategyEventMenuItem);
        eventTable.setContextMenu(eventTableContextMenu);
    }
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
     * stops the given strategy.
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
//    private static class StringComparator
//            implements Comparator<String>
//    {
//        /* (non-Javadoc)
//         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
//         */
//        @Override
//        public int compare(String inO1,
//                           String inO2)
//        {
//            return new CompareToBuilder().append(inO1,inO2).toComparison();
//        }
//        private final static StringComparator instance = new StringComparator();
//    }
    /**
     * listens for strategy events
     */
    private StrategyEventListener strategyEventListener;
    /**
     * interval at which runtime update events will be sent out
     */
    @Value("${metc.strategy.runtime.update.interval:1000}")
    private long strategyRuntimeUpdateInterval;
    private Timer strategyRuntimeUpdateTimer;
    private final String ALL_STRATEGIES = "<all strategies>";
    private int eventTableCurrentPage;
    private int eventTablePageSize;
    private Pagination eventTablePagination;
    private MenuItem startStrategyMenuItem;
    private MenuItem stopStrategyMenuItem;
    private MenuItem unloadStrategyMenuItem;
    private MenuItem clearEventsMenuItem;
    private MenuItem copyStrategyEventMenuItem;
    private MenuItem deleteStrategyEventMenuItem;
    private MenuItem cancelStrategyUploadMenuItem;
    private ContextMenu strategyTableContextMenu;
    private ContextMenu eventTableContextMenu;
    private ComboBox<String> strategyIdComboBox;
    private ComboBox<Severity> severityComboBox;
    private GridPane filterLayout;
    private VBox mainLayout;
    private HBox buttonLayout;
    private TableColumn<DisplayStrategyInstance,String> strategyNameColumn;
    private TableColumn<DisplayStrategyInstance,StrategyStatus> strategyStatusColumn;
    private TableColumn<DisplayStrategyInstance,Period> strategyUptimeColumn;
    private TableColumn<DisplayStrategyInstance,String> strategyOwnerColumn;
    private TableColumn<DisplayStrategyInstance,Double> strategyProgressColumn;
    private TableColumn<DisplayStrategyMessage,String> eventStrategyNameColumn;
    private TableColumn<DisplayStrategyMessage,DateTime> eventTimestampColumn;
    private TableColumn<DisplayStrategyMessage,Severity> eventSeverityColumn;
    private TableColumn<DisplayStrategyMessage,String> eventMessageColumn;
    private Button loadStrategyButton;
    private TableView<DisplayStrategyInstance> strategyTable;
    private TableView<DisplayStrategyMessage> eventTable;
    private StrategyClientService strategyClient;
    /**
     * global name of the strategy
     */
    private static final String NAME = "Strategy View";
}
