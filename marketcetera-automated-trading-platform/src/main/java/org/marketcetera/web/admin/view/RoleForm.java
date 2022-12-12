package org.marketcetera.web.admin.view;

import org.marketcetera.admin.Role;
import org.marketcetera.admin.impl.SimpleRole;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class RoleForm
        extends FormLayout
{
    private SimpleRole role;
    TextField name = new TextField("Name"); 
    TextField description = new TextField("Description");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<SimpleRole> binder = new BeanValidationBinder<>(SimpleRole.class); 
    /**
     * Create a new RoleForm instance.
     */
    public RoleForm()
    {
        addClassName("contact-form"); 
        binder.bindInstanceFields(this); 
        name.setEnabled(true);
        name.setReadOnly(false);
        description.setEnabled(true);
        description.setReadOnly(false);
        add(name, 
            description,
            createButtonsLayout());
    }

    public void setRole(Role inRole,
                        boolean inIsAdd)
    {
        isAdd = inIsAdd;
        if(inRole == null) {
            role = null;
        } else {
            if(inRole instanceof SimpleRole) {
                role = (SimpleRole)inRole;
            } else {
                role = new SimpleRole(inRole);
            }
        }
        binder.readBean(role);
    }
    public static abstract class RoleFormEvent
            extends ComponentEvent<RoleForm>
    {
        protected RoleFormEvent(RoleForm source,
                                SimpleRole inRole)
        { 
            super(source,
                  false);
            role = inRole;
        }

        public SimpleRole getRole()
        {
            return role;
        }
        /**
         * underlying role value
         */
        private SimpleRole role;
        private static final long serialVersionUID = -2009373271619947320L;
    }
    public abstract static class AddOrEditEvent
            extends RoleFormEvent
    {
        /**
         * Create a new AddOrEditEvent instance.
         *
         * @param inSource a <code>RoleForm</code> value
         * @param inRole a <code>SimpleRole</code> value
         */
        protected AddOrEditEvent(RoleForm inSource,
                                 SimpleRole inRole)
        {
            super(inSource,
                  inRole);
        }
        protected abstract boolean isAdd();
        
        private static final long serialVersionUID = -6556241127404177408L;
    }
    public static class EditEvent
            extends AddOrEditEvent
    {
        /**
         * Create a new EditEvent instance.
         *
         * @param source
         * @param inRole
         */
        private EditEvent(RoleForm source,
                          SimpleRole inRole)
        {
            super(source,
                  inRole);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.RoleForm.AddOrEditEvent#isAdd()
         */
        @Override
        protected boolean isAdd()
        {
            return false;
        }
        private static final long serialVersionUID = 7549688556551002867L;
    }
    public static class AddEvent
            extends AddOrEditEvent
    {
        private AddEvent(RoleForm source,
                         SimpleRole inRole)
        {
            super(source,
                  inRole);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.admin.view.RoleForm.AddOrEditEvent#isAdd()
         */
        @Override
        protected boolean isAdd()
        {
            return true;
        }
        private static final long serialVersionUID = 8977748946930495310L;
    }

    public static class DeleteEvent
            extends RoleFormEvent
    {
        private DeleteEvent(RoleForm source,
                    SimpleRole inRole)
        {
            super(source,
                  inRole);
        }
        private static final long serialVersionUID = -5539408852378587953L;
    }

    public static class CloseEvent
            extends RoleFormEvent
    {
        private CloseEvent(RoleForm source)
        {
            super(source,
                  null);
        }
        private static final long serialVersionUID = -2418276950521492103L;
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) { 
        return getEventBus().addListener(eventType,
                                         listener);
    }

    private Component createButtonsLayout()
    {
        // TODO check permissions
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave()); 
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this,
                                                                   role))); 
        close.addClickListener(event -> fireEvent(new CloseEvent(this))); 

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); 
        return new HorizontalLayout(save,
                                    delete,
                                    close);
    }
    /**
     * 
     *
     *
     */
    private void validateAndSave()
    {
        try {
            binder.writeBean(role);
            if(isAdd) {
                fireEvent(new AddEvent(this,
                                       role)); 
            } else {
                fireEvent(new EditEvent(this,
                                        role)); 
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
    private boolean isAdd;
    private static final long serialVersionUID = -4925927232864950173L;
}
