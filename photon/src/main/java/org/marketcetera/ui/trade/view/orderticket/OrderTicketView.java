package org.marketcetera.ui.trade.view.orderticket;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedMap;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.client.Validations;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.HasSuggestion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.client.OrderValidationException;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.StyleService;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.ui.service.trade.TradeClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.ValidatingTextField;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
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
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose(javafx.stage.WindowEvent)
     */
    @Override
    public void onClose(WindowEvent inEvent)
    {
        try {
            serviceManager.getService(AdminClientService.class).removeBrokerStatusListener(this);
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
        rootLayout = new VBox();
        orderTicketLayout = new GridPane();
        scene = new Scene(rootLayout);
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
        orderTicketLabel = new Label("New Order Ticket");
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
        sendClearLayout = new HBox(5);
        adviceLabel = new Label("");
        adviceSeparator = new Separator(Orientation.HORIZONTAL);
        // find saved data, if any
        String xmlData = StringUtils.trimToNull(getViewProperties().getProperty(ExecutionReport.class.getCanonicalName()));
        ExecutionReport replaceExecutionReport = null;
        if(xmlData != null) {
            try {
                replaceExecutionReport = xmlService.unmarshall(xmlData);
            } catch (JAXBException e) {
                webMessageService.post(new NotificationEvent("Replace Order",
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
                webMessageService.post(new NotificationEvent("Trade Order",
                                                             "Unable to trade order: " + PlatformServices.getMessage(e),
                                                             AlertType.ERROR));
            }
        }
        String orderTicketLabelHeader = replaceExecutionReportOption.isPresent() ? "Replace " : "New ";
        orderTicketLabel.textProperty().set(orderTicketLabelHeader + "Order Ticket");
        averageFillPriceOption = Optional.ofNullable(averageFillPrice);
        Suggestion suggestion = null;
        if(getNewWindowEvent() instanceof HasSuggestion) {
            suggestion = ((HasSuggestion)getNewWindowEvent()).getSuggestion();
        }
        suggestionOption = Optional.ofNullable(suggestion);
        orderTicketLabel.setFont(new Font(32));
        styleService.addStyle(orderTicketLabel);
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
        serviceManager.getService(AdminClientService.class).addBrokerStatusListener(this);
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
                String symbol = StringUtils.trimToNull(String.valueOf(inNewValue));
                if(symbol == null) {
                    orderTicketLabel.textProperty().set(orderTicketLabelHeader + "Order Ticket");
                    return;
                }
                resolvedInstrument = serviceManager.getService(TradeClientService.class).resolveSymbol(symbol);
                if(resolvedInstrument == null) {
                    orderTicketLabel.textProperty().set(orderTicketLabelHeader + "Order Ticket");
                    symbolTextField.setStyle(PhotonServices.errorStyle);
                    adviceLabel.textProperty().set("Cannot resolve symbol");
                    return;
                }
                symbolTextField.setStyle(PhotonServices.successStyle);
                adviceLabel.textProperty().set("");
                symbolTextField.setTooltip(new Tooltip(resolvedInstrument.toString()));
                orderTicketLabel.textProperty().set(orderTicketLabelHeader + resolvedInstrument.getSecurityType().name() + " Order Ticket");
                // TODO if peg-to-midpoint is checked, we need to change the market data subscription
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
                    priceTextField.setText("");
                    pegToMidpointCheckBox.selectedProperty().set(false);
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
        pegToMidpointCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            boolean value = newValue;
            pegToMidpointLockedCheckBox.setDisable(!value);
            // enable/disable price if the peg-to-midpoint is selected
            priceTextField.setDisable(value);
            if(!value) {
                pegToMidpointLockedCheckBox.selectedProperty().set(false);
            }
            // TODO need to subscribe to market data (if we have a symbol)
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
        TableColumn<CustomFieldHolder,Boolean> customFieldsEnabledColumn = new TableColumn<>("Enabled");
        customFieldsEnabledColumn.setCellValueFactory( cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().isEnabledProperty().get()));
        customFieldsEnabledColumn.setCellFactory(CheckBoxTableCell.<CustomFieldHolder>forTableColumn(customFieldsEnabledColumn));
        customFieldsEnabledColumn.setId(getClass().getCanonicalName() + ".customFieldsEnabledColumn");
        customFieldsEnabledColumn.setEditable(true);
        TableColumn<CustomFieldHolder,String> customFieldsKeyColumn = new TableColumn<>("Key");
        customFieldsKeyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        customFieldsKeyColumn.setCellFactory(TextFieldTableCell.<CustomFieldHolder>forTableColumn());
        customFieldsKeyColumn.setId(getClass().getCanonicalName() + ".customFieldsKeyColumn");
        customFieldsKeyColumn.setEditable(true);
        TableColumn<CustomFieldHolder,String> customFieldsValueColumn = new TableColumn<>("Value");
        customFieldsValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        customFieldsValueColumn.setCellFactory(TextFieldTableCell.<CustomFieldHolder>forTableColumn());
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
                    customFieldsTable.getItems().add(new CustomFieldHolder());
                }
            }}
        );
        styleService.addStyleToAll(customFieldsAccordion,
                                   customFieldsPane,
                                   customFieldsTable);
        // send button
        KeyCombination sendKeyCombination = new KeyCodeCombination(KeyCode.ENTER);
        Mnemonic sendMnemonic = new Mnemonic(sendButton,
                                             sendKeyCombination);
        scene.addMnemonic(sendMnemonic);
        sendClearLayout.getChildren().addAll(sendButton,
                                             clearButton);
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
                // TODO
