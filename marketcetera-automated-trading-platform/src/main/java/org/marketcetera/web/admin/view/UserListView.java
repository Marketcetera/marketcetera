package org.marketcetera.web.admin.view;

import java.util.Collection;
import java.util.Map;

import javax.annotation.security.PermitAll;

import org.marketcetera.admin.impl.SimpleUser;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.webui.views.MainLayout;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Users | MATP")
@Route(value="users", layout = MainLayout.class) 
public class UserListView
        extends AbstractListView<SimpleUser,UserListView.UserForm>
{
    /**
     * Create a new UserListView instance.
     */
    public UserListView()
    {
        super(SimpleUser.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#setColumns(com.vaadin.flow.component.grid.Grid)
     */
    @Override
    protected void setColumns(Grid<SimpleUser> inGrid)
    {
        inGrid.setColumns("name",
                          "description");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createNewValue()
     */
    @Override
    protected SimpleUser createNewValue()
    {
        return new SimpleUser();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getUpdatedList()
     */
    @Override
    protected Collection<SimpleUser> getUpdatedList()
    {
        Collection<SimpleUser> users = Lists.newArrayList();
        getServiceClient().getUsers().forEach(user -> users.add((user instanceof SimpleUser ? (SimpleUser)user : new SimpleUser(user))));
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doCreate(java.lang.Object)
     */
    @Override
    protected void doCreate(SimpleUser inValue)
    {
        // TODO
        getServiceClient().createUser(inValue,
                                      "pazzword");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doUpdate(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doUpdate(SimpleUser inValue,
                            Map<String,Object> inValueKeyData)
    {
        getServiceClient().updateUser(String.valueOf(inValueKeyData.get("name")),
                                      inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doDelete(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doDelete(SimpleUser inValue,
                            Map<String,Object> inValueKeyData)
    {
        getServiceClient().deleteUser(String.valueOf(inValueKeyData.get("name")));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#registerInitialValue(java.lang.Object, java.util.Map)
     */
    @Override
    protected void registerInitialValue(SimpleUser inValue,
                                        Map<String,Object> inOutValueKeyData)
    {
        inOutValueKeyData.put("name",
                              inValue.getName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createForm()
     */
    @Override
    protected UserForm createForm()
    {
        form = new UserForm();
        return form;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getDataClazzName()
     */
    @Override
    protected String getDataClazzName()
    {
        return "User";
    }
    /**
     * Get the service client to use for this view.
     *
     * @return an <code>AdminClientService</code> value
     */
    private AdminClientService getServiceClient()
    {
        return ServiceManager.getInstance().getService(AdminClientService.class);
    }
    /**
     * Provides the create/edit subform for users.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    class UserForm
            extends AbstractListView<SimpleUser,UserForm>.AbstractListForm
    {
        /**
         * Create a new UserForm instance.
         */
        private UserForm()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.AbstractListView.AbstractListForm#createFormComponentLayout(com.vaadin.flow.data.binder.Binder)
         */
        @Override
        protected Component createFormComponentLayout(Binder<SimpleUser> inBinder)
        {
            name = new TextField("Name"); 
            description = new TextField("Description");
            name.setEnabled(true);
            name.setReadOnly(false);
            description.setEnabled(true);
            description.setReadOnly(false);
            componentLayout = new VerticalLayout();
            componentLayout.add(name,
                                description);
            inBinder.bind(name,"name");
            inBinder.bind(description,"description");
            return componentLayout;
        }
        /**
         * name widget
         */
        private TextField name;
        /**
         * description widget
         */
        private TextField description;
        /**
         * editor components layout value
         */
        private VerticalLayout componentLayout;
        private static final long serialVersionUID = -4925927232864950173L;
    }
    /**
     * edit form instance
     */
    private UserForm form;
    private static final long serialVersionUID = -8930087273314672465L;
}
