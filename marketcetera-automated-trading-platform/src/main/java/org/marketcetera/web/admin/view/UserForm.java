package org.marketcetera.web.admin.view;

import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.User;
import org.marketcetera.admin.impl.SimpleUserFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class UserForm
        extends FormLayout
{
    private MutableUser user;
    TextField name = new TextField("Name"); 
    TextField description = new TextField("Description");
    ComboBox<Boolean> status = new ComboBox<>("Active");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<MutableUser> binder = new BeanValidationBinder<>(MutableUser.class); 
    /**
     * Create a new UserForm instance.
     */
    public UserForm()
    {
        addClassName("contact-form"); 
        binder.bindInstanceFields(this); 
        status.setItems(Boolean.TRUE,
                        Boolean.FALSE);
        name.setEnabled(true);
        name.setReadOnly(false);
        description.setEnabled(true);
        description.setReadOnly(false);
        add(name, 
            description,
            status,
            createButtonsLayout());
    }

    public void setUser(User inUser)
    {
        if(inUser == null) {
            user = null;
        } else {
            user = (MutableUser)(new SimpleUserFactory().create(inUser));
        }
        binder.readBean(user);
    }
    public static abstract class UserFormEvent
            extends ComponentEvent<UserForm>
    {
        protected UserFormEvent(UserForm source,
                                MutableUser inUser)
        { 
            super(source,
                  false);
            user = inUser;
        }

        public User getUser()
        {
            return user;
        }
        /**
         * underlying user value
         */
        private MutableUser user;
        private static final long serialVersionUID = -2009373271619947320L;
    }

    public static class SaveEvent
            extends UserFormEvent
    {
        SaveEvent(UserForm source,
                  MutableUser inUser) {
            super(source,
                  inUser);
        }
    }

    public static class DeleteEvent
            extends UserFormEvent
    {
        DeleteEvent(UserForm source,
                    MutableUser inUser)
        {
            super(source,
                  inUser);
        }
    }

    public static class CloseEvent
            extends UserFormEvent
    {
        CloseEvent(UserForm source)
        {
            super(source,
                  null);
        }
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
                                                                   user))); 
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
            binder.writeBean(user); 
            fireEvent(new SaveEvent(this,
                                    user)); 
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
    private static final long serialVersionUID = -4925927232864950173L;
}
