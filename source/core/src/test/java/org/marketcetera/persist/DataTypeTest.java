package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.util.file.CopyCharsUnicodeUtils;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.test.UnicodeData;
import org.marketcetera.util.log.I18NBoundMessage3P;
import static org.marketcetera.persist.Messages.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;

import javax.persistence.TemporalType;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.File;

/* $License$ */
/**
 * Tests various JPA capabilities.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class DataTypeTest extends
        CorePersistNDTestBase<DataTypes,SummaryDataType> {
    private static final double DELTA_DOUBLE = 0.0000000001;
    private static final double DELTA_FLOAT = 0.00001;

    /**
     * Verifies that the queries verify that string parameters
     * are validated to only contain characters that are supported
     * by the database.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void unsupportedCharQueryValidation() throws Exception {
        String s = "mytestname";
        final int errIdx = s.length();
        // mysql version dependency: this piece of code depends on the
        // specific version of mysql and may need to change whenever
        // mysql version is updated
        s += UnicodeData.GOATS_LNB;
        final String expSubString = "tname";
        final String value = s;

        //Verify single entity query
        assertFailure(new Callable<Object>(){
            public Object call() throws Exception {
                fetchByName(value);
                return null;
            }
        },ValidationException.class,
                new I18NBoundMessage3P(UNSUPPORTED_CHARACTER,
                        UnicodeData.GOATS_LNB_CHARS[0], errIdx,
                        expSubString));
        //Verify multi-entity query
        assertFailure(new Callable<Object>(){
            public Object call() throws Exception {
                MultiDataTypesQuery q = MultiDataTypesQuery.all();
                q.setDescriptionFilter(new StringFilter(value));
                q.fetchSummary();
                return null;
            }
        },ValidationException.class,
                new I18NBoundMessage3P(UNSUPPORTED_CHARACTER,
                        UnicodeData.GOATS_LNB_CHARS[0], errIdx,
                        expSubString));
    }

    /**
     * Verifies the string validation failure.
     *
     * @throws Exception if there was an error
     */
    @Test
    public void unsupportedCharSaveValidation() throws Exception {
        //Verifies that validation errors for unsupported Char
        //are correctly thrown during insert / update
        DataTypes d = new DataTypes();
        //append string having surrogate chars.
        String s = "mytestname";
        final int errIdx = s.length();
        s += UnicodeData.GOATS_LNB;
        final String expSubString = "tname";
        d.setName(s);
        assertSaveFailure(d,ValidationException.class,
                new I18NBoundMessage3P(UNSUPPORTED_CHARACTER,
                        UnicodeData.GOATS_LNB_CHARS[0], errIdx,
                        expSubString));
        //reset the value and save the entity
        d.setName(randomString());
        d.save();
        assertSavedEntity(d);
        //verify that the validation happens during update as well
        d.setName(s);
        assertSaveFailure(d,ValidationException.class,
                new I18NBoundMessage3P(UNSUPPORTED_CHARACTER,
                        UnicodeData.GOATS_LNB_CHARS[0], errIdx,
                        expSubString));
    }

    /**
     * Verifies database's lack of support for supplementary characters
     *
     * @throws Exception if there was a failure
     */
    @Test
    public void unsupportedCharacterDBFailure() throws Exception {
        // mysql version dependency: this piece of code depends on the
        // specific version of mysql and may need to change whenever
        // mysql version is updated
        DataTypes d = new DataTypes();
        d.setName(randomString());
        d.setDescription(randomString() + UnicodeData.G_CLEF_MSC);
        // Test for jdbc exception from mysql
        assertSaveFailure(d, PersistenceException.class, UNEXPECTED_ERROR);
    }

    /**
     * Verifies that all the supported characters can be saved
     * and retrieved from the database
     *
     * @throws Exception if there were errors
     */
    @Test
    public void supportedCharacters() throws Exception {
        //choose an increment greater than 1 to make the test run faster.
        final int len = 255;
        String s;
        DataTypes d;
        int iterations = 0;
        // mysql version dependency: this piece of code depends on the
        // specific version of mysql and may need to change whenever
        // mysql version is updated
        while((s = getNextString(len)) != null) {
            d = new DataTypes();
            d.setName(randomString());
            d.setDescription(s);
            d.save();
            assertEntityEquals(d, fetchByName(d.getName()));
            iterations++;
        }
        final int nChars = Character.MAX_VALUE -
                ((Character.MAX_HIGH_SURROGATE - Character.MIN_HIGH_SURROGATE) +
                (Character.MAX_LOW_SURROGATE - Character.MIN_LOW_SURROGATE));
        //verify that we had expected number of iterations
        assertEquals(nChars / len +
                //add an extra iteration if there's a non-zero remainder
                Math.min(nChars % len, 1),
                iterations);
    }

    /**
     * Tests clob database operations
     *
     * @throws Exception if there were errors
     */
    @Test
    public void clob() throws Exception {
        //Create an entity and then read / write the clob
        DataTypes d = createFilled();
        d.save();
        assertSavedEntity(d);
        DataTypes saved = fetchByID(d.getId());
        //Verify that the test file exists
        assertTrue(TEST_UNICODE_FILE.exists());
        //Verify initial value
        File f = File.createTempFile("clob","persist"); //$NON-NLS-1$ //$NON-NLS-2$
        f.deleteOnExit();
        f.delete();
        assertFalse(f.exists());
        d.readFromClob(f.toURI().toURL());
        assertTrue(f.exists());
        assertEquals(0, CopyCharsUnicodeUtils.copy(f.getAbsolutePath(),
                DECODING_STRATEGY).length);
        //Now write stuff to the file
        CopyCharsUnicodeUtils.copy(TEST_UNICODE_FILE.getAbsolutePath(),
                DECODING_STRATEGY, f.getAbsolutePath());
        //make state changes
        changeAttributes(d);
        //Write to the clob
        d.saveAndWriteClob(f.getAbsoluteFile().toURI().toURL());
        assertSavedEntity(saved,d);
        saved = fetchByID(d.getId());
        //Verify that the state changes are saved.
        assertEntityEquals(d,saved);
        //Read stuff back from the clob
        File read = File.createTempFile("clob","persist"); //$NON-NLS-1$ //$NON-NLS-2$
        read.delete();
        read.deleteOnExit();
        assertFalse(read.exists());
        //make state changes
        d.setDescription(randomString());
        d.readFromClob(read.toURI().toURL());
        //verify state changes were not saved
        saved = fetchByID(d.getId());
        assertFalse(saved.getDescription().equals(d.getDescription()));
        assertEquals(saved.getUpdateCount(),d.getUpdateCount());
        //Verify that the clob was correctly read
        assertTrue(read.exists());
        assertArrayEquals(read.getAbsolutePath(),
                CopyCharsUnicodeUtils.copy(f.getAbsolutePath(),
                        DECODING_STRATEGY),
                CopyCharsUnicodeUtils.copy(read.getAbsolutePath(),
                        DECODING_STRATEGY));
        f.delete();
        read.delete();
    }

    /**
     * Tests blob operations.
     *
     * @throws Exception if there was an error
     */
    @Test
    public void blob() throws Exception {
        //Create an entity and then read / write the blob
        DataTypes d = createFilled();
        d.save();
        assertSavedEntity(d);
        DataTypes saved = fetchByID(d.getId());
        //Verify initial value
        File f = File.createTempFile("blob","persist"); //$NON-NLS-1$ //$NON-NLS-2$
        f.deleteOnExit();
        f.delete();
        assertFalse(f.exists());
        d.readFromBlob(f.toURI().toURL());
        assertTrue(f.exists());
        assertEquals(0,f.length());
        //Now write stuff to the file
        CopyCharsUtils.copy(randomString().toCharArray(), f.getAbsolutePath());
        //Make state changes
        changeAttributes(d);
        //Write to the blob
        d.saveAndWriteBlob(f.toURI().toURL());
        assertSavedEntity(saved,d);
        saved = fetchByID(d.getId());
        //Verify that the state changes are saved.
        assertEntityEquals(d,saved);
        //Read stuff back from the blob
        File read = File.createTempFile("blob","persist"); //$NON-NLS-1$ //$NON-NLS-2$
        read.delete();
        read.deleteOnExit();
        assertFalse(read.exists());
        //make state changes.
        d.setDescription(randomString());
        d.readFromBlob(read.toURI().toURL());
        //verify state changes were not saved
        saved = fetchByID(d.getId());
        assertFalse(saved.getDescription().equals(d.getDescription()));
        assertEquals(saved.getUpdateCount(),d.getUpdateCount());
        //Verify that the blob was correctly read.
        assertTrue(read.exists());
        assertEquals(f.length(), read.length());
        assertTrue(Arrays.equals(CopyCharsUtils.copy(f.getAbsolutePath()),
                     CopyCharsUtils.copy(read.getAbsolutePath())));
    }

    /**
     * Verify that we can save all kinds of ascii chars
     */
    @Test
    public void asciiStringSet() throws Exception {
        DataTypes e = createFilled();
        StringBuilder sb = new StringBuilder();
        for(byte b = 0; b < 0x7f; b++) {
            if(!Character.isISOControl(b)) {
                sb.append(b);
            }
        }
        e.setDescription(sb.toString());
        save(e);
        assertSavedEntity(e);
        e = fetchByID(e.getId());
        assertEquals(sb.toString(),e.getDescription());
    }

    /**
     * Validates string filter constructor
     * @throws Exception
     */
    @Test
    public void stringFilterValidations() throws Exception {
        try {
            new StringFilter(null);
            fail();
        } catch(NullPointerException expected) {
        }
        stringFilterValidationFailure(""); //$NON-NLS-1$
        stringFilterValidationFailure("_"); //$NON-NLS-1$
        stringFilterValidationFailure("%"); //$NON-NLS-1$
        stringFilterValidationFailure("abd\tdfe"); //$NON-NLS-1$
    }

    private void stringFilterValidationFailure(String value) {
        try {
            new StringFilter(value);
            fail(value);
        } catch(ValidationException expected) {
            assertEquals(Messages.INVALID_STRING_FILTER,
                    expected.getI18NBoundMessage().getMessage());
            assertArrayEquals(new Object[]{value,
                    StringFilter.VALIDATOR.toString()},
                    expected.getI18NBoundMessage().getParams());
        }
    }

    /**
     * Tests that we can carry out db operations in parallel
     * @throws Exception
     */
    @Test
    public void concurrentDBAccess() throws Exception {
        //Create a threadpool to run these tests in a couple threads
        //Make sure that the number of threads is less than the
        //connection pool size
        ExecutorService exec = Executors.newFixedThreadPool(10);
        LinkedList<SingleTestThread> list = new LinkedList<SingleTestThread>();
        //Create a bunch of tasks to execute in parallel
        for(int i = 0; i < 100; i++) {
            list.add(new SingleTestThread());
        }
        //Run all the tasks in parallel.
        List<Future<Boolean>> results = exec.invokeAll(list);
        for(Future<Boolean> result: results) {
            //verify that they all passed
            assertTrue(result.get());
        }
    }
    /**
     * Tests what happens when we try and do more operations in parallel
     * than we have connections available
     * @throws Exception
     */
    @Test
    public void concurrentDBConnectionOverflow() throws Exception {
        //Create a threadpool to run these tests in a couple threads
        //Make sure that the number of threads is less than the
        //connection pool size
        ExecutorService exec = Executors.newFixedThreadPool(12);
        LinkedList<SingleTestThread> list = new LinkedList<SingleTestThread>();
        //Create a bunch of tasks to execute in parallel
        for(int i = 0; i < 100; i++) {
            list.add(new SingleTestThread());
        }
        //Run all the tasks in parallel.
        List<Future<Boolean>> results = exec.invokeAll(list);
        for(Future<Boolean> result: results) {
            //verify that they all passed
            assertTrue(result.get());
        }
    }

    private class SingleTestThread implements Callable<Boolean> {
        public Boolean call() throws Exception {
            lifecycle();
            return true;
        }
    }

    /**
     * Tests concurrent blob / clob operations
     * @throws Exception
     */
    @Test
    public void concurrentClobBlobAccess() throws Exception {
        //Create a threadpool to run these tests in a couple threads
        //Make sure that the number of threads is less than the
        //connection pool size
        ExecutorService exec = Executors.newFixedThreadPool(10);
        LinkedList<SingleLobThread> list = new LinkedList<SingleLobThread>();
        //Create a bunch of tasks to execute in parallel
        for(int i = 0; i < 100; i++) {
            list.add(new SingleLobThread(getGenerator().nextBoolean()));
        }
        //Run all the tasks in parallel.
        List<Future<Boolean>> results = exec.invokeAll(list);
        for(Future<Boolean> result: results) {
            //verify that they all passed
            assertTrue(result.get());
        }
    }

    private class SingleLobThread implements Callable<Boolean> {
        private SingleLobThread(boolean clob) {
            isClob = clob;
        }
        public Boolean call() throws Exception {
            if(isClob) {
                clob();
            } else {
                blob();
            }
            return true;
        }
        private boolean isClob;
    }

    /**
     * Stress tests if you can run multiple transactions in sequence.
     * @throws Exception
     */
    @Test
    @Ignore
    public void multipleTransactions() throws Exception {
        int numIterations = 1000;
        while(numIterations-- > 0) {
            lifecycle();
        }
    }

    /**
     * Tests carrying out multiple db operations within a transaction,
     * running nested transactions and transaction failures.
     * @throws Exception
     */
    @Test
    public void transactionTest() throws Exception {
        //Test saving two entities in the same transaction
        //This tests nested transactions as well
        DataTypes d1 = createFilled();
        DataTypes d2 = createFilled();
        List<DataTypes> l = DataTypes.saveMultiple(d1, d2);
        assertEquals(2,l.size());
        DataTypes r1 = l.get(0);
        DataTypes r2 = l.get(1);
        assertSavedEntity(r1);
        assertSavedEntity(r2);
        d1 = fetchByID(r1.getId());
        assertEntityEquals(r1, d1);
        d2 = fetchByID(r2.getId());
        assertEntityEquals(r2, d2);
        //Test a failed transaction
        changeAttributes(r1);
        changeAttributes(r2);
        //Set up to cause constraint violation
        r2.setName(r1.getName());
        try {
            DataTypes.saveMultiple(r1,r2);
            fail("Transaction should've failed with constraint violation"); //$NON-NLS-1$
        } catch (EntityExistsException expected) {
        }
        //verify that the entities are changed
        assertEntityEquals(d1,fetchByID(d1.getId()));
        assertEntityEquals(d2,fetchByID(d2.getId()));
    }

