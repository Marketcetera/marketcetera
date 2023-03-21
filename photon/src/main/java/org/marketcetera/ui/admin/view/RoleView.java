package org.marketcetera.ui.admin.view;

import java.util.Comparator;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.impl.SimplePermission;
import org.marketcetera.admin.impl.SimpleRole;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/* $License$ */

/**
 * Provides a view for roles.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleView
        extends AbstractContentView
{
    /**
     * Create a new RoleView instance.
     *
     * @param inParentWindow a <code>Node</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public RoleView(Node inParentWindow,
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
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        adminClientService = serviceManager.getService(AdminClientService.class);
        mainLayout = new VBox(5);
        initializeTable();
        buttonLayout = new HBox(5);
        addRoleButton = new Button("Add Role");
        boolean userHasCreateRolePermission = authzHelperService.hasPermission(AdminPermissions.CreateRoleAction);
        addRoleButton.setVisible(userHasCreateRolePermission);
        addRoleButton.setDisable(!userHasCreateRolePermission);
        addRoleButton.setOnAction(event -> doAddOrUpdateRole(new SimpleRole(),true));
        buttonLayout.getChildren().add(addRoleButton);
        mainLayout.getChildren().addAll(rolesTable,
                                    buttonLayout);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getNode()
     */
    @Override
    public Node getNode()
    {
        return mainLayout;
    }
    /**
     * Update the users displayed in the table.
     */
    private void updateRoles()
    {
        rolesTable.getItems().clear();
        adminClientService.getRoles().forEach(role -> {
            if(role instanceof SimpleRole) {
                rolesTable.getItems().add((SimpleRole)role);
            } else {
                SimpleRole newRole = new SimpleRole();
                newRole.setName(role.getName());
                newRole.setDescription(role.getDescription());
                for(Permission permission : role.getPermissions()) {
                    if(permission instanceof SimplePermission) {
                        newRole.getPermissions().add(permission);
                    } else {
                        SimplePermission newPermission = new SimplePermission();
                        newPermission.setName(permission.getName());
                        newPermission.setDescription(permission.getDescription());
                        newRole.getPermissions().add(newPermission);
                    }
                }
                rolesTable.getItems().add(newRole);
            }
        });
    }
    /**
     * Initialize the table and fill it with data.
     */
    private void initializeTable()
    {
        rolesTable = new TableView<>();
        rolesTable.setPlaceholder(new Label("no roles to display"));
        initializeColumns();
        initializeContextMenu();
        rolesTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<SimpleRole>) (inObservable,inOldValue,inNewValue) -> {
            enableContextMenuItems(inNewValue);
        });
        updateRoles();
    }
    /**
     * Enables or disables the table context menu items based on the selected item.
     *
     * @param inNewValue a <code>SimpleRole</code> value
     */
    private void enableContextMenuItems(SimpleRole inNewValue)
    {
        if(inNewValue == null) {
            return;
        }
        // TODO disable if the role is in use?
    }
    /**
     * Execute a context menu action.
     *
     * @param inSelectedItem a <code>SimpleRole</code> value
     * @param inTitle a <code>String</code> value for the message dialogs
     * @param inContent a <code>String</code> value for the message dialogs
     * @param inAction a <code>Consumer&lt;String&gt;</code> value to conduct the action
     */
    private void doContextMenuAction(SimpleRole inSelectedItem,
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
                    SLF4JLoggerProxy.info(RoleView.this,
                                          "{} performing {} on {}",
                                          SessionUser.getCurrent(),
                                          inTitle,
                                          inSelectedItem);
                    inAction.accept(inSelectedItem.getName());
                    SLF4JLoggerProxy.info(RoleView.this,
                                          "{} on {} succeeded",
                                          inTitle,
                                          inSelectedItem);
                    updateRoles();
                    webMessageService.post(new NotificationEvent(inTitle,
                                                                 inContent + " succeeded",
                                                                 AlertType.INFORMATION));
                } catch (Exception e) {
                    String message = PlatformServices.getMessage(e);
                    SLF4JLoggerProxy.warn(RoleView.this,
                                          e,
                                          "Unable to perform {} on {}: {}",
                                          inTitle,
                                          inSelectedItem,
                                          message);
                    webMessageService.post(new NotificationEvent(inTitle,
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
        rolesTableContextMenu = new ContextMenu();
        deleteRoleMenuItem = new MenuItem("Delete");
        deleteRoleMenuItem.setOnAction(event -> {
            SimpleRole selectedRole = rolesTable.getSelectionModel().getSelectedItem();
            if(selectedRole == null) {
                return;
            }
            doContextMenuAction(selectedRole,
                                "Delete Role",
                                "Delete " + selectedRole.getName(),
                                inRoleName -> adminClientService.deleteRole(inRoleName));
        });
        updateRoleMenuItem = new MenuItem("Edit");
        updateRoleMenuItem.setOnAction(event -> {
            SimpleRole selectedRole = rolesTable.getSelectionModel().getSelectedItem();
            if(selectedRole == null) {
                return;
            }
            doAddOrUpdateRole(selectedRole,
                              false);
        });
        if(authzHelperService.hasPermission(AdminPermissions.UpdateRoleAction)) {
            rolesTableContextMenu.getItems().add(updateRoleMenuItem);
        }
        if(authzHelperService.hasPermission(AdminPermissions.DeleteRoleAction)) {
            rolesTableContextMenu.getItems().add(deleteRoleMenuItem);
        }
        rolesTable.setContextMenu(rolesTableContextMenu);
    }
    /**
     * Execute an add user or update role action based on the given parameters.
     *
     * @param inSelectedRole a <code>SimpleRole</code> value
     * @param inIsAdd a <code>boolean</code> value
     */
    private void doAddOrUpdateRole(SimpleRole inSelectedRole,
                                   boolean inIsAdd)
    {
        Dialog<SimpleRole> roleDialog = new Dialog<>();
        VBox mainLayout = new VBox(5);
        GridPane roleDialogGrid = new GridPane();
        roleDialogGrid.setHgap(10);
        roleDialogGrid.setVgap(10);
        roleDialogGrid.setPadding(new Insets(20,150,10,10));
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        GridPane permissionsListLayout = new GridPane();
        permissionsListLayout.setHgap(10);
        permissionsListLayout.setVgap(10);
        permissionsListLayout.setPadding(new Insets(20,150,10,10));
        ListView<Permission> leftListView = new ListView<>();
        ListView<Permission> rightListView = new ListView<>();
        leftListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        leftListView.setCellFactory(PermissionCellFactory.instance);
        rightListView.setCellFactory(PermissionCellFactory.instance);
        VBox moveButtonLayout = new VBox(5);
        Button moveLeftButton = new Button("<");
        Button moveRightButton = new Button(">");
        moveButtonLayout.setAlignment(Pos.CENTER);
        moveButtonLayout.getChildren().addAll(moveLeftButton,
                                              moveRightButton);
        int rowCount = 0;
        permissionsListLayout.add(new Label("Permissions"),0,rowCount,3,1);
        permissionsListLayout.add(new Label("Available"),0,++rowCount);
        permissionsListLayout.add(new Label("Selected"),2,rowCount);
        permissionsListLayout.add(leftListView,0,++rowCount);
        permissionsListLayout.add(moveButtonLayout,1,rowCount);
        permissionsListLayout.add(rightListView,2,rowCount);
        rightListView.getItems().addAll(inSelectedRole.getPermissions());
        leftListView.getItems().addAll(adminClientService.getPermissions());
        Comparator<Permission> permissionComparator = new Comparator<>() {
            @Override
            public int compare(Permission inO1,
                               Permission inO2)
            {
                return new CompareToBuilder().append(inO1.getName(),inO2.getName()).toComparison();
            }
        };
        for(Permission permission : rightListView.getItems()) {
            leftListView.getItems().remove(permission);
        }
        for(Permission permission : leftListView.getItems()) {
            rightListView.getItems().remove(permission);
        }
        leftListView.getItems().sort(permissionComparator);
        rightListView.getItems().sort(permissionComparator);
        moveLeftButton.setOnAction(event -> {
            ObservableList<Permission> selectedItems = rightListView.getSelectionModel().getSelectedItems();
            if(selectedItems == null) {
                return;
            }
            selectedItems.forEach(permission -> leftListView.getItems().add(permission));
            rightListView.getItems().removeAll(selectedItems);
            leftListView.getItems().sort(permissionComparator);
            rightListView.getItems().sort(permissionComparator);
        });
        moveRightButton.setOnAction(event -> {
            ObservableList<Permission> selectedItems = leftListView.getSelectionModel().getSelectedItems();
            if(selectedItems == null) {
                return;
            }
            selectedItems.forEach(permission -> rightListView.getItems().add(permission));
            leftListView.getItems().removeAll(selectedItems);
            leftListView.getItems().sort(permissionComparator);
            rightListView.getItems().sort(permissionComparator);
        });
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
        nameField.setText(inSelectedRole.getName());
        descriptionField.setText(inSelectedRole.getDescription());
        roleDialog.getDialogPane().lookupButton(okButtonType).disableProperty().bind(disableOkButton);
        nameField.textProperty().addListener((observer,oldValue,newValue) -> {
            disableOkButton.set(disableOkFunction.apply(null));
        });
        rowCount = 0;
        roleDialogGrid.add(new Label(inIsAdd?"Create New Role":"Update "+ inSelectedRole.getName()),0,rowCount,2,1);
        roleDialogGrid.add(new Label("Name"),0,++rowCount);
        roleDialogGrid.add(nameField,1,rowCount);
        roleDialogGrid.add(new Label("Description"),0,++rowCount);
        roleDialogGrid.add(descriptionField,1,rowCount);
        roleDialogGrid.add(adviceLabel,0,++rowCount,2,1);
        mainLayout.getChildren().addAll(roleDialogGrid,
                                        permissionsListLayout);
        roleDialog.getDialogPane().setContent(mainLayout);
        roleDialog.setResultConverter(dialogButton -> {
            if(dialogButton == okButtonType) {
                return inSelectedRole;
            }
            return null;
        });
        PhotonServices.style(roleDialog.getDialogPane().getScene());
        roleDialog.getDialogPane().getScene().getWindow().sizeToScene();
        Optional<SimpleRole> roleOption = roleDialog.showAndWait();
        if(roleOption.isPresent()) {
            try {
                SimpleRole role = roleOption.get();
                role.setDescription(descriptionField.getText());
                role.setName(nameField.getText());
                role.getPermissions().clear();
                role.getPermissions().addAll(rightListView.getItems());
                if(inIsAdd) {
                    adminClientService.createRole(role);
                } else {
                    adminClientService.updateRole(inSelectedRole.getName(),
                                                  role);
                }
                webMessageService.post(new NotificationEvent(inIsAdd ? "Create Role" : "Update Role",
                                                             "Role '" + inSelectedRole.getName() + "' " + (inIsAdd ? "created" : "updated"),
                                                             AlertType.INFORMATION));
            } catch (Exception e) {
                String message = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(RoleView.this,
                                      e,
                                      "Unable to create or update role {}: {}",
                                      inSelectedRole,
                                      message);
                webMessageService.post(new NotificationEvent(inIsAdd ? "Create Role" : "Update Role",
                                                             (inIsAdd?"Create":"Update") + " role failed: " + message,
                                                             AlertType.ERROR));
            }
        }
        updateRoles();
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
        rolesTable.getColumns().add(nameColumn);
        rolesTable.getColumns().add(descriptionColumn);
    }
    /**
     * Cell factory for displaying {@link Permission} values.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class PermissionCellFactory
            implements Callback<ListView<Permission>, ListCell<Permission>>
    {
        @Override
        public ListCell<Permission> call(ListView<Permission> param) {
            return new ListCell<>(){
                @Override
                public void updateItem(Permission person, boolean empty) {
                    super.updateItem(person, empty);
                    if (empty || person == null) {
                        setText(null);
                    } else {
                        setText(person.getName());
                    }
                }
            };
        }
        private static final PermissionCellFactory instance = new PermissionCellFactory();
    }
    /**
     * update user context menu item
     */
    private MenuItem updateRoleMenuItem;
    /**
     * delete user context menu item
     */
    private MenuItem deleteRoleMenuItem;
    /**
     * name of the user
     */
    private TableColumn<SimpleRole,String> nameColumn;
    /**
     * optional description of the user
     */
    private TableColumn<SimpleRole,String> descriptionColumn;
    /**
     * layout to hold the buttons
     */
    private HBox buttonLayout;
    /**
     * performs an add role action
     */
    private Button addRoleButton;
    /**
     * provides access to admin client services
     */
    private AdminClientService adminClientService;
    /**
     * view table
     */
    private TableView<SimpleRole> rolesTable;
    /**
     * table context menu
     */
    private ContextMenu rolesTableContextMenu;
    /**
     * main layout of the view
     */
    private VBox mainLayout;
    /**
     * global name of this view
     */
    private static final String NAME = "Roles View";
}
