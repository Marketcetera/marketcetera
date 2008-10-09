package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.Messages.ENTITY_EXISTS_GENERIC_ERROR;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.BeforeClass;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/* $License$ */
/**
 * Test base class for testing many to many relationships.
 * The documentation in this class uses the term owner / container
 * interchangeably to refer to the entity on the owning side
 * of the many to many relationship per JPA contract.
 * The other side is refered to as the contained entity.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@SuppressWarnings("unchecked") //$NON-NLS-1$
public abstract class ManyToManyTestBase<SE extends SummaryEntityBase,
        E extends SE,
        SO extends SummaryEntityBase,
        O extends SO> extends PersistTestBase {

    @BeforeClass
    public static void springSetup() throws Exception {
            springSetup(new String[]{"persist.xml"}); //$NON-NLS-1$
    }

    /**
     * Tests the lifecycle of many to many relationship
     *
     * @throws Exception if there was an error
     */
    @Test
    public void lifecycle() throws Exception {
        //Create a bunch of objects.
        E e1 = newFilled();
        E e2 = newFilled();
        E e3 = newFilled();
        O o1 = newOwner();
        O o2 = newOwner();
        O o3 = newOwner();

        //Now add a relationship between the o1, e1 and verify all
        createRelationship(o1,e1);
        assertRelationship(o1,e1);
        assertRelationship(o2);
        assertRelationship(o3);
        assertReverseRelationship(e1,o1);
        assertReverseRelationship(e2);
        assertReverseRelationship(e3);
        //Add another relationship between o1,e2 and verify all
        createRelationship(o1,e1,e2);
        assertRelationship(o1,e1,e2);
        assertRelationship(o2);
        assertRelationship(o3);
        assertReverseRelationship(e1,o1);
        assertReverseRelationship(e2,o1);
        assertReverseRelationship(e3);
        //Add another relationship between o2,e2,e3 and verify all
        createRelationship(o2,e2,e3);
        assertRelationship(o1,e1,e2);
        assertRelationship(o2,e2,e3);
        assertRelationship(o3);
        assertReverseRelationship(e1,o1);
        assertReverseRelationship(e2,o1,o2);
        assertReverseRelationship(e3,o2);
        //Add another relationship between o3,e1,e2,e3 and verify all
        createRelationship(o3,e1,e2,e3);
        assertRelationship(o1,e1,e2);
        assertRelationship(o2,e2,e3);
        assertRelationship(o3,e1,e2,e3);
        assertReverseRelationship(e1,o1,o3);
        assertReverseRelationship(e2,o1,o2,o3);
        assertReverseRelationship(e3,o2,o3);
        //Reset relationship between o3,e3 and verify all
        createRelationship(o3,e3);
        assertRelationship(o1,e1,e2);
        assertRelationship(o2,e2,e3);
        assertRelationship(o3,e3);
        assertReverseRelationship(e1,o1);
        assertReverseRelationship(e2,o1,o2);
        assertReverseRelationship(e3,o2,o3);
        //Reset o1 relationship and verify all
        createRelationship(o1);
        assertRelationship(o1);
        assertRelationship(o2,e2,e3);
        assertRelationship(o3,e3);
        assertReverseRelationship(e1);
        assertReverseRelationship(e2,o2);
        assertReverseRelationship(e3,o2,o3);
        //Reset all relationships and verify all
        createRelationship(o2);
        createRelationship(o3);
        assertRelationship(o1);
        assertRelationship(o2);
        assertRelationship(o3);
        assertReverseRelationship(e1);
        assertReverseRelationship(e2);
        assertReverseRelationship(e3);
        //Delete all of them
        delete(e1);
        delete(e2);
        delete(e3);
        deleteOwner(o1);
        deleteOwner(o2);
        deleteOwner(o3);
    }

    /**
     * Verifies that the contained entity can be deleted
     * or not while its related to owner, depending on the
     * value returned by {@link #isContainedDeleteAllowedWhenRelated()}
     * 
     * @throws Exception if there's an error
     */
    @Test
    public void deleteContainedWhileRelated() throws Exception {
        if (isContainedDeleteAllowedWhenRelated()) {
            //Verify individual delete
            E e1 = newFilled();
            E e2 = newFilled();
            O o = newOwner();
            createRelationship(o,e1,e2);
            assertRelationship(o,e1,e2);
            assertReverseRelationship(e1,o);
            assertReverseRelationship(e2,o);
            //verify that we can delete it
            delete(e1);
            //verify that its deleted
            doesNotExist(e1);
            //refresh owner
            o = fetchOwner(o.getId());
            //verify that other relationships are intact
            assertRelationship(o,e2);
            assertReverseRelationship(e2,o);
            e1 = newFilled();
            createRelationship(o,e1,e2);
            assertRelationship(o,e1,e2);
            assertReverseRelationship(e1,o);
            assertReverseRelationship(e2,o);
            //Now delete all
            deleteAll();
            doesNotExist(e1);
            doesNotExist(e2);
            //refresh owner
            o = fetchOwner(o.getId());
            assertRelationship(o);
            deleteOwner(o);
        } else {
            //Create a relationship
            E e = newFilled();
            O o = newOwner();
            createRelationship(o,e);
            assertRelationship(o,e);
            assertReverseRelationship(e,o);
            //verify that the contained entity cannot be deleted
            try {
                delete(e);
                fail("Delete should've failed"); //$NON-NLS-1$
            } catch(RollbackException expected) {
            }
            assertContainedEquals(e,fetch(e.getId()));
            //Remove relationship and verify that it can be deleted.
            createRelationship(o);
            assertRelationship(o);
            assertReverseRelationship(e);
            delete(e);
            doesNotExist(e);
            //Recreate relationship
            e = newFilled();
            createRelationship(o,e);
            assertRelationship(o,e);
            assertReverseRelationship(e,o);
            //verify that the contained entity cannot be via bulk delete
            try {
                deleteAll();
                fail("Delete should've failed"); //$NON-NLS-1$
            } catch(EntityExistsException expected) {
                assertEquals(ENTITY_EXISTS_GENERIC_ERROR,
                        expected.getI18NBoundMessage().getMessage());
            }
            assertContainedEquals(e,fetch(e.getId()));
            //Remove relationship and verify that it can be deleted.
            createRelationship(o);
            assertRelationship(o);
            assertReverseRelationship(e);
            deleteAll();
            doesNotExist(e);
        }
    }
    /**
     * Verifies that the owner entity can be deleted
     * while its related to contained entities.
     *
     * @throws Exception if there's an error
     */
    @Test
    public void deleteOwnerWhileRelated() throws Exception {
        E e = newFilled();
        O o1 = newOwner();
        O o2 = newOwner();
        createRelationship(o1,e);
        createRelationship(o2,e);
        assertRelationship(o1,e);
        assertRelationship(o2,e);
        assertReverseRelationship(e,o1,o2);
        //verify we can delete one owner
        deleteOwner(o1);
        ownerDoesNotExist(o1);
        //verify other relationships are intact
        assertRelationship(o2,e);
        assertReverseRelationship(e,o2);
        o1 = newOwner();
        createRelationship(o1,e);
        assertRelationship(o1,e);
        assertRelationship(o2,e);
        assertReverseRelationship(e,o1,o2);
        //Now delete all owners
        deleteOwnerAll();
        ownerDoesNotExist(o1);
        ownerDoesNotExist(o2);
        assertReverseRelationship(e);
        delete(e);
    }

    private void ownerDoesNotExist(O o1) throws Exception {
        try {
            fetchOwner(o1.getId());
            fail("Should not exist"); //$NON-NLS-1$
        } catch(NoResultException expected) {
        }
    }

    private void doesNotExist(E e1) throws Exception {
        try {
            fetch(e1.getId());
            fail("Should not exist"); //$NON-NLS-1$
        } catch(NoResultException expected) {
        }
    }

    private void createRelationship(O o, E... es) throws Exception {
        HashSet<SE> contained = new HashSet<SE>();
        contained.addAll(Arrays.asList(es));
        set(contained,o);
        saveOwner(o);
    }
    private void assertRelationship(O o, E... entities) throws Exception {
        O fetchedOwner = fetchOwner(o.getId());
        //Verify owner's persisted state matches
        assertOwnerEquals(o,fetchedOwner);
        Set<SE> contained = getContained(fetchedOwner);
        if(entities.length == 0) {
            assertTrue(contained.isEmpty());
            return;
        }
        HashSet<SE> hse = new HashSet<SE>(Arrays.asList(entities));
        //verify that the contents match, although this test
        //is redundant
        assertCollectionPermutation(contained, hse);
        //Verify the summary views of the contained entities
        for(SE entity: contained) {
            verifySummary(entity);
        }
        //Verify the container relation from contained entities.
        if (isContainerAvailable()) {
            for(E entity:entities) {
                E fetchedEntity = fetch(entity.getId());
                Set<SO> containers = getContainers(fetchedEntity);
                assertFalse(containers.isEmpty());
                //Verify that the container is found and that its
                //summary view is complete and correct
                assertOwnerSummaryEquals(fetchedOwner,find(containers,
                        fetchedOwner.getId()));
            }
        }
    }
    private void assertReverseRelationship(E entity, O... owners)
            throws Exception {
        //Refetch the entity to get its current state
        if(isContainerAvailable()) {
            E fetchedEntity = fetch(entity.getId());
            Set<SO> containers = getContainers(fetchedEntity);
            assertEquals(owners.length,containers.size());
            for(O owner:owners) {
                //verify that each owner's summary view matches
                //the one obtained from the entity
                assertOwnerSummaryEquals(fetchOwner(owner.getId()),
                        find(containers,owner.getId()));
            }
        }
    }

    private <V extends SummaryEntityBase> V find(Set<V> s, long id) {
        for(V v:s) {
            if(id == v.getId()) {
                return v;
            }
        }
        fail("id not found in"+s); //$NON-NLS-1$
        return null;
    }

    private void verifySummary(SE e) throws Exception {
        SE ne = fetch(e.getId());
        assertContainedEquals(ne,e);
    }

    /**
     * This method should be over-ridden to compare all
     * the fields of the supplied entities.
     * @param e1 the first entity instance
     * @param e2 the second entity instance
     */
    protected void assertContainedEquals(SE e1, SE e2) {
        assertEquals(e1.getId(),e2.getId());
        assertEquals(e1.getUpdateCount(),e2.getUpdateCount());
    }

    /**
     * This method should be over-ridden to compare the lazy
     * loaded fields.
     *
     * @param o1 the first entity instance
     * @param o2 the second entity instance
     */
    protected void assertOwnerEquals(O o1, O o2) {
        assertOwnerSummaryEquals(o1,o2);
    }
    /**
     * This method should be over-ridden to compare all
     * the fields of the supplied entities, except for
     * the lazy loaded fields.
     *
     * @param o1 the first entity instance
     * @param o2 the second entity instance
     */
    protected void assertOwnerSummaryEquals(O o1, SO o2) {
        assertEquals(o1.getId(),o2.getId());
        assertEquals(o1.getUpdateCount(),o2.getUpdateCount());
    }

    private E newFilled() throws Exception {
        E e = save(createFilled());
        Set<SO> so = getContainers(e);
        assertNull(so);
        return e;
    }

    private O newOwner() throws Exception {
        O o = saveOwner(createFilledOwner());
        assertNull(getContained(o));
        return o;
    }

    /**
     * Returns true if the contained entity is aware of
     * its container, by default this method returns true.
     * If this method is over-ridden to return false,
     * {@link #getContainers(SummaryEntityBase)} should
     * be implemented to return a null value.
     *
     * @return true if the contained entity is aware of its container.
     */
    public boolean isContainerAvailable() {
        return true;
    }

    /**
     * Returns true if the contained entity can be deleted while
     * its related to the owning entity.
     * Do note that by default JPA doesn't allow contained entity
     * to be deleted while its related to to the owner. And hence
     * this method returns false by default.
     * In certain cases, the contained entity may have a special
     * implementation such that it resets the relationship on the
     * owner to allow the delete of the container, in which case
     * this method should be over-ridden to return true.
     *
     * @return true if the contained entity can be deleted while
     * its related to owner(s).
     */
    public boolean isContainedDeleteAllowedWhenRelated() {
        return false;
    }

    /**
     * Creates the contained entity filled with minimal data so
     * that it can be saved.
     *
     * @return the contained entity.
     */
    protected abstract E createFilled();
    /**
     * Creates the owner entity filled with minimal data so
     * that it can be saved.
     *
     * @return the owner entity.
     */
    protected abstract O createFilledOwner();

    /**
     * Sets the supplied contained entities as related to the
     * supplied container / owner entity.
     *
     * @param contained the set of contained entities.
     * @param container the owner / container entity
     *
     * @throws Exception if there was an error
     */
    protected abstract void set(Set<SE> contained, O container)
            throws Exception;

    /**
     * Saves the contained entity
     *
     * @param e the contained entity
     *
     * @return the supplied entity
     *
     * @throws Exception if there was an error
     */
    protected abstract E save(E e) throws Exception;
    /**
     * Saves the owner entity
     *
     * @param o the owner entity
     *
     * @return the supplied entity
     *
     * @throws Exception if there was an error
     */
    protected abstract O saveOwner(O o) throws Exception;

    /**
     * Deletes the supplied contained entity.
     *
     * @param e the supplied entity
     *
     * @throws Exception if there was an error
     */
    protected abstract void delete(E e) throws Exception;
    /**
     * Deletes the supplied owner entity.
     *
     * @param o the supplied entity
     *
     * @throws Exception if there was an error
     */
    protected abstract void deleteOwner(O o) throws Exception;

    /**
     * Deletes all contained entities.
     *
     * @return the count of entities deleted
     *
     * @throws Exception if there was an error
     */
    protected abstract int deleteAll() throws Exception;
    /**
     * Deletes all owner entities.
     *
     * @return the count of entities deleted
     *
     * @throws Exception if there was an error
     */
    protected abstract int deleteOwnerAll() throws Exception;

    /**
     * Fetches the contained entity, given its ID
     *
     * @param id the ID of the contained entity.
     *
     * @return the fetched entity.
     *
     * @throws Exception if there was an error
     */
    protected abstract E fetch(long id) throws Exception;
    /**
     * Fetches the owner entity, given its ID
     *
     * @param id the ID of the owner entity.
     *
     * @return the fetched entity.
     *
     * @throws Exception if there was an error
     */
    protected abstract O fetchOwner(long id) throws Exception;

    /**
     * Returns the set of contained entities from the supplied
     * owner instance
     *
     * @param o the instance of the owner entity.
     *
     * @return the set of contained entities.
     *
     * @throws Exception if there was an error
     */
    protected abstract Set<SE> getContained(O o) throws Exception;
    /**
     * Returns the set of container entities from the supplied
     * contained instance
     *
     * @param e the instance of the contained entity.
     *
     * @return the set of container entities.
     * 
     * @throws Exception if there was an error
     */
    protected abstract Set<SO> getContainers(E e) throws Exception;
}
