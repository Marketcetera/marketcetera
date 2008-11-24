package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;

import java.util.List;

/* $License$ */
/**
 * Tests persistence of Authorization Entity
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AuthorizationTest extends
        CorePersistNDTestBase<Authorization,Authorization> {

/* ************************Implement necessary operations************* */
    protected Authorization fetchByName(String name) throws Exception {
        return new SingleAuthorizationQuery(name).fetch();
    }

    protected Authorization fetchSummaryByName(String name)
            throws Exception {
        return new SingleAuthorizationQuery(name).fetchSummary();
    }

    protected void save(Authorization authorization) throws Exception {
        authorization.save();
    }

    protected void delete(Authorization auth) throws Exception {
        auth.delete();
    }

    protected void deleteAll() throws Exception {
        MultiAuthorizationQuery.all().delete();
    }

    protected Authorization fetchByID(long id) throws Exception {
        return new SingleAuthorizationQuery(id).fetch();
    }

    protected Authorization fetchSummaryByID(long id) throws Exception {
        return new SingleAuthorizationQuery(id).fetchSummary();
    }

    protected boolean fetchExistsByName(String name) throws Exception {
        return new SingleAuthorizationQuery(name).exists();
    }

    protected boolean fetchExistsByID(long id) throws Exception {
        return new SingleAuthorizationQuery(id).exists();
    }

    protected Authorization createEmpty() {
        return new Authorization();
    }

    protected Class<Authorization> getEntityClass() {
        return Authorization.class;
    }

    protected Class<? extends MultipleEntityQuery>
            getMultiQueryClass() {
        return MultiAuthorizationQuery.class;
    }

    protected List<Authorization> fetchSummaryQuery(
            MultipleEntityQuery query) throws Exception {
        return ((MultiAuthorizationQuery)query).fetch();
    }

    protected List<Authorization> fetchQuery(
            MultipleEntityQuery query) throws Exception {
        return ((MultiAuthorizationQuery)query).fetch();
    }

    protected MultipleEntityQuery getAllQuery() {
        return MultiAuthorizationQuery.all();
    }
}
