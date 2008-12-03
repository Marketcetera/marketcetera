package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import static org.marketcetera.persist.Messages.DEFAULT_ENTITY_NAME;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.*;

import javax.persistence.*;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/* $License$ */
/**
 * Base test class for testing persistent entities.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class EntityTestBase<E extends EntityBase,
        S extends SummaryEntityBase>
        extends PersistTestBase {

    @Before
    public void cleanDatabase() throws Exception {
        deleteAll();
    }

    /**
     * Tests the empty object. Over-ride, to add more tests
     * on the empty object.
     *
     * @throws Exception if there's an error
     */
    @Test
    public void emptyObject() throws Exception {
        E e = createEmpty();
        assertDefaultValues(e);
        assertUnsavedEntity(e);
        assertEquals(0,getAllQuery().fetchCount());
        assertEquals(0,fetchQuery(getAllQuery()).size());
        assertEquals(0,fetchSummaryQuery(getAllQuery()).size());
    }

    /**
     * Runs basic lifecycle tests
     *
     * @throws Exception if there's an error
     */
    @Test
    public void lifecycle() throws Exception {
        E e = createFilled();
        assertUnsavedEntity(e);
        //test the getters and setters by creating a copy
        E copy = createCopy(e);
        assertEntityEquals(e,copy);
        assertUnsavedEntity(copy);
        //Now save the entity
        save(e);
        //Verify that the save changes its state as expected
        assertSavedEntity(e);

        //Verify that it can be retrieved
        E fetched = fetchByID(e.getId());
        assertEntityEquals(e,fetched);
        //Verify Summary View as well
        S sFetched = fetchSummaryByID(e.getId());
        assertEntitySummaryEquals(fetched, sFetched);
        //Do a save without any changes and verify that update count gets incremented
        save(e);
        assertSavedEntity(fetched, e, true);
        fetched = fetchByID(e.getId());
        assertEntityEquals(e,fetched);
        //Test that the saved entity can be updated.
        changeAttributes(fetched);
        save(fetched);
        assertSavedEntity(e,fetched);
        E refetched = fetchByID(e.getId());
        assertEntityEquals(fetched, refetched);
        //Verify Summary View as well
        sFetched = fetchSummaryByID(refetched.getId());
        assertEntitySummaryEquals(refetched, sFetched);
        //Test that the saved entity can be copied
        copy = createCopy(fetched);
        assertEntityEquals(fetched, copy, true);
        //Verify that the copied entity can be saved
        save(copy);
        assertSavedEntity(fetched, copy);
        //refetch and verify
        refetched = fetchByID(copy.getId());
        assertEntityEquals(copy, refetched);
        //Now delete the object
        delete(copy);
        assertUnsavedEntity(copy);
        //Verify that it doesn't exist
        try {
            fetchByID(fetched.getId());
            fail("Entity should've been deleted:"+copy); //$NON-NLS-1$
        }catch(NoResultException expected) {
        }
        //Verify summary view as well
        try {
            fetchSummaryByID(fetched.getId());
            fail("Entity should've been deleted:"+copy); //$NON-NLS-1$
        }catch(NoResultException expected) {
        }
        //find out what happens if we try to save it
        save(copy);
        //Since this is a new entity, it should get a different ID.
        assertFalse(copy.getId() == fetched.getId());
        assertSavedEntity(copy);
    }

    /**
     * Verifies that an unsaved entity can be deleted without issues.
     * @throws Exception if there's an error
     */
    @Test
    public void unsavedDelete() throws Exception {
        E e = createFilled();
        delete(e);
    }

    /**
     * Verifies that the object remains same after save.
     * @throws Exception if there's an error
     */
    @Test
    public void objectRemainSame() throws Exception {
        E e = createFilled();
        save(e);
        E f = fetchByID(e.getId());
        save(e);
        // The update count gets incremented some times
        // but not always. Since the object's contents have
        // not been updated, it doesn't really matter if the
        // update count has been updated. If the update count
        // does get updated, the user may get a spurious
        // dirty object write error, when its not needed.
        assertSavedEntity(f,e,true);
    }

    /**
     * Verifies that the entity ID remains the same after save
     * @throws Exception if there's an error
     */
    @Test
    public void idRemainSame() throws Exception {
        E e = createFilled();
        save(e);
        long id = e.getId();
        save(e);
        assertEquals(id,e.getId());
    }

    /**
     * Verifies that the entity is not cached.
     * @throws Exception if there's an error
     */
    @Test
    public void caching() throws Exception {
        E e = createFilled();
        save(e);
        assertSavedEntity(e);
        S s = fetchSummaryByID(e.getId());
        assertEntitySummaryEquals(e,s);
        S s2 = fetchSummaryByID(e.getId());
        assertNotSame(s,s2);
    }

    /**
     * Verifies that dirty writes to the entity fail
     * @throws Exception if there's an error
     */
    @Test
    public void dirtyWrites() throws Exception {
        E e = createFilled();
        save(e);
        assertSavedEntity(e);
        E fetched = fetchByID(e.getId());
        assertEntityEquals(e,fetched);
        changeAttributes(e);
        //save the entity again to up its update count
        save(e);
        assertTrue(e.getUpdateCount() > fetched.getUpdateCount());
        changeAttributes(fetched);
        //try to save the dirty entity
        //should fail as its a dirty instance
        try {
            save(fetched);
            fail("This test should've failed"); //$NON-NLS-1$
        } catch (OptimisticLockException expected) {
            assertEquals(new I18NBoundMessage1P(Messages.OPTMISTIC_LOCK_ERROR,
                    getUserFriendlyName()).getText(),
                    expected.getI18NBoundMessage().getText());
            assertNotNull(expected.getCause());
            assertTrue(expected.getCause() instanceof
                    javax.persistence.OptimisticLockException);
        }
        save(e);
    }

    /**
     * The expected user friendly name for the entity being tested.
     * This method should be over-ridden if the entity defines a
     * custom name message for itself.
     *
     * @return the custom message for entity name
     */
    protected String getUserFriendlyName() {
        return DEFAULT_ENTITY_NAME.getText(getEntityClass().getSimpleName());
    }

    /**
     * Verify the failure when the entity is not found.
     * @throws Exception if there's an error
     */
    @Test(expected = NoResultException.class)
    public void singleByIDNotExists() throws Exception {
        assertFalse(fetchExistsByID(Long.MIN_VALUE));
        fetchByID(Long.MIN_VALUE);
        fail();
    }
    /**
     * Verify the failure when the entity is not found.
     * @throws Exception if there's an error
     */
    @Test(expected = NoResultException.class)
    public void singleByIDSummaryNotExists() throws Exception {
        fetchSummaryByID(Long.MIN_VALUE);
        fail();
    }

    /**
     * Tests performance of fetching the same entity by ID
     * multiple times.
     * @throws Exception if there's an error
     */
    @Test
    @Ignore
    public void idFetchPerformance() throws Exception {
        E e = createFilled();
        save(e);
        assertSavedEntity(e);
        for(int i = 0; i < 10000; i++) {
            fetchSummaryByID(e.getId());
        }
    }

    
    /**
     * Tests multi query ordering.
     * @throws Exception if there's an error
     */
    @Test
    public void multiQueryOrder() throws Exception {
        //Get the list of query order helpers
        List<MultiQueryOrderTestHelper<E, S>> helpers = getOrderTestHelpers();
        //Iterate through each helper
        for(MultiQueryOrderTestHelper<E,S> helper: helpers) {
            //Create test instances for the helper
            for(int i = 0; i < helper.getNumInstances(); i++) {
                E e = createFilled();
                //set the attribute value to the test value.
                helper.setOrderField(e,i);
                save(e);
            }
            //Verify that correct number of instances get created.
            assertEquals(helper.getNumInstances(),getAllQuery().fetchCount());
            //Get the query instance and verify ordering once in regular order
            //And second time in reverse
            for(boolean isReverse:new boolean[]{false,true}) {
                MultipleEntityQuery q = helper.getQuery();
                q.setEntityOrder(helper.getOrder());
                assertEquals(helper.getOrder(), q.getEntityOrder());
                q.setReverseOrder(isReverse);
                //Test paging, set page to a random size between the total number
                //of instances and 1
                int pageSize = getGenerator().nextInt(helper.getNumInstances()) + 1;
                q.setMaxResult(pageSize);
                S prev = null;
                for (int startResult = 0; startResult < helper.getNumInstances(); startResult += pageSize) {
                    q.setFirstResult(startResult);
                    List<S> l = fetchSummaryQuery(q);
                    List<E> ld = fetchQuery(q);
                    if(startResult + pageSize > helper.getNumInstances()) {
                        assertTrue(l.size() < pageSize);
                    } else {
                        assertEquals(pageSize,l.size());
                    }
                    assertEquals(l.size(), ld.size());
                    Iterator<E> ldi = ld.iterator();
                    for(S s:l) {
                        if(prev != null) {
                            //Compare adjacent instances for the correct order
                            final String prevValue = helper.getOrderField(prev).toString();
                            final String thisValue = helper.getOrderField(s).toString();
                            if(isReverse) {
                                assertTrue(prevValue +
                                        "!>=" + //$NON-NLS-1$
                                        thisValue,
                                        helper.compareOrderField(prev,s) >= 0);
                            } else {
                                assertTrue(prevValue +
                                        "!<=" + //$NON-NLS-1$
                                        thisValue,
                                        helper.compareOrderField(prev,s) <= 0);
                            }
                        }
                        E e = ldi.next();
                        assertEntitySummaryEquals(e,s);
                        prev = s;
                    }
                }
            }
            //Clean up all the test instances
            deleteAll();
        }
    }

    /**
     * Tests multi query filtering
     * @throws Exception if there's an error
     */
    @Test
    public void multiQueryFilter() throws Exception {
        //Get the list of query filter helpers. 
        List<MultiQueryFilterTestHelper<E,S>> helpers = getFilterTestHelpers();
        //Iterate through all the helpers
        for(MultiQueryFilterTestHelper<E,S> helper: helpers) {
            //Create test entity instances for testing.
            for(int i = 0; i < helper.getNumInstances(); i++) {
                E e = createFilled();
                //Set the attribute value to the test value
                helper.setField(e,i);
                save(e);
            }
            //Get Query instance
            MultipleEntityQuery query = helper.getQuery();
            //Get all the filters that need to be tested
            MultiQueryFilterTestHelper.FilterPair[] filters = helper.getFilters();
            //Apply each filter to the query and test them.
            for(MultiQueryFilterTestHelper.FilterPair filter: filters) {
                helper.applyFilter(query,filter.getFirstMember());
                //Verify that number of retrieved instances matches the expected count
                assertEquals(filter.getFirstMember().toString(),
                        (long)filter.getSecondMember(),query.fetchCount());
                List<S> l = fetchSummaryQuery(query);
                assertEquals(filter.getFirstMember().toString(),
                        (long)filter.getSecondMember(),l.size());
                //Verify that each retrieved value matches.
                for(S s:l) {
                    assertTrue(filter.getFirstMember().toString() + s,
                            helper.matches(filter.getFirstMember(),s));
                }
            }
            //Clean up all the test instances
            deleteAll();
        }
    }

    /**
     * Verifies the multi query defaults.
     * @throws Exception if there's an error
     */
    @Test
    public void multiQueryDefaults() throws Exception {
        MultipleEntityQuery q = getAllQuery();
        assertQueryDefaults(q);
    }

    /**
     * Verifies the default values. Over-ride this method to
     * verify the default values of entity attributes
     * @param q the query whose default values need to be
     * verified
     */
    protected void assertQueryDefaults(MultipleEntityQuery q) {
        assertEquals(-1,q.getFirstResult());
        assertEquals(-1,q.getMaxResult());
        assertFalse(q.isReverseOrder());
    }

    /**
     * Verifies the default values. Over-ride this method to
     * verify the default values of entity attributes.
     * @param e the entity.
     */
    protected void assertDefaultValues(E e) {
        assertFalse(e.isPersistent());
        assertTrue(String.valueOf(e.getLastUpdated()), e.getLastUpdated() == null);
        assertTrue(String.valueOf(e.getUpdateCount()), e.getUpdateCount() == -1);
    }

    /**
     * Verifies the state of the entity after its been saved.
     * You may need to over-ride this method, if you defined your entity
     * such that its changes its state in a testable way when its saved
     *
     * @param e the entity after its been saved.
     *
     * @throws Exception if there's an error
     */
    protected void assertSavedEntity(E e) throws Exception {
        assertSavedEntity(null,e);
    }
    /**
     * Verifies the state of the entity after its been updated.
     * This method ensures that the update count of the entity
     * is greater than the previous instance's value.
     * Invoking this method is the same as invoking
     * <code>assertSavedEntity(E, E, false)</code>
     *
     * @param prev the previous copy of the entity.
     * @param e the recently saved entity
     *
     * @throws Exception if there's an error
     */
    protected void assertSavedEntity(E prev, E e) throws Exception {
        assertSavedEntity(prev,e,false);
    }

    /**
     * Verifies the state of the entity after its been updated.
     *
     * @param prev the previous copy of the entity.
     * @param e the recently saved entity
     * @param equalUpdateCount if the update count of two entities
     * can be equal
     *
     * @throws Exception if there's an error
     */
    protected void assertSavedEntity(E prev, E e, boolean equalUpdateCount)
            throws Exception {
        assertTrue(fetchExistsByID(e.getId()));
        assertTrue(e.isPersistent());
        assertTrue(String.valueOf(e.getUpdateCount()), e.getUpdateCount() >= 0);
        assertNotNull(e.getLastUpdated());
        if(prev != null) {
            assertEquals(prev.getId(),e.getId());
            if (equalUpdateCount) {
                assertTrue(e.getUpdateCount() >= prev.getUpdateCount());
                assertTrue(prev.getLastUpdated().compareTo(e.getLastUpdated()) <= 0);
            } else {
                assertTrue(e.getUpdateCount() > prev.getUpdateCount());
                assertTrue(prev.getLastUpdated().compareTo(e.getLastUpdated()) < 0);
            }
        }
        // Sleep to get adequate difference between
        // last updated timestamp values
        Thread.sleep(1000);
    }

    /**
     * Verifies the state of the entity when its not saved.
     * You may need to over-ride this method, if you defined your entity
     * such that its changes its state in a testable way when its saved
     *
     * @param e the entity instance
     */
    protected void assertUnsavedEntity(E e) {
        assertFalse(e.isPersistent());
        assertTrue(String.valueOf(e.getUpdateCount()), e.getUpdateCount() == -1);
        assertTrue(String.valueOf(e.getLastUpdated()), e.getLastUpdated() == null);
    }

    /**
     * Verifies that the attribute values of the supplied instances
     * are equal. This method should be over-ridden to test the attributes
     * added in the subclasses.
     * Since this method operates on the detail view of the entity
     * it should test the lazy loaded attributes of the entity.
     *
     * @param e1 the first entity instance
     * @param e2 the second entity instance
     * @param skipTimestamp if timestamp comparison should be skipped
     */
    protected void assertEntityEquals(E e1, E e2, boolean skipTimestamp) {
        assertEquals(e1.getId(),e2.getId());
        assertEquals(e1.getUpdateCount(), e2.getUpdateCount());
        if (!skipTimestamp) {
            assertCalendarEquals(e1.getLastUpdated(),
                    e2.getLastUpdated(), TemporalType.TIMESTAMP);
        }
    }
    /**
     * Verifies that the attribute values of the supplied instances
     * are equal. Invoking this method is the same as invoking
     * {@link #assertEntityEquals(EntityBase, EntityBase, boolean)}
     * with the last parameter as false.
     *
     * @param e1 the first entity instance
     * @param e2 the second entity instance
     */
    protected final void assertEntityEquals(E e1, E e2) {
        assertEntityEquals(e1,e2,false);
    }
    /**
     * Verifies that the attribute values of the supplied instances
     * are equal. This method should be over-ridden to test the attributes
     * added in the subclasses.
     *
     * @param e the entity detail instance
     * @param s the entity summary view instance
     */
    protected void assertEntitySummaryEquals(E e, S s) {
        assertEquals(e.getId(),s.getId());
        assertEquals(e.getUpdateCount(), s.getUpdateCount());
    }

    /**
     * This method should be over-ridden to save the entity
     * being tested.
     * @param e the entity instance.
     * @throws Exception if there's an error
     */
    protected abstract void save(E e) throws Exception;

    /**
     * This method should be over-ridden to delete the entity
     * being tested.
     * @param e the entity instance.
     * @throws Exception if there's an error
     */
    protected abstract void delete(E e)throws Exception;

    /**
     * This method should be over-ridden to delete
     * all instances of the entity from the database.
     * @throws Exception if there's an error
     */
    protected abstract void deleteAll() throws Exception;

    /**
     * Fetches the Entity given the ID.
     *
     * @param id the entity ID.
     *
     * @return the entity instance, if found.
     * 
     * @throws NoResultException if an entity with that ID wasn't found
     */
    protected abstract E fetchByID(long id) throws Exception;

    /**
     * returns true if the entity with the supplied ID exists.
     *
     * @param id the entity ID
     *
     * @return true if the entity exists.
     * 
     * @throws Exception if there's an error
     */
    protected abstract boolean fetchExistsByID(long id) throws Exception;

    /**
     * Fetches the summary view of the entity given the ID.
     * In case the entity doesn't have a summary view, the return
     * value is the same as {@link #fetchByID(long)}
     *
     * @param id the entity ID.
     *
     * @return the summary view of the entity, if found.
     *
     * @throws NoResultException if an entity with that ID wasn't found
     */
    protected abstract S fetchSummaryByID(long id) throws Exception;

    /**
     * Fetches the summary view by executing the supplied query.
     * 
     * @param query the query to execute
     *
     * @return the list of summary views obtained by running the query.
     *
     * @throws Exception if there's an error
     */
    protected abstract List<S> fetchSummaryQuery(MultipleEntityQuery query)
            throws Exception;
    /**
     * Fetches the entity by executing the supplied query.
     *
     * @param query the query to execute
     *
     * @return the list of entities obtained by running the query.
     * 
     * @throws Exception if there's an error
     */
    protected abstract List<E> fetchQuery(MultipleEntityQuery query)
            throws Exception;

    /**
     * Returns a query that will retrieve all the entities when executed.
     *
     * @return a query to retrieve all the entities.
     *
     * @throws Exception if there's an error
     */
    protected abstract MultipleEntityQuery getAllQuery() throws Exception;

    /**
     * Create an empty instance of the entity
     * 
     * @return an empty instance
     *
     * @throws Exception if there's an error
     */
    protected abstract E createEmpty() throws Exception;

    /**
     * Creates an object with random filled values.
     * Do ensure that two invocations of this method
     * within the same test do not result in objects
     * that cannot be saved because of constraint
     * violation exceptions.
     *
     * @return the filled entity.
     * 
     * @throws Exception if there's an error
     */
    protected abstract E createFilled() throws Exception;

    /**
     * Updates the attributes of the supplied entity for
     * testing updates to it. The updated attributes should
     * not change the identity of the supplied entity.
     *
     * @param e the supplied entity.
     */
    protected abstract void changeAttributes(E e);

    /**
     * Returns the class that represents the entity being tested.
     *
     * @return the class for the entity being tested
     */
    protected abstract Class<E> getEntityClass();

    /**
     * The class for the multi entity query for the entity being tested
     *
     * @return the class for multi entity query of the entity being tested.
     */
    protected abstract Class<? extends MultipleEntityQuery> getMultiQueryClass();

    /**
     * Returns a list of multiquery ordering test helpers.
     * There should be one instance per available EntityOrder for the
     * multiquery for the entity being tested.
     * 
     * @return a list of multiquery ordering test helpers.
     *
     * @throws Exception if there's an error
     */
    protected List<MultiQueryOrderTestHelper<E,S>> getOrderTestHelpers()
            throws Exception {
        LinkedList<MultiQueryOrderTestHelper<E, S>> l =
                new LinkedList<MultiQueryOrderTestHelper<E, S>>();
        l.add(new MultiQueryOrderLastUpdatedTestHelper<E,S>(
                MultipleEntityQuery.BY_LAST_UPDATED,getEntityClass(),
                EntityBase.ATTRIBUTE_LAST_UPDATED,getAllQuery()));
        return l;
    }

    /**
     * Returns a list of multiquery filtering test helpers
     *
     * @return a list of multiquery filtering test helpers
     * 
     * @throws Exception if there's an error
     */
    protected List<MultiQueryFilterTestHelper<E,S>> getFilterTestHelpers()
            throws Exception {
        LinkedList<MultiQueryFilterTestHelper<E, S>> helpers = new LinkedList<MultiQueryFilterTestHelper<E, S>>();
        helpers.add(dateFilterHelper(EntityBase.ATTRIBUTE_LAST_UPDATED, "updatedAfterFilter", true));
        helpers.add(dateFilterHelper(EntityBase.ATTRIBUTE_LAST_UPDATED, "updatedBeforeFilter", false));
        return helpers;
    }

    /**
     * Create copy of the supplied object. This
     * method should be over-ridden to copy all the pertinent
     * fields of the entity.
     * 
     * @param src the source object.
     *
     * @return the copy of the source.
     *
     * @throws Exception if there's an error
     */
    protected E createCopy(E src) throws Exception {
        E copy = createEmpty();
        copy.setId(src.getId());
        copy.setUpdateCount(src.getUpdateCount());
        return copy;
    }

    /**
     * Creates an instance of order test helper for string attributes
     *
     * @param order The entity order instance
     * @param attributeName The attribute name
     *
     * @return the order test helper instance.
     *
     * @throws Exception if there was an error
     */
    protected final MultiQueryOrderTestHelper<E,S> stringOrderHelper(
            EntityOrder order, String attributeName) throws Exception {
        return new MultiQueryOrderStringTestHelper<E, S>(order,
                getEntityClass(), attributeName, getAllQuery());
    }

    /**
     * Creates an instance of filter test helper for string attributes.
     *
     * @param attributeName the attribute name
     * @param filterName the filter attribute name on the multi query
     *
     * @return the filter test helper instance
     *
     * @throws Exception if there was an error
     */
    protected final MultiQueryFilterTestHelper<E,S> stringFilterHelper(
            String attributeName, String filterName) throws Exception {
        return new MultiQueryStringFilterTestHelper<E,S>(
                getEntityClass(), attributeName, getMultiQueryClass(),
                filterName, getAllQuery());
    }

    /**
     * Creates an instance of filter test helper for date attributes.
     *
     * @param attributeName the attribute name
     * @param filterName the filter attribute name on the multi query
     * @param isAfter if the filter matches dates after the filter date.
     *
     * @return the filter test helper instance
     *
     * @throws Exception if there was an error
     */
    protected final MultiQueryFilterTestHelper<E,S> dateFilterHelper(
            String attributeName, String filterName, boolean isAfter)
            throws Exception {
        return new MultiQueryDateFilterTestHelper<E,S>(
                getEntityClass(), attributeName, getMultiQueryClass(),
                filterName, getAllQuery(), isAfter);
    }
    /**
     * Creates an instance of filter test helper for boolean attributes.
     *
     * @param attributeName the attribute name
     * @param filterName the filter attribute name on the multi query
     *
     * @return the filter test helper instance
     *
     * @throws Exception if there was an error
     */
    protected final MultiQueryFilterTestHelper<E,S> booleanFilterHelper(
            String attributeName, String filterName) throws Exception {
        return new MultiQueryBooleanFilterTestHelper<E,S>(
                getEntityClass(), attributeName, getMultiQueryClass(),
                filterName, getAllQuery());
    }

    /**
     * Verfies that an attempt to save the supplied entity instance
     * fails with the supplied exception type.
     *
     * @param e the entity instance to save
     * @param clazz the expected exception class
     * @param message the optional message on the exception class.
     */
    protected void assertSaveFailure(final E e,
                                   Class<? extends PersistenceException> clazz,
                                   I18NBoundMessage message) {
        assertFailure(new Callable<Object>(){
            public Object call() throws Exception {
                save(e);
                return null;
            }
        }, clazz, message);
    }

    /**
     * Verifies that the supplied code block fails with the supplied failure
     *
     * @param callable the code block to test
     * @param clazz the exception class of the expected failure
     * @param message the expected exception message
     */
    protected static void assertFailure(Callable<?> callable,
                                        Class<? extends PersistenceException> clazz,
                                        I18NBoundMessage message) {
        try {
            callable.call();
            fail("expected to fail"); //$NON-NLS-1$
        } catch(Exception ex) {
            assertTrue(ex.toString(), clazz.isInstance(ex));
            PersistenceException pe = (PersistenceException) ex;
            if (message != null) {
                assertEquals(message.getMessage(),
                        pe.getI18NBoundMessage().getMessage());
                assertArrayEquals(message.getParams(),
                        pe.getI18NBoundMessage().getParams());
            }
        }
    }
}
