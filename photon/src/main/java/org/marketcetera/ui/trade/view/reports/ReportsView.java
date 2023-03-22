package org.marketcetera.ui.trade.view.reports;

import java.util.Properties;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.trade.view.AbstractFixMessageView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
        extends AbstractFixMessageView<DisplayReport,Report>
        implements ContentView,TradeMessageListener
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
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#initializeContextMenu(javafx.scene.control.TableView)
     */
    @Override
    protected void initializeContextMenu(TableView<DisplayReport> inTableView)
    {
        super.initializeContextMenu(inTableView);
        ContextMenu reportsTableContextMenu = inTableView.getContextMenu();
        SeparatorMenuItem contextMenuSeparator = new SeparatorMenuItem();
        deleteReportMenuItem = new MenuItem("Delete Report");
        deleteReportMenuItem.setOnAction(event -> {
            DisplayReport report = inTableView.getSelectionModel().getSelectedItem();
            Alert alert = PhotonServices.generateAlert("Delete Report " + report.getReportID(),
                                                       "Deleting a report may modify positions, continue?",
                                                       AlertType.CONFIRMATION);
            ButtonType okButton = new ButtonType("Ok",
                                                 ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel",
                                                     ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton,
                                          cancelButton);
            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    try {
                        tradeClientService.deleteReport(report.getReportID());
                        updateReports();
                        webMessageService.post(new NotificationEvent("Delete Report",
                                                                     "Report " + report.getReportID() + " deleted",
                                                                     AlertType.INFORMATION));
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to delete {}",
                                              report);
                        webMessageService.post(new NotificationEvent("Delete Report",
                                                                     "Report " + report.getReportID() + " not deleted: " + PlatformServices.getMessage(e),
                                                                     AlertType.ERROR));
                    }
                } else {
                    return;
                }
            });
        });
        if(authzHelperService.hasPermission(TradePermissions.DeleteReportAction)) {
            reportsTableContextMenu.getItems().addAll(contextMenuSeparator,
                                                      deleteReportMenuItem);
        }
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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#enableContextMenuItems(org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType)
     */
    @Override
    protected void enableContextMenuItems(DisplayReport inNewValue)
    {
        super.enableContextMenuItems(inNewValue);
        if(inNewValue == null) {
            return;
        }
        // enable/disable menu selections based on selected report
        // any report can be deleted, may ${DEITY} have mercy on your soul
        deleteReportMenuItem.setDisable(false);
    }
    /**
     * delete report menu item for the context menu
     */
    private MenuItem deleteReportMenuItem;
    /**
     * report message type column
     */
    private TableColumn<DisplayReport,ReportType> msgTypeColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Messages View";
}
