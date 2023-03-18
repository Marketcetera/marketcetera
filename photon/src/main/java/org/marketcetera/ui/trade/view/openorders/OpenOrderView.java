package org.marketcetera.ui.trade.view.openorders;

import java.util.Properties;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.AbstractFixMessageView;
import org.marketcetera.ui.view.ContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


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
     * @param inParentWindow a <code>Node</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public OpenOrderView(Node inParentWindow,
                         NewWindowEvent inEvent,
                         Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
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
     * root order id table column
     */
    private TableColumn<DisplayOrderSummary,OrderID> rootOrderIdColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "Open Orders View";
}