//                Notification.show("Unable to submit: " + errorMessage,
//                                  Type.ERROR_MESSAGE);
                sendButton.requestFocus();
                return;
            }
            SLF4JLoggerProxy.info(OrderTicketView.this,
                                  "{} submitting {}",
                                  SessionUser.getCurrent(),
                                  newOrder);
            SendOrderResponse response = serviceManager.getService(TradeClientService.class).send(newOrder);
            if(response.getFailed()) {
//                Notification.show("Unable to submit: " + response.getOrderId() + " " + response.getMessage(),
//                                  Type.ERROR_MESSAGE);
                sendButton.requestFocus();
                return;
            } else {
//                Notification.show(response.getOrderId() + " submitted",
//                                  Type.TRAY_NOTIFICATION);
                if(replaceExecutionReportOption.isPresent()) {
                    // close containing ticket
                    getParentWindow().close();
                } else {
                    // partially clear ticket
                    resetTicket(false);
                }
            }
        });
        orderTicketLayout.setVgap(5);
        orderTicketLayout.setHgap(5);
        orderTicketLayout.add(orderTicketLabel,0,0,7,1);
        orderTicketLayout.add(brokerLayout,0,1);
        orderTicketLayout.add(sideLayout,1,1);
        orderTicketLayout.add(quantityLayout,2,1);
        orderTicketLayout.add(symbolLayout,3,1);
        orderTicketLayout.add(orderTypeLayout,4,1);
        orderTicketLayout.add(priceLayout,5,1);
        orderTicketLayout.add(timeInForceLayout,6,1);
        orderTicketLayout.add(otherAccordion,0,2);
        orderTicketLayout.add(brokerAlgoAccordion,1,2);
        orderTicketLayout.add(customFieldsAccordion,2,2);
        orderTicketLayout.add(sendClearLayout,0,3);
        adviceSeparator.setId(getClass().getCanonicalName() + ".adviceSeparator");
        adviceLabel.setId(getClass().getCanonicalName() + ".adviceLabel");
        styleService.addStyleToAll(adviceSeparator,
                                   adviceLabel,
                                   rootLayout);
        rootLayout.getChildren().addAll(orderTicketLayout,
                                        adviceSeparator,
                                        adviceLabel);
    }
    /**
     * Create a new OrderTicketView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public OrderTicketView(Stage inParent,
                           NewWindowEvent inEvent,
                           Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
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
    public static class CustomFieldHolder
    {
        private CustomFieldHolder()
        {
            isEnabled = new SimpleBooleanProperty();
            key = new SimpleStringProperty();
            value = new SimpleStringProperty();
        }
        public BooleanProperty isEnabledProperty()
        {
            return isEnabled;
        }
        public StringProperty keyProperty()
        {
            return key;
        }
        public StringProperty valueProperty()
        {
            return value;
        }
        private final BooleanProperty isEnabled;
        private final StringProperty key;
        private final StringProperty value;
        
    }
    private VBox rootLayout;
    private Label orderTicketLabel;
    private VBox brokerLayout;
    private Label brokerLabel;
    private ComboBox<BrokerID> brokerComboBox;
    private VBox sideLayout;
    private Label sideLabel;
    private ComboBox<Side> sideComboBox;
    private VBox quantityLayout;
    private Label quantityLabel;
    private ValidatingTextField quantityTextField;
    private VBox symbolLayout;
    private Label symbolLabel;
    private TextField symbolTextField;
    private VBox orderTypeLayout;
    private Label orderTypeLabel;
    private ComboBox<OrderType> orderTypeComboBox;
    private VBox priceLayout;
    private Label priceLabel;
    private ValidatingTextField priceTextField;
    private VBox timeInForceLayout;
    private Label timeInForceLabel;
    private ComboBox<TimeInForce> timeInForceComboBox;
    private Accordion otherAccordion;
    private TitledPane otherPane;
    private GridPane otherLayout;
    private Label textLabel;
    private TextField textTextField;
    private Label accountLabel;
    private TextField accountTextField;
    private Label exDestinationLabel;
    private TextField exDestinationTextField;
    private Label maxFloorLabel;
    private ValidatingTextField maxFloorTextField;
    private Label pegToMidpointLabel;
    private CheckBox pegToMidpointCheckBox;
    private Label pegToMidpointLockedLabel;
    private CheckBox pegToMidpointLockedCheckBox;
    private Accordion brokerAlgoAccordion;
    private TitledPane brokerAlgoPane;
    private GridPane brokerAlgoLayout;
    private Label brokerAlgoLabel;
    private ComboBox<BrokerAlgoSpec> brokerAlgoComboBox;
    private TableView<BrokerAlgoTag> brokerAlgoTagTable;
    private Accordion customFieldsAccordion;
    private TitledPane customFieldsPane;
    private TableView<CustomFieldHolder> customFieldsTable;
    private Button sendButton;
    private Button clearButton;
    private HBox sendClearLayout;
    private Instrument resolvedInstrument;
    private Label adviceLabel;
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
     * main scene object
     */
    private Scene scene;
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
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
}
