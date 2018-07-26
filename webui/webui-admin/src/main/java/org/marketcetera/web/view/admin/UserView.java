package org.marketcetera.web.view.admin;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.User;
import org.marketcetera.admin.impl.SimpleUser;
import org.marketcetera.web.services.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;

/* $License$ */

/**
 * Provides a view for User CRUD.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringView(name=UserView.NAME)
public class UserView
        extends AbstractAdminView<User>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Users";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 100;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.USER;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractAdminView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "User";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractAdminView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("name",
                             "description",
                             "active");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<User> createDataContainer()
    {
        return new UserPagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onCreateNew(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    protected void onCreateNew(ClickEvent inEvent)
    {
        SimpleUser newUser = new SimpleUser();
        createOrEdit(newUser,
                     true);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#onCreateOrEdit(com.vaadin.ui.Layout, org.marketcetera.persist.SummaryNDEntityBase, boolean)
     */
    @Override
    protected void onCreateOrEdit(Layout inContentLayout,
                                  User inSubject,
                                  boolean inIsNew)
    {
        passwordField = new PasswordField("Password");
        passwordField.setDescription("Password of the user");
        passwordField.setRequired(true);
        passwordField.setRequiredError("Password is required");
        passwordField.setValidationVisible(false);
        final PasswordField confirmPassword = new PasswordField("Confirm");
        confirmPassword.setDescription("Re-enter the password");
        confirmPassword.setRequired(true);
        confirmPassword.setRequiredError("Password is required");
        confirmPassword.setValidationVisible(false);
        confirmPassword.addValidator(inValue -> {
            if(!(new EqualsBuilder().append(passwordField.getValue(),confirmPassword.getValue())).isEquals()) {
                throw new InvalidValueException("Passwords not equal");
            }
        });
        passwordField.addValueChangeListener(inEvent -> {
            passwordField.setValidationVisible(true);
        });
        confirmPassword.addValueChangeListener(inEvent -> {
            confirmPassword.setValidationVisible(true);
        });
        activeOptionGroup = new OptionGroup("Active");
        activeOptionGroup.setDescription("Indicate if the user is able to log in");
        activeOptionGroup.addItems(OPTION_ACTIVE,
                                   OPTION_INACTIVE);
        activeOptionGroup.setMultiSelect(false);
        activeOptionGroup.setNullSelectionAllowed(false);
        activeOptionGroup.setValue(OPTION_ACTIVE);
        inContentLayout.addComponents(passwordField,
                                      confirmPassword,
                                      activeOptionGroup);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        SimpleUser selectedItem = getSelectedItem();
        if(selectedItem == null || inEvent.getProperty().getValue() == null) {
            return;
        }
        String action = String.valueOf(inEvent.getProperty().getValue());
        try {
            if(action.equals(ACTION_DEACTIVATE)) {
                AdminClientService.getInstance().deactivateUser(selectedItem.getName());
            } else {
                super.onActionSelect(inEvent);
            }
        } catch (Exception e) {
            Notification.show(action + " Problem",
                              ExceptionUtils.getRootCauseMessage(e),
                              Type.ERROR_MESSAGE);
        } finally {
            getActionSelect().setValue(null);
            getDataContainer().update();
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getSelectedItem()
     */
    @Override
    protected SimpleUser getSelectedItem()
    {
        User user = super.getSelectedItem();
        if(user instanceof SimpleUser) {
            return (SimpleUser)user;
        }
        return new SimpleUser(user.getName(),
                              user.getDescription(),
                              user.getHashedPassword(),
                              user.isActive());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractAdminView#addActions(com.vaadin.ui.ComboBox)
     */
    @Override
    protected void addActions(ComboBox inActionSelect)
    {
        super.addActions(inActionSelect);
        inActionSelect.addItem(ACTION_DEACTIVATE);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doCreate(org.marketcetera.persist.SummaryNDEntityBase)
     */
    @Override
    protected void doCreate(User inSubject)
    {
        SimpleUser user = (SimpleUser)inSubject;
        user.setIsActive(OPTION_ACTIVE.equals(activeOptionGroup.getValue()));
        AdminClientService.getInstance().createUser(inSubject,
                                                    passwordField.getValue());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doUpdate(java.lang.String, org.marketcetera.persist.SummaryNDEntityBase)
     */
    @Override
    protected void doUpdate(String inName,
                            User inSubject)
    {
        SimpleUser user = (SimpleUser)inSubject;
        user.setIsActive(OPTION_ACTIVE.equals(activeOptionGroup.getValue()));
        AdminClientService.getInstance().updateUser(inName,
                                                    inSubject);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doDelete(java.lang.String)
     */
    @Override
    protected void doDelete(String inName)
    {
        AdminClientService.getInstance().deleteUser(inName);
    }
    /**
     * password value of the user
     */
    private PasswordField passwordField;
    /**
     * indicates if the user is active or not
     */
    private OptionGroup activeOptionGroup;
    /**
     * global name of this view
     */
    public static final String NAME = "UserView";
    /**
     * action label for user deactivate
     */
    private final String ACTION_DEACTIVATE = "Deactivate";
    /**
     * option label for active user
     */
    private final String OPTION_ACTIVE = "Active";
    /**
     * option label for inactive user
     */
    private final String OPTION_INACTIVE = "Inactive";
    private static final long serialVersionUID = 4968995343460371648L;
}
