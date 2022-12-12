package org.marketcetera.web.admin.view;

import javax.annotation.security.PermitAll;

import org.marketcetera.admin.Role;
import org.marketcetera.admin.impl.SimpleRole;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.webui.views.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Roles | MATP")
@Route(value="roles", layout = MainLayout.class) 
public class RoleListView
        extends VerticalLayout
{
    /**
     * Create a new RoleListView instance.
     */
    public RoleListView()
    {
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        updateList();
        closeEditor(); 
    }

    private HorizontalLayout getContent()
    {
        HorizontalLayout content = new HorizontalLayout(grid,
                                                        form);
        content.setFlexGrow(2,
                            grid);
        content.setFlexGrow(1,
                            form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm()
    {
        form = new RoleForm();
        form.setWidth("25em");
        form.addListener(RoleForm.AddEvent.class,
                         this::saveRole); 
        form.addListener(RoleForm.DeleteEvent.class,
                         this::deleteRole); 
        form.addListener(RoleForm.EditEvent.class,
                         this::saveRole); 
        form.addListener(RoleForm.CloseEvent.class,
                         e -> closeEditor()); 
    }

    private void saveRole(RoleForm.AddOrEditEvent inEvent)
    {
        AdminClientService service = ServiceManager.getInstance().getService(AdminClientService.class);
        if(inEvent.isAdd()) {
            service.createRole(inEvent.getRole());
        } else {
            // TODO use original role name on edit
            service.updateRole(inEvent.getRole().getName(),
                               inEvent.getRole());
        }
        updateList();
        closeEditor();
    }

    private void deleteRole(RoleForm.DeleteEvent event)
    {
        AdminClientService service = ServiceManager.getInstance().getService(AdminClientService.class);
        service.deleteRole(event.getRole().getName());
        updateList();
        closeEditor();
    }
    /**
     * 
     *
     *
     */
    private void configureGrid()
    {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("name","description");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> addOrEditRole(event.getValue(),
                                                                            false)); 
    }

    private HorizontalLayout getToolbar()
    {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        Button addRoleButton = new Button("Add role");
        addRoleButton.addClickListener(click -> addRole()); 
        HorizontalLayout toolbar = new HorizontalLayout(filterText,
                                                        addRoleButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void addOrEditRole(Role inRole,
                              boolean inIsAdd)
    { 
        if(inRole == null) {
            closeEditor();
        } else {
            form.setRole(inRole,
                         inIsAdd);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor()
    {
        form.setRole(null,
                     false);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addRole()
    {
        grid.asSingleSelect().clear();
        addOrEditRole(new SimpleRole(),
                      true);
    }


    private void updateList()
    {
        // TODO filter
//        grid.setItems(service.findAllContacts(filterText.getValue()));
        AdminClientService service = ServiceManager.getInstance().getService(AdminClientService.class);
        grid.setItems(service.getRoles());
    }
    private Grid<Role> grid = new Grid<>(Role.class);
    private TextField filterText = new TextField();
    private RoleForm form;
    private static final long serialVersionUID = -8930087273314672465L;
}