/* ****Implement necessary methods**** */

    protected DataTypes fetchByName(String name) throws Exception {
        return new SingleDataTypesQuery(name).fetch();
    }

    protected boolean fetchExistsByName(String name) throws Exception {
        return new SingleDataTypesQuery(name).exists();
    }

    protected SummaryDataType fetchSummaryByName(String name) throws Exception {
        return new SingleDataTypesQuery(name).fetchSummary();
    }

    protected void save(DataTypes dataTypes) throws Exception {
        dataTypes.save();
    }

    protected void delete(DataTypes dataTypes) throws Exception {
        dataTypes.delete();
    }

    protected void deleteAll() throws Exception {
        MultiDataTypesQuery.all().delete();
    }

    protected DataTypes fetchByID(long id) throws Exception {
        return new SingleDataTypesQuery(id).fetch();
    }

    protected boolean fetchExistsByID(long id) throws Exception {
        return new SingleDataTypesQuery(id).exists();
    }

    protected SummaryDataType fetchSummaryByID(long id) throws Exception {
        return new SingleDataTypesQuery(id).fetchSummary();
    }

    protected List<SummaryDataType> fetchSummaryQuery(
            MultipleEntityQuery query) throws Exception {
        return ((MultiDataTypesQuery)query).fetchSummary();
    }

    protected List<DataTypes> fetchQuery(
            MultipleEntityQuery query) throws Exception {
        return ((MultiDataTypesQuery)query).fetch();
    }

    protected MultipleEntityQuery getAllQuery() {
        return MultiDataTypesQuery.all();
    }

    protected DataTypes createEmpty() throws Exception {
        return new DataTypes();
    }

    protected Class<DataTypes> getEntityClass() {
        return DataTypes.class;
    }

    protected Class<? extends MultipleEntityQuery> getMultiQueryClass() {
        return MultiDataTypesQuery.class;
    }

    /* *************Override necessary methods************* */

    @Override
    protected void assertDefaultValues(DataTypes dataTypes) {
        super.assertDefaultValues(dataTypes);
        assertNull(dataTypes.getBigDecimal());
        assertNull(dataTypes.getBigInteger());
        assertNull(dataTypes.getBytes());
        assertNull(dataTypes.getDate());
        assertEquals(0.0d, dataTypes.getNumDouble(), DELTA_DOUBLE);
        assertEquals(0.0f, dataTypes.getNumFloat(), DELTA_FLOAT);
        assertEquals(0,dataTypes.getNumInt());
        assertNull(dataTypes.getSerialTester());
        assertNull(dataTypes.getTestEnum());
        assertNull(dataTypes.getTime());
        assertNull(dataTypes.getTimestamp());
    }

    @Override
    protected void assertEntityEquals(DataTypes e1, DataTypes e2, boolean skipTimestamp) {
        super.assertEntityEquals(e1, e2, skipTimestamp);
        assertSummaryEquals(e1, e2);
    }

    @Override
    protected void assertEntitySummaryEquals(DataTypes e, SummaryDataType s) {
        super.assertEntitySummaryEquals(e, s);
        assertSummaryEquals(e, s);
    }

    private static void assertSummaryEquals(SummaryDataType s1, SummaryDataType s2) {
        if (s1.getBigDecimal() != null && s2.getBigDecimal() != null) {
            assertTrue(Math.abs(s1.getBigDecimal().subtract(s2.getBigDecimal()).doubleValue()) < DELTA_DOUBLE);
        } else {
            assertNull(s1.getBigDecimal());
            assertNull(s2.getBigDecimal());
        }
        assertEquals(s1.getBigInteger(), s2.getBigInteger());
        assertTrue(Arrays.equals(s1.getBytes(), s2.getBytes()));
        assertCalendarEquals(s1.getDate(), s2.getDate(), TemporalType.DATE);
        assertEquals(s1.getNumDouble(), s2.getNumDouble(), DELTA_DOUBLE);
        assertEquals(s1.getNumFloat(), s2.getNumFloat(), DELTA_FLOAT);
        assertEquals(s1.getNumInt(), s2.getNumInt());
        assertEquals(s1.getSerialTester(), s2.getSerialTester());
        assertEquals(s1.getTestEnum(), s2.getTestEnum());
        assertCalendarEquals(s1.getTime(), s2.getTime(), TemporalType.TIME);
        assertCalendarEquals(s1.getTimestamp(), s2.getTimestamp(), TemporalType.TIMESTAMP);
    }

    @Override
    protected DataTypes createCopy(DataTypes src) throws Exception {
        DataTypes d = super.createCopy(src);
        d.setBigDecimal(src.getBigDecimal());
        d.setBigInteger(src.getBigInteger());
        d.setBytes(src.getBytes());
        d.setDate(src.getDate());
        d.setNumDouble(src.getNumDouble());
        d.setNumFloat(src.getNumFloat());
        d.setNumInt(src.getNumInt());
        d.setSerialTester(src.getSerialTester());
        d.setTestEnum(src.getTestEnum());
        d.setTime(src.getTime());
        d.setTimestamp(src.getTimestamp());
        return d;
    }

    @Override
    protected DataTypes createFilled() throws Exception {
        DataTypes d = super.createFilled();
        changeAttributes(d);
        return d;
    }

    @Override
    protected void changeAttributes(DataTypes d) {
        super.changeAttributes(d);
        final Random random = getGenerator();
        d.setBigDecimal(new BigDecimal(random.nextDouble()));
        byte []b = new byte[random.nextInt(5) + 1];
        random.nextBytes(b);
        d.setBigInteger(new BigInteger(b));
        random.nextBytes(b);
        d.setBytes(b);
        d.setDate(new Date(new java.util.Date().getTime()));
        d.setNumDouble(random.nextDouble());
        d.setNumFloat(random.nextFloat());
        d.setNumInt(random.nextInt());
        d.setSerialTester(new DataTypes.SerializedTester(randomString()));
        d.setTestEnum(DataTypes.TestEnum.values()[random.nextInt(DataTypes.TestEnum.values().length)]);
        d.setTime(new Time(new java.util.Date().getTime()));
        d.setTimestamp(new Timestamp(new java.util.Date().getTime()));
    }

    /**
     * Generates strings of supplied length, with increasing
     * character values, avoiding the high/low surrogate characters.
     *
     * @param len the requested length of the string.
     *
     * @return the generated string, null if characters upto
     * {link Character#MAX_VALUE} have already been generated. The length of
     * the returned string may be less than the requested length.
     */
    private String getNextString(int len) {
        if(count > Character.MAX_VALUE) {
            return null;
        }
        StringBuilder sb = new StringBuilder(len);
        char c;
        while(len-- > 0) {
            c = (char) count;
            // mysql version dependency: this piece of code depends on the
            // specific version of mysql and may need to change whenever
            // mysql version is updated
            while(Character.isLowSurrogate(c) ||
                    Character.isHighSurrogate(c)) {
                count++;
                c = (char) count;
            }
            if(count > Character.MAX_VALUE) {
                break;
            }
            sb.append(c);
            count++;
        }
        return sb.toString();
    }
    private int count = Character.MIN_CODE_POINT;
    private static final DecodingStrategy DECODING_STRATEGY =
            DecodingStrategy.SIG_REQ;

    private static final File TEST_UNICODE_FILE = new File(TEST_SAMPLE_DATA,
            "unicode.txt"); //$NON-NLS-1$
}
