package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.ManyToManyTestBase;

import java.util.Set;

/* $License$ */
/**
 * Tests the relationship between Authorizations and Groups
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AuthorizationGroupTest extends ManyToManyTestBase
        <Authorization, Authorization, SummaryGroup, Group> {
    protected Authorization createFilled() {
        Authorization a = new Authorization();
        a.setName(randomString());
        return a;
    }


    protected Group createFilledOwner() {
        Group g = new Group();
        g.setName(randomString());
        return g;
    }

    protected void set(Set<Authorization> contained, Group container)
            throws Exception {
        container.setAuthorizations(contained);
    }

    protected Authorization save(Authorization authorization)
            throws Exception {
        authorization.save();
        return authorization;
    }

    protected Group saveOwner(Group group) throws Exception {
        group.save();
        return group;
    }

    protected void delete(Authorization authorization) throws Exception {
        authorization.delete();
    }

    protected void deleteOwner(Group group) throws Exception {
        group.delete();
    }

    protected int deleteAll() throws Exception {
        return MultiAuthorizationQuery.all().delete();
    }

    protected int deleteOwnerAll() throws Exception {
        return MultiGroupQuery.all().delete();
    }

    protected Authorization fetch(long id) throws Exception {
        return new SingleAuthorizationQuery(id).fetch();
    }

    protected Group fetchOwner(long id) throws Exception {
        return new SingleGroupQuery(id).fetch();
    }

    protected Set<Authorization> getContained(Group group) throws Exception {
        return group.getAuthorizations();
    }

    protected Set<SummaryGroup> getContainers(Authorization authorization) throws Exception {
        return null;
    }

    /**
     * Overridden to indicate that the relationship is not
     * navigable from contained to container
     *
     * @return false
     */
    @Override
    public boolean isContainerAvailable() {
        return false;
    }

    /**
     * Over-ridden to indicate that Authorizations can be deleted
     * even when you have groups refering to them
     * @return true
     */
    @Override
    public boolean isContainedDeleteAllowedWhenRelated() {
        return true;
    }
}
