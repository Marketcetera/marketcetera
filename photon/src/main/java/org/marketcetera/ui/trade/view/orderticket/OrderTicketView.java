package org.marketcetera.ui.trade.view.orderticket;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedMap;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.client.Validations;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.HasSuggestion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.client.OrderValidationException;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.marketdata.service.MarketDataClientService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.StyleService;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.ui.trade.service.TradeClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.ValidatingTextField;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/* $License$ */

/**
 * Provides a view for Order Tickets.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderTicketView
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
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
      return NAME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClose()
     */
    @Override
    public void onClose()
    {
        cancelPegToMidpointMarketDataRequest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        marketDataClient = serviceManager.getService(MarketDataClientService.class);
        rootLayout = new VBox();
        orderTicketLayout = new GridPane();
        // create controls and layouts
        brokerLabel = new Label("Broker");
        brokerComboBox = new ComboBox<>();
        pegToMidpointLabel = new Label("Peg to Midpoint");
        pegToMidpointCheckBox = new CheckBox();
        pegToMidpointLockedLabel = new Label("Peg to Midpoint Locked");
        pegToMidpointLockedCheckBox = new CheckBox();
        textLabel = new Label("Text");
        textTextField = new TextField();
        accountLabel = new Label("Account");
        accountTextField = new TextField();
        brokerLayout = new VBox(5);
        sideLayout = new VBox(5);
        sideLabel = new Label("Side");
        sideComboBox = new ComboBox<>();
        quantityLayout = new VBox(5);
        quantityLabel = new Label("Quantity");
        quantityTextField = new ValidatingTextField(input -> input.matches("^(-|\\+)?(([1-9][0-9]*)|(0))(?:\\.[0-9]+)?$")); // TODO i18n
        symbolLayout = new VBox(5);
        symbolLabel = new Label("Symbol");
        symbolTextField = new TextField();
        orderTypeLayout = new VBox(5);
        orderTypeLabel = new Label("Order Type");
        orderTypeComboBox = new ComboBox<>();
        priceLayout = new VBox(5);
        priceLabel = new Label("Price");
        priceTextField = new ValidatingTextField(input -> input.matches("^(-|\\+)?(([1-9][0-9]*)|(0))(?:\\.[0-9]+)?$")); // TODO i18n
        timeInForceLayout = new VBox(5);
        timeInForceLabel = new Label("Time in Force");
        timeInForceComboBox = new ComboBox<>();
        otherAccordion = new Accordion();
        otherLayout = new GridPane();
        otherPane = new TitledPane("Other",
                                   otherLayout);
        exDestinationLabel = new Label("External Destination");
        exDestinationTextField = new TextField();
        maxFloorLabel = new Label("Max Floor");
        maxFloorTextField = new ValidatingTextField(input -> input.matches("^(-|\\+)?(([1-9][0-9]*)|(0))(?:\\.[0-9]+)?$")); // TODO i18n
        brokerAlgoAccordion = new Accordion();
        brokerAlgoLayout = new GridPane();
        brokerAlgoPane = new TitledPane("Broker Algos",
                                        brokerAlgoLayout);
        brokerAlgoLabel = new Label("Algo");
        brokerAlgoComboBox = new ComboBox<>();
        brokerAlgoTagTable = new TableView<>();
        customFieldsAccordion = new Accordion();
        customFieldsTable = new TableView<>();
        customFieldsPane = new TitledPane("Custom Fields",
                                          customFieldsTable);
        sendButton = new Button("Send");
        clearButton = new Button("Clear");
        buttonLayout = new HBox(5);
        adviceLabel = new Label("");
        adviceSeparator = new Separator(Orientation.HORIZONTAL);
        // find saved data, if any
        String xmlData = StringUtils.trimToNull(getViewProperties().getProperty(ExecutionReport.class.getCanonicalName()));
        ExecutionReport replaceExecutionReport = null;
        if(xmlData != null) {
            try {
                replaceExecutionReport = xmlService.unmarshall(xmlData);
            } catch (JAXBException e) {
                uiMessageService.post(new NotificationEvent("Replace Order",
                                                            "Unable to replace order: " + PlatformServices.getMessage(e),
                                                            AlertType.ERROR));
            }
        }
        replaceExecutionReportOption = Optional.ofNullable(replaceExecutionReport);
        xmlData = StringUtils.trimToNull(getViewProperties().getProperty(AverageFillPrice.class.getCanonicalName()));
        AverageFillPrice averageFillPrice = null;
        if(xmlData != null) {
            try {
                averageFillPrice = xmlService.unmarshall(xmlData);
            } catch (JAXBException e) {
                uiMessageService.post(new NotificationEvent("Trade Order",
                                                            "Unable to trade order: " + PlatformServices.getMessage(e),
                                                            AlertType.ERROR));
            }
        }
        averageFillPriceOption = Optional.ofNullable(averageFillPrice);
        Suggestion suggestion = null;
        if(getNewWindowEvent() instanceof HasSuggestion) {
            suggestion = ((HasSuggestion)getNewWindowEvent()).getSuggestion();
        }
        suggestionOption = Optional.ofNullable(suggestion);
        // prepare the first row
        // broker combo
        brokerComboBox.setPromptText("Broker");
        brokerComboBox.setId(getClass().getCanonicalName() + ".brokerComboBox");
        brokerComboBox.setTooltip(new Tooltip("Select order destination"));
        brokerComboBox.getItems().add(AUTO_SELECT_BROKER);
        brokerComboBox.setValue(AUTO_SELECT_BROKER);
        brokerComboBox.valueProperty().addListener(new ChangeListener<BrokerID>() {
            @Override
            public void changed(ObservableValue<? extends BrokerID> inObservable,
                                BrokerID inOldValue,
                                BrokerID inNewValue)
            {
                BrokerID selectedBroker = inNewValue;
                brokerAlgoComboBox.setValue(null);
                //              brokerAlgoComboBox.clear();
                // TODO clear brokerAlgoTagGrid
                if(selectedBroker == null || AUTO_SELECT_BROKER.equals(selectedBroker)) {
                    brokerAlgoComboBox.setDisable(true);
                    brokerAlgoLabel.setTooltip(new Tooltip("Broker algos available only when a broker is selected and that broker offers algos"));
                    brokerAlgoTagTable.setPrefHeight(100);
                } else {
                    brokerAlgoLabel.setTooltip(null);
                    brokerAlgoComboBox.setTooltip(new Tooltip("Select broker algo"));
                    ActiveFixSession underlyingFixSession = availableBrokers.get(selectedBroker);
                    if(underlyingFixSession != null && !underlyingFixSession.getBrokerAlgos().isEmpty()) {
                        brokerAlgoComboBox.setDisable(false);
                        brokerAlgoComboBox.getItems().addAll(underlyingFixSession.getBrokerAlgos());
                    }
                }
                adjustSendButton();
            }}
        );
        brokerLayout.getChildren().addAll(brokerLabel,
                                          brokerComboBox);
        styleService.addStyleToAll(brokerLabel,
                                   brokerComboBox,
                                   brokerLayout);
        initializeBrokerStatusListener();
        // side combo
        sideComboBox.setPromptText("Side");
        sideComboBox.setId(getClass().getCanonicalName() + ".sideComboBox");
        sideComboBox.getItems().addAll(EnumSet.complementOf(EnumSet.of(Side.Unknown)));
        sideComboBox.setValue(Side.Buy);
        sideComboBox.setTooltip(new Tooltip("FIX field " + quickfix.field.Side.FIELD));
        sideComboBox.valueProperty().addListener(new ChangeListener<Side>() {
            @Override
            public void changed(ObservableValue<? extends Side> inObservable,
                                Side inOldValue,
                                Side inNewValue)
            {
                adjustSendButton();
            }}
        );
        sideLayout.getChildren().addAll(sideLabel,
                                        sideComboBox);
        styleService.addStyleToAll(sideLabel,
                                   sideComboBox,
                                   sideLayout);
        // quantity
        quantityTextField.setPromptText("100");
        quantityTextField.setId(getClass().getCanonicalName() + ".quantityTextField");
        quantityTextField.textProperty().addListener((ChangeListener<String>) (inObservable,inOldValue,inNewValue) -> adjustSendButton());
        quantityTextField.setTooltip(new Tooltip("FIX field " + quickfix.field.OrderQty.FIELD));
        quantityTextField.isValidProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                quantityTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.textProperty().set("");
            } else {
                quantityTextField.setStyle(PhotonServices.errorStyle);
                adviceLabel.textProperty().set("Numeric quantity required");
            }
        });
        styleService.addStyleToAll(quantityLabel,
                                   quantityTextField,
                                   quantityLayout);
        quantityLayout.getChildren().addAll(quantityLabel,
                                            quantityTextField);
        // symbol
        symbolTextField.textProperty().addListener((ChangeListener<String>) (inObservable,inOldValue,inNewValue) -> {
            try {
                String symbol = serviceManager.getService(TradeClientService.class).getTreatedSymbol(String.valueOf(inNewValue));
                if(symbol == null) {
                    pegToMidpointCheckBox.setDisable(true);
                    pegToMidpointLockedCheckBox.setDisable(true);
                    pegToMidpointCheckBox.selectedProperty().set(false);
                    pegToMidpointLockedCheckBox.selectedProperty().set(false);
                    cancelPegToMidpointMarketDataRequest();
                    return;
                }
                resolvedInstrument = serviceManager.getService(TradeClientService.class).resolveSymbol(symbol);
                if(resolvedInstrument == null) {
                    symbolTextField.setStyle(PhotonServices.errorStyle);
                    adviceLabel.textProperty().set("Cannot resolve symbol");
                    pegToMidpointCheckBox.setDisable(true);
                    pegToMidpointLockedCheckBox.setDisable(true);
                    pegToMidpointCheckBox.selectedProperty().set(false);
                    pegToMidpointLockedCheckBox.selectedProperty().set(false);
                    cancelPegToMidpointMarketDataRequest();
                    return;
                }
                pegToMidpointCheckBox.setDisable(false);
                symbolTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.textProperty().set("");
                symbolTextField.setTooltip(new Tooltip(resolvedInstrument.toString()));
                doPegToMidpointMarketDataRequest();
            } finally {
                adjustSendButton();
            }
        });
        symbolLayout.getChildren().addAll(symbolLabel,
                                          symbolTextField);
        styleService.addStyleToAll(symbolLabel,
                                   symbolTextField,
                                   symbolLayout);
        // order type
        orderTypeComboBox.setPromptText("Order Type");
        orderTypeComboBox.setId(getClass().getCanonicalName() + ".orderTypeComboBox");
        orderTypeComboBox.getItems().addAll(EnumSet.complementOf(EnumSet.of(OrderType.Unknown)));
        orderTypeComboBox.setValue(null);
        orderTypeComboBox.valueProperty().addListener(new ChangeListener<OrderType>() {
            @Override
            public void changed(ObservableValue<? extends OrderType> inObservable,
                                OrderType inOldValue,
                                OrderType inNewValue)
            {
                OrderType value = inNewValue;
                boolean isMarket = false;
                if(value == null) {
                    isMarket = false;
                } else {
                    isMarket = value.isMarketOrder();
                }
                if(isMarket) {
                    cancelPegToMidpointMarketDataRequest();
                    priceTextField.setText("");
                    pegToMidpointCheckBox.selectedProperty().set(false);
                    pegToMidpointLockedCheckBox.selectedProperty().set(false);
                }
                priceTextField.setDisable(isMarket);
                pegToMidpointCheckBox.setDisable(isMarket);
                adjustSendButton();
            }}
        );
        orderTypeComboBox.setTooltip(new Tooltip("FIX Field " + quickfix.field.OrdType.FIELD));
        styleService.addStyleToAll(orderTypeLabel,
                                   orderTypeComboBox,
                                   orderTypeLayout);
        orderTypeLayout.getChildren().addAll(orderTypeLabel,
                                             orderTypeComboBox);
        // price
        priceTextField.setPromptText("50.00");
        priceTextField.setId(getClass().getCanonicalName() + ".priceTextField");
        priceTextField.setTooltip(new Tooltip("FIX field " + quickfix.field.OrderQty.FIELD));
        priceTextField.isValidProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                priceTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.textProperty().set("");
            } else {
                priceTextField.setStyle(PhotonServices.errorStyle);
                adviceLabel.textProperty().set("Numeric price required");
            }
            adjustSendButton();
        });
        priceTextField.textProperty().addListener((ChangeListener<String>) (inObservable,inOldValue,inNewValue) -> adjustSendButton());
        styleService.addStyleToAll(priceLabel,
                                   priceTextField,
                                   priceLayout);
        priceLayout.getChildren().addAll(priceLabel,
                                         priceTextField);
        // time-in-force
        timeInForceComboBox.setPromptText("Time in Force");
        timeInForceComboBox.setId(getClass().getCanonicalName() + ".timeInForceComboBox");
        timeInForceComboBox.getItems().addAll(EnumSet.complementOf(EnumSet.of(TimeInForce.Unknown)));
        timeInForceComboBox.setValue(null);
        timeInForceComboBox.setTooltip(new Tooltip("FIX Field " + quickfix.field.TimeInForce.FIELD));
        timeInForceComboBox.valueProperty().addListener((ChangeListener<TimeInForce>) (inObservable,inOldValue,inNewValue) -> adjustSendButton());
        timeInForceLayout.getChildren().addAll(timeInForceLabel,
                                               timeInForceComboBox);
        styleService.addStyleToAll(timeInForceLabel,
                                   timeInForceComboBox,
                                   timeInForceLayout);
        // other group
        otherAccordion.setId(getClass().getCanonicalName() + ".otherAccordion");
        otherPane.expandedProperty().set(false);
        otherPane.setId(getClass().getCanonicalName() + ".otherPane");
        otherLayout.setHgap(5);
        otherLayout.setVgap(5);
        otherLayout.setId(getClass().getCanonicalName() + ".otherLayout");
        otherAccordion.getPanes().add(otherPane);
        styleService.addStyleToAll(otherAccordion,
                                   otherLayout,
                                   otherPane);
        // text
        textLabel.setId(getClass().getCanonicalName() + ".textLabel");
        textTextField.setPromptText("optional order text");
        textTextField.setId(getClass().getCanonicalName() + ".textTextField");
        textTextField.setTooltip(new Tooltip("FIX field " + quickfix.field.Text.FIELD));
        otherLayout.add(textLabel,0,0);
        otherLayout.add(textTextField,1,0);
        styleService.addStyleToAll(textLabel,
                                   textTextField);
        // account
        accountLabel.setId(getClass().getCanonicalName() + ".accountLabel");
        accountTextField.setPromptText("optional account");
        accountTextField.setId(getClass().getCanonicalName() + ".accountTextField");
        accountTextField.setTooltip(new Tooltip("FIX field " + quickfix.field.Account.FIELD));
        otherLayout.add(accountLabel,0,1);
        otherLayout.add(accountTextField,1,1);
        styleService.addStyleToAll(accountLabel,
                                   accountTextField);
        // Ex Destination
        exDestinationLabel.setId(getClass().getCanonicalName() + ".exDestinationLabel");
        exDestinationTextField.setPromptText("optional ex destination");
        exDestinationTextField.setId(getClass().getCanonicalName() + ".exDestinationTextField");
        exDestinationTextField.setTooltip(new Tooltip("FIX field " + quickfix.field.ExDestination.FIELD));
        styleService.addStyleToAll(exDestinationLabel,
                                   exDestinationTextField);
        otherLayout.add(exDestinationLabel,0,2);
        otherLayout.add(exDestinationTextField,1,2);
        // max floor
        maxFloorTextField.setPromptText("max floor");
        maxFloorTextField.setId(getClass().getCanonicalName() + ".maxFloorTextField");
        maxFloorTextField.setTooltip(new Tooltip("FIX field " + quickfix.field.MaxFloor.FIELD));
        maxFloorTextField.isValidProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue || StringUtils.trimToNull(maxFloorTextField.textProperty().get()) == null) {
                maxFloorTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.textProperty().set("");
            } else {
                maxFloorTextField.setStyle(PhotonServices.errorStyle);
                adviceLabel.textProperty().set("Numeric max floor quantity required");
            }
            // TODO probably need to also check that max floor <= quantity
            adjustSendButton();
        });
        styleService.addStyleToAll(maxFloorLabel,
                                   maxFloorTextField);
        otherLayout.add(maxFloorLabel,0,3);
        otherLayout.add(maxFloorTextField,1,3);
        // peg to midpoint
        // peg-to-midpoint selector
        pegToMidpointLabel.setId(getClass().getCanonicalName() + ".pegToMidpointLabel");
        pegToMidpointCheckBox.setId(getClass().getCanonicalName() + ".pegToMidpointCheckBox");
        pegToMidpointCheckBox.setTooltip(new Tooltip("Price pegged to the midpoint of the spread when the order is routed"));
        pegToMidpointBidEventProperty.addListener((observableValue,oldValue,newValue) -> updatePegToMidpointPrice());
        pegToMidpointAskEventProperty.addListener((observableValue,oldValue,newValue) -> updatePegToMidpointPrice());
        pegToMidpointCheckBox.setDisable(true);
        pegToMidpointLockedCheckBox.setDisable(true);
        pegToMidpointCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            boolean value = newValue;
            pegToMidpointLockedCheckBox.setDisable(!value);
            // enable/disable price if the peg-to-midpoint is selected
            priceTextField.setDisable(value);
            if(!value) {
                pegToMidpointLockedCheckBox.selectedProperty().set(false);
                cancelPegToMidpointMarketDataRequest();
                adjustSendButton();
                return;
            }
            doPegToMidpointMarketDataRequest();
        });
        styleService.addStyleToAll(pegToMidpointLabel,
                                   pegToMidpointCheckBox);
        otherLayout.add(pegToMidpointLabel,0,4);
        otherLayout.add(pegToMidpointCheckBox,1,4);
        // peg-to-midpoint locked
        pegToMidpointLockedLabel.setId(getClass().getCanonicalName() + ".pegToMidpointLockedLabel");
        pegToMidpointLockedCheckBox.setId(getClass().getCanonicalName() + ".pegToMidpointLockedCheckBox");
        pegToMidpointLockedCheckBox.setDisable(true);
        pegToMidpointLockedCheckBox.setTooltip(new Tooltip("Price pegged to the midpoint of the spread when the order is sent"));
        styleService.addStyleToAll(pegToMidpointLockedLabel,
                                   pegToMidpointLockedCheckBox);
        otherLayout.add(pegToMidpointLockedLabel,0,5);
        otherLayout.add(pegToMidpointLockedCheckBox,1,5);
        // broker algos
        brokerAlgoAccordion.setId(getClass().getCanonicalName() + ".brokerAlgoAccordion");
        brokerAlgoPane.expandedProperty().set(false);
        brokerAlgoPane.setId(getClass().getCanonicalName() + ".brokerAlgoPane");
        brokerAlgoLayout.setHgap(5);
        brokerAlgoLayout.setVgap(5);
        brokerAlgoLayout.setId(getClass().getCanonicalName() + ".brokerAlgoLayout");
        brokerAlgoAccordion.getPanes().add(brokerAlgoPane);
        styleService.addStyleToAll(brokerAlgoAccordion,
                                   brokerAlgoLayout,
                                   brokerAlgoPane);
        // broker algo select
        brokerAlgoComboBox.setPromptText("Broker Algo");
        brokerAlgoComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // TODO populate/enable brokerAlgoTagGrid
        });
        brokerAlgoLabel.setId(getClass().getCanonicalName() + ".brokerAlgoLabel");
        brokerAlgoComboBox.setId(getClass().getCanonicalName() + ".brokerAlgoComboBox");
        styleService.addStyleToAll(brokerAlgoLabel,
                                   brokerAlgoComboBox);
        brokerAlgoLayout.add(brokerAlgoLabel,0,0);
        brokerAlgoLayout.add(brokerAlgoComboBox,1,0);
        // broker algo tag table
        // TODO this doesn't look editable
        brokerAlgoTagTable.setId(getClass().getCanonicalName() + ".brokerAlgoTable");
        TableColumn<BrokerAlgoTag,String> brokerAlgoTagLabelColumn = new TableColumn<>("Tag");
        brokerAlgoTagLabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        TableColumn<BrokerAlgoTag,String> brokerAlgoTagValueColumn = new TableColumn<>("Value");
        brokerAlgoTagValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        brokerAlgoTagTable.getColumns().add(brokerAlgoTagLabelColumn);
        brokerAlgoTagTable.getColumns().add(brokerAlgoTagValueColumn);
        brokerAlgoTagTable.setPlaceholder(new Label("No broker algos available from this broker"));
        styleService.addStyleToAll(brokerAlgoTagTable);
        brokerAlgoLayout.add(brokerAlgoTagTable,0,1,2,1);
        // custom fields
        customFieldsTable.setEditable(true);
        customFieldsAccordion.setId(getClass().getCanonicalName() + ".customFieldsAccordion");
        customFieldsPane.expandedProperty().set(false);
        customFieldsPane.setId(getClass().getCanonicalName() + ".customFieldsPane");
        customFieldsAccordion.getPanes().add(customFieldsPane);
        customFieldsTable.setId(getClass().getCanonicalName() + ".customFieldsTable");
        TableColumn<DisplayCustomField,Boolean> customFieldsEnabledColumn = new TableColumn<>("Enabled");
        customFieldsEnabledColumn.setCellValueFactory( cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().isEnabledProperty().get()));
        customFieldsEnabledColumn.setCellFactory(CheckBoxTableCell.<DisplayCustomField>forTableColumn(customFieldsEnabledColumn));
        customFieldsEnabledColumn.setId(getClass().getCanonicalName() + ".customFieldsEnabledColumn");
        customFieldsEnabledColumn.setEditable(true);
        TableColumn<DisplayCustomField,String> customFieldsKeyColumn = new TableColumn<>("Key");
        customFieldsKeyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        customFieldsKeyColumn.setCellFactory(TextFieldTableCell.<DisplayCustomField>forTableColumn());
        customFieldsKeyColumn.setId(getClass().getCanonicalName() + ".customFieldsKeyColumn");
        customFieldsKeyColumn.setEditable(true);
        TableColumn<DisplayCustomField,String> customFieldsValueColumn = new TableColumn<>("Value");
        customFieldsValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        customFieldsValueColumn.setCellFactory(TextFieldTableCell.<DisplayCustomField>forTableColumn());
        customFieldsValueColumn.setId(getClass().getCanonicalName() + ".customFieldsValueColumn");
        customFieldsValueColumn.setEditable(true);
        customFieldsTable.getColumns().add(customFieldsEnabledColumn);
        customFieldsTable.getColumns().add(customFieldsKeyColumn);
        customFieldsTable.getColumns().add(customFieldsValueColumn);
        customFieldsTable.setPlaceholder(new Label("No custom fields selected"));
        customFieldsTable.setTooltip(new Tooltip("Double click to add a custom field"));
        customFieldsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent inEvent)
            {
                if(inEvent.getClickCount() == 2) {
                    customFieldsTable.getItems().add(new DisplayCustomField());
                }
            }}
        );
        styleService.addStyleToAll(customFieldsAccordion,
                                   customFieldsPane,
                                   customFieldsTable);
        buttonLayout.getChildren().addAll(sendButton,
                                          clearButton);
        rootLayout.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.ENTER && !sendButton.disabledProperty().get()) {
               sendButton.fire();
               event.consume(); 
            }
        });
        sendButton.setOnMouseClicked(inEvent -> {
            NewOrReplaceOrder newOrder;
            if(replaceExecutionReportOption.isPresent()) {
                newOrder = Factory.getInstance().createOrderReplace(replaceExecutionReportOption.get());
            } else {
                newOrder = Factory.getInstance().createOrderSingle();
            }
            newOrder.setAccount(StringUtils.trimToNull(accountTextField.textProperty().get()));
            if(brokerAlgoComboBox.getValue() != null) {
                BrokerAlgoSpec brokerAlgoSpec = (BrokerAlgoSpec)brokerAlgoComboBox.getValue();
                // TODO
            }
            if(brokerComboBox.getValue() != null) {
                BrokerID selectedBroker = brokerComboBox.getValue();
                if(!AUTO_SELECT_BROKER.equals(selectedBroker)) {
                    ActiveFixSession selectedActiveFixSession = availableBrokers.get(selectedBroker);
                    newOrder.setBrokerID(new BrokerID(selectedActiveFixSession.getFixSession().getBrokerId()));
                }
            }
            // TODO custom fields
            //        newOrder.setCustomFields(inCustomFields);
            String maxFloorQtyRaw = StringUtils.trimToNull(maxFloorTextField.textProperty().get());
            if(maxFloorQtyRaw != null) {
                newOrder.setDisplayQuantity(new BigDecimal(maxFloorQtyRaw));
            }
            newOrder.setExecutionDestination(StringUtils.trimToNull(exDestinationTextField.textProperty().get()));
            newOrder.setInstrument(resolvedInstrument);
//          newOrder.setOrderCapacity(inOrderCapacity);
            if(orderTypeComboBox.getValue() != null) {
                newOrder.setOrderType(orderTypeComboBox.getValue());
            }
            if(pegToMidpointCheckBox.selectedProperty().get()) {
                if(!pegToMidpointLockedCheckBox.selectedProperty().get()) {
                    newOrder.setPegToMidpoint(true);
                }
            }
//        newOrder.setPositionEffect(inPositionEffect);
            String rawPrice = StringUtils.trimToNull(priceTextField.textProperty().get());
            if(rawPrice != null) {
                newOrder.setPrice(new BigDecimal(rawPrice));
            }
            String rawQuantity = StringUtils.trimToNull(quantityTextField.textProperty().get());
            if(rawQuantity != null) {
                newOrder.setQuantity(new BigDecimal(rawQuantity));
            }
            if(sideComboBox.getValue() != null) {
                newOrder.setSide(sideComboBox.getValue());
            }
//          newOrder.setText(inText);
            if(timeInForceComboBox.getValue() != null) {
                newOrder.setTimeInForce(timeInForceComboBox.getValue());
            }
            SLF4JLoggerProxy.debug(OrderTicketView.this,
                                   "Validating {}",
                                   newOrder);
            try {
                if(replaceExecutionReportOption.isPresent()) {
                    Validations.validate((OrderReplace)newOrder);
                } else {
                    Validations.validate((OrderSingle)newOrder);
                }
            } catch (OrderValidationException e) {
                String errorMessage = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(OrderTicketView.this,
                                      e,
                                      "{} failed validation: {}",
                                      newOrder,
                                      errorMessage);
                uiMessageService.post(new NotificationEvent("Submit Order Failed",
                                                            "Unable to submit order: " + errorMessage,
                                                            AlertType.ERROR));
                sendButton.requestFocus();
                return;
            }
            SLF4JLoggerProxy.info(OrderTicketView.this,
                                  "{} submitting {}",
                                  SessionUser.getCurrent(),
                                  newOrder);
            SendOrderResponse response = serviceManager.getService(TradeClientService.class).send(newOrder);
            if(response.getFailed()) {
                uiMessageService.post(new NotificationEvent("Submit Order Failed",
                                                            "Unable to submit: " + response.getOrderId() + " " + response.getMessage(),
                                                            AlertType.ERROR));
                sendButton.requestFocus();
                return;
            } else {
                uiMessageService.post(new NotificationEvent("Submit Order Succeeded",
                                                            response.getOrderId() + " submitted",
                                                            AlertType.INFORMATION));
                if(replaceExecutionReportOption.isPresent()) {
                    // close containing ticket
                    // TODO need to trigger a close action
                    getParentWindow().setVisible(false);
                } else {
                    // partially clear ticket
                    resetTicket(false);
                }
            }
        });
        clearButton.setOnAction(event -> resetTicket(true));
        orderTicketLayout.setVgap(5);
        orderTicketLayout.setHgap(5);
        int rowCount = 0;
        int colCount = 0;
        orderTicketLayout.add(brokerLayout,colCount,rowCount);
        orderTicketLayout.add(sideLayout,++colCount,rowCount);
        orderTicketLayout.add(quantityLayout,++colCount,rowCount);
        orderTicketLayout.add(symbolLayout,++colCount,rowCount);
        orderTicketLayout.add(orderTypeLayout,++colCount,rowCount);
        orderTicketLayout.add(priceLayout,++colCount,rowCount);
        orderTicketLayout.add(timeInForceLayout,++colCount,rowCount); colCount = 0;
        orderTicketLayout.add(otherAccordion,colCount,++rowCount);
        orderTicketLayout.add(brokerAlgoAccordion,++colCount,rowCount);
        orderTicketLayout.add(customFieldsAccordion,++colCount,rowCount); colCount = 0;
        orderTicketLayout.add(buttonLayout,colCount,++rowCount);
        adviceSeparator.setId(getClass().getCanonicalName() + ".adviceSeparator");
        adviceLabel.setId(getClass().getCanonicalName() + ".adviceLabel");
        styleService.addStyleToAll(adviceSeparator,
                                   adviceLabel,
                                   rootLayout);
        orderTicketLayout.prefHeightProperty().bind(rootLayout.widthProperty());
        rootLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
        rootLayout.prefWidthProperty().bind(getParentWindow().widthProperty());
        rootLayout.getChildren().addAll(orderTicketLayout,
                                        adviceSeparator,
                                        adviceLabel);
        serviceManager.getService(AdminClientService.class).addClientStatusListener(this);
        if(replaceExecutionReportOption.isPresent()) {
            fillFromExecutionReport(replaceExecutionReportOption.get());
        } else if(suggestionOption.isPresent()) {
            fillFromSuggestion(suggestionOption.get());
        }
    }
    /**
     * Create a new OrderTicketView instance.
     *
     * @param inParent a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public OrderTicketView(Region inParent,
                           NewWindowEvent inEvent,
                           Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientStatusListener#receiveClientStatus(boolean)
     */
    @Override
    public void receiveClientStatus(boolean inIsAvailable)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received client status available: {}",
                               inIsAvailable);
        if(inIsAvailable) {
            initializeBrokerStatusListener();
        } else {
            synchronized(availableBrokers) {
                availableBrokers.clear();
            }
            Platform.runLater(() -> {
                brokerComboBox.getItems().clear();
                brokerComboBox.valueProperty().set(null);
            });
        }
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientDisconnect()
     */
    @Override
    protected void onClientDisconnect()
    {
        synchronized(availableBrokers) {
            availableBrokers.clear();
        }
        Platform.runLater(() -> {
            brokerComboBox.getItems().clear();
            brokerComboBox.valueProperty().set(null);
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onBrokerStatusChange(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    protected void onBrokerStatusChange(ActiveFixSession inActiveFixSession)
    {
        boolean brokersStatusChanged = false;
        BrokerID activeFixSessionKey = new BrokerID(inActiveFixSession.getFixSession().getName());
        synchronized(availableBrokers) {
            if(inActiveFixSession.getStatus().isLoggedOn() && !inActiveFixSession.getFixSession().isAcceptor()) {
                brokersStatusChanged = (availableBrokers.put(activeFixSessionKey,
                                                             inActiveFixSession) == null);
            } else {
                brokersStatusChanged = (availableBrokers.remove(activeFixSessionKey) != null);
            }
            if(brokersStatusChanged) {
                availableBrokers.notifyAll();
            }
        }
        if(brokersStatusChanged) {
            Platform.runLater(new Runnable () {
                @Override
                public void run()
                {
                    BrokerID currentSelectedBroker = brokerComboBox.getValue();
                    brokerComboBox.getItems().clear();
                    brokerComboBox.valueProperty().set(null);
                    brokerComboBox.getItems().add(AUTO_SELECT_BROKER);
                    brokerComboBox.getItems().addAll(availableBrokers.keySet());
                    if(AUTO_SELECT_BROKER.equals(currentSelectedBroker)) {
                        brokerComboBox.setValue(AUTO_SELECT_BROKER);
                    } else {
                        if(availableBrokers.containsKey(currentSelectedBroker)) {
                            brokerComboBox.setValue(currentSelectedBroker);
                        } else {
                            brokerComboBox.setValue(AUTO_SELECT_BROKER);
                        }
                    }
                }}
            );
        }
    }
    /**
     * Format the order ticket from the given suggestion.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    private void fillFromSuggestion(Suggestion inSuggestion)
    {
        if(inSuggestion instanceof OrderSingleSuggestion) {
            OrderSingleSuggestion orderSingleSuggestion = (OrderSingleSuggestion)inSuggestion;
            OrderSingle suggestedOrder = orderSingleSuggestion.getOrder();
            if(suggestedOrder.getSide() != null) {
                sideComboBox.valueProperty().set(suggestedOrder.getSide());
            }
            if(suggestedOrder.getQuantity() != null) {
                quantityTextField.setText(suggestedOrder.getQuantity().toPlainString());
            }
            if(suggestedOrder.getInstrument() != null) {
                symbolTextField.setText(suggestedOrder.getInstrument().getFullSymbol());
            }
            if(suggestedOrder.getOrderType() != null) {
                orderTypeComboBox.setValue(suggestedOrder.getOrderType());
            }
            if(suggestedOrder.getPrice() != null) {
                priceTextField.setText(suggestedOrder.getPrice().toPlainString());
            }
            if(suggestedOrder.getTimeInForce() != null) {
                timeInForceComboBox.setValue(suggestedOrder.getTimeInForce());
            }
            if(suggestedOrder.getText() != null) {
                textTextField.setText(suggestedOrder.getText());
            }
            if(suggestedOrder.getAccount() != null) {
                accountTextField.setText(suggestedOrder.getAccount());
            }
        }
    }
    /**
     * Format the order ticket from the given report.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    private void fillFromExecutionReport(ExecutionReport inExecutionReport)
    {
        if(inExecutionReport.getBrokerId() != null) {
            brokerComboBox.valueProperty().set(inExecutionReport.getBrokerId());
        }
        if(inExecutionReport.getSide() != null) {
            sideComboBox.valueProperty().set(inExecutionReport.getSide());
        }
        if(inExecutionReport.getLeavesQuantity() != null) {
            quantityTextField.setText(inExecutionReport.getLeavesQuantity().toPlainString());
        }
        if(inExecutionReport.getInstrument() != null) {
            symbolTextField.setText(inExecutionReport.getInstrument().getSymbol());
        }
        if(inExecutionReport.getOrderType() != null) {
            orderTypeComboBox.setValue(inExecutionReport.getOrderType());
        }
        if(inExecutionReport.getPrice() != null) {
            priceTextField.setText(inExecutionReport.getPrice().toPlainString());
        }
        if(inExecutionReport.getTimeInForce() != null) {
            timeInForceComboBox.setValue(inExecutionReport.getTimeInForce());
        }
        if(inExecutionReport.getText() != null) {
            textTextField.setText(inExecutionReport.getText());
        }
        if(inExecutionReport.getAccount() != null) {
            accountTextField.setText(inExecutionReport.getAccount());
        }
        if(inExecutionReport.getLastMarket() != null) {
            // TODO this might not be correct
            exDestinationTextField.setText(inExecutionReport.getLastMarket());
        }
    }
    /**
     * Calculate the new order price from the most recent quote events, if possible.
     */
    private void updatePegToMidpointPrice()
    {
        if(pegToMidpointBidEventProperty.get() == null || pegToMidpointAskEventProperty.get() == null) {
            priceTextField.textProperty().set("");
            return;
        }
        BigDecimal askPrice = pegToMidpointAskEventProperty.get().getPrice();
        BigDecimal bidPrice = pegToMidpointBidEventProperty.get().getPrice();
        try {
            BigDecimal midpointPirce = askPrice.add(bidPrice).divide(new BigDecimal(2),
                                                                     PlatformServices.divisionContext);
            priceTextField.textProperty().set(BigDecimalUtil.renderCurrency(midpointPirce));
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Cannot set peg-to-midpoint price, will try again shortly");
        }
    }
    /**
     * Handle an error that occurred while requesting market data for peg-to-midpoint. 
     *
     * @param inMessage a <code>String</code> value
     */
    private void handleMarketDataRequestError(String inMessage)
    {
        uiMessageService.post(new NotificationEvent("Peg-to-Midpoint Market Data Request",
                                                    "Unable to request market data for peg-to-midpoint price: " + inMessage,
                                                    AlertType.ERROR));
        pegToMidpointCheckBox.selectedProperty().set(false);
        priceTextField.textProperty().set("");
        cancelPegToMidpointMarketDataRequest();
    }
    /**
     * Executes the peg-to-midpoint market data request, if appropriate.
     */
    private void doPegToMidpointMarketDataRequest()
    {
        if(!pegToMidpointCheckBox.selectedProperty().get()) {
            return;
        }
        cancelPegToMidpointMarketDataRequest();
        if(symbolTextField.textProperty().get() != null) {
            pegToMidpointMarketDataRequestId = marketDataClient.request(MarketDataRequestBuilder.newRequest().withSymbols(symbolTextField.textProperty().get()).withContent(Content.TOP_OF_BOOK).create(),
                                                                        new MarketDataListener() {
                /* (non-Javadoc)
                 * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
                 */
                @Override
                public void receiveMarketData(Event inEvent)
                {
                    if(inEvent instanceof BidEvent) {
                        pegToMidpointBidEventProperty.set((BidEvent)inEvent);
                    } else if(inEvent instanceof AskEvent) {
                        pegToMidpointAskEventProperty.set((AskEvent)inEvent);
                    } else {
                        handleMarketDataRequestError("Received unexpected peg-to-midpoint event: " + inEvent.getClass());
                    }
                }
                /* (non-Javadoc)
                 * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
                 */
                @Override
                public void onError(Throwable inThrowable)
                {
                    handleMarketDataRequestError(PlatformServices.getMessage(inThrowable));
                }
                /* (non-Javadoc)
                 * @see org.marketcetera.marketdata.MarketDataListener#onError(org.marketcetera.util.log.I18NBoundMessage)
                 */
                @Override
                public void onError(I18NBoundMessage inMessage)
                {
                    handleMarketDataRequestError(inMessage.getText());
                }
                /* (non-Javadoc)
                 * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.String)
                 */
                @Override
                public void onError(String inMessage)
                {
                    handleMarketDataRequestError(inMessage);
                }
            });
        }
    }
    /**
     * Cancels the extant market data request for peg-to-midpoint, if any.
     */
    private void cancelPegToMidpointMarketDataRequest()
    {
        SLF4JLoggerProxy.debug(this,
                               "Canceling market data request: {}",
                               pegToMidpointMarketDataRequestId);
        if(pegToMidpointMarketDataRequestId != null) {
            try {
                marketDataClient.cancel(pegToMidpointMarketDataRequestId);
            } catch (Exception ignored) {
            } finally {
                pegToMidpointMarketDataRequestId = null;
            }
        }
    }
    /**
     * Adjust the send button based on the other fields.
     * 
     * <p>Sets the enabled state of the send button based on the state of the rest of the order ticket.
     */
    private void adjustSendButton()
    {
        // TODO need to check if fields are valid
        boolean enabled = true;
        enabled &= sideComboBox.getValue() != null;
        enabled &= StringUtils.trimToNull(quantityTextField.getText()) != null;
        enabled &= StringUtils.trimToNull(symbolTextField.getText()) != null;
        enabled &= orderTypeComboBox.getValue() != null;
        if(enabled) {
            OrderType orderType = (OrderType)orderTypeComboBox.getValue();
            if(!orderType.isMarketOrder()) {
                enabled &= StringUtils.trimToNull(priceTextField.getText()) != null;
            }
        }
        StringBuilder windowCaption = new StringBuilder();
        if(sideComboBox.getValue() == null) {
            windowCaption.append("Trade");
        } else {
            Side currentSide = (Side)sideComboBox.getValue();
            if(currentSide.isBuy()) {
                windowCaption.append("Buy");
            } else {
                windowCaption.append("Sell");
            }
        }
        if(resolvedInstrument != null) {
            windowCaption.append(" ").append(resolvedInstrument.getFullSymbol());
        }
        // time in force is optional
        // account is optional
        // ex destination is optional
        // max floor is optional, but, if specified, needs to be valid TODO
        // peg-to-midpoint is optional
        // broker algo is optional
        // custom fields are optional
        sendButton.setDisable(!enabled);
    }
    /**
     * Reset the ticket, either completely or partially.
     *
     * @param inCompletelyReset a <code>boolean</code> value
     */
    private void resetTicket(boolean inCompletelyReset)
    {
        SLF4JLoggerProxy.debug(this,
                               "Clearing order ticket");
        // this one is always cleared (to prevent accidentally submitting the same order twice)
        quantityTextField.clear();
        if(inCompletelyReset) {
            brokerComboBox.setValue(AUTO_SELECT_BROKER);
            sideComboBox.setValue(null);
            symbolTextField.clear();
            orderTypeComboBox.setValue(null);
            priceTextField.clear();
            timeInForceComboBox.setValue(null);
            accountTextField.clear();
            exDestinationTextField.clear();
            maxFloorTextField.clear();
            pegToMidpointCheckBox.selectedProperty().set(false);
            pegToMidpointLockedCheckBox.selectedProperty().set(false);
            brokerAlgoComboBox.setValue(null);
            //         brokerAlgoTagGrid.clear();
            //         customFieldsGrid.clear();
            brokerComboBox.requestFocus();
        } else {
            quantityTextField.requestFocus();
        }
    }
    /**
     * Holds custom fields to display.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DisplayCustomField
    {
        /**
         * Create a new DisplayCustomField instance.
         */
        private DisplayCustomField()
        {
            isEnabled = new SimpleBooleanProperty();
            key = new SimpleStringProperty();
            value = new SimpleStringProperty();
        }
        /**
         * Indicates if the field is enabled.
         *
         * @return a <code>BooleanProperty</code> value
         */
        private BooleanProperty isEnabledProperty()
        {
            return isEnabled;
        }
        /**
         * Get the key property value.
         *
         * @return a <code>StringProperty</code> value
         */
        private StringProperty keyProperty()
        {
            return key;
        }
        /**
         * Get the value property value.
         *
         * @return a <code>StringProperty</code> value
         */
        private StringProperty valueProperty()
        {
            return value;
        }
        /**
         * indicates if the custom field is enabled
         */
        private final BooleanProperty isEnabled;
        /**
         * key property
         */
        private final StringProperty key;
        /**
         * value property
         */
        private final StringProperty value;
    }
    /**
     * stores the most recent peg-to-midpoint bid event
     */
    private final ObjectProperty<BidEvent> pegToMidpointBidEventProperty = new SimpleObjectProperty<>();
    /**
     * stores the most recent peg-to-midpoint ask event
     */
    private final ObjectProperty<AskEvent> pegToMidpointAskEventProperty = new SimpleObjectProperty<>();
    /**
     * contains the market data request id for peg-to-midpoint requests, if any
     */
    private String pegToMidpointMarketDataRequestId;
    /**
     * root layout of the view
     */
    private VBox rootLayout;
    /**
     * broker selection layout
     */
    private VBox brokerLayout;
    /**
     * broker label widget
     */
    private Label brokerLabel;
    /**
     * broker selection widget
     */
    private ComboBox<BrokerID> brokerComboBox;
    /**
     * side layout
     */
    private VBox sideLayout;
    /**
     * side label widget
     */
    private Label sideLabel;
    /**
     * side selection widget
     */
    private ComboBox<Side> sideComboBox;
    /**
     * quantity layout
     */
    private VBox quantityLayout;
    /**
     * quantity label widget
     */
    private Label quantityLabel;
    /**
     * quantity entry widget
     */
    private ValidatingTextField quantityTextField;
    /**
     * symbol layout
     */
    private VBox symbolLayout;
    /**
     * symbol layout
     */
    private Label symbolLabel;
    /**
     * symbol entry widget
     */
    private TextField symbolTextField;
    /**
     * order type layout
     */
    private VBox orderTypeLayout;
    /**
     * order type label widget
     */
    private Label orderTypeLabel;
    /**
     * order type selection widget
     */
    private ComboBox<OrderType> orderTypeComboBox;
    /**
     * price layout
     */
    private VBox priceLayout;
    /**
     * price label widget
     */
    private Label priceLabel;
    /**
     * price entry widget
     */
    private ValidatingTextField priceTextField;
    /**
     * time in force layout
     */
    private VBox timeInForceLayout;
    /**
     * time in force label widget
     */
    private Label timeInForceLabel;
    /**
     * time in force selection widget
     */
    private ComboBox<TimeInForce> timeInForceComboBox;
    /**
     * other values accordion widget
     */
    private Accordion otherAccordion;
    /**
     * other values layout
     */
    private TitledPane otherPane;
    /**
     * other layout
     */
    private GridPane otherLayout;
    /**
     * text value label widget
     */
    private Label textLabel;
    /**
     * text entry widget
     */
    private TextField textTextField;
    /**
     * account label widget
     */
    private Label accountLabel;
    /**
     * account entry widget
     */
    private TextField accountTextField;
    /**
     * external destination label widget
     */
    private Label exDestinationLabel;
    /**
     * external destination entry widget
     */
    private TextField exDestinationTextField;
    /**
     * max floor label widget
     */
    private Label maxFloorLabel;
    /**
     * max floor entry widget
     */
    private ValidatingTextField maxFloorTextField;
    /**
     * peg to midpoint label widget
     */
    private Label pegToMidpointLabel;
    /**
     * peg to midpoint selection widget
     */
    private CheckBox pegToMidpointCheckBox;
    /**
     * peg to midpoint locked label widget
     */
    private Label pegToMidpointLockedLabel;
    /**
     * peg to midpoint locked selection widget
     */
    private CheckBox pegToMidpointLockedCheckBox;
    /**
     * broker algo accordion widget
     */
    private Accordion brokerAlgoAccordion;
    /**
     * broker algo layout pane
     */
    private TitledPane brokerAlgoPane;
    /**
     * broker algo layout grid
     */
    private GridPane brokerAlgoLayout;
    /**
     * broker algo label widget
     */
    private Label brokerAlgoLabel;
    /**
     * broker algo selection widget
     */
    private ComboBox<BrokerAlgoSpec> brokerAlgoComboBox;
    /**
     * broker algo tag entry widget
     */
    private TableView<BrokerAlgoTag> brokerAlgoTagTable;
    /**
     * custom fields accordion widget
     */
    private Accordion customFieldsAccordion;
    /**
     * custom fields layout pane
     */
    private TitledPane customFieldsPane;
    /**
     * custom fields entry widget
     */
    private TableView<DisplayCustomField> customFieldsTable;
    /**
     * send button widget
     */
    private Button sendButton;
    /**
     * clear button widget
     */
    private Button clearButton;
    /**
     * button layout
     */
    private HBox buttonLayout;
    /**
     * instrument resolved from symbol property
     */
    private Instrument resolvedInstrument;
    /**
     * advice label widget
     */
    private Label adviceLabel;
    /**
     * advice separator widget
     */
    private Separator adviceSeparator;
    /**
     * contains currently available brokers
     */
    @GuardedBy("availableBrokers")
    private final SortedMap<BrokerID,ActiveFixSession> availableBrokers = Maps.newTreeMap();
    /**
     * token to indicate that the broker should be auto-selected
     */
    private final static BrokerID AUTO_SELECT_BROKER = new BrokerID("Auto Select");
    /**
     * global name of this view
     */
    private static final String NAME = "Order Ticket View";
    /**
     * optional suggestion that might have been included on the new window event
     */
    private Optional<Suggestion> suggestionOption;
    /**
     * optional replace execution report
     */
    private Optional<ExecutionReport> replaceExecutionReportOption;
    /**
     * optional average fill price
     */
    private Optional<AverageFillPrice> averageFillPriceOption;
    /**
     * root container for the scene
     */
    private GridPane orderTicketLayout;
    /**
     * provides access to market data services
     */
    private MarketDataClientService marketDataClient;
    /**
     * provides access to style services
     */
    @Autowired
    private StyleService styleService;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    /**
     * provides access to XML services
     */
    @Autowired
    private XmlService xmlService;
}
