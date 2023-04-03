package org.marketcetera.ui.trade.view;

import java.util.Collection;
import java.util.Properties;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;

/* $License$ */

/**
 * Provides common behaviors for Fix message views that need to be able to delete reports;
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractDeletableFixMessageView<FixClazz extends DeletableFixMessageDisplayType,ClientClazz>
        extends AbstractFixMessageView<FixClazz,ClientClazz>
{
    /**
     * Create a new AbstractDeletableFixMessageView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractDeletableFixMessageView(Region inParentWindow,
                                              NewWindowEvent inEvent,
                                              Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#initializeContextMenu(javafx.scene.control.TableView)
     */
    @Override
    protected void initializeContextMenu(TableView<FixClazz> inTableView)
    {
        super.initializeContextMenu(inTableView);
        ContextMenu reportsTableContextMenu = inTableView.getContextMenu();
        SeparatorMenuItem contextMenuSeparator = new SeparatorMenuItem();
        deleteReportMenuItem = new MenuItem("Delete Report");
        deleteReportMenuItem.setOnAction(event -> {
            FixClazz report = inTableView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Report " + report.getReportID());
            alert.setContentText("Deleting a report may modify positions, continue?");
            ButtonType okButton = new ButtonType("Ok",
                                                 ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel",
                                                     ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton,
                                          cancelButton);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add("dark-mode.css");
            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    try {
                        tradeClientService.deleteReport(report.getReportID());
                        updateReports();
                        uiMessageService.post(new NotificationEvent("Delete Report",
                                                                     "Report " + report.getReportID() + " deleted",
                                                                     AlertType.INFORMATION));
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to delete {}",
                                              report);
                        uiMessageService.post(new NotificationEvent("Delete Report",
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
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#enableContextMenuItems(java.util.Collection)
     */
    @Override
    protected void enableContextMenuItems(Collection<FixClazz> inSelectedItems)
    {
        super.enableContextMenuItems(inSelectedItems);
        if(inSelectedItems == null || inSelectedItems.isEmpty()) {
            deleteReportMenuItem.setDisable(true);
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
}
