package org.marketcetera.web.admin.view;

import java.util.Collection;
import java.util.Map;

import javax.annotation.security.PermitAll;

import org.marketcetera.admin.impl.SimplePermission;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.AbstractListView;
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
@PageTitle("Permissions | MATP")
@Route(value="permissions", layout = MainLayout.class) 
public class PermissionListView
        extends AbstractListView<SimplePermission,PermissionListView.PermissionForm>
{
    /**
     * Create a new PermissionListView instance.
     */
    public PermissionListView()
    {
        super(SimplePermission.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#setColumns(com.vaadin.flow.component.grid.Grid)
     */
    @Override
    protected void setColumns(Grid<SimplePermission> inGrid)
    {
        inGrid.setColumns("name",
                          "description");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createNewValue()
     */
    @Override
    protected SimplePermission createNewValue()
    {
        return new SimplePermission();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getUpdatedList()
     */
    @Override
    protected Collection<SimplePermission> getUpdatedList()
    {
        Collection<SimplePermission> permissions = Lists.newArrayList();
        getServiceClient().getPermissions().forEach(permission -> permissions.add((permission instanceof SimplePermission ? (SimplePermission)permission : new SimplePermission(permission))));
        return permissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doCreate(java.lang.Object)
     */
    @Override
    protected void doCreate(SimplePermission inValue)
    {
        getServiceClient().createPermission(inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doUpdate(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doUpdate(SimplePermission inValue,
                            Map<String,Object> inValueKeyData)
    {
        getServiceClient().updatePermission(String.valueOf(inValueKeyData.get("name")),
                                      inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doDelete(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doDelete(SimplePermission inValue,
                            Map<String,Object> inValueKeyData)
    {
        getServiceClient().deletePermission(String.valueOf(inValueKeyData.get("name")));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#registerInitialValue(java.lang.Object, java.util.Map)
     */
    @Override
    protected void registerInitialValue(SimplePermission inValue,
                                        Map<String,Object> inOutValueKeyData)
    {
        inOutValueKeyData.put("name",
                              inValue.getName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createForm()
     */
    @Override
    protected PermissionForm createForm()
    {
        form = new PermissionForm();
        return form;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getDataClazzName()
     */
    @Override
    protected String getDataClazzName()
    {
        return "Permission";
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
     * Provides the create/edit subform for permissions.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    class PermissionForm
            extends AbstractListView<SimplePermission,PermissionForm>.AbstractListForm
    {
        /**
         * Create a new PermissionForm instance.
         */
        private PermissionForm()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.AbstractListView.AbstractListForm#createFormComponentLayout(com.vaadin.flow.data.binder.Binder)
         */
        @Override
        protected Component createFormComponentLayout(Binder<SimplePermission> inBinder)
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
    private PermissionForm form;
    private static final long serialVersionUID = -8930087273314672465L;
}
