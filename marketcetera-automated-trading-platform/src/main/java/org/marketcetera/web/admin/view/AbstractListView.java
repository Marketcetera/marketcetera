package org.marketcetera.web.admin.view;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;

/* $License$ */

/**
 * Provides a parent class for a grid/editor split-screen layout.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractListView<DataClazz,
                                       FormClazz extends AbstractListView<DataClazz,FormClazz>.AbstractListForm>
        extends VerticalLayout
{
    /**
     * Create a new AbstractListView instance.
     *
     * @param inClassType a <code>Class&lt;DataClazz&gt;</code> value
     */
    protected AbstractListView(Class<DataClazz> inClassType)
    {
        dataClazz = inClassType;
        grid = new Grid<>(dataClazz);
        filterText = new TextField();
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(),
            getContent());
        updateList();
        closeEditor(); 
    }
    /**
     * Configure the already-created grid.
     */
    protected void configureGrid()
    {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        setColumns(grid);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> addOrEditFormValue(event.getValue(),
                                                                                 form,
                                                                                 false)); 
    }
    /**
     * Construct and return the toolbar.
     *
     * @return a <code>HorizontalLayout</code> value
     */
    protected HorizontalLayout getToolbar()
    {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setVisible(enableFilterText());
        Button addValueButton = new Button("Add " + getDataClazzName());
        addValueButton.addClickListener(click -> doAdd());
        addValueButton.setVisible(enableAddButton());
        HorizontalLayout toolbar = new HorizontalLayout(filterText,
                                                        addValueButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    /**
     * Get the human-readable name of <code>DataClazz</code> objects.
     * 
     * <p>Subclasses may override this method if the default value is not desirable.</p>
     *
     * @return a <code>String</code> value
     */
    protected String getDataClazzName()
    {
        return dataClazz.getSimpleName();
    }
    /**
     * Indicates whether the filter text widget should be visible or not.
     *
     * <p>Subclasses may override this method if the default value is not desirable.</p>
     * 
     * <p>The default value is <code>true</code>.</p>
     *
     * @return a <code>boolean</code> value
     */
    protected boolean enableFilterText()
    {
        return true;
    }
    /**
     * Indicates whether the add new value widget should be visible or not.
     *
     * <p>Subclasses may override this method if the default value is not desirable.</p>
     * 
     * <p>The default value is <code>true</code>.</p>
     *
     * @return a <code>boolean</code> value
     */
    protected boolean enableAddButton()
    {
        return true;
    }
    /**
     * Prepares the form for adding a new value from the grid.
     */
    protected void doAdd()
    {
        grid.asSingleSelect().clear();
        addOrEditFormValue(createNewValue(),
                           form,
                           true);
    }
    /**
     * Prepares the form for adding a new value or editing an existing grid value.
     *
     * @param inValue a <code>DataClazz</code> value
     * @param inForm a <code>FormClazz</code> value
     * @param inIsAdd a <code>boolean</code> value
     */
    protected void addOrEditFormValue(DataClazz inValue,
                                      FormClazz inForm,
                                      boolean inIsAdd)
    {
        if(inValue == null) {
            closeEditor();
        } else {
            registerInitialValue(inValue,
                                 valueKeyData);
            inForm.setValue(inValue,
                            inIsAdd);
            inForm.setVisible(true);
            addClassName("editing");
        }
    }
    /**
     * Close and hide the add/edit form.
     */
    protected void closeEditor()
    {
        form.setValue(null,
                      false);
        form.setVisible(false);
        removeClassName("editing");
    }
    /**
     * Cause the grid to be refreshed.
     */
    protected void updateList()
    {
        grid.setItems(getUpdatedList());
    }
    /**
     * Create a new, initialized value.
     *
     * @return a <code>DataClazz</code> value
     */
    protected abstract DataClazz createNewValue();
    /**
     * Set the columns to be shown in the grid.
     *
     * @param inGrid a <code>Grid&lt;DataClazz&gt;<code> value
     */
    protected abstract void setColumns(Grid<DataClazz> inGrid);
    /**
     * Get the updated grid list from the data store.
     *
     * @return a <code>Collection&lt;DataClazz&gt;</code> value
     */
    protected abstract Collection<DataClazz> getUpdatedList();
    /**
     * Create the given value in the datastore.
     *
     * @param inValue a <code>DataClazz</code> value
     */
    protected abstract void doCreate(DataClazz inValue);
    /**
     * Update the given value in the datastore.
     *
     * @param inValue a <code>DataClazz</code> value
     * @param inValueKeyData a <code>Map&lt;String,Object&gt;</code> value
     */
    protected abstract void doUpdate(DataClazz inValue,
                                     Map<String,Object> inValueKeyData);
    /**
     * Delete the given value in the datastore.
     *
     * @param inValue a <code>DataClazz</code> value
     * @param inValueKeyData a <code>Map&lt;String,Object&gt;</code> value
     */
    protected abstract void doDelete(DataClazz inValue,
                                     Map<String,Object> inValueKeyData);
    /**
     * Create the form view instance.
     *
     * @return a <code>FormClazz</code> value
     */
    protected abstract FormClazz createForm();
    /**
     * Get the edit form object.
     *
     * @return a <code>FormClazz</code> value
     */
    protected FormClazz getForm()
    {
        return form;
    }
    /**
     * Gets the content for the view.
     * 
     * <p>Subclasses may override this method to customize the view layout.</p>
     *
     * @return a <code>Component</code> value
     */
    protected Component getContent()
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
    /**
     * Registers the initial value selected from the grid to be used as reference while adding, editing, or deleting.
     * 
     * <p>The default behavior is to do nothing. Subclasses may need to override this method in order to initialize the
     * edit form.</p>
     * 
     * <p>The key data is passed to {@link #doDelete(Object)} and {@link #doUpdate(Object)} to help with the datastore action.</p>
     *
     * @param inValue a <code>DataClazz</code> value
     * @param inValueKeyData a <code>Map&lt;String,Object&gt;</code> value
     */
    protected void registerInitialValue(DataClazz inValue,
                                        Map<String,Object> inValueKeyData)
    {
    }
    /**
     * Create or update the given value and refresh the view.
     *
     * @param inValue a <code>DataClazz</code> value
     * @param inValueKeyData a <code>Map&lt;String,Object&gt;</code> value
     * @param inIsAdd a <code>boolean</code> value
     */
    protected void saveValueAndRefreshView(DataClazz inValue,
                                           Map<String,Object> inValueKeyData,
                                           boolean inIsAdd)
    {
        if(inIsAdd) {
            doCreate(inValue);
        } else {
            doUpdate(inValue,
                     inValueKeyData);
        }
        refreshView();
    }
    /**
     * Delete the given value and refresh the view.
     *
     * @param inValue a <code>DataClazz</code> value
     * @param inValueKeyData a <code>Map&lt;String,Object&gt;</code> value
     */
    protected void deleteValueAndRefreshView(DataClazz inValue,
                                             Map<String,Object> inValueKeyData)
    {
        doDelete(inValue,
                 inValueKeyData);
        refreshView();
    }
    /**
     * Refresh the view, including fetching fresh data from the datastore.
     */
    protected void refreshView()
    {
        updateList();
        closeEditor();
    }
    /**
     * Configure the editor form.
     *
     * <p>Subclasses may override this method if the default behavior is undesirable.</p>
     */
    protected void configureForm()
    {
        form = createForm();
        form.setWidth("25em");
    }
    /**
     * Get the main grid widget.
     *
     * @return a <code>Grid&lt;DataClazz&gt;</code> value
     */
    protected Grid<DataClazz> getGrid()
    {
        return grid;
    }
    /**
     * Get the filter text widget.
     *
     * @return a <code>TextField</code> value
     */
    protected TextField getFilterText()
    {
        return filterText;
    }
    /**
     * Provides the base class implementation to the embedded edit form.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public abstract class AbstractListForm
            extends FormLayout
    {
        /**
         * Create a new AbstractListForm instance.
         */
        protected AbstractListForm()
        {
            save = new Button("Save");
            delete = new Button("Delete");
            close = new Button("Cancel");
            binder = new BeanValidationBinder<>(dataClazz);
            addClassName("contact-form"); 
            binder.bindInstanceFields(this);
            add(createFormComponentLayout(binder),
                createButtonsLayout());
        }
        /**
         * Create the editor components layout.
         * 
         * <p>This layout should contain all the data fields for the type displayed in the editor.</p>
         *
         * @return a <code>Component</code> value
         */
        protected abstract Component createFormComponentLayout(Binder<DataClazz> inBinder);
        /**
         * Set the value being worked on in the editor form.
         *
         * @param inValue a <code>DataClazz</code> value
         * @param inIsAdd a <code>boolean</code> value
         */
        protected void setValue(DataClazz inValue,
                                boolean inIsAdd)
        {
            isAdd = inIsAdd;
            if(inValue == null) {
                subject = null;
            } else {
                subject = inValue;
            }
            binder.readBean(subject);
        }
        /**
         * Create the editor buttons layout.
         *
         * @return a <code>Component</code> value
         */
        protected Component createButtonsLayout()
        {
            // TODO check permissions
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            save.addClickShortcut(Key.ENTER);
            close.addClickShortcut(Key.ESCAPE);
            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> deleteValueAndRefreshView(subject,
                                                                       valueKeyData));
            close.addClickListener(event -> closeEditor());
            binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
            return new HorizontalLayout(save,
                                        delete,
                                        close);
        }
        /**
         * Validate the subject and cause it to be saved.
         */
        protected void validateAndSave()
        {
            try {
                binder.writeBean(subject);
                if(isAdd) {
                    saveValueAndRefreshView(subject,
                                            valueKeyData,
                                            true);
                } else {
                    saveValueAndRefreshView(subject,
                                            valueKeyData,
                                            false);
                }
            } catch (ValidationException e) {
                // TODO
                e.printStackTrace();
            }
        }
        /**
         * Get the isAdd value.
         *
         * @return a <code>boolean</code> value
         */
        protected boolean isAdd()
        {
            return isAdd;
        }
        /**
         * Get the subject value.
         *
         * @return a <code>DataClazz</code> value
         */
        protected DataClazz getSubject()
        {
            return subject;
        }
        /**
         * Sets the subject value.
         *
         * @param inSubject a <code>DataClazz</code> value
         */
        protected void setSubject(DataClazz inSubject)
        {
            subject = inSubject;
        }
        /**
         * Get the save value.
         *
         * @return a <code>Button</code> value
         */
        protected Button getSave()
        {
            return save;
        }
        /**
         * Get the delete value.
         *
         * @return a <code>Button</code> value
         */
        protected Button getDelete()
        {
            return delete;
        }
        /**
         * Get the close value.
         *
         * @return a <code>Button</code> value
         */
        protected Button getClose()
        {
            return close;
        }
        /**
         * Get the binder value.
         *
         * @return a <code>Binder<DataClazz></code> value
         */
        protected Binder<DataClazz> getBinder()
        {
            return binder;
        }
        /**
         * indicates if the editor is in add mode or edit mode
         */
        private boolean isAdd;
        /**
         * contains the value being displayed in the editor
         */
        private DataClazz subject;
        /**
         * save button widget
         */
        private final Button save;
        /**
         * delete button widget
         */
        private final Button delete;
        /**
         * close button widget
         */
        private final Button close;
        /**
         * data binder widget
         */
        private final Binder<DataClazz> binder;
        private static final long serialVersionUID = -1717470443251096115L;
    }
    /**
     * class of the object being displayed in the grid and editor
     */
    private final Class<DataClazz> dataClazz;
    /**
     * grid widget
     */
    private final Grid<DataClazz> grid;
    /**
     * filter widget
     */
    private final TextField filterText;
    /**
     * stores key information about the selected row in the grid to be used for datastore operations
     */
    private final Map<String,Object> valueKeyData = Maps.newHashMap();
    /**
     * editor form widget
     */
    private FormClazz form;
    private static final long serialVersionUID = 3983597030320388010L;
}
