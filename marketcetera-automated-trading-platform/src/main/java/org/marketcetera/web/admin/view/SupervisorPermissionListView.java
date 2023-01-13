package org.marketcetera.web.admin.view;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.PermitAll;

import org.apache.commons.collections4.CollectionUtils;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.User;
import org.marketcetera.admin.impl.SimpleSupervisorPermission;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.AbstractListView;
import org.marketcetera.webui.views.MainLayout;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Supervisor Permissions | MATP")
@Route(value="supervisorPermissions", layout = MainLayout.class) 
public class SupervisorPermissionListView
        extends AbstractListView<SimpleSupervisorPermission,SupervisorPermissionListView.SupervisorPermissionForm>
{
    /**
     * Create a new PermissionListView instance.
     */
    public SupervisorPermissionListView()
    {
        super(SimpleSupervisorPermission.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#setColumns(com.vaadin.flow.component.grid.Grid)
     */
    @Override
    protected void setColumns(Grid<SimpleSupervisorPermission> inGrid)
    {
        inGrid.setColumns("name",
                          "description");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createNewValue()
     */
    @Override
    protected SimpleSupervisorPermission createNewValue()
    {
        return new SimpleSupervisorPermission();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getUpdatedList()
     */
    @Override
    protected Collection<SimpleSupervisorPermission> getUpdatedList()
    {
        Collection<SimpleSupervisorPermission> supervisorPermissions = Lists.newArrayList();
        getServiceClient().getSupervisorPermissions().forEach(supervisorPermission -> supervisorPermissions.add((supervisorPermission instanceof SimpleSupervisorPermission ? (SimpleSupervisorPermission)supervisorPermission : new SimpleSupervisorPermission(supervisorPermission))));
        return supervisorPermissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doCreate(java.lang.Object)
     */
    @Override
    protected void doCreate(SimpleSupervisorPermission inValue)
    {
        inValue.setPermissions(form.getSelectedPermissions());
        inValue.setSubjects(form.getSelectedSubjects());
        inValue.setSupervisor(form.supervisorPermissionSupervisor.getValue());
        getServiceClient().createSupervisorPermission(inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doUpdate(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doUpdate(SimpleSupervisorPermission inValue,
                            Map<String,Object> inValueKeyData)
    {
        if(!CollectionUtils.isEqualCollection(inValue.getPermissions(),form.getSelectedPermissions(),PermissionComparator.instance)) {
            inValue.getPermissions().clear();
            inValue.setPermissions(form.getSelectedPermissions());
        }
        if(!CollectionUtils.isEqualCollection(inValue.getSubjects(),form.getSelectedSubjects(),UserComparator.instance)) {
            inValue.getSubjects().clear();
            inValue.setSubjects(form.getSelectedSubjects());
        }
        inValue.setSupervisor(form.supervisorPermissionSupervisor.getValue());
        getServiceClient().updateSupervisorPermission(String.valueOf(inValueKeyData.get("name")),
                                                      inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#doDelete(java.lang.Object, java.util.Map)
     */
    @Override
    protected void doDelete(SimpleSupervisorPermission inValue,
                            Map<String,Object> inValueKeyData)
    {
        getServiceClient().deleteSupervisorPermission(String.valueOf(inValueKeyData.get("name")));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#registerInitialValue(java.lang.Object, java.util.Map)
     */
    @Override
    protected void registerInitialValue(SimpleSupervisorPermission inValue,
                                        Map<String,Object> inOutValueKeyData)
    {
        Collection<Permission> allPermissions = getServiceClient().getPermissions();
        form.supervisorPermissions.setItems(allPermissions);
        Set<String> currentPermissionNames = Sets.newHashSet();
        inValue.getPermissions().forEach(permission -> currentPermissionNames.add(permission.getName()));
        allPermissions.forEach(permission -> {
            if(currentPermissionNames.contains(permission.getName())) {
                form.supervisorPermissions.select(permission);
            }
        });
        Collection<User> allSubjects = getServiceClient().getUsers();
        form.supervisorSubjects.setItems(allSubjects);
        Set<String> currentSubjectNames = Sets.newHashSet();
        inValue.getSubjects().forEach(subject -> currentSubjectNames.add(subject.getName()));
        allSubjects.forEach(subject -> {
            if(currentSubjectNames.contains(subject.getName())) {
                form.supervisorSubjects.select(subject);
            }
        });
        form.supervisorPermissionSupervisor.setItems(allSubjects);
        form.supervisorPermissionSupervisor.setValue(inValue.getSupervisor());
        inOutValueKeyData.put("name",
                              inValue.getName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#createForm()
     */
    @Override
    protected SupervisorPermissionForm createForm()
    {
        form = new SupervisorPermissionForm();
        return form;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.admin.view.AbstractListView#getDataClazzName()
     */
    @Override
    protected String getDataClazzName()
    {
        return "Supervisor Permission";
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
    class SupervisorPermissionForm
            extends AbstractListView<SimpleSupervisorPermission,SupervisorPermissionForm>.AbstractListForm
    {
        /**
         * Create a new SupervisorPermissionForm instance.
         */
        private SupervisorPermissionForm()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.AbstractListView.AbstractListForm#createFormComponentLayout(com.vaadin.flow.data.binder.Binder)
         */
        @Override
        protected Component createFormComponentLayout(Binder<SimpleSupervisorPermission> inBinder)
        {
            name = new TextField("Name"); 
            description = new TextField("Description");
            name.setEnabled(true);
            name.setReadOnly(false);
            description.setEnabled(true);
            description.setReadOnly(false);
            supervisorPermissionSupervisor = new ComboBox<>("Supervisor");
            supervisorPermissionSupervisor.setItemLabelGenerator(inItem -> inItem.getName());
            permissionsLabel = new Label("Permissions");
            supervisorPermissions = new MultiSelectListBox<>();
            supervisorPermissions.setItemLabelGenerator(inItem -> inItem.getName());
            subjectsLabel = new Label("Subjects");
            supervisorSubjects = new MultiSelectListBox<>();
            supervisorSubjects.setItemLabelGenerator(inItem -> inItem.getName());
            componentLayout = new VerticalLayout();
            componentLayout.add(name,
                                description,
                                permissionsLabel,
                                supervisorPermissionSupervisor,
                                supervisorPermissions,
                                subjectsLabel,
                                supervisorSubjects);
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
            return supervisorPermissions.getSelectedItems();
        }
        /**
         * Get the selected subjects.
         *
         * @return a <code>Set&lt;User&gt;</code> value
         */
        private Set<User> getSelectedSubjects()
        {
            return supervisorSubjects.getSelectedItems();
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
         * indicates whom the permissions are granted to
         */
        private ComboBox<User> supervisorPermissionSupervisor;
        /**
         * caption label for {@link #supervisorPermissions}
         */
        private Label permissionsLabel;
        /**
         * holds permissions selected and unselected
         */
        private MultiSelectListBox<Permission> supervisorPermissions;
        /**
         * caption label for {@link #supervisorSubjects}
         */
        private Label subjectsLabel;
        /**
         * holds subjects selected and unselected
         */
        private MultiSelectListBox<User> supervisorSubjects;
        /**
         * editor components layout value
         */
        private VerticalLayout componentLayout;
        private static final long serialVersionUID = -4925927232864950173L;
    }
    /**
     * edit form instance
     */
    private SupervisorPermissionForm form;
    private static final long serialVersionUID = -8930087273314672465L;
}
