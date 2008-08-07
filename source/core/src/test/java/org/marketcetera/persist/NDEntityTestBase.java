package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import static org.marketcetera.persist.Messages.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/* $License$ */
/**
 * Base classes for testing entities that extend
 * {@link org.marketcetera.persist.NDEntityBase}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class NDEntityTestBase<E extends NDEntityBase,
        S extends SummaryNDEntityBase> extends
        EntityTestBase<E,S> {
    /**
     * Verify the failure when the entity lookup by name
     * returns no result.
     *
     * @throws Exception if there's an error
     */
    @Test(expected = NoResultException.class)
    public void singleByNameNotExists() throws Exception {
        String s = "notexists"; //$NON-NLS-1$
        assertFalse(fetchExistsByName(s));
        fetchByName(s);
        fail();
    }
    /**
     * Verify the failure when the entity lookup by name
     * returns no result.
     *
     * @throws Exception if there's an error
     */
    @Test(expected = NoResultException.class)
    public void singleSummaryByNameNotExists() throws Exception {
        fetchSummaryByName("notexists"); //$NON-NLS-1$
        fail();
    }

    /**
     * Verify single query by name
     * @throws Exception if there's an error
     */
    @Test
    public void singleByName() throws Exception {
        E e = createFilled();
        save(e);
        assertSavedEntity(e);
        E fetched = fetchByName(e.getName());
        assertEntityEquals(e,fetched);
        //Verify Summary View as well
        S sFetched = fetchSummaryByName(fetched.getName());
        assertEntitySummaryEquals(fetched, sFetched);
    }

    /**
     * Verify that we get duplicate name constraint violation
     * @throws Exception if there's an error
     */
    @Test
    public void duplicateNameConstraintViolation() throws Exception {
        E e = createFilled();
        save(e);
        assertSavedEntity(e);
        E n = createFilled();
        //Try saving an entity with the duplicate name.
        n.setName(e.getName());
        try {
            save(n);
            fail();
        } catch(EntityExistsException expected) {
            assertEquals(new I18NBoundMessage1P(Messages.ENTITY_EXISTS_INSERT_ERROR,
                    getUserFriendlyName()).getText(),
                    expected.getI18NBoundMessage().getText());
            assertNotNull(expected.getCause());
            assertTrue(expected.getCause() instanceof
                    javax.persistence.EntityExistsException);
        }
        //Verify that the entity state is not
        //dirtied as the result of failed transaction
        assertUnsavedEntity(n);
        n.setName(randomString());
        save(n);
        assertSavedEntity(n);
    }

    /**
     * Verify that name validations are invoked when saving
     * the entity.
     * @throws Exception if there's an error
     */
    @Test
    public void nameValidations() throws Exception {
        //Verify that validation happens on an unsaved entity
        E e = createFilled();
        nameValidationFailures(e);
        //Verify that validations fail on a saved entity.
        //Test the maximum size for the name, while we are at it
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 255; i++) {
            sb.append('a');
        }
        e.setName(sb.toString());
        save(e);
        assertSavedEntity(e);
        nameValidationFailures(e);
    }

    private void nameValidationFailures(E e) {
        e.setName(null);
        assertSaveFailure(e, ValidationException.class,UNSPECIFIED_NAME_ATTRIBUTE);
        e.setName(""); //$NON-NLS-1$
        assertSaveFailure(e,ValidationException.class,UNSPECIFIED_NAME_ATTRIBUTE);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 256; i++) {
            sb.append('a');
        }
        e.setName(sb.toString());
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage1P(NAME_ATTRIBUTE_TOO_LONG,sb.toString()));
        String name = "a12_"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
        name = "a12%"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
        name = "a12#"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
        name = "a12$"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
        name = "a12^"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
        name = "a12?"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
        name = "a12*"; //$NON-NLS-1$
        e.setName(name);
        assertSaveFailure(e,ValidationException.class,
                new I18NBoundMessage2P(NAME_ATTRIBUTE_INVALID,name,
                        NDEntityBase.namePattern.toString()));
    }

    /**
     * Fetches an instance given its name.
     *
     * @param name The entity name
     *
     * @return the retrieved entity
     *
     * @throws Exception if there's an error
     */
    protected abstract E fetchByName(String name) throws Exception;

    /**
     * Returns true if the entity that has the supplied
     * name exists.
     *
     * @param name the entity name
     *
     * @return true if an entity with the supplied name exists
     *
     * @throws Exception if there's an error
     */
    protected abstract boolean fetchExistsByName(String name) throws Exception;
    /**
     * Fetches a summary view of an instance given its name.
     * 
     * @param name The entity name
     *
     * @return the retrieved entity
     *
     * @throws Exception if there's an error
     */
    protected abstract S fetchSummaryByName(String name) throws Exception;

    @Override
    protected void assertDefaultValues(E e) {
        super.assertDefaultValues(e);
        assertNull(e.getName());
        assertNull(e.getDescription());
    }

    @Override
    protected void assertEntityEquals(E e1, E e2, boolean skipTimestamp) {
        super.assertEntityEquals(e1, e2, skipTimestamp);
        assertEquals(e1.getName(),e2.getName());
        assertEquals(e1.getDescription(),e2.getDescription());
    }

    @Override
    protected void assertEntitySummaryEquals(E e, S s) {
        super.assertEntitySummaryEquals(e, s);
        assertEquals(e.getName(),s.getName());
        assertEquals(e.getDescription(),s.getDescription());
    }

    @Override
    protected void assertSavedEntity(E e) throws Exception {
        super.assertSavedEntity(e);
        assertTrue(fetchExistsByName(e.getName()));
    }

    @Override
    protected E createCopy(E src) throws Exception {
        E copy = super.createCopy(src);
        copy.setName(src.getName());
        copy.setDescription(src.getDescription());
        return copy;
    }

    @Override
    protected E createFilled() throws Exception {
        E n = createEmpty();
        n.setName(randomString());
        n.setDescription(randomString());
        return n;
    }

    @Override
    protected void changeAttributes(E e) {
        e.setName(randomString());
        e.setDescription(randomString());
    }

    @Override
    protected void assertQueryDefaults(MultipleEntityQuery q) {
        super.assertQueryDefaults(q);
        MultiNDQuery ndq = (MultiNDQuery) q;
        assertNull(ndq.getNameFilter());
        assertNull(ndq.getDescriptionFilter());
    }

    @Override
    protected List<MultiQueryOrderTestHelper<E, S>> getOrderTestHelpers() throws Exception {
        List<MultiQueryOrderTestHelper<E,S>> l = super.getOrderTestHelpers();
        l.add(stringOrderHelper(MultiNDQuery.BY_NAME,
                NDEntityBase.ATTRIBUTE_NAME));
        l.add(stringOrderHelper(MultiNDQuery.BY_DESCRIPTION,
                NDEntityBase.ATTRIBUTE_DESCRIPTION));
        return l;
    }

    @Override
    protected List<MultiQueryFilterTestHelper<E, S>> getFilterTestHelpers() throws Exception {
        List<MultiQueryFilterTestHelper<E,S>> l = super.getFilterTestHelpers();
        l.add(stringFilterHelper(NDEntityBase.ATTRIBUTE_NAME, "nameFilter")); //$NON-NLS-1$
        l.add(stringFilterHelper(NDEntityBase.ATTRIBUTE_DESCRIPTION, "descriptionFilter")); //$NON-NLS-1$
        return l;
    }
}
