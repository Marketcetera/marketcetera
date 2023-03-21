package org.marketcetera.ui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.girod.javafx.svgimage.SVGLoader;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.marketcetera.admin.User;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.persist.SummaryNDEntityBase;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.util.Callback;

/* $License$ */

/**
 * Provides services for Photon.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PhotonServices
{
    public static Image getIcon(String inName)
    {
        // TODO maybe cache these?
        return new Image(inName);
    }
    public static Optional<User> getCurrentUser()
    {
        SessionUser currentSessionUser = SessionUser.getCurrent();
        if(currentSessionUser == null) {
            return Optional.empty();
        }
        User currentUser = currentSessionUser.getAttribute(User.class);
        if(currentUser == null) {
            AdminClientService adminClient = ServiceManager.getInstance().getService(AdminClientService.class);
            currentUser = adminClient.getCurrentUser();
            currentSessionUser.setAttribute(User.class,
                                            currentUser);
        }
        return Optional.of(currentUser);
    }
    public static <Clazz> Dialog<Clazz> styleDialog(Dialog<Clazz> inDialog)
    {
        DialogPane dialogPane = inDialog.getDialogPane();
        dialogPane.getStylesheets().clear();
        dialogPane.getStylesheets().add("dark-mode.css");
        return inDialog;
    }
    public static Scene style(Scene inScene)
    {
        inScene.getStylesheets().clear();
        inScene.getStylesheets().add("dark-mode.css");
        return inScene;
    }
    public static Alert generateAlert(String inTitle,
                                      String inContent,
                                      AlertType inAlertType)
    {
        Alert alert = new Alert(inAlertType);
        alert.setTitle(inTitle);
        alert.setContentText(inContent);
        styleDialog(alert);
        return alert;
    }
    public static boolean isSocketAlive(String inHostname,
                                        int inPort)
    {
        boolean isAlive = false;
        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(inHostname,
                                                            inPort);
        Socket socket = new Socket();
        // Timeout required - it's in milliseconds
        int timeout = 2000;
        try {
            socket.connect(socketAddress,
                           timeout);
            socket.close();
            isAlive = true;
        } catch (SocketTimeoutException e) {
            SLF4JLoggerProxy.warn(PhotonServices.class,
                                  "SocketTimeoutException {}:{} -> " + e.getMessage(),
                                  inHostname,
                                  inPort,
                                  ExceptionUtils.getCause(e));
        } catch (IOException e) {
            SLF4JLoggerProxy.warn(PhotonServices.class,
                                  "IOException {}:{} -> " + e.getMessage(),
                                  inHostname,
                                  inPort,
                                  ExceptionUtils.getCause(e));
        }
        return isAlive;
    }
    public static boolean isValidHostNameSyntax(String candidateHost)
    {
        if (candidateHost.contains("/")) {
            return false;
        }
        try {
            // WORKAROUND: add any scheme and port to make the resulting URI valid
            return new URI("my://userinfo@" + candidateHost + ":80").getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
    /**
     * Load an SVG image into a node.
     *
     * @param inUrl a <code>URL</code> value
     * @return a <code>Node</code> value
     */
    public static Node getSvgResource(URL inUrl)
    {
        Group svgImage = SVGLoader.load(inUrl);
        svgImage.setScaleX(0.5);
        svgImage.setScaleY(0.5);
        return svgImage;
    }
    public static class NDEntityCellFactory<Clazz extends SummaryNDEntityBase>
            implements Callback<ListView<Clazz>, ListCell<Clazz>>
    {
        @Override
        public ListCell<Clazz> call(ListView<Clazz> inParameter)
        {
            return new ListCell<>(){
                @Override
                public void updateItem(Clazz inData,
                                       boolean isEmpty)
                {
                    super.updateItem(inData, isEmpty);
                    if (isEmpty || inData == null) {
                        setText(null);
                    } else {
                        setText(inData.getName());
                    }
                }
            };
        }
    }
    public static <T> TableCell<T,BigDecimal> renderNumberCell(TableColumn<T,BigDecimal> inTableColumn)
    {
        TableCell<T,BigDecimal> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty && inItem != null){
                    this.setText(BigDecimalUtil.render(inItem));
                }
            }
        };
        return tableCell;
    }
    public static <T> TableCell<T,BigDecimal> renderCurrencyCell(TableColumn<T,BigDecimal> inTableColumn)
    {
        TableCell<T,BigDecimal> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty && inItem != null){
                    // TODO need to set up decimal preferences
                    this.setText(BigDecimalUtil.renderCurrency(inItem));
                }
            }
        };
        return tableCell;
    }
    public static <T> TableCell<T,Date> renderDateCell(TableColumn<T,Date> inTableColumn)
    {
        TableCell<T,Date> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Date inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty){
                    this.setText(isoDateFormatter.print(new DateTime(inItem)));
                }
            }
        };
        return tableCell;
    }
    public static <T> TableCell<T,DateTime> renderDateTimeCell(TableColumn<T,DateTime> inTableColumn)
    {
        TableCell<T,DateTime> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(DateTime inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty){
                    this.setText(isoDateFormatter.print(inItem));
                }
            }
        };
        return tableCell;
    }
    public static <T> TableCell<T,Period> renderPeriodCell(TableColumn<T,Period> inTableColumn)
    {
        TableCell<T,Period> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Period inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty){
                    this.setText(TimeFactoryImpl.periodFormatter.print(inItem));
                }
            }
        };
        return tableCell;
    }
    public static final DateTimeFormatter isoDateFormatter = TimeFactoryImpl.FULL_MILLISECONDS;
    public static String successMessage = String.format("-fx-text-fill: GREEN;");
    public static String errorMessage = String.format("-fx-text-fill: RED;");
    public static String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    public static String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
}
