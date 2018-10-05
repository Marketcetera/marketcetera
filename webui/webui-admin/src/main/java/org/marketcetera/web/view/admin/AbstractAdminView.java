package org.marketcetera.web.view.admin;

import java.util.Iterator;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.persist.SummaryNDEntityBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.AbstractGridView;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides common behaviors for admin views.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractAdminView<Clazz extends SummaryNDEntityBase>
        extends AbstractGridView<Clazz>
{
    /**
     * Get the webMessageService value.
     *
     * @return a <code>WebMessageService</code> value
     */
    WebMessageService getWebMessageService()
    {
        return webMessageService;
    }
    /**
     * Sets the webMessageService value.
     *
     * @param inWebMessageService a <code>WebMessageService</code> value
     */
    void setWebMessageService(WebMessageService inWebMessageService)
    {
        webMessageService = inWebMessageService;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractAdminView#addActions(com.vaadin.ui.ComboBox)
     */
    @Override
    protected void addActions(ComboBox inActionSelect)
    {
        inActionSelect.addItems(ACTION_EDIT,
                                ACTION_DELETE);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        Clazz selectedItem = getSelectedItem();
        if(selectedItem == null || inEvent.getProperty().getValue() == null) {
            return;
        }
        String action = String.valueOf(inEvent.getProperty().getValue());
        switch(action) {
            case ACTION_EDIT:
                createOrEdit(selectedItem,
                             false);
                break;
            case ACTION_DELETE:
                doDelete(selectedItem.getName());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    /**
     * Create or edit the given object.
     *
     * @param inSubject a <code>Clazz</code> value
     * @param inIsNew a <code>boolean</code> value
     */
    @SuppressWarnings("unchecked")
    protected void createOrEdit(Clazz inSubject,
                                boolean inIsNew)
    {
        NDEntityBase subject = (NDEntityBase)inSubject;
        final String originalName = subject.getName();
        final Window formWindow = new Window((inIsNew?"Create ":"Edit ")+getViewSubjectName());
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        formWindow.setContent(content);
        formWindow.center();
        formWindow.setModal(true);
        formWindow.setSizeUndefined();
        formWindow.setClosable(false);
        formWindow.setDraggable(false);
        formWindow.setResizable(false);
        final FormLayout fieldLayout = new FormLayout();
        fieldLayout.setMargin(true);
        fieldLayout.setSizeFull();
        final TextField nameField = new TextField("Name");
        nameField.setNullRepresentation("");
        nameField.setDescription("Unique name value");
        nameField.setRequired(true);
        nameField.setRequiredError("Name is required");
        nameField.setValue(subject.getName());
        nameField.addValidator(new RegexpValidator(NDEntityBase.namePattern.pattern(),
                                                   "Names may contain up to 255 letters, numbers, spaces, or the dash char ('-')"));
        nameField.addValidator(inValue -> {
            if(inValue == null) {
                throw new InvalidValueException("Name is required");
            }
            if(!inIsNew) {
                return;
            }
        });
        nameField.addValueChangeListener(inEvent -> {
            nameField.setValidationVisible(true);
        });
        nameField.setValidationVisible(false);
        final TextField descriptionField = new TextField("Description");
        descriptionField.setNullRepresentation("");
        descriptionField.setDescription("Optional description");
        descriptionField.setRequired(false);
        descriptionField.setValue(subject.getDescription());
        descriptionField.addValidator(new StringLengthValidator("Description may contain up to 255 characters",
                                                                0,
                                                                255,
                                                                true));
        fieldLayout.addComponents(nameField,
                                  descriptionField);
        content.addComponent(fieldLayout);
        onCreateOrEdit(fieldLayout,
                       inSubject,
                       inIsNew);
        CssLayout buttonLayout = new CssLayout();
        content.addComponent(buttonLayout);
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        buttonLayout.addComponents(okButton,
                                   cancelButton);
        cancelButton.addClickListener(inEvent -> {
            formWindow.close();
        });
        okButton.addClickListener(inEvent -> {
            Iterator<Component> components = fieldLayout.iterator();
            while(components.hasNext()) {
                Component component = components.next();
                if(component instanceof Validatable) {
                    Validatable validatableComponent = (Validatable)component;
                    if(!validatableComponent.isValid()) {
                        return;
                    }
                }
            }
            formWindow.close();
            try {
                subject.setName(nameField.getValue());
                subject.setDescription(descriptionField.getValue());
                if(inIsNew) {
                    doCreate((Clazz)subject);
                } else {
                    doUpdate(originalName,
                             (Clazz)subject);
                }
                getGrid().deselectAll();
            } catch (Exception e) {
                String message = ExceptionUtils.getRootCauseMessage(e);
                SLF4JLoggerProxy.error(AbstractAdminView.this,
                                       e,
                                       "Error editing or creating object: {}",
                                       subject);
                Notification.show((inIsNew?"Create ":"Edit ") + getViewSubjectName() + " Error",
                                  "Error occurred storing object: " + message,
                                  Type.ERROR_MESSAGE);
            }
        });
        UI.getCurrent().addWindow(formWindow);
    }
    /**
     * Invoked when an object is to be added or edited.
     *
     * @param inContentLayout a <code>Layout</code> value
     * @param inSubject a <code>Clazz</code> value
     * @param inIsNew a <code>boolean</code> value
     */
    protected void onCreateOrEdit(Layout inContentLayout,
                                  Clazz inSubject,
                                  boolean inIsNew)
    {
    }
    /**
     * Cause the given object to be created.
     *
     * @param inSubject a <code>Clazz</code> value
     */
    protected abstract void doCreate(Clazz inSubject);
    /**
     * Cause the given object with the given name to be updated.
     *
     * @param inName a <code>String</code> value
     * @param inSubject a <code>Clazz</code> value
     */
    protected abstract void doUpdate(String inName,
                                     Clazz inSubject);
    /**
     * Delete the object with the given name.
     *
     * @param inName a <code>String</code> value
     */
    protected abstract void doDelete(String inName);
    /**
     * Get the admin client service for the current session.
     *
     * @return an <code>AdminClientService</code> value
     */
    protected AdminClientService getAdminClientService()
    {
        return VaadinSession.getCurrent().getAttribute(AdminClientService.class);
    }
    /**
     * provides access to web message services
     */
    protected WebMessageService webMessageService;
    /**
     * edit action label
     */
    private final String ACTION_EDIT = "Edit";
    /**
     * delete action label
     */
    private final String ACTION_DELETE = "Delete";
    private static final long serialVersionUID = 3025204147505272438L;
}
