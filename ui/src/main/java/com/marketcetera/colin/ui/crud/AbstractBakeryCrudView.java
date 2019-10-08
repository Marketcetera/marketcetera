package com.marketcetera.colin.ui.crud;

import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.marketcetera.colin.app.security.CurrentUser;
import com.marketcetera.colin.backend.data.entity.AbstractEntity;
import com.marketcetera.colin.backend.data.entity.util.EntityUtil;
import com.marketcetera.colin.backend.service.FilterableCrudService;
import com.marketcetera.colin.ui.components.SearchBar;
import com.marketcetera.colin.ui.utils.TemplateUtil;
import com.marketcetera.colin.ui.views.HasNotifications;
import elemental.json.Json;

import java.util.function.Consumer;

public abstract class AbstractBakeryCrudView<E extends AbstractEntity> extends Crud<E>
        implements HasUrlParameter<Long>, HasNotifications {

    private static final String DISCARD_MESSAGE = "There are unsaved modifications to the %s. Discard changes?";
    private static final String DELETE_MESSAGE = "Are you sure you want to delete the selected %s? This action cannot be undone.";

    private final Grid<E> grid;

    private final CrudEntityPresenter<E> entityPresenter;

    protected abstract String getBasePage();

    protected abstract void setupGrid(Grid<E> grid);

    public AbstractBakeryCrudView(Class<E> beanType, FilterableCrudService<E> service,
                                  Grid<E> grid, CrudEditor<E> editor, CurrentUser currentUser) {
        super(beanType, grid, editor);
        this.grid = grid;
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        CrudI18n crudI18n = CrudI18n.createDefault();
        String entityName = EntityUtil.getName(beanType);
        crudI18n.setNewItem("New " + entityName);
        crudI18n.setEditItem("Edit " + entityName);
        crudI18n.setEditLabel("Edit " + entityName);
        crudI18n.getConfirm().getCancel().setContent(String.format(DISCARD_MESSAGE, entityName));
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.setDeleteItem("Delete");
        setI18n(crudI18n);

        CrudEntityDataProvider<E> dataProvider = new CrudEntityDataProvider<>(service);
        grid.setDataProvider(dataProvider);
        setupGrid(grid);
        Crud.addEditColumn(grid);

        entityPresenter = new CrudEntityPresenter<>(service, currentUser, this);

        SearchBar searchBar = new SearchBar();
        searchBar.setActionText("New " + entityName);
        searchBar.setPlaceHolder("Search");
        searchBar.addFilterChangeListener(e -> dataProvider.setFilter(searchBar.getFilter()));
        searchBar.getActionButton().getElement().setAttribute("new-button", true);

        setToolbar(searchBar);
        setupCrudEventListeners(entityPresenter);
    }

    private void setupCrudEventListeners(CrudEntityPresenter<E> entityPresenter) {
        Consumer<E> onSuccess = entity -> navigateToEntity(null);
        Consumer<E> onFail = entity -> {
            throw new RuntimeException("The operation could not be performed.");
        };

        addEditListener(e ->
                entityPresenter.loadEntity(e.getItem().getId(),
                        entity -> navigateToEntity(entity.getId().toString())));

        addCancelListener(e -> navigateToEntity(null));

        addSaveListener(e ->
                entityPresenter.save(e.getItem(), onSuccess, onFail));

        addDeleteListener(e ->
                entityPresenter.delete(e.getItem(), onSuccess, onFail));
    }

    protected void navigateToEntity(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long id) {
        if (id != null) {
            E item = getEditor().getItem();
            if (item != null && id.equals(item.getId())) {
                return;
            }
            entityPresenter.loadEntity(id, entity -> edit(entity, EditMode.EXISTING_ITEM));
        } else {
            setOpened(false);
        }
    }
}
