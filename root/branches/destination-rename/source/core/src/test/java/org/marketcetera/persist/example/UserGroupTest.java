package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.ManyToManyTestBase;
import static org.junit.Assert.assertEquals;

import java.util.Set;

/* $License$ */
/**
 * Tests user group relationship.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class UserGroupTest extends ManyToManyTestBase<SummaryUser,User,
        SummaryGroup,Group> {
    /* *Override necessary methods* */

    @Override
    protected void assertContainedEquals(SummaryUser e1, SummaryUser e2) {
        super.assertContainedEquals(e1, e2);
        assertEquals(e1.getName(), e2.getName());
        assertEquals(e1.getDescription(), e2.getDescription());
        assertEquals(e1.getEmail(), e2.getEmail());
        assertEquals(e1.getEmployeeID(), e2.getEmployeeID());
        assertEquals(e1.getNumFailedPasswordAttempts(),
                e2.getNumFailedPasswordAttempts());
        assertEquals(e1.isEnabled(), e2.isEnabled());
        assertEquals(e1.isLocked(), e2.isLocked());
    }

    @Override
    protected void assertOwnerEquals(Group o1, Group o2) {
        super.assertOwnerEquals(o1, o2);
        assertCollectionPermutation(o1.getUsers(),o2.getUsers());
        assertCollectionPermutation(o1.getAuthorizations(),
                o2.getAuthorizations());
    }

    @Override
    protected void assertOwnerSummaryEquals(Group o1, SummaryGroup o2) {
        super.assertOwnerSummaryEquals(o1, o2);
        assertEquals(o1.getName(),o2.getName());
        assertEquals(o1.getDescription(),o2.getDescription());
    }

    /* *Implement necessary methods* */
    protected User createFilled() {
        User u = new User();
        u.setName(randomString());
        return u;
    }

    protected Group createFilledOwner() {
        Group g = new Group();
        g.setName(randomString());
        return g;
    }

    protected void set(Set<SummaryUser> contained, Group container)
            throws Exception {
        container.setUsers(contained);
    }

    protected User save(User user) throws Exception {
        user.save();
        return user;
    }

    protected Group saveOwner(Group group) throws Exception {
        group.save();
        return group;
    }

    protected void delete(User user) throws Exception {
        user.delete();
    }

    protected void deleteOwner(Group group) throws Exception {
        group.delete();
    }

    protected int deleteAll() throws Exception {
        return MultiUserQuery.all().delete();
    }

    protected int deleteOwnerAll() throws Exception {
        return MultiGroupQuery.all().delete();
    }

    protected User fetch(long id) throws Exception {
        return new SingleUserQuery(id).fetch();
    }

    protected Group fetchOwner(long id) throws Exception {
        return new SingleGroupQuery(id).fetch();
    }

    protected Set<SummaryUser> getContained(Group group) throws Exception {
        return group.getUsers();
    }

    protected Set<SummaryGroup> getContainers(User user) throws Exception {
        return user.getGroups();
    }
}
