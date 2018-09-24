package org.marketcetera.web.view.admin;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.impl.SimplePermission;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button.ClickEvent;

/* $License$ */

/**
 * Provides a view for Role CRUD.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@org.springframework.stereotype.Component
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
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Permissions";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 300;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.HAND_STOP_O;
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
