package org.marketcetera.ui.trade.view.suggestions;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.SuggestionListener;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.event.SuggestionEvent;
import org.marketcetera.ui.trade.service.TradeClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/* $License$ */

/**
 * Displays trade suggestions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@AutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SuggestionsView
        extends AbstractContentView
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        tradeClient = serviceManager.getService(TradeClientService.class);
        mainLayout = new VBox(10);
        initializeSuggestionTable();
        mainLayout.getChildren().add(suggestionTable);
        suggestionTable.prefWidthProperty().bind(mainLayout.widthProperty());
        suggestionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        initializeSuggestionListener();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose()
     */
    @Override
    public void onClose()
    {
        if(suggestionListener != null) {
            try {
                tradeClient.removeSuggestionListener(suggestionListener);
                suggestionListener = null;
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
        updateSuggestions();
        initializeSuggestionListener();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientDisconnect()
     */
    @Override
    protected void onClientDisconnect()
    {
        Platform.runLater(() -> {
            suggestionTable.getItems().clear();
        });
    }
    /**
     * Create a new SuggestionsView instance.
     *
     * @param inParent a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public SuggestionsView(Region inParent,
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
    private void initializeSuggestionListener()
    {
        if(suggestionListener != null) {
            try {
                tradeClient.removeSuggestionListener(suggestionListener);
                suggestionListener = null;
            } catch (Exception ignored) {}
        }
        suggestionListener = new SuggestionListener() {
            @Override
            public void receiveSuggestion(Suggestion inSuggestion)
            {
                SLF4JLoggerProxy.trace(SuggestionsView.this,
                                       "Received {}",
                                       inSuggestion);
                suggestionTable.getItems().add(new DisplaySuggestion(inSuggestion));
            }
        };
        tradeClient.addSuggestionListener(suggestionListener);
    }
    /**
     * Update the strategies table.
     */
    private void updateSuggestions()
    {
        // TODO nothing really to do right now because the suggestions are not persisted - see MATP-1159
//        Platform.runLater(() -> {
//        });
    }
    /**
     * Initialize the strategy table.
     */
    private void initializeSuggestionTable()
    {
        suggestionTable = new TableView<>();
        suggestionTable.setPlaceholder(new Label("no suggestions"));
        initializeSuggestionTableColumns();
        initializeSuggestionContextMenu();
        suggestionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    /**
     * Render the given column as an Instrument cell.
     *
     * @param inTableColumn a <code>TableColumn&lt;DisplaySuggestion,Instrument&gt;</code> value
     * @return a <code>TableCell&lt;DisplaySuggestion,Instrument&gt;</code> value
     */
    public static TableCell<DisplaySuggestion,Instrument> renderInstrumentCell(TableColumn<DisplaySuggestion,Instrument> inTableColumn)
    {
        TableCell<DisplaySuggestion,Instrument> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Instrument inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty && inItem != null){
                    this.setText(inItem.getFullSymbol());
                }
            }
        };
        return tableCell;
    }
    /**
     * Initialize the strategy table columns.
     */
    private void initializeSuggestionTableColumns()
    {
        identifierColumn = new TableColumn<>("Identifier"); 
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        scoreColumn = new TableColumn<>("Score"); 
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn,
                                                                                  4,
                                                                                  4));
        quantityColumn = new TableColumn<>("Quantity"); 
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        sideColumn = new TableColumn<>("Side"); 
        sideColumn.setCellValueFactory(new PropertyValueFactory<>("side"));
        quantityColumn = new TableColumn<>("Quantity"); 
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        instrumentColumn = new TableColumn<>("Instrument"); 
        instrumentColumn.setCellValueFactory(new PropertyValueFactory<>("instrument"));
        instrumentColumn.setCellFactory(tableColumn -> renderInstrumentCell(tableColumn));
        priceColumn = new TableColumn<>("Price"); 
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        orderTypeColumn = new TableColumn<>("Order Type"); 
        orderTypeColumn.setCellValueFactory(new PropertyValueFactory<>("orderType"));
        timestampColumn = new TableColumn<>("Timestamp"); 
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampColumn.setCellFactory(tableColumn -> PhotonServices.renderDateCell(tableColumn));
        suggestionTable.getColumns().add(identifierColumn);
        suggestionTable.getColumns().add(scoreColumn);
        suggestionTable.getColumns().add(sideColumn);
        suggestionTable.getColumns().add(instrumentColumn);
        suggestionTable.getColumns().add(quantityColumn);
        suggestionTable.getColumns().add(priceColumn);
        suggestionTable.getColumns().add(orderTypeColumn);
        suggestionTable.getColumns().add(timestampColumn);
    }
    /**
     * Enable or disable context menu items based on the given selected row items.
     *
     * @param inSelectedItems a <code>Collection&lt;DisplaySuggestion&gt;</code> value
     */
    protected void enableContextMenuItems(Collection<DisplaySuggestion> inSelectedItems)
    {
        if(inSelectedItems == null || inSelectedItems.isEmpty()) {
            executeMenuItem.setDisable(true);
            deleteMenuItem.setDisable(true);
            return;
        }
        // any suggestion can be executed or deleted, assuming appropriate permissions
        executeMenuItem.setDisable(!authzHelperService.hasPermission(TradePermissions.SendOrderAction));
        deleteMenuItem.setDisable(false);
    }
    /**
     * Get the selected suggestions.
     *
     * @return a <code>Collection&lt;DisplaySuggestion&gt;</code> value
     */
    private Collection<DisplaySuggestion> getSelectedSuggestions()
    {
        return suggestionTable.getSelectionModel().getSelectedItems();
    }
    /**
     * Initialize the strategy context menu.
     */
    private void initializeSuggestionContextMenu()
    {
        executeMenuItem = new MenuItem("Execute");
        executeMenuItem.setOnAction(event -> {
            Collection<DisplaySuggestion> selectedItems = getSelectedSuggestions();
            if(selectedItems == null || selectedItems.isEmpty()) {
                return;
            }
            doExecute(selectedItems);
        });
        deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            Collection<DisplaySuggestion> selectedItems = getSelectedSuggestions();
            if(selectedItems == null || selectedItems.isEmpty()) {
                return;
            }
            doDelete(selectedItems);
        });
        copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(event -> {
            Collection<DisplaySuggestion> selectedItems = getSelectedSuggestions();
            if(selectedItems == null || selectedItems.isEmpty()) {
                return;
            }
            doCopy(selectedItems);
        });
        suggestionContextMenu = new ContextMenu();
        suggestionContextMenu.getItems().addAll(executeMenuItem,
                                                deleteMenuItem,
                                                new SeparatorMenuItem(),
                                                copyMenuItem);
        suggestionTable.setContextMenu(suggestionContextMenu);
        suggestionTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<DisplaySuggestion>) (inObservable,inOldValue,inNewValue) -> {
            enableContextMenuItems(getSelectedSuggestions());
        });
    }
    /**
     * Perform the execute operation on the given suggestions.
     *
     * @param inSuggestions a <code>Collection&lt;DisplaySuggestion&gt;</code> value
     */
    private void doExecute(Collection<DisplaySuggestion> inSuggestions)
    {
        for(DisplaySuggestion displaySuggestion : inSuggestions) {
            uiMessageService.post(applicationContext.getBean(SuggestionEvent.class,
                                                             displaySuggestion.sideProperty().get().name() + " " + displaySuggestion.instrumentProperty().get().getFullSymbol(),
                                                             displaySuggestion.sourceProperty().get()));
        }
    }
    /**
     * Perform the delete operation on the given suggestions.
     *
     * @param inSuggestions a <code>Collection&lt;DisplaySuggestion&gt;</code> value
     */
    private void doDelete(Collection<DisplaySuggestion> inSuggestions)
    {
        suggestionTable.getItems().removeAll(inSuggestions);
    }
    /**
     * Perform the copy operation on the given suggestions.
     *
     * @param inSuggestions a <code>Collection&lt;DisplaySuggestion&gt;</code> value
     */
    private void doCopy(Collection<DisplaySuggestion> selectedItems)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        StringBuilder output = new StringBuilder();
        boolean commaNeeded = false;
        for(TableColumn<DisplaySuggestion,?> column : suggestionTable.getColumns()) {
            if(commaNeeded) {
                output.append(',');
            }
            output.append(column.getText());
            commaNeeded = true;
        }
        output.append(StringUtils.LF);
        // need to be able to retrieve the field from the column header name
        for(DisplaySuggestion suggestion : selectedItems) {
            commaNeeded = false;
            for(TableColumn<DisplaySuggestion,?> column : suggestionTable.getColumns()) {
                if(commaNeeded) {
                    output.append(',');
                }
                try {
                    output.append(PhotonServices.getFieldValue(suggestion,column.getText()));
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
                commaNeeded = true;
            }
            output.append(StringUtils.LF);
        }
        clipboardContent.putString(output.toString());
        clipboard.setContent(clipboardContent);
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * identifier column
     */
    private TableColumn<DisplaySuggestion,String> identifierColumn;
    /**
     * side column
     */
    private TableColumn<DisplaySuggestion,Side> sideColumn;
    /**
     * quantity column
     */
    private TableColumn<DisplaySuggestion,BigDecimal> quantityColumn;
    /**
     * price column
     */
    private TableColumn<DisplaySuggestion,BigDecimal> priceColumn;
    /**
     * score column
     */
    private TableColumn<DisplaySuggestion,BigDecimal> scoreColumn;
    /**
     * instrument column
     */
    private TableColumn<DisplaySuggestion,Instrument> instrumentColumn;
    /**
     * order type column
     */
    private TableColumn<DisplaySuggestion,OrderType> orderTypeColumn;
    /**
     * timestamp column
     */
    private TableColumn<DisplaySuggestion,Date> timestampColumn;
    // TODO suggestion type column?
    // TODO suggestion owner column?
    /**
     * context menu for the suggestion table
     */
    private ContextMenu suggestionContextMenu;
    /**
     * execute menu item for the suggestion table
     */
    private MenuItem executeMenuItem;
    /**
     * delete menu item for the suggestion table
     */
    private MenuItem deleteMenuItem;
    /**
     * copy menu item for the suggestion table
     */
    private MenuItem copyMenuItem;
    /**
     * listens for suggestions
     */
    private SuggestionListener suggestionListener;
    /**
     * main view layout
     */
    private VBox mainLayout;
    /**
     * strategy table
     */
    private TableView<DisplaySuggestion> suggestionTable;
    /**
     * provides access to trade services
     */
    private TradeClientService tradeClient;
    /**
     * global name of the view
     */
    private static final String NAME = "Suggestions View";
}
