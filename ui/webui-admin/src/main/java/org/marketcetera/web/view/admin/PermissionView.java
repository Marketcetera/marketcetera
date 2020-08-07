package org.marketcetera.web.view.admin;

import java.util.Properties;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.impl.SimplePermission;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.admin.AdminClientService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view for Role CRUD.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PermissionView
        extends AbstractAdminView<Permission>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new PermissionView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public PermissionView(Window inParentWindow,
                          NewWindowEvent inEvent,
                          Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractAdminView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Permission";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#getDataContainerType()
     */
    @Override
    protected Class<PermissionPagedDataContainer> getDataContainerType()
    {
        return PermissionPagedDataContainer.class;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onCreateNew(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    protected void onCreateNew(ClickEvent inEvent)
    {
        SimplePermission newPermission = new SimplePermission();
        createOrEdit(newPermission,
                     true);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doDelete(java.lang.String)
     */
    @Override
    protected void doDelete(String inName)
    {
        AdminClientService.getInstance().deletePermission(inName);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doCreate(org.marketcetera.persist.SummaryNDEntityBase)
     */
    @Override
    protected void doCreate(Permission inSubject)
    {
        AdminClientService.getInstance().createPermission(inSubject);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doUpdate(java.lang.String, org.marketcetera.persist.SummaryNDEntityBase)
     */
    @Override
    protected void doUpdate(String inName,
                            Permission inSubject)
    {
        AdminClientService.getInstance().updatePermission(inName,
                                                          inSubject);
    }
    /**
     * global name of this view
     */
    private static final String NAME = "Permissions";
    private static final long serialVersionUID = 3066342312753582309L;
}
