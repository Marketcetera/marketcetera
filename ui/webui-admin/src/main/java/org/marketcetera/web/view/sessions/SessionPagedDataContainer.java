package org.marketcetera.web.view.sessions;

import java.util.Collection;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.entity.DisplayFixSession;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>DisplayFixSession</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionPagedDataContainer
        extends PagedDataContainer<DisplayFixSession>
{
    /**
     * Create a new SessionPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; ? extends DisplayFixSession&gt;</code> value
     * @param inCollection a <code>Collection&lt;? extends DisplayFixSession&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public SessionPagedDataContainer(Collection<? extends DisplayFixSession> inCollection,
                                     PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayFixSession.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new SessionPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; super DisplayFixSession&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public SessionPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayFixSession.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<DisplayFixSession> getDataContainerContents(PageRequest inPageRequest)
    {
        CollectionPageResponse<ActiveFixSession> activeFixSessions = AdminClientService.getInstance().getFixSessions(inPageRequest);
        CollectionPageResponse<DisplayFixSession> displayFixSessions = new CollectionPageResponse<>(activeFixSessions);
        activeFixSessions.getElements().forEach(activeFixSession -> displayFixSessions.getElements().add(new DisplayFixSession(activeFixSession)));
        return displayFixSessions;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(DisplayFixSession inO1,
                                   DisplayFixSession inO2)
    {
        return inO1.equals(inO2);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "FixSession";
    }
    private static final long serialVersionUID = -1643583263489594148L;
}
