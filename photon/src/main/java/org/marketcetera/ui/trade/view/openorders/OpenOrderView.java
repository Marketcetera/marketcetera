package org.marketcetera.ui.trade.view.openorders;

import java.util.Properties;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.AbstractFixMessageView;
import org.marketcetera.ui.view.ContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;


/* $License$ */

/**
 * Provides a view for open orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OpenOrderView
        extends AbstractFixMessageView<DisplayOrderSummary,OrderSummary>
        implements ContentView
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new OpenOrderView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public OpenOrderView(Region inParentWindow,
                         NewWindowEvent inEvent,
                         Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#onStart()
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        cancelOpenOrdersButton = new Button();
        cancelOpenOrdersButton.setTooltip(new Tooltip("Cancel all open orders"));
        cancelOpenOrdersButton.setGraphic(new ImageView(PhotonServices.getIcon("images/erase.png")));
        cancelOpenOrdersButton.setPadding(new Insets(0,30,0,00));
        cancelOpenOrdersButton.setOnAction(event -> cancelOpenOrders());
        getAboveTableLayout().setAlignment(Pos.BASELINE_RIGHT);
        getAboveTableLayout().getChildren().add(cancelOpenOrdersButton);
        getAboveTableLayout().prefWidthProperty().bind(getMainLayout().widthProperty());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#getTableSelectionMode()
     */
    @Override
    protected SelectionMode getTableSelectionMode()
    {
        return SelectionMode.MULTIPLE;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#getClientReports(org.marketcetera.persist.PageRequest)
     */
    @Override
    protected CollectionPageResponse<OrderSummary> getClientReports(PageRequest inPageRequest)
    {
        return tradeClientService.getOpenOrders(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#createFixDisplayObject(java.lang.Object)
     */
    @Override
    protected DisplayOrderSummary createFixDisplayObject(OrderSummary inClientClazz)
    {
        return new DisplayOrderSummary(inClientClazz);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#getPlaceholder()
     */
    @Override
    protected Node getPlaceholder()
    {
        return new Label("no open orders");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#initializeColumns(javafx.scene.control.TableView)
     */
    @Override
    protected void initializeColumns(TableView<DisplayOrderSummary> inTableView)
    {
        super.initializeColumns(inTableView);
        rootOrderIdColumn = new TableColumn<>("RootOrdId"); 
        rootOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("rootOrderId"));
        inTableView.getColumns().add(2,
                                     rootOrderIdColumn);
    }
    /**
     * Cancel all open orders displayed in the FIX table.
     */
    private void cancelOpenOrders()
    {
        for(DisplayOrderSummary orderSummary : reportsTableView.getItems()) {
            cancelOrder(orderSummary);
        }
    }
    /**
     * cancel open orders button
     */
    private Button cancelOpenOrdersButton;
    /**
     * root order id table column
     */
    private TableColumn<DisplayOrderSummary,OrderID> rootOrderIdColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "Open Orders View";
}
