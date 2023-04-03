package org.marketcetera.ui.admin.view;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.impl.SimplePermission;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
 * Provides a view for permissions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PermissionView
        extends AbstractContentView
{
    /**
     * Create a new PermissionView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public PermissionView(Region inParentWindow,
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
        addPermissionButton = new Button("Add Permission");
        boolean userHasCreatePermissionPermission = authzHelperService.hasPermission(AdminPermissions.CreatePermissionAction);
        addPermissionButton.setVisible(userHasCreatePermissionPermission);
        addPermissionButton.setDisable(!userHasCreatePermissionPermission);
        addPermissionButton.setOnAction(event -> doAddOrUpdatePermission(new SimplePermission(),true));
        buttonLayout.getChildren().add(addPermissionButton);
        mainLayout.getChildren().addAll(permissionsTable,
                                    buttonLayout);
    }
    /**
     * Update the users displayed in the table.
     */
    private void updatePermissions()
    {
        permissionsTable.getItems().clear();
        adminClientService.getPermissions().forEach(permission -> {
            if(permission instanceof SimplePermission) {
                permissionsTable.getItems().add((SimplePermission)permission);
            } else {
                SimplePermission newPermission = new SimplePermission();
                newPermission.setName(permission.getName());
                newPermission.setDescription(permission.getDescription());
                permissionsTable.getItems().add(newPermission);
            }
        });
    }
    /**
     * Initialize the table and fill it with data.
     */
    private void initializeTable()
    {
        permissionsTable = new TableView<>();
        permissionsTable.setPlaceholder(new Label("no permissions to display"));
        initializeColumns();
        initializeContextMenu();
        permissionsTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<SimplePermission>) (inObservable,inOldValue,inNewValue) -> {
            enableContextMenuItems(inNewValue);
        });
        updatePermissions();
    }
    /**
     * Enables or disables the table context menu items based on the selected item.
     *
     * @param inNewValue a <code>SimplePermission</code> value
     */
    private void enableContextMenuItems(SimplePermission inNewValue)
    {
        if(inNewValue == null) {
            return;
        }
        // TODO disable if the permission is in use?
    }
    /**
     * Execute a context menu action.
     *
     * @param inSelectedItem a <code>SimplePermission</code> value
     * @param inTitle a <code>String</code> value for the message dialogs
     * @param inContent a <code>String</code> value for the message dialogs
     * @param inAction a <code>Consumer&lt;String&gt;</code> value to conduct the action
     */
    private void doContextMenuAction(SimplePermission inSelectedItem,
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
                    SLF4JLoggerProxy.info(PermissionView.this,
                                          "{} performing {} on {}",
                                          SessionUser.getCurrent(),
                                          inTitle,
                                          inSelectedItem);
                    inAction.accept(inSelectedItem.getName());
                    SLF4JLoggerProxy.info(PermissionView.this,
                                          "{} on {} succeeded",
                                          inTitle,
                                          inSelectedItem);
                    updatePermissions();
                    uiMessageService.post(new NotificationEvent(inTitle,
                                                                 inContent + " succeeded",
                                                                 AlertType.INFORMATION));
                } catch (Exception e) {
                    String message = PlatformServices.getMessage(e);
                    SLF4JLoggerProxy.warn(PermissionView.this,
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
        permissionsTableContextMenu = new ContextMenu();
        deletePermissionMenuItem = new MenuItem("Delete");
        deletePermissionMenuItem.setOnAction(event -> {
            SimplePermission selectedPermission = permissionsTable.getSelectionModel().getSelectedItem();
            if(selectedPermission == null) {
                return;
            }
            doContextMenuAction(selectedPermission,
                                "Delete Permission",
                                "Delete " + selectedPermission.getName(),
                                inPermissionName -> adminClientService.deletePermission(inPermissionName));
        });
        updatePermissionMenuItem = new MenuItem("Edit");
        updatePermissionMenuItem.setOnAction(event -> {
            SimplePermission selectedPermission = permissionsTable.getSelectionModel().getSelectedItem();
            if(selectedPermission == null) {
                return;
            }
            doAddOrUpdatePermission(selectedPermission,
                              false);
        });
        if(authzHelperService.hasPermission(AdminPermissions.UpdatePermissionAction)) {
            permissionsTableContextMenu.getItems().add(updatePermissionMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.DeletePermissionAction)) {
            permissionsTableContextMenu.getItems().add(deletePermissionMenuItem);
        }
        permissionsTable.setContextMenu(permissionsTableContextMenu);
    }
    /**
     * Execute an add user or update permission action based on the given parameters.
     *
     * @param inSelectedPermission a <code>SimplePermission</code> value
     * @param inIsAdd a <code>boolean</code> value
     */
    private void doAddOrUpdatePermission(SimplePermission inSelectedPermission,
                                         boolean inIsAdd)
    {
        Dialog<SimplePermission> roleDialog = new Dialog<>();
        VBox mainLayout = new VBox(5);
        GridPane roleDialogGrid = new GridPane();
        roleDialogGrid.setHgap(10);
        roleDialogGrid.setVgap(10);
        roleDialogGrid.setPadding(new Insets(20,150,10,10));
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        Label adviceLabel = new Label();
        ButtonType okButtonType = new ButtonType("OK",
                                                 ButtonData.OK_DONE);
        roleDialog.getDialogPane().getButtonTypes().addAll(okButtonType,
                                                           ButtonType.CANCEL);
        final BooleanProperty disableOkButton = new SimpleBooleanProperty(false);
        Function<Void,Boolean> disableOkFunction = new Function<>() {
            @Override
            public Boolean apply(Void inIgnored)
            {
                adviceLabel.setText("");
                adviceLabel.setStyle(PhotonServices.successMessage);
                nameField.setStyle(PhotonServices.successStyle);
                // check name
                String computedValue = StringUtils.trimToNull(nameField.getText());
                if(computedValue == null) {
                    adviceLabel.setText("Name required");
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                    nameField.setStyle(PhotonServices.errorStyle);
                    return true;
                }
                if(computedValue.length() > 255) {
                    adviceLabel.setText("Name may contain up to 255 characters");
                    adviceLabel.setStyle(PhotonServices.errorMessage);
                    nameField.setStyle(PhotonServices.errorStyle);
                    return true;
                }
                // TODO are role names required to conform to the NDEntity pattern?
                // TODO check role name in use
                return false;
            }
        };
        nameField.setText(inSelectedPermission.getName());
        descriptionField.setText(inSelectedPermission.getDescription());
        roleDialog.getDialogPane().lookupButton(okButtonType).disableProperty().bind(disableOkButton);
        nameField.textProperty().addListener((observer,oldValue,newValue) -> {
            disableOkButton.set(disableOkFunction.apply(null));
        });
        int rowCount = 0;
        roleDialogGrid.add(new Label(inIsAdd?"Create New Permission":"Update "+ inSelectedPermission.getName()),0,rowCount,2,1);
        roleDialogGrid.add(new Label("Name"),0,++rowCount);
        roleDialogGrid.add(nameField,1,rowCount);
        roleDialogGrid.add(new Label("Description"),0,++rowCount);
        roleDialogGrid.add(descriptionField,1,rowCount);
        roleDialogGrid.add(adviceLabel,0,++rowCount,2,1);
        mainLayout.getChildren().addAll(roleDialogGrid);
        roleDialog.getDialogPane().setContent(mainLayout);
        roleDialog.setResultConverter(dialogButton -> {
            if(dialogButton == okButtonType) {
                return inSelectedPermission;
            }
            return null;
        });
        PhotonServices.style(roleDialog.getDialogPane().getScene());
        roleDialog.getDialogPane().getScene().getWindow().sizeToScene();
        Optional<SimplePermission> permissionOption = roleDialog.showAndWait();
        if(permissionOption.isPresent()) {
            try {
                SimplePermission permission = permissionOption.get();
                permission.setDescription(descriptionField.getText());
                permission.setName(nameField.getText());
                if(inIsAdd) {
                    adminClientService.createPermission(permission);
                } else {
                    adminClientService.updatePermission(inSelectedPermission.getName(),
                                                        permission);
                }
                uiMessageService.post(new NotificationEvent(inIsAdd ? "Create Permission" : "Update Permission",
                                                             "Permission '" + inSelectedPermission.getName() + "' " + (inIsAdd ? "created" : "updated"),
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                String message = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(PermissionView.this,
                                      e,
                                      "Unable to create or update role {}: {}",
                                      inSelectedPermission,
                                      message);
                uiMessageService.post(new NotificationEvent(inIsAdd ? "Create Permission" : "Update Permission",
                                                             (inIsAdd?"Create":"Update") + " role failed: " + message,
                                                             AlertType.ERROR));
            }
        }
        updatePermissions();
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
        permissionsTable.getColumns().add(nameColumn);
        permissionsTable.getColumns().add(descriptionColumn);
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
    private MenuItem updatePermissionMenuItem;
    /**
     * delete user context menu item
     */
    private MenuItem deletePermissionMenuItem;
    /**
     * name of the user
     */
    private TableColumn<SimplePermission,String> nameColumn;
    /**
     * optional description of the user
     */
    private TableColumn<SimplePermission,String> descriptionColumn;
    /**
     * layout to hold the buttons
     */
    private HBox buttonLayout;
    /**
     * performs an add role action
     */
    private Button addPermissionButton;
    /**
     * view table
     */
    private TableView<SimplePermission> permissionsTable;
    /**
     * table context menu
     */
    private ContextMenu permissionsTableContextMenu;
    /**
     * main layout
     */
    private VBox mainLayout;
    /**
     * global name of this view
     */
    private static final String NAME = "Permissions View";
}
