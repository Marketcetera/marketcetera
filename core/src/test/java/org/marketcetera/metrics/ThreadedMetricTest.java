package org.marketcetera.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.JMX;
import javax.management.MBeanServer;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.NamedThreadFactory;
import org.marketcetera.util.test.RegExAssert;

/* $License$ */
/**
 * Tests {@link ThreadedMetric}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class ThreadedMetricTest {

    @BeforeClass
    public static void logSetup() {
        ThreadedMetric.setEnabled(true);
    }

    @Test
    public void single() throws Exception {
        final long currentTime = System.nanoTime();
        String firstID = "first";
        String secondID = "second";
        String thirdID = "third";

        //Run one iteration
        oneIteration(TRUE, firstID, secondID, thirdID);

        //Verify the summary
        String[][][] sets = processCSV(summarize());
        assertEquals(1, sets.length);
        String[][] rows = sets[0];
        assertOutput(new Object[][]{
                {ThreadedMetric.BEGIN_IDENTIFIER, firstID, secondID, thirdID,
                        ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 1}

        }, rows);
    }

    @Test
    public void ignoreExceptionInCondition() throws Exception {
        final long currentTime = System.nanoTime();
        String firstID = "first";
        String secondID = "second";
        //throw an exception, this should cause this metric to be ignored
        oneIteration(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                throw new IllegalArgumentException();
            }
        }, firstID, secondID);
        //Do another iteration
        oneIteration(TRUE, firstID, secondID);
        String[][][] sets = processCSV(summarize());
        assertEquals(1, sets.length);
        String [][]rows = sets[0];
        assertEquals(2, rows.length);
        assertOutput(new Object[][]{
                {ThreadedMetric.BEGIN_IDENTIFIER, firstID, secondID,
                ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 2}

        }, rows);
    }

    @Test
    public void missingCheckpoints() throws Exception {
        final long currentTime = System.nanoTime();
        String firstID = "first";
        String secondID = "second";
        String thirdID = "third";

        //have an iteration with all the checkpoints
        oneIteration(TRUE, firstID, secondID, thirdID);
        //now have iterations with couple checkpoints missing
        oneIteration(TRUE, secondID, thirdID);
        oneIteration(TRUE, firstID, thirdID);
        oneIteration(TRUE, firstID, secondID);
        oneIteration(TRUE, firstID);
        oneIteration(TRUE, secondID);
        oneIteration(TRUE, thirdID);
        //and one final iteration with all the checkpoints
        oneIteration(TRUE, firstID, secondID, thirdID);


        String[][][] sets = processCSV(summarize());
        assertEquals(1, sets.length);
        String[][] rows = sets[0];
        assertEquals(9, rows.length);
        assertOutput(new Object[][]{
                {ThreadedMetric.BEGIN_IDENTIFIER, firstID, secondID, thirdID, ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 1},
                {currentTime, "", sleepInterval, sleepInterval, sleepInterval, 2},
                {currentTime, sleepInterval, "", sleepInterval, sleepInterval, 3},
                {currentTime, sleepInterval, sleepInterval, "", sleepInterval, 4},
                {currentTime, sleepInterval, "", "", sleepInterval, 5},
                {currentTime, "", sleepInterval, "", sleepInterval, 6},
                {currentTime, "", "", sleepInterval, sleepInterval, 7},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 8},
        },rows);
    }

    @Test
    public void extraCheckpoints() throws Exception {
        final long currentTime = System.nanoTime();
        String firstID = "first";
        String secondID = "second";
        String thirdID = "third";
        String fourthID = "fourth";

        //have an iteration with all the checkpoints
        oneIteration(TRUE, firstID, thirdID);
        //now have iterations with couple extra checkpoints
        oneIteration(TRUE, firstID, firstID, thirdID);
        oneIteration(TRUE, firstID, secondID, thirdID);
        oneIteration(TRUE, firstID, thirdID, fourthID);
        oneIteration(TRUE, firstID, firstID, secondID, thirdID);
        oneIteration(TRUE, firstID, secondID, firstID, thirdID);
        oneIteration(TRUE, firstID, secondID, secondID,  thirdID, firstID, fourthID, firstID);
        //and one final iteration with all the checkpoints
        oneIteration(TRUE, firstID, thirdID);

        String[][][] sets = processCSV(summarize());
        assertEquals(1, sets.length);
        String[][] rows = sets[0];
        assertEquals(9, rows.length);
        assertOutput(new Object[][]{
                {ThreadedMetric.BEGIN_IDENTIFIER, firstID, thirdID, ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 1},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 2, "\\[first=" + NPTN + "\\]"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 3, "\\[second=" + NPTN + "\\]"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 4, "\\[fourth=" + NPTN + "\\]"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 5, "\\[first=" + NPTN + ":second=" + NPTN + "\\]"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 6, "\\[second=" + NPTN + ":first=" + NPTN + "\\]"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 7, "\\[second=" + NPTN + ":second=" + NPTN + ":first=" + NPTN + ":fourth=" + NPTN + ":first=" + NPTN + "\\]"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 8}
        },rows);
    }
    
    @Test
    public void extraDetails() throws Exception {
        final long currentTime = System.nanoTime();
        String firstID = "first";
        String secondID = "second";
        //Simple cases
        //detail in begin.
        ThreadedMetric.begin("detail1", 2);
        events(firstID, secondID);
        ThreadedMetric.end(TRUE);
        //detail in events
        ThreadedMetric.begin();
        sleep();
        ThreadedMetric.event(firstID, 3, "detail4");
        events(secondID);
        ThreadedMetric.end(TRUE);
        //detail in end
        ThreadedMetric.begin();
        events(firstID, secondID);
        ThreadedMetric.end(TRUE, "detail5", true);
        //detail for each checkpoint
        ThreadedMetric.begin("detail1");
        sleep();
        ThreadedMetric.event(firstID, 43);
        sleep();
        ThreadedMetric.event(secondID, "detail6");
        sleep();
        ThreadedMetric.end(TRUE, "detail7");
        //no details
        oneIteration(TRUE, firstID, secondID);
        String[][][] sets = processCSV(summarize());
        assertEquals(1, sets.length);
        String[][] rows = sets[0];
        assertEquals(6, rows.length);
        assertOutput(new Object[][]{
                {ThreadedMetric.BEGIN_IDENTIFIER, firstID, secondID, ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 1, "\\{BEGIN=\\[detail1;2\\]\\}"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 2, "\\{first=\\[3;detail4\\]\\}"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 3, "\\{END=\\[detail5;true\\]\\}"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 4, "\\{BEGIN=\\[detail1\\]:first=\\[43\\]:second=\\[detail6\\]:END=\\[detail7\\]\\}"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, 5},
        },rows);

    }

    @Test
    public void empty() throws Exception {
        assertEmptySummary();
    }

    @Test(timeout = 60000)
    public void multiple() throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool(
                new NamedThreadFactory("ThreadedMetricTest-"));
        final long currentTime = System.nanoTime();
        final String firstID = "first";
        final String secondID = "second";
        final String thirdID = "third";
        final Callable<Boolean> condition = ConditionsFactory.createSamplingCondition(10, null);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        //Start off 10 threads with 100 iterations each
        for (int i = 0; i < 10; i++) {
            futures.add(exec.submit(new Callable<Object>(){
                public Object call() throws Exception {
                    for (int i = 0 ; i < 109; i++) {
                        oneIteration(condition, firstID,secondID,thirdID);
                    }
                    return null;
                }
            }));
        }
        //Wait for all the tasks to complete
        for(Future<?> future:futures) {
            future.get();
        }

        String[][][] sets = processCSV(summarize());
        assertEquals(10,sets.length);
        for (String[][]rows: sets) {
            assertOutput(new Object[][]{
                    {ThreadedMetric.BEGIN_IDENTIFIER, firstID, secondID,
                    thirdID, ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 10},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 20},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 30},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 40},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 50},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 60},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 70},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 80},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 90},
                    {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 100}
            }, rows);
        }
    }
    
    @Test
    public void nested() throws Exception {
        final long currentTime = System.nanoTime();
        nestedCall1(2,2);
        nestedCall1(1,1);
        String[][][] sets = processCSV(summarize());
        assertEquals(1, sets.length);
        String[][] rows = sets[0];
        assertEquals(3, rows.length);
        assertOutput(new Object[][]{
                {ThreadedMetric.BEGIN_IDENTIFIER, "beforeNest1", "beforeNest2", "nest3First", "nest3Second", "afterNest2", "afterNest1", ThreadedMetric.END_IDENTIFIER, ThreadedMetric.ITERATIONS_HEADER},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 1, "\\[nest3First=" + NPTN + ":nest3Second=" + NPTN + ":beforeNest2=" + NPTN + ":nest3First=" + NPTN + ":nest3Second=" + NPTN + ":nest3First=" + NPTN + ":nest3Second=" + NPTN + ":afterNest2=" + NPTN + "\\]", "\\{beforeNest1=\\[2\\]:beforeNest2=\\[2\\]:beforeNest2=\\[2\\]\\}"},
                {currentTime, sleepInterval, sleepInterval, sleepInterval, sleepInterval, sleepInterval, sleepInterval, sleepInterval, 2, "\\{beforeNest1=\\[1\\]:beforeNest2=\\[1\\]\\}"},
        },rows);
    }

    @Test
    public void disable() throws Exception {
        verifyEnabled();
        //Clear the summary
        ThreadedMetric.clear();
        assertEmptySummary();
        ThreadedMetric.setEnabled(false);
        try {
            verifyDisabled();
        } finally {
            ThreadedMetric.setEnabled(true);
        }
    }

    private void verifyDisabled() throws Exception {
        oneIteration(TRUE, "first", "second", "third");
        assertEmptySummary();
    }

    private static void assertEmptySummary() throws IOException {
        String[][][] sets = processCSV(summarize());
        assertEquals(0, sets.length);
    }

    private void verifyEnabled() throws Exception {
        single();
    }

    @Test
    public void jmx() throws Exception {
        assertEquals("org.marketcetera.metrics:name=ThreadedMetric",JmxUtils.DEFAULT_NAME.toString());
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        assertFalse(server.isRegistered(JmxUtils.DEFAULT_NAME));
        JmxUtils.registerMgmtInterface(server);
        assertTrue(server.isRegistered(JmxUtils.DEFAULT_NAME));

        //Test various jmx methods.
        ThreadedMetricMXBean metric = JMX.newMXBeanProxy(server,
                JmxUtils.DEFAULT_NAME, ThreadedMetricMXBean.class);
        //verify that the summary is empty
        assertEmptySummary();
        //verify default value (as set by this unit test)
        assertEquals(true, metric.isEnabled());
        //now disable instrumentation.
        metric.setEnabled(false);
        assertEquals(false, metric.isEnabled());
        assertEquals(false, ThreadedMetric.isEnabled());
        //verify that the metric is disabled.
        verifyDisabled();
        //now enable it
        metric.setEnabled(true);
        //and verify that it is enabled.
        assertEquals(true, metric.isEnabled());
        assertEquals(true, ThreadedMetric.isEnabled());
        verifyEnabled();
        //Verify the clear operation
        metric.clear();
        assertEmptySummary();
        //Run the summarize operations to test that they do not fail.
        metric.summarize(true);
        metric.summarize(false);
        //verify the reported configured properties.
        assertThat(metric.getConfiguredProperties(),
                Matchers.allOf(Matchers.hasEntry("metc.metrics.enable","false"),
                        Matchers.hasEntry("metc.metrics.jmx.enable","false")));

        //unregister the management interface
        JmxUtils.unregisterMgmtInterface(server);
        //And verify that it did get unregistered.
        assertFalse(server.isRegistered(JmxUtils.DEFAULT_NAME));
    }

    private static void nestedCall1(int inNumLoop1, int inNumLoop2) throws Exception {
        ThreadedMetric.begin();
        sleep();
        ThreadedMetric.event("beforeNest1",inNumLoop1);
        for(int i = 0; i < inNumLoop1; i++) {
            nestedCall2(inNumLoop2);
        }
        ThreadedMetric.event("afterNest1");
        sleep();
        ThreadedMetric.end(TRUE);
    }

    private static void nestedCall2(int inNumLoop2) throws Exception {
        sleep();
        ThreadedMetric.event("beforeNest2",inNumLoop2);
        for(int i = 0; i < inNumLoop2; i++) {
            nestedCall3();
        }
        ThreadedMetric.event("afterNest2");
        sleep();
    }

    private static void nestedCall3() throws Exception {
        events("nest3First", "nest3Second");
    }

    @After
    public void clearMetrics() {
        ThreadedMetric.clear();
    }
    static void sleep() throws InterruptedException {
        Thread.sleep(sleepInterval);
    }
    private static final Callable<Boolean> TRUE = new Callable<Boolean>() {
        public Boolean call() throws Exception {
            return Boolean.TRUE;
        }
    };
    private static void oneIteration(Callable<Boolean> inCondition,
                                     String... inEventIDs) throws Exception {
        ThreadedMetric.begin();
        events(inEventIDs);
        ThreadedMetric.end(inCondition);
    }
    private static void events(String ... inEventIDs) throws Exception {
        for(String eventID: inEventIDs) {
            sleep();
            ThreadedMetric.event(eventID);
        }
        sleep();
    }
    private static String[][][]processCSV(byte[][] inputs) throws IOException {
        String [][][]value = new String[inputs.length][][];
        int idx = 0;
        for(byte[] input: inputs) {
            value[idx++] = processCSV(input);
        }
        return value;
    }
    private static String[][]processCSV(byte[] input) throws IOException {
        InputStream is = new ByteArrayInputStream(input);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        LinkedList<String[]> rows = new LinkedList<String[]>();
        while((line = reader.readLine()) != null) {
            rows.add(line.split(","));
        }
        return rows.toArray(new String[rows.size()][]);
    }

    private static byte[][] summarize() throws IOException {
        final List<ByteArrayOutputStream> streams = new LinkedList<ByteArrayOutputStream>();
        ThreadedMetric.summarizeResults(new PrintStreamFactory(){
            @Override
            public PrintStream getStream(String inName) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                streams.add(baos);
                return new PrintStream(baos);
            }

            @Override
            public void done(PrintStream inStream) throws IOException {
                inStream.close();
            }
        });
        //Uncomment the following line to observe summary generated by the tests. 
        //ThreadedMetric.summarizeResults(StdErrFactory.INSTANCE);
        byte[][]value = new byte[streams.size()][];
        int idx = 0;
        for(ByteArrayOutputStream baos: streams) {
            value[idx++] = baos.toByteArray();
        }
        return value;
    }

    /**
     * Verifies that the supplied matrix of strings matches the expectation.
     *
     * @param inExpected the matrix of expectations. Numeric expectations,
     * expect the supplied string to be numeric and have a value less than or
     * equal to the expectation. String expectations are expected to be regex
     * patterns and the actual string is matched against the regex. String
     * expectations of length 0, expect the actual value to be 0. Any other
     * expectation types are expected to be equal to the actual value.
     * @param inActual the matrix of actual values.
     *
     * @throws Exception if there were any unexpected errors.
     */
    static void assertOutput(Object[][] inExpected, String [][]inActual) throws Exception {
        assertEquals(inExpected.length, inActual.length);
        for(int i = 0; i < inExpected.length; i++) {
            Object [] expected = inExpected[i];
            String [] actual = inActual[i];
            assertEquals("iteration:" + i,expected.length, actual.length);
            for(int j = 0; j < expected.length; j++) {
                Object expect = expected[j];
                final String errMsg = "iteration:" + i + "," + j;
                if(expect instanceof Comparable && expect instanceof Number) {
                    assertThat(errMsg,
                            parseNumber(actual[j], ((Comparable)expect).getClass()),
                            Matchers.greaterThanOrEqualTo((Comparable)expect));
                } else if(expect instanceof String){
                    String exp = (String) expect;
                    if(exp.length() > 0) {
                        //assume regex
                        RegExAssert.assertMatches(errMsg, exp, actual[j]);
                    } else {
                        assertEquals(errMsg, expect, actual[j]);
                    }
                } else {
                    assertEquals(errMsg, expect, actual[j]);
                }
            }
        }
    }
    static Long parseLong(String inValue) {
        try {
            return Long.parseLong(inValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    static <T extends Comparable> T parseNumber(String inString, Class<T> inClass) throws Exception {
        return inClass.getConstructor(String.class).newInstance(inString);
    }
    static final long sleepInterval = 100;
    /**
     * A regex patter that matches the number of nanoseconds of time interval
     * corresponding to sleepInterval above. Due to timing inaccuracies
     * the actual time might be slightly less that the sleepInterval above.
     * And this pattern should account for that.
     */
    static final String NPTN = "\\d{8,9}";
}
