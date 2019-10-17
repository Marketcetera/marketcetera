package org.marketcetera.web.view.admin;

import java.util.Properties;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.impl.SimplePermission;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;

import com.vaadin.ui.Button.ClickEvent;

/* $License$ */

/**
 * Provides a view for Role CRUD.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
     * @param inViewProperties
     */
    PermissionView(Properties inViewProperties)
    {
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
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<Permission> createDataContainer()
    {
        return new PermissionPagedDataContainer(this);
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
