package org.marketcetera.ui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;

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
    public static String successMessage = String.format("-fx-text-fill: GREEN;");
    public static String errorMessage = String.format("-fx-text-fill: RED;");
    public static String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    public static String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
}
