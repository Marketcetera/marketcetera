package org.marketcetera.web.admin.view;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.PermitAll;

import org.apache.commons.collections4.CollectionUtils;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.impl.SimpleRole;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.AbstractListView;
import org.marketcetera.webui.views.MainLayout;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Roles | MATP")
@Route(value="roles", layout = MainLayout.class) 
public class RoleListView
        extends AbstractListView<SimpleRole,RoleListView.RoleForm>
{
    /**
     * Create a new RoleListView instance.
     */
    public RoleListView()
    {
        super(SimpleRole.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#setColumns(com.vaadin.flow.component.grid.Grid)
     */
    @Override
    protected void setColumns(Grid<SimpleRole> inGrid)
    {
        inGrid.setColumns("name",
                          "description");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createNewValue()
     */
    @Override
    protected SimpleRole createNewValue()
    {
        return new SimpleRole();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getUpdatedList()
     */
    @Override
    protected Collection<SimpleRole> getUpdatedList()
    {
        Collection<SimpleRole> roles = Lists.newArrayList();
        getServiceClient().getRoles().forEach(role -> roles.add((role instanceof SimpleRole ? (SimpleRole)role : new SimpleRole(role))));
        return roles;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doCreate(java.lang.Object)
     */
    @Override
    protected void doCreate(SimpleRole inValue)
    {
        getServiceClient().createRole(inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doUpdate(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doUpdate(SimpleRole inValue,
                            Map<String,Object> inValueKeyData)
    {
        if(!CollectionUtils.isEqualCollection(inValue.getPermissions(),form.getSelectedPermissions(),PermissionComparator.instance)) {
            inValue.getPermissions().clear();
            inValue.setPermissions(form.getSelectedPermissions());
        }
        getServiceClient().updateRole(String.valueOf(inValueKeyData.get("name")),
                                      inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doDelete(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doDelete(SimpleRole inValue,
                            Map<String,Object> inValueKeyData)
    {
        getServiceClient().deleteRole(String.valueOf(inValueKeyData.get("name")));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#registerInitialValue(java.lang.Object, java.util.Map)
     */
    @Override
    protected void registerInitialValue(SimpleRole inValue,
                                        Map<String,Object> inOutValueKeyData)
    {
        Collection<Permission> allPermissions = getServiceClient().getPermissions();
        form.allPermissions.setItems(allPermissions);
        Set<String> currentPermissionNames = Sets.newHashSet();
        inValue.getPermissions().forEach(permission -> currentPermissionNames.add(permission.getName()));
        allPermissions.forEach(permission -> {
            if(currentPermissionNames.contains(permission.getName())) {
                form.allPermissions.select(permission);
            }
        });
        inOutValueKeyData.put("name",
                              inValue.getName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createForm()
     */
    @Override
    protected RoleForm createForm()
    {
        form = new RoleForm();
        return form;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getDataClazzName()
     */
    @Override
    protected String getDataClazzName()
    {
        return "Role";
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
     * Provides the create/edit subform for roles.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    class RoleForm
            extends AbstractListView<SimpleRole,RoleForm>.AbstractListForm
    {
        /**
         * Create a new RoleForm instance.
         */
        private RoleForm()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.AbstractListView.AbstractListForm#createFormComponentLayout(com.vaadin.flow.data.binder.Binder)
         */
        @Override
        protected Component createFormComponentLayout(Binder<SimpleRole> inBinder)
        {
            name = new TextField("Name"); 
            description = new TextField("Description");
            permissionsLabel = new Label("Permissions");
            allPermissions = new MultiSelectListBox<>();
            allPermissions.setItemLabelGenerator(inItem -> inItem.getName());
            name.setEnabled(true);
            name.setReadOnly(false);
            description.setEnabled(true);
            description.setReadOnly(false);
            componentLayout = new VerticalLayout();
            componentLayout.add(name,
                                description,
                                permissionsLabel,
                                allPermissions);
            inBinder.bind(name,"name");
            inBinder.bind(description,"description");
            return componentLayout;
        }
        /**
         * Get the selected permissions.
         *
         * @return a <code>Set&lt;Permission&gt;</code> value
         */
        private Set<Permission> getSelectedPermissions()
        {
            return allPermissions.getSelectedItems();
        }
        /**
         * caption label for {@link #allPermissions}
         */
        private Label permissionsLabel;
        /**
         * holds permissions selected and unselected
         */
        private MultiSelectListBox<Permission> allPermissions;
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
    private RoleForm form;
    private static final long serialVersionUID = -8930087273314672465L;
}
