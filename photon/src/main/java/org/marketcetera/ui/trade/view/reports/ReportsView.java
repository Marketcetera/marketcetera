package org.marketcetera.ui.trade.view.reports;

import java.util.Properties;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportType;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.AbstractDeletableFixMessageView;
import org.marketcetera.ui.view.ContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

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
public class ReportsView
        extends AbstractDeletableFixMessageView<DisplayReport,Report>
        implements ContentView
{
    /**
     * Create a new ReportsView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public ReportsView(Region inParentWindow,
                       NewWindowEvent inEvent,
                       Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
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
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#getClientReports(org.marketcetera.persist.PageRequest)
     */
    @Override
    protected CollectionPageResponse<Report> getClientReports(PageRequest inPageRequest)
    {
        return tradeClientService.getReports(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#createFixDisplayObject(java.lang.Object)
     */
    @Override
    protected DisplayReport createFixDisplayObject(Report inClientClazz)
    {
        return new DisplayReport(inClientClazz);
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
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#initializeColumns(javafx.scene.control.TableView)
     */
    @Override
    protected void initializeColumns(TableView<DisplayReport> inTableView)
    {
        super.initializeColumns(inTableView);
        msgTypeColumn = new TableColumn<>("MsgType"); 
        msgTypeColumn.setCellValueFactory(new PropertyValueFactory<>("msgType"));
        inTableView.getColumns().add(2,
                                     msgTypeColumn);
    }
    /**
     * report message type column
     */
    private TableColumn<DisplayReport,ReportType> msgTypeColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Messages View";
}
