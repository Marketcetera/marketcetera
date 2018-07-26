package org.marketcetera.web.view.sessions;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.services.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>ActiveFixSession</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionPagedDataContainer
        extends PagedDataContainer<ActiveFixSession>
{
    /**
     * Create a new SessionPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; super ActiveFixSession&gt;</code> value
     * @param inCollection a <code>Collection&lt;? extends ActiveFixSession&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public SessionPagedDataContainer(Collection<? extends ActiveFixSession> inCollection,
                                     PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(ActiveFixSession.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new SessionPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; super ActiveFixSession&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public SessionPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(ActiveFixSession.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<ActiveFixSession> getDataContainerContents(PageRequest inPageRequest)
    {
        return AdminClientService.getInstance().getFixSessions(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(ActiveFixSession inO1,
                                   ActiveFixSession inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName())
                .append(inO1.getSessionId(),inO2.getSessionId())
                .append(inO1.getInstance(),inO2.getInstance())
                .append(inO1.getStatus(),inO2.getStatus())
                .append(inO1.getSenderSequenceNumber(),inO2.getSenderSequenceNumber())
                .append(inO1.getTargetSequenceNumber(),inO2.getTargetSequenceNumber()).isEquals();
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
