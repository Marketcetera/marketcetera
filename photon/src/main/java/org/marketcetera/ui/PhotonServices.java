package org.marketcetera.ui;

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
    public static String successMessage = String.format("-fx-text-fill: GREEN;");
    public static String errorMessage = String.format("-fx-text-fill: RED;");
    public static String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    public static String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
}
