package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.util.file.CopyCharsUnicodeUtils;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.test.UnicodeData;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.StringUtils;
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
import java.text.Collator;

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
        String s = "mytestname"; //$NON-NLS-1$
        final int errIdx = s.length();
        // mysql version dependency: this piece of code depends on the
        // specific version of mysql and may need to change whenever
        // mysql version is updated
        s += UnicodeData.GOATS_LNB;
        final String expSubString = "tname"; //$NON-NLS-1$
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
        String s = "mytestname"; //$NON-NLS-1$
        final int errIdx = s.length();
        s += UnicodeData.GOATS_LNB;
        final String expSubString = "tname"; //$NON-NLS-1$
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

    /**
     * This test demonstrates that mysql collation does not generate
     * ordering that is consistent with ordering generated by any
     * possible configuration of the java collator.
     * <p>
     * Note that this test may fail because we are using strings
     * that contain characters from more than one language. Its
     * unclear that unicode collation is defined for all code points
     * in unicode. Its more likely that a locale specific collation
     * defines collation for the characters within that language
     * and gives unknown results for characters from other languages.
     * If that is indeed the case, we might not be able to get
     * java and mysql collators to agree unless we limit our test
     * strings to only contain data from a single language. 
     * <p>
     * Note that achieving consistency between mysql & java collation
     * is considered low priority until we find a use-case where it
     * is necessary to have them produce consistent results.
     * <p>
     * As of now this test fails and hence it is ignored. In future
     * if we figure out that we need a java collator that generates
     * ordering consistent with mysql, we can use this test to help
     * verify consistency. 
     *
     * @throws Exception if there was an error
     */
    @Ignore
    @Test
    public void dbJavaOrderingCompare() throws Exception {
        //Create test data, with descriptions set to a set of string values
        //that have shown inconsistent orderings between mysql and java
        SLF4JLoggerProxy.debug(this,"Original Ordering"); //$NON-NLS-1$
        for (String s : TEST_STRINGS) {
            DataTypes data = new DataTypes();
            data.setName(randomString());
            data.setDescription(s);
            data.save();
            SLF4JLoggerProxy.debug(this, "{} {}", s, StringUtils.toUCPArrayStr(s)); //$NON-NLS-1$
        }
        //Run a mysql query and order the results by description 
        MultiDataTypesQuery mq = MultiDataTypesQuery.all();
        mq.setEntityOrder(MultiDataTypesQuery.BY_DESCRIPTION);
        List<DataTypes> dbResults = mq.fetch();
        SLF4JLoggerProxy.debug(this,"MySQL Ordering"); //$NON-NLS-1$
        for(DataTypes d: dbResults) {
            SLF4JLoggerProxy.debug(this, "{} {}", d.getDescription(), //$NON-NLS-1$
                    StringUtils.toUCPArrayStr(d.getDescription()));
        }
        //Carry out sorting using java collator and print the
        //results. Useful to visually compare with sort order
        //generated by mysql
        Collator c = Collator.getInstance(Locale.US);
        c.setStrength(Collator.TERTIARY);
        c.setDecomposition(Collator.FULL_DECOMPOSITION);
        SLF4JLoggerProxy.debug(this,"Collator {} {}", //$NON-NLS-1$
                c.getDecomposition(), c.getStrength());
        String[] sorted = Arrays.copyOf(TEST_STRINGS, TEST_STRINGS.length);
        Arrays.sort(sorted, c);
        SLF4JLoggerProxy.debug(this,"Java Ordering"); //$NON-NLS-1$
        for(String s: sorted) {
            SLF4JLoggerProxy.debug(this, "{} {}", s, //$NON-NLS-1$
                    StringUtils.toUCPArrayStr(s));
        }
        boolean isFailed;
        //Now run through the actual test
        //Iterate through all available collation locales.
        for (Locale locale:Collator.getAvailableLocales()) {
            c = Collator.getInstance(locale);
            //Iterate through all possible strength values
            for (int strength: new int[]{Collator.PRIMARY, Collator.SECONDARY,
                    Collator.TERTIARY, Collator.IDENTICAL}) {
                c.setStrength(strength);
                //Iterate through all possible decomposition values
                for (int decomp: new int[]{Collator.CANONICAL_DECOMPOSITION,
                        Collator.FULL_DECOMPOSITION, Collator.NO_DECOMPOSITION}) {
                    c.setDecomposition(decomp);
                    isFailed = false;
                    int i = 0;
                    String prev = null;
                    //Compare mysql collation with java's
                    for(DataTypes au: dbResults) {
                        if(prev != null) {
                            if(c.compare(prev,au.getDescription()) <= 0) {
                                isFailed = true;
                                SLF4JLoggerProxy.debug(this,"Failed Collator {} {} {}", //$NON-NLS-1$
                                        locale.getDisplayName(),
                                        c.getDecomposition(),
                                        c.getStrength());
                                SLF4JLoggerProxy.debug(this,"at index:{} {} !<= {}", i, //$NON-NLS-1$
                                        StringUtils.toUCPArrayStr(prev),
                                        StringUtils.toUCPArrayStr(au.getDescription()));
                                break;
                            }
                        }
                        prev = au.getDescription();
                        i++;
                    }
                    //If a matching collator is found return with a successful result
                    if(!isFailed) {
                        SLF4JLoggerProxy.error(this,"Succeeded Collator {} {} {}", //$NON-NLS-1$
                                locale.getDisplayName(),
                                c.getDecomposition(),
                                c.getStrength());
                        return;
                    }
                }
            }
        }
        fail("No Java Collator found to match mysql Collation"); //$NON-NLS-1$
    }

    /**
     * Test strings from unit test failures in the past that showed inconsistencies
     * between java and mysql collation. 
     */
    private static final String[] TEST_STRINGS = {
        "\u0032\u00C4\u1F3A\u0DC2\u83D1\uC4E0\u4820\u7399\u6897\u4953\u932E\u6751", //$NON-NLS-1$
        "\u0032\u00E4\u1148\u599D\u738F\u5602\u4108\uC418\u3525\uF9E1\u8A49\u1EC4", //$NON-NLS-1$
        "\u0032\u00C4\u1FAB\uC7EE\uCA7B\u5CC4\u071E\u3BFF\u49CF\u483D\u0491\u11DD", //$NON-NLS-1$
        "\u0032\u0061\u1250\u437F\u91E0\u1813\u14F4\uD555\uB545\u4570\u0293\u527F", //$NON-NLS-1$
        "\u0032\u0041\u1FF8\u78C8\uB79D\uC3FF\uD10C\uB4E4\u8F1F\u8168\u7AD3\u8519", //$NON-NLS-1$
        "\u0032\u00E4\u1D21\uBD9F\uB28E\u7825\u71BC\u1545\u1D4D\u9A89\u7BAB\u397A", //$NON-NLS-1$
        "\u0032\u00C4\u1FAB\uC7EE\uCA7B\u5CC4\u071E\u3BFF\u49CF\u483D\u0491\u11DD", //$NON-NLS-1$
        "\u0032\u0061\u1250\u437F\u91E0\u1813\u14F4\uD555\uB545\u4570\u0293\u527F", //$NON-NLS-1$
        "\u0032\u0062\u1F8F\u7CEC\uADC2\uB5D6\u65BF\u6FE8\u3ACF\u0D0C\u5BF1\u7B51", //$NON-NLS-1$
        "\u0032\u0042\u0791\u4B5A\uF912\u136C\u7C37\u3432\u41E8\u38C5\uFDF2\uB8F6", //$NON-NLS-1$
        "\u0032\u0041\u1F31\u53AF\uCE0B\u5A3B\u76EB\uD43A\u52EB\u5086\u65E7\u01A4", //$NON-NLS-1$
        "\u0032\u00C4\u1643\u52F0\u0B1A\u86AC\u13BB\u6773\u9AD4\u5119\uC7BD\u3CCB" //$NON-NLS-1$
    };

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

    private static final File TEST_UNICODE_FILE = new File(LoggerConfiguration.TEST_SAMPLE_DATA,
            "unicode.txt"); //$NON-NLS-1$
}
