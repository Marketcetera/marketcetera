package org.marketcetera.ui.admin.view;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.impl.SimpleUser;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/* $License$ */

/**
 * Provides a view for users.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserView
        extends AbstractContentView
{
    /**
     * Create a new UserView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public UserView(Region inParentWindow,
                    NewWindowEvent inEvent,
                    Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getMainLayout()
     */
    @Override
    public Region getMainLayout()
    {
        return mainLayout;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        mainLayout = new VBox(5);
        initializeTable();
        buttonLayout = new HBox(5);
        addUserButton = new Button("Add User");
        boolean userHasCreateUserPermission = authzHelperService.hasPermission(AdminPermissions.CreateUserAction);
        addUserButton.setVisible(userHasCreateUserPermission);
        addUserButton.setDisable(!userHasCreateUserPermission);
        addUserButton.setOnAction(event -> doAddOrUpdateUser(new SimpleUser(),true));
        buttonLayout.getChildren().add(addUserButton);
        mainLayout.getChildren().addAll(usersTable,
                                        buttonLayout);
    }
    /**
     * Update the users displayed in the table.
     */
    private void updateUsers()
    {
        usersTable.getItems().clear();
        adminClientService.getUsers().forEach(user -> {
            if(user instanceof SimpleUser) {
                usersTable.getItems().add((SimpleUser)user);
            } else {
                usersTable.getItems().add(new SimpleUser(user.getName(),
                                                         user.getDescription(),
                                                         "",
                                                         user.isActive()));
            }
        });
    }
    /**
     * Initialize the table and fill it with data.
     */
    private void initializeTable()
    {
        usersTable = new TableView<>();
        usersTable.setPlaceholder(new Label("no users to display"));
        initializeColumns();
        initializeContextMenu();
        usersTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<SimpleUser>) (inObservable,inOldValue,inNewValue) -> {
            enableContextMenuItems(inNewValue);
        });
        updateUsers();
    }
    /**
     * Enables or disables the table context menu items based on the selected item.
     *
     * @param inNewValue a <code>SimpleUser</code> value
     */
    private void enableContextMenuItems(SimpleUser inNewValue)
    {
        if(inNewValue == null) {
            return;
        }
        if(inNewValue.isActive()) {
            activateUserMenuItem.setDisable(true);
            deactivateUserMenuItem.setDisable(false);
            deleteUserMenuItem.setDisable(true);
        } else {
            activateUserMenuItem.setDisable(false);
            deactivateUserMenuItem.setDisable(true);
            deleteUserMenuItem.setDisable(false);
        }
    }
    /**
     * Execute a context menu action.
     *
     * @param inSelectedItem a <code>SimpleUser</code> value
     * @param inTitle a <code>String</code> value for the message dialogs
     * @param inContent a <code>String</code> value for the message dialogs
     * @param inAction a <code>Consumer&lt;String&gt;</code> value to conduct the action
     */
    private void doContextMenuAction(SimpleUser inSelectedItem,
                                     String inTitle,
                                     String inContent,
                                     Consumer<String> inAction)
    {
        if(inSelectedItem == null) {
            return;
        }
        Alert alert = PhotonServices.generateAlert(inTitle,
                                                   inContent + "?",
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
                    SLF4JLoggerProxy.info(UserView.this,
                                          "{} performing {} on {}",
                                          SessionUser.getCurrent(),
                                          inTitle,
                                          inSelectedItem);
                    inAction.accept(inSelectedItem.getName());
                    SLF4JLoggerProxy.info(UserView.this,
                                          "{} on {} succeeded",
                                          inTitle,
                                          inSelectedItem);
                    updateUsers();
                    uiMessageService.post(new NotificationEvent(inTitle,
                                                                 inContent + " succeeded",
                                                                 AlertType.INFORMATION));
                } catch (Exception e) {
                    String message = PlatformServices.getMessage(e);
                    SLF4JLoggerProxy.warn(UserView.this,
                                          e,
                                          "Unable to perform {} on {}: {}",
                                          inTitle,
                                          inSelectedItem,
                                          message);
                    uiMessageService.post(new NotificationEvent(inTitle,
                                                                 inContent + " failed: " + message,
                                                                 AlertType.ERROR));
                }
            } else {
                return;
            }
        });
    }
    /**
     * Initialize the context menu of the table.
     */
    private void initializeContextMenu()
    {
        usersTableContextMenu = new ContextMenu();
        deleteUserMenuItem = new MenuItem("Delete");
        deleteUserMenuItem.setOnAction(event -> {
            SimpleUser selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if(selectedUser == null) {
                return;
            }
            if(selectedUser.getName().equals(SessionUser.getCurrent().getUsername())) {
                SLF4JLoggerProxy.warn(UserView.this,
                                      "Cannot delete current user {}",
                                      selectedUser);
                uiMessageService.post(new NotificationEvent("Delete User",
                                                             "Cannot delete current user",
                                                             AlertType.ERROR));
                return;
            }
            doContextMenuAction(selectedUser,
                                "Delete User",
                                "Delete " + selectedUser.getName(),
                                inUsername -> adminClientService.deleteUser(inUsername));
        });
        deactivateUserMenuItem = new MenuItem("Deactivate");
        deactivateUserMenuItem.setOnAction(event -> {
            SimpleUser selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if(selectedUser == null) {
                return;
            }
            if(selectedUser.getName().equals(SessionUser.getCurrent().getUsername())) {
                SLF4JLoggerProxy.warn(UserView.this,
                                      "Cannot deactivate current user {}",
                                      selectedUser);
                uiMessageService.post(new NotificationEvent("Deactivate User",
                                                             "Cannot deactivate current user",
                                                             AlertType.ERROR));
                return;
            }
            doContextMenuAction(selectedUser,
                                "Deactivate User",
                                "Deactivate " + selectedUser.getName(),
                                inUsername -> adminClientService.deactivateUser(inUsername));
        });
        // TODO no activateUser client action
        activateUserMenuItem = new MenuItem("Activate");
//        activateUserMenuItem.setOnAction(event -> {
//            SimpleUser selectedUser = userTable.getSelectionModel().getSelectedItem();
//            if(selectedUser == null) {
//                return;
//            }
//            doContextMenuAction(selectedUser,
//                                "Activate User",
//                                "Activate " + selectedUser.getName(),
//                                inUsername -> adminClientService.activateUser(inUsername));
//        });
        updateUserMenuItem = new MenuItem("Edit");
        updateUserMenuItem.setOnAction(event -> {
            SimpleUser selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if(selectedUser == null) {
                return;
            }
            doAddOrUpdateUser(selectedUser,
                              false);
        });
        resetPasswordMenuItem = new MenuItem("Reset Password");
        resetPasswordMenuItem.setOnAction(event -> {
            SimpleUser selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if(selectedUser == null) {
                return;
            }
            doResetPassword(selectedUser);
        });
        boolean atLeastOneGroupOneMenuItem = false;
        boolean separatorOneAdded = false;
        if(authzHelperService.hasPermission(AdminPermissions.UpdateUserAction)) {
            usersTableContextMenu.getItems().add(updateUserMenuItem);
            atLeastOneGroupOneMenuItem = true;
        }
        if(authzHelperService.hasPermission(AdminPermissions.ResetUserPasswordAction)) {
            usersTableContextMenu.getItems().add(resetPasswordMenuItem);
            atLeastOneGroupOneMenuItem = true;
        }
        if(authzHelperService.hasPermission(AdminPermissions.DeleteUserAction)) {
            if(atLeastOneGroupOneMenuItem && !separatorOneAdded) {
                usersTableContextMenu.getItems().add(new SeparatorMenuItem());
                separatorOneAdded = true;
            }
            usersTableContextMenu.getItems().add(deleteUserMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.DeleteUserAction)) { // TODO should be DeactivateUserAction
            if(atLeastOneGroupOneMenuItem && !separatorOneAdded) {
                usersTableContextMenu.getItems().add(new SeparatorMenuItem());
                separatorOneAdded = true;
            }
            usersTableContextMenu.getItems().add(deactivateUserMenuItem);
        }
//        if(authzHelperService.hasPermission(AdminPermissions.ActivateUserAction)) { // TODO should be ActivateUserAction
//        if(atLeastOneGroupOneMenuItem && !separatorOneAdded) {
//            usersTableContextMenu.getItems().add(new SeparatorMenuItem());
//            separatorOneAdded = true;
//        }
//            usersTableContextMenu.getItems().add(activateUserMenuItem);
//        }
        usersTable.setContextMenu(usersTableContextMenu);
    }
    /**
     * Reset the password of the given user.
     *
     * @param inSelectedUser a <code>SimpleUser</code> value
     */
    private void doResetPassword(SimpleUser inSelectedUser)
    {
        Dialog<String> resetPasswordDialog = new Dialog<>();
        resetPasswordDialog.setTitle("Reset Password");
        GridPane passwordDialogGrid = new GridPane();
        passwordDialogGrid.setHgap(10);
        passwordDialogGrid.setVgap(10);
        passwordDialogGrid.setPadding(new Insets(20,150,10,10));
        PasswordField password1Field = new PasswordField();
        PasswordField password2Field = new PasswordField();
        Label adviceLabel = new Label();
        ButtonType okButtonType = new ButtonType("OK",
                                                 ButtonData.OK_DONE);
        resetPasswordDialog.getDialogPane().getButtonTypes().addAll(okButtonType,
                                                                    ButtonType.CANCEL);
        password2Field.textProperty().addListener((observable,oldValue,newValue) -> {
            String password1Value = StringUtils.trimToNull(password1Field.getText());
            String password2Value = StringUtils.trimToNull(password2Field.getText());
            if(password1Value == null) {
                adviceLabel.setText("Password required");
                password1Field.setStyle(PhotonServices.errorStyle);
                adviceLabel.setStyle(PhotonServices.errorMessage);
                resetPasswordDialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
            } else if(password1Value.equals(password2Value)) {
                adviceLabel.setText("");
                adviceLabel.setStyle(PhotonServices.successMessage);
                password1Field.setStyle(PhotonServices.successStyle);
                password2Field.setStyle(PhotonServices.successStyle);
                resetPasswordDialog.getDialogPane().lookupButton(okButtonType).setDisable(false);
            } else {
                adviceLabel.setText("Passwords do not match");
                password1Field.setStyle(PhotonServices.errorStyle);
                password2Field.setStyle(PhotonServices.errorStyle);
                adviceLabel.setStyle(PhotonServices.errorMessage);
                resetPasswordDialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
            }
        });
        passwordDialogGrid.add(new Label("Reset " + inSelectedUser.getName() + " Password"),0,0,2,1);
        passwordDialogGrid.add(new Label("New Password"),0,1);
        passwordDialogGrid.add(password1Field,1,1);
        passwordDialogGrid.add(new Label("Verify Password"),0,2);
        passwordDialogGrid.add(password2Field,1,2);
        passwordDialogGrid.add(adviceLabel,0,3,2,1);
        resetPasswordDialog.getDialogPane().setContent(passwordDialogGrid);
        resetPasswordDialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
        resetPasswordDialog.setResultConverter(dialogButton -> {
            if(dialogButton == okButtonType) {
                return password1Field.getText();
            }
            return null;
        });
        PhotonServices.style(resetPasswordDialog.getDialogPane().getScene());
        Optional<String> newPasswordOption = resetPasswordDialog.showAndWait();
        if(newPasswordOption.isPresent()) {
            try {
                adminClientService.resetUserPassword(inSelectedUser.getName(),
                                                     newPasswordOption.get());
                uiMessageService.post(new NotificationEvent("Reset Password",
                                                             "Password for '" + inSelectedUser.getName() + "' reset",
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                String message = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(UserView.this,
                                      e,
                                      "Unable to reset password for {}: {}",
                                      inSelectedUser,
                                      message);
                uiMessageService.post(new NotificationEvent("Reset Password",
                                                             "Reset Password for '" + inSelectedUser.getName() + "' failed: " + message,
                                                             AlertType.ERROR));
            }
        }
    }
    /**
     * Execute an add user or update user action based on the given parameters.
     *
     * @param inSelectedUser a <code>SimpleUser</code> value
     * @param inIsAdd a <code>boolean</code> value
     */
    private void doAddOrUpdateUser(SimpleUser inSelectedUser,
                                   boolean inIsAdd)
    {
        Dialog<SimpleUser> userDialog = new Dialog<>();
        GridPane userDialogGrid = new GridPane();
        userDialogGrid.setHgap(10);
        userDialogGrid.setVgap(10);
        userDialogGrid.setPadding(new Insets(20,150,10,10));
        TextField usernameField = new TextField();
        TextField descriptionField = new TextField();
        PasswordField password1Field = new PasswordField();
        PasswordField password2Field = new PasswordField();
        CheckBox isActiveCheckBox = new CheckBox();
        isActiveCheckBox.setIndeterminate(false);
        Label adviceLabel = new Label();
        ButtonType okButtonType = new ButtonType("OK",
                                                 ButtonData.OK_DONE);
        userDialog.getDialogPane().getButtonTypes().addAll(okButtonType,
                                                           ButtonType.CANCEL);
        final BooleanProperty disableOkButton = new SimpleBooleanProperty(false);
        Function<Void,Boolean> disableOkFunction = new Function<>() {
            @Override
            public Boolean apply(Void inIgnored)
            {
                adviceLabel.setText("");
                adviceLabel.setStyle(PhotonServices.successMessage);
                usernameField.setStyle(PhotonServices.successStyle);
                password1Field.setStyle(PhotonServices.successStyle);
                password2Field.setStyle(PhotonServices.successStyle);
                // check username
                String computedValue = StringUtils.trimToNull(usernameField.getText());
                if(computedValue == null) {
                    adviceLabel.setText("Name required");
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                    usernameField.setStyle(PhotonServices.errorStyle);
                    return true;
                }
                if(computedValue.length() > 255) {
                    adviceLabel.setText("Name may contain up to 255 characters");
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                    usernameField.setStyle(PhotonServices.errorStyle);
                    return true;
                }
                // TODO are user names required to conform to the NDEntity pattern?
                // check password, but only for new users
                if(inIsAdd) {
                    String password1Value = StringUtils.trimToNull(password1Field.getText());
                    String password2Value = StringUtils.trimToNull(password2Field.getText());
                    // we want to make sure the passwords match, but we don't want to be obnoxious about it
                    if(password1Value == null) {
                        if(password2Value == null) {
                            // we can assume they haven't started typing yet, though this isn't strictly true,
                            //  so, disable the OK button, but don't mark anything up
                            return true;
                        }
                        // p1 is null and p2 isn't null
                        adviceLabel.setText("Password required");
                        password1Field.setStyle(PhotonServices.errorStyle);
                        adviceLabel.setStyle(PhotonServices.errorMessage);
                        return true;
                    } else {
                        // p1 is non-null
                        // if p2 is null, they probably haven't started typing yet, so don't mark anything up
                        if(password2Value == null) {
                            return true;
                        }
                    }
                    // p1 is non-null, p2 is either null or non-null
                    if(!password1Value.equals(password2Value)) {
                        adviceLabel.setText("Passwords do not match");
                        password1Field.setStyle(PhotonServices.errorStyle);
                        password2Field.setStyle(PhotonServices.errorStyle);
                        adviceLabel.setStyle(PhotonServices.errorMessage);
                        return true;
                    }
                }
                return false;
            }
        };
        if(inIsAdd) {
            password1Field.textProperty().addListener((observable,oldValue,newValue) -> {
                disableOkButton.set(disableOkFunction.apply(null));
            });
            password2Field.textProperty().addListener((observable,oldValue,newValue) -> {
                disableOkButton.set(disableOkFunction.apply(null));
            });
        } else {
            usernameField.setText(inSelectedUser.getName());
            descriptionField.setText(inSelectedUser.getDescription());
        }
        isActiveCheckBox.setSelected(inSelectedUser.isActive());
        userDialog.getDialogPane().lookupButton(okButtonType).disableProperty().bind(disableOkButton);
        usernameField.textProperty().addListener((observer,oldValue,newValue) -> {
            disableOkButton.set(disableOkFunction.apply(null));
        });
        int rowCount = 0;
        userDialogGrid.add(new Label(inIsAdd?"Create New User":"Update "+ inSelectedUser.getName()),0,rowCount,2,1);
        userDialogGrid.add(new Label("Username"),0,++rowCount);
        userDialogGrid.add(usernameField,1,rowCount);
        userDialogGrid.add(new Label("Description"),0,++rowCount);
        userDialogGrid.add(descriptionField,1,rowCount);
        if(inIsAdd) {
            userDialogGrid.add(new Label("Password"),0,++rowCount);
            userDialogGrid.add(password1Field,1,rowCount);
            userDialogGrid.add(new Label("Verify Password"),0,++rowCount);
            userDialogGrid.add(password2Field,1,rowCount);
        }
        userDialogGrid.add(new Label("User Active"),0,++rowCount);
        userDialogGrid.add(isActiveCheckBox,1,rowCount);
        userDialogGrid.add(adviceLabel,0,++rowCount,2,1);
        userDialog.getDialogPane().setContent(userDialogGrid);
        userDialog.setResultConverter(dialogButton -> {
            if(dialogButton == okButtonType) {
                return inSelectedUser;
            }
            return null;
        });
        PhotonServices.style(userDialog.getDialogPane().getScene());
        Optional<SimpleUser> userOption = userDialog.showAndWait();
        if(userOption.isPresent()) {
            try {
                SimpleUser user = userOption.get();
                user.setDescription(descriptionField.getText());
                user.setHashedPassword(password1Field.getText()); // TODO need to hash this?
                user.setIsActive(isActiveCheckBox.isSelected());
                user.setName(usernameField.getText());
                if(inIsAdd) {
                    adminClientService.createUser(user,
                                                  password1Field.getText());
                } else {
                    adminClientService.updateUser(inSelectedUser.getName(),
                                                  user);
                }
                uiMessageService.post(new NotificationEvent(inIsAdd ? "Create User" : "Update User",
                                                             "User '" + inSelectedUser.getName() + "' " + (inIsAdd ? "created" : "updated"),
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                String message = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(UserView.this,
                                      e,
                                      "Unable to change password for {}: {}",
                                      inSelectedUser,
                                      message);
                uiMessageService.post(new NotificationEvent(inIsAdd ? "Create User" : "Update User",
                                                             (inIsAdd?"Create":"Update") + " user failed: " + message,
                                                             AlertType.ERROR));
            }
        }
        updateUsers();
    }
    /**
     * Initialize the columns of the table.
     */
    private void initializeColumns()
    {
        nameColumn = new TableColumn<>("Name"); 
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn = new TableColumn<>("Description"); 
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        isActiveColumn = new TableColumn<>("Active");
        isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        usersTable.getColumns().add(nameColumn);
        usersTable.getColumns().add(descriptionColumn);
        usersTable.getColumns().add(isActiveColumn);
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
     * update user context menu item
     */
    private MenuItem updateUserMenuItem;
    /**
     * delete user context menu item
     */
    private MenuItem deleteUserMenuItem;
    /**
     * deactivate user context menu item
     */
    private MenuItem deactivateUserMenuItem;
    /**
     * active user context menu item
     */
    private MenuItem activateUserMenuItem;
    /**
     * user password change context menu item
     */
    private MenuItem resetPasswordMenuItem;
    /**
     * name of the user
     */
    private TableColumn<SimpleUser,String> nameColumn;
    /**
     * optional description of the user
     */
    private TableColumn<SimpleUser,String> descriptionColumn;
    /**
     * indicates if the user is active or not
     */
    private TableColumn<SimpleUser,Boolean> isActiveColumn;
    /**
     * layout to hold the buttons
     */
    private HBox buttonLayout;
    /**
     * performs an add user action
     */
    private Button addUserButton;
    /**
     * view table
     */
    private TableView<SimpleUser> usersTable;
    /**
     * table context menu
     */
    private ContextMenu usersTableContextMenu;
    /**
     * main node of the view
     */
    private VBox mainLayout;
    /**
     * global name of this view
     */
    private static final String NAME = "Users View";
}
