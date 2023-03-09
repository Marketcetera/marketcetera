package org.marketcetera.ui.strategy.view;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.view.AbstractContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* $License$ */

/**
 *
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
    private void initializeStrategyTable()
    {
        strategyTable = new TableView<>();
        strategyTable.setPlaceholder(new Label("no loaded strategies"));
        initializeStrategyTableColumns();
        initializeStrategyContextMenu();
    }
    private void initializeEventTable()
    {
        eventTable = new TableView<>();
        eventTable.setPlaceholder(new Label("no strategy events to display"));
        initializeEventTableColumns();
        initializeEventContextMenu();
    }
    private void initializeEventTableColumns()
    {
        eventStrategyIdColumn = new TableColumn<>("Strategy Id");
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
        strategyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        strategyIdColumn = new TableColumn<>("Strategy Id");
        strategyIdColumn.setCellValueFactory(new PropertyValueFactory<>("strategyId"));
        strategyStatusColumn = new TableColumn<>("Status");
        strategyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        strategyUptimeColumn = new TableColumn<>("Uptime");
        strategyUptimeColumn.setCellValueFactory(new PropertyValueFactory<>("started"));
        strategyUptimeColumn.setCellFactory(tableColumn -> PhotonServices.renderDateTimeCell(tableColumn));
        strategyOwnerColumn = new TableColumn<>("Owner");
        strategyOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        strategyTable.getColumns().add(strategyNameColumn);
        strategyTable.getColumns().add(strategyIdColumn);
        strategyTable.getColumns().add(strategyStatusColumn);
        strategyTable.getColumns().add(strategyUptimeColumn);
        strategyTable.getColumns().add(strategyOwnerColumn);
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
    private void initializeStrategyContextMenu()
    {
        strategyTableContextMenu = new ContextMenu();
        startStrategyMenuItem = new MenuItem("Start");
        stopStrategyMenuItem = new MenuItem("Stop");
        unloadStrategyMenuItem = new MenuItem("Unload");
        clearEventsMenuItem = new MenuItem("Clear Events");
        strategyTableContextMenu.getItems().addAll(startStrategyMenuItem,
                                                   stopStrategyMenuItem,
                                                   unloadStrategyMenuItem,
                                                   new SeparatorMenuItem(),
                                                   clearEventsMenuItem);
        strategyTable.setContextMenu(strategyTableContextMenu);
    }
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
    private TableColumn<DisplayStrategy,String> strategyIdColumn;
    private TableColumn<DisplayStrategy,StrategyStatus> strategyStatusColumn;
    private TableColumn<DisplayStrategy,DateTime> strategyUptimeColumn;
    private TableColumn<DisplayStrategy,String> strategyOwnerColumn;
    private TableColumn<DisplayStrategyEvent,String> eventStrategyIdColumn;
    private TableColumn<DisplayStrategyEvent,DateTime> eventTimestampColumn;
    private TableColumn<DisplayStrategyEvent,Severity> eventSeverityColumn;
    private TableColumn<DisplayStrategyEvent,String> eventTypeColumn;
    private TableColumn<DisplayStrategyEvent,String> eventMessageColumn;
    private Button loadStrategyButton;
    private TableView<DisplayStrategy> strategyTable;
    private TableView<DisplayStrategyEvent> eventTable;
    /**
     * main scene object
     */
    private Scene mainScene;
    /**
     * global name of the strategy
     */
    private static final String NAME = "Strategy View";
}
