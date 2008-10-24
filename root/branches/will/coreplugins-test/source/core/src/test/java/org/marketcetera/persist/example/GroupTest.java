package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;
import static org.junit.Assert.assertNull;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import java.util.List;
import java.util.HashSet;

/* $License$ */
/**
 * Tests persistence of Group Entity
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class GroupTest extends CorePersistNDTestBase<Group,SummaryGroup> {

/* ************************Implement necessary operations************* */
    protected Group fetchByName(String name) throws Exception {
        return new SingleGroupQuery(name).fetch();
    }

    protected SummaryGroup fetchSummaryByName(String name) throws Exception {
        return new SingleGroupQuery(name).fetchSummary();
    }

    protected void save(Group group) throws Exception {
        group.save();
    }

    protected void delete(Group group) throws Exception {
        group.delete();
    }

    protected void deleteAll() throws Exception {
        MultiGroupQuery.all().delete();
    }

    protected Group fetchByID(long id) throws Exception {
        return new SingleGroupQuery(id).fetch();
    }

    protected SummaryGroup fetchSummaryByID(long id) throws Exception {
        return new SingleGroupQuery(id).fetchSummary();
    }

    protected boolean fetchExistsByName(String name) throws Exception {
        return new SingleGroupQuery(name).exists();
    }

    protected boolean fetchExistsByID(long id) throws Exception {
        return new SingleGroupQuery(id).exists();
    }

    protected Group createEmpty() {
        return new Group();
    }

    protected Class<Group> getEntityClass() {
        return Group.class;
    }

    protected Class<? extends MultipleEntityQuery> getMultiQueryClass() {
        return MultiGroupQuery.class;
    }

    protected List<SummaryGroup> fetchSummaryQuery(
            MultipleEntityQuery query) throws Exception {
        return ((MultiGroupQuery)query).fetchSummary();
    }

    protected List<Group> fetchQuery(MultipleEntityQuery query)
            throws Exception {
        return ((MultiGroupQuery)query).fetch();
    }

    protected MultipleEntityQuery getAllQuery() {
        return MultiGroupQuery.all();
    }

    /* ************************Override necessary operations************* */
    @Override
    protected Group createFilled() throws Exception {
        Group g = super.createFilled();
        HashSet<SummaryUser> users = new HashSet<SummaryUser>();
        users.add(user1);
        g.setUsers(users);
        HashSet<Authorization> auths = new HashSet<Authorization>();
        auths.add(auth1);
        g.setAuthorizations(auths);
        return g;
    }

    @Override
    protected void changeAttributes(Group group) {
        super.changeAttributes(group);
        HashSet<SummaryUser> users = new HashSet<SummaryUser>();
        users.add(user2);
        group.setUsers(users);
        HashSet<Authorization> auths = new HashSet<Authorization>();
        auths.add(auth2);
        group.setAuthorizations(auths);
    }

    @Override
    protected Group createCopy(Group src) throws Exception {
        Group g = super.createCopy(src);
        g.setUsers(src.getUsers());
        g.setAuthorizations(src.getAuthorizations());
        return g;
    }

    @Override
    protected void assertDefaultValues(Group group) {
        super.assertDefaultValues(group);
        assertNull(group.getUsers());
        assertNull(group.getAuthorizations());
    }

    @Override
    protected void assertEntityEquals(Group e1, Group e2, boolean skipTimestamp) {
        super.assertEntityEquals(e1, e2, skipTimestamp);
        assertCollectionPermutation(e1.getUsers(),e2.getUsers());
        assertCollectionPermutation(e1.getAuthorizations(),e2.getAuthorizations());
    }

    @Override
    protected String getUserFriendlyName() {
        return Messages.NAME_GROUP.getText();
    }
/* ********* Test setup and cleanup *********** */

    @BeforeClass
    public static void setupData() throws Exception {
        user1 = createUser();
        user2 = createUser();
        auth1 = createAuth();
        auth2 = createAuth();
    }

    @AfterClass
    public static void cleanupData() throws Exception {
        user1.delete();
        user1 = null;
        user2.delete();
        user2 = null;
        auth1.delete();
        auth1 = null;
        auth2.delete();
        auth2 = null;
    }

    private static Authorization createAuth() throws PersistenceException {
        Authorization a = new Authorization();
        a.setName(randomString());
        a.save();
        return a;
    }

    private static User createUser() throws PersistenceException {
        User u = new User();
        u.setName(randomString());
        u.save();
        return u;
    }

    private static User user1;
    private static User user2;
    private static Authorization auth1;
    private static Authorization auth2;
}
