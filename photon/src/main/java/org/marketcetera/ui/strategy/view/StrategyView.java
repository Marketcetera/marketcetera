package org.marketcetera.ui.strategy.view;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.marketcetera.admin.User;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.strategy.StrategyInstance;
import org.marketcetera.strategy.StrategyInstanceFactory;
import org.marketcetera.strategy.StrategyPermissions;
import org.marketcetera.strategy.StrategyStatus;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.strategy.service.StrategyClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import info.schnatterer.mobynamesgenerator.MobyNamesGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/* $License$ */

/**
 * Displays loaded strategies and events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StrategyView
        extends AbstractContentView
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        strategyClient = serviceManager.getService(StrategyClientService.class);
        mainLayout = new VBox(5);
        initializeStrategyTable();
        initializeEventTable();
        eventTablePagination = new Pagination();
        eventTablePagination.setPageCount(10);
        eventTablePagination.setCurrentPageIndex(1);
        eventTablePagination.setMaxPageIndicatorCount(10);
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
        severityComboBox = new ComboBox<>();
        severityComboBox.getItems().addAll(Severity.values());
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
        buttonLayout = new HBox(5);
        buttonLayout.getChildren().add(loadStrategyButton);
        mainLayout.getChildren().addAll(strategyTable,
                                        new Separator(Orientation.HORIZONTAL),
                                        filterLayout,
                                        eventTable,
                                        eventTablePagination,
                                        new Separator(Orientation.HORIZONTAL),
                                        buttonLayout);
        mainScene = new Scene(mainLayout);
        updateStrategies();
        updateEvents();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getScene()
     */
    @Override
    public Scene getScene()
    {
        return mainScene;
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
     * Create a new StrategyView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public StrategyView(Stage inParent,
                        NewWindowEvent inEvent,
                        Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
    }
    private void updateEvents()
    {
        
    }
    private void loadStrategy()
    {
        Optional<User> ownerOption = PhotonServices.getCurrentUser();
        if(ownerOption.isEmpty()) {
            SLF4JLoggerProxy.warn(this,
                                  "Cannot load a strategy because the current user cannot be determined");
            webMessageService.post(new NotificationEvent("Load Strategy",
                                                         "Cannot load a new strategy because the current user cannot be determined",
                                                         AlertType.ERROR));
            return;
        }
        User owner = ownerOption.get();
        // TODO to avoid duplicates, check the existing strategies for this user and add a retry
        String name = MobyNamesGenerator.getRandomName();
        String nonce = UUID.randomUUID().toString();
        FileChooser strategyFileChooser = new FileChooser();
        strategyFileChooser.setTitle("Choose the Strategy JAR File");
        strategyFileChooser.getExtensionFilters().add(new ExtensionFilter("JAR Files",
                                                                          "*.jar"));
        File result = strategyFileChooser.showOpenDialog(getParentWindow());
        if(result != null) {
            if(!(result.exists() && result.canRead())) {
                webMessageService.post(new NotificationEvent("Load Strategy",
                                                             "File '" + result.getAbsolutePath() + "' could not be read",
                                                             AlertType.WARNING));
                return;
            }
            StrategyInstance newStrategyInstance = strategyInstanceFactory.create();
            try {
                // TODO allow the user to specify a name
                // TODO check that the name is unique
                newStrategyInstance.setFilename(nonce + ".jar");
                newStrategyInstance.setName(name);
                newStrategyInstance.setUser(owner);
                String shaChecksum = PhotonServices.getFileChecksum(result);
                newStrategyInstance.setHash(shaChecksum);
                // TODO transfer file
                StrategyStatus status = strategyClient.loadStrategyInstance(newStrategyInstance);
                SLF4JLoggerProxy.info(this,
                                      "Strategy '{}' loaded for {} with status {}",
                                      name,
                                      owner,
                                      status);
                updateStrategies();
                webMessageService.post(new NotificationEvent("Load Strategy",
                                                             "Strategy '" + name + "' loaded with status: " + status,
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to create '{}'",
                                      newStrategyInstance);
                webMessageService.post(new NotificationEvent("Load Strategy",
                                                             "File '" + result.getAbsolutePath() + "' could not be read",
                                                             AlertType.WARNING));
            }
        }
    }
    private void updateStrategies()
    {
        Platform.runLater(() -> {
            strategyTable.getItems().clear();
            Collection<? extends StrategyInstance> results = strategyClient.getStrategyInstances();
            if(results == null) {
                return;
            }
            List<DisplayStrategy> displayStrategies = Lists.newArrayList();
            results.forEach(result -> displayStrategies.add(new DisplayStrategy(result)));
            strategyTable.getItems().addAll(displayStrategies);
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
        eventStrategyIdColumn = new TableColumn<>("Strategy");
        eventStrategyIdColumn.setCellValueFactory(new PropertyValueFactory<>("strategyId"));
        eventTimestampColumn = new TableColumn<>("Timestamp");
        eventTimestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        eventTimestampColumn.setCellFactory(tableColumn -> PhotonServices.renderDateTimeCell(tableColumn));
        eventSeverityColumn = new TableColumn<>("Severity");
        eventSeverityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        eventTypeColumn = new TableColumn<>("Event Type");
        eventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        eventMessageColumn = new TableColumn<>("Message");
        eventMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        eventTable.getColumns().add(eventStrategyIdColumn);
        eventTable.getColumns().add(eventTimestampColumn);
        eventTable.getColumns().add(eventSeverityColumn);
        eventTable.getColumns().add(eventTypeColumn);
        eventTable.getColumns().add(eventMessageColumn);
    }
    private void initializeStrategyTableColumns()
    {
        strategyNameColumn = new TableColumn<>("Name");
        strategyNameColumn.setCellValueFactory(new PropertyValueFactory<>("strategyName"));
        strategyStatusColumn = new TableColumn<>("Status");
        strategyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("strategyStatus"));
        strategyUptimeColumn = new TableColumn<>("Uptime");
        strategyUptimeColumn.setCellValueFactory(new PropertyValueFactory<>("started"));
        strategyUptimeColumn.setCellFactory(tableColumn -> PhotonServices.renderPeriodCell(tableColumn));
        strategyOwnerColumn = new TableColumn<>("Owner");
        strategyOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        strategyTable.getColumns().add(strategyNameColumn);
        strategyTable.getColumns().add(strategyStatusColumn);
        strategyTable.getColumns().add(strategyUptimeColumn);
        strategyTable.getColumns().add(strategyOwnerColumn);
        strategyTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<DisplayStrategy>) (inObservable,inOldValue,inNewValue) -> {
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
    private void enableStrategyContextMenuItems(DisplayStrategy inNewValue)
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
                break;
            case RUNNING:
                startStrategyMenuItem.setDisable(true);
                stopStrategyMenuItem.setDisable(false);
                unloadStrategyMenuItem.setDisable(true);
                clearEventsMenuItem.setDisable(false);
                break;
            case STOPPED:
                startStrategyMenuItem.setDisable(false);
                stopStrategyMenuItem.setDisable(true);
                unloadStrategyMenuItem.setDisable(false);
                clearEventsMenuItem.setDisable(false);
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
        if(authzHelperService.hasPermission(StrategyPermissions.ClearStrategyEventsAction)) {
            if(firstGroup) {
                strategyTableContextMenu.getItems().add(new SeparatorMenuItem());
            }
            strategyTableContextMenu.getItems().add(clearEventsMenuItem);
        }
        strategyTable.setContextMenu(strategyTableContextMenu);
    }
    @Autowired
    private StrategyInstanceFactory strategyInstanceFactory;
    protected int eventTableCurrentPage;
    protected int eventTablePageSize;
    protected Pagination eventTablePagination;
    private MenuItem startStrategyMenuItem;
    private MenuItem stopStrategyMenuItem;
    private MenuItem unloadStrategyMenuItem;
    private MenuItem clearEventsMenuItem;
    private MenuItem copyStrategyEventMenuItem;
    private MenuItem deleteStrategyEventMenuItem;
    private ContextMenu strategyTableContextMenu;
    private ContextMenu eventTableContextMenu;
    private ComboBox<String> strategyIdComboBox;
    private ComboBox<Severity> severityComboBox;
    private GridPane filterLayout;
    private VBox mainLayout;
    private HBox buttonLayout;
    private TableColumn<DisplayStrategy,String> strategyNameColumn;
    private TableColumn<DisplayStrategy,StrategyStatus> strategyStatusColumn;
    private TableColumn<DisplayStrategy,Period> strategyUptimeColumn;
    private TableColumn<DisplayStrategy,String> strategyOwnerColumn;
    private TableColumn<DisplayStrategyEvent,String> eventStrategyIdColumn;
    private TableColumn<DisplayStrategyEvent,DateTime> eventTimestampColumn;
    private TableColumn<DisplayStrategyEvent,Severity> eventSeverityColumn;
    private TableColumn<DisplayStrategyEvent,String> eventTypeColumn;
    private TableColumn<DisplayStrategyEvent,String> eventMessageColumn;
    private Button loadStrategyButton;
    private TableView<DisplayStrategy> strategyTable;
    private TableView<DisplayStrategyEvent> eventTable;
    private StrategyClientService strategyClient;
    /**
     * main scene object
     */
    private Scene mainScene;
    /**
     * global name of the strategy
     */
    private static final String NAME = "Strategy View";
}
