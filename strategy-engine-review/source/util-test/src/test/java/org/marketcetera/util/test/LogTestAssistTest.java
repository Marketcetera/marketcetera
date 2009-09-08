package org.marketcetera.util.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;


/* $License$ */
/**
 * Tests {@link LogTestAssist}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
public class LogTestAssistTest {

    @Before
    public void setup()
    {
        mAssist = new LogTestAssist(TEST_CATEGORY, Level.ERROR);
    }

    @Test
    public void noEvents()
    {
        assertNoEvents();
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertNoEvents();
    }

    @Test(expected=AssertionError.class)
    public void someEvents()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertNoEvents();
    }

    @Test
    public void lastEventCorrect()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertLastEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(1,getAppender().getEvents().size());
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertLastEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(2,getAppender().getEvents().size());
    }

    @Test(expected=AssertionError.class)
    public void noLastEvent()
    {
        assertLastEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test
    public void someEventCorrect()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(null,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,null,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,TEST_CATEGORY,null,TEST_LOCATION);
        assertSomeEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectLevel()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectName()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectMessage()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectLocation()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,"");
    }

    @Test(expected=AssertionError.class)
    public void noSomeEvent()
    {
        assertSomeEvent(null,null,null,null);
    }


    @Test
    public void singleEventCorrect()
    {
        setLevel(TEST_CATEGORY,Level.WARN);
        Logger.getLogger(TEST_CATEGORY).warn(TEST_MESSAGE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertNoEvents();
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertNoEvents();
    }

    @Test(expected=AssertionError.class)
    public void noSingleEvent()
    {
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void tooManyEvents()
    {
        Logger.getLogger(TEST_CATEGORY).warn(TEST_MESSAGE);
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test
    public void untrackedCategory()
    {
        Logger.getLogger("AnotherCategory").error(TEST_MESSAGE);
        assertNoEvents();
    }

    @Test
    public void appenderReset()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        resetAppender();
        assertNoEvents();
    }

    @Test
    public void categoryTrack()
    {
        String anotherCategory = "AnotherCategory";
        String yetAnotherCategory = "YetAnotherCategory";
        Logger.getLogger(anotherCategory).error(TEST_MESSAGE);
        Logger.getLogger(yetAnotherCategory).info(TEST_MESSAGE);
        Logger.getLogger(yetAnotherCategory).warn(TEST_MESSAGE);
        assertNoEvents();

        trackCategory(anotherCategory, Level.ERROR);
        Logger.getLogger(anotherCategory).error(TEST_MESSAGE);
        Logger.getLogger(yetAnotherCategory).info(TEST_MESSAGE);
        Logger.getLogger(yetAnotherCategory).warn(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR, anotherCategory, TEST_MESSAGE, TEST_LOCATION);

        trackCategory(yetAnotherCategory, Level.WARN);
        Logger.getLogger(anotherCategory).error(TEST_MESSAGE);
        Logger.getLogger(yetAnotherCategory).warn(TEST_MESSAGE);
        Logger.getLogger(yetAnotherCategory).info(TEST_MESSAGE);
        assertLastEvent(Level.WARN, yetAnotherCategory, TEST_MESSAGE, TEST_LOCATION);
        assertSomeEvent(Level.ERROR, anotherCategory, TEST_MESSAGE, TEST_LOCATION);
    }

    @Test
    public void defaultConstructor() {
        mAssist = new LogTestAssist();
        assertNoEvents();
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertNoEvents();
        trackCategory(TEST_CATEGORY, Level.ERROR);
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR, TEST_CATEGORY,  TEST_MESSAGE, TEST_LOCATION);
    }

    protected void trackCategory(String inName, Level inLevel)
    {
        mAssist.trackLogger(inName, inLevel);
    }

    protected void resetAppender()
    {
        mAssist.resetAppender();
    }

    protected void assertNoEvents()
    {
        mAssist.assertNoEvents();
    }

    protected void assertLastEvent(Level inLevel, String inTestCategory,
                                   String inTestMessage, String inTestLocation)
    {
        mAssist.assertLastEvent(inLevel, inTestCategory,
                inTestMessage, inTestLocation);
    }

    protected void assertSomeEvent(Level inLevel, String inTestCategory,
                                   String inTestMessage, String inTestLocation)
    {
        mAssist.assertSomeEvent(inLevel,  inTestCategory,
                inTestMessage, inTestLocation);
    }

    protected void assertSingleEvent(Level inLevel, String inTestCategory,
                                     String inTestMessage, String inTestLocation)
    {
        mAssist.assertSingleEvent(inLevel, inTestCategory,
                inTestMessage, inTestLocation);
    }

    private void setLevel(String inTestCategory, Level inInfo)
    {
        TestCaseBase.setLevel(inTestCategory, inInfo);
    }

    protected MemoryAppender getAppender()
    {
        return mAssist.getAppender();
    }

    protected static final String TEST_CATEGORY=
        "TestCategory";
    private static final String TEST_MESSAGE=
        "Test message (expected)";
    private static final String TEST_LOCATION=
        LogTestAssistTest.class.getName();
    protected LogTestAssist mAssist;
}
