package org.marketcetera.web.admin.view;

import javax.annotation.security.PermitAll;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.webui.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Users | MATP")
@Route(value="users", layout = MainLayout.class) 
public class UserListView
        extends VerticalLayout
{
    /**
     * Create a new UserListView instance.
     */
    public UserListView()
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
        form = new UserForm();
        form.setWidth("25em");
        form.addListener(UserForm.SaveEvent.class,
                         this::saveUser); 
        form.addListener(UserForm.DeleteEvent.class,
                         this::deleteUser); 
        form.addListener(UserForm.CloseEvent.class,
                         e -> closeEditor()); 
    }

    private void saveUser(UserForm.SaveEvent event)
    {
        AdminClientService service = ServiceManager.getInstance().getService(AdminClientService.class);
        // TODO need to save vs create? need to provide password
        // TODO use original user name
        service.updateUser(event.getUser().getName(),
                           event.getUser());
        updateList();
        closeEditor();
    }

    private void deleteUser(UserForm.DeleteEvent event)
    {
        AdminClientService service = ServiceManager.getInstance().getService(AdminClientService.class);
        service.deactivateUser(event.getUser().getName());
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
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editUser(event.getValue())); 
    }

    private HorizontalLayout getToolbar()
    {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        Button addUserButton = new Button("Add user");
        addUserButton.addClickListener(click -> addUser()); 
        HorizontalLayout toolbar = new HorizontalLayout(filterText,
                                                        addUserButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editUser(User inUser)
    { 
        if(inUser == null) {
            closeEditor();
        } else {
            form.setUser(inUser);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor()
    {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addUser()
    {
        grid.asSingleSelect().clear();
        editUser(userFactory.create());
    }


    private void updateList() {
        // TODO filter
//        grid.setItems(service.findAllContacts(filterText.getValue()));
        AdminClientService service = ServiceManager.getInstance().getService(AdminClientService.class);
      grid.setItems(service.getUsers());
    }
    private Grid<User> grid = new Grid<>(User.class);
    private TextField filterText = new TextField();
    private UserForm form;
    @Autowired
    private UserFactory userFactory;
    private static final long serialVersionUID = 6331528916955469155L;
}
