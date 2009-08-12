package org.marketcetera.ors.info;

import java.util.Iterator;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class InfoTestBase
    extends TestCaseBase
{
    protected static final String KEY_INT_1=
        HELLO_EN;
    protected static final Integer VALUE_INT_1=
        new Integer(1);
    protected static final String KEY_NULL_VALUE=
        HOUSE_AR;
    private static final String KEY_UNSET_VALUE=
        GOODBYE_JA;
    private static final String KEY_INT_2=
        GOATS_LNB;
    private static final Integer VALUE_INT_2=
        new Integer(2);

    private static final I18NBoundMessage MSG_NULL_KEY=
        Messages.NULL_KEY;
    private static final I18NBoundMessage MSG_MISSING_VALUE=
        new I18NBoundMessage1P(Messages.MISSING_VALUE,KEY_UNSET_VALUE);
    private static final I18NBoundMessage MSG_NULL_VALUE=
        new I18NBoundMessage1P(Messages.NULL_VALUE,KEY_NULL_VALUE);
    private static final I18NBoundMessage MSG_BAD_CLASS_VALUE=
        new I18NBoundMessage3P(Messages.BAD_CLASS_VALUE,KEY_INT_1,
                               VALUE_INT_1.toString(),Long.class);
    private static final I18NBoundMessage MSG_VALUE_EXISTS=
        new I18NBoundMessage3P(Messages.VALUE_EXISTS,KEY_INT_1,
                               VALUE_INT_1.toString(),VALUE_INT_2.toString());

    protected static final SystemInfoImpl SYSTEM_INFO=
        new SystemInfoImpl();
    protected static final SessionInfoImpl SESSION_INFO=
        new SessionInfoImpl(SYSTEM_INFO);
    protected static final RequestInfoImpl REQUEST_INFO=
        new RequestInfoImpl(SESSION_INFO);
    
    protected static final String SYSTEM_INFO_NAME=
        "SystemInfo"+NameGenerator.INT_SEPARATOR+"0000000001";
    protected static final String SESSION_INFO_NAME=
        "SessionInfo"+NameGenerator.INT_SEPARATOR+"0000000001";
    protected static final String REQUEST_INFO_NAME=
        "RequestInfo"+NameGenerator.INT_SEPARATOR+"0000000001";

    protected static final String TEST_NAME=
        "StoreName";

    private static final String LOG_GET_LOCATION=
        ReadInfoImpl.class.getName();
    private static final String LOG_SET_LOCATION=
        ReadWriteInfoImpl.class.getName();


    /**
     * Tests read-only access for the given store. Assumes that the
     * store contains just the following entries: key {@link
     * #KEY_INT_1} with value {@link #VALUE_INT_1}, key {@link
     * #KEY_NULL_VALUE} with value null.
     *
     * @param info The store.
     * @param name The expected store name.
     * @param path The expected store path.
     */

    protected void read
        (ReadInfo info,
         String name,
         String path)
        throws Exception
    {
        // Turn off logging by default and clear log: no events should
        // be recorded during accesses below until logging tests
        // start.

        String category=info.getClass().getName();
        setLevel(category,Level.OFF);
        getAppender().clear();

        // Name and path.

        assertEquals(name,info.getName());
        assertEquals(path,info.getPath());

        // toString().

        assertTrue(info.toString(),info.toString().startsWith(path));

        // contains().

        assertTrue(info.contains(KEY_INT_1));
        try {
            info.contains(null);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        assertFalse(info.contains(KEY_UNSET_VALUE));
        assertTrue(info.contains(KEY_NULL_VALUE));

        // getValue().

        assertSame(VALUE_INT_1,
                   info.getValue(KEY_INT_1));
        try {
            info.getValue(null);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        assertNull(info.getValue(KEY_UNSET_VALUE));
        assertNull(info.getValue(KEY_NULL_VALUE));

        // getValueIfSet().

        assertSame(VALUE_INT_1,
                   info.getValueIfSet(KEY_INT_1));
        try {
            info.getValueIfSet(null);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfSet(KEY_UNSET_VALUE);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_MISSING_VALUE,
                         ex.getI18NBoundMessage());
        }
        assertNull(info.getValueIfSet(KEY_NULL_VALUE));

        // getValueIfNonNull().

        assertSame(VALUE_INT_1,
                   info.getValueIfNonNull(KEY_INT_1));
        try {
            info.getValueIfNonNull(null);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfNonNull(KEY_UNSET_VALUE);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_MISSING_VALUE,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfNonNull(KEY_NULL_VALUE);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_VALUE,
                         ex.getI18NBoundMessage());
        }
 
        // getValueIfInstanceOf().

        assertSame(VALUE_INT_1,
                   info.getValueIfInstanceOf(KEY_INT_1,Integer.class));
        try {
            info.getValueIfInstanceOf(null,Integer.class);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfInstanceOf(KEY_INT_1,Long.class);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_BAD_CLASS_VALUE,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfInstanceOf(KEY_UNSET_VALUE,Object.class);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_MISSING_VALUE,
                         ex.getI18NBoundMessage());
        }
        assertNull(info.getValueIfInstanceOf(KEY_NULL_VALUE,Integer.class));
        assertNull(info.getValueIfInstanceOf(KEY_NULL_VALUE,Object.class));

        // getValueIfNonNullInstanceOf().

        assertSame(VALUE_INT_1,
                   info.getValueIfNonNullInstanceOf(KEY_INT_1,Integer.class));
        try {
            info.getValueIfNonNullInstanceOf(null,Integer.class);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfNonNullInstanceOf(KEY_INT_1,Long.class);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_BAD_CLASS_VALUE,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfNonNullInstanceOf(KEY_UNSET_VALUE,Object.class);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_MISSING_VALUE,
                         ex.getI18NBoundMessage());
        }
        try {
            info.getValueIfNonNullInstanceOf(KEY_NULL_VALUE,Integer.class);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_VALUE,
                         ex.getI18NBoundMessage());
        }

        // Logging.

        assertNoEvents();
        setLevel(category,Level.DEBUG);

        info.contains(KEY_NULL_VALUE);
        assertSingleEvent
            (Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_NULL_VALUE+
             "' is present.",LOG_GET_LOCATION);

        info.contains(KEY_UNSET_VALUE);
        assertSingleEvent
            (Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_UNSET_VALUE+
             "' is absent.",LOG_GET_LOCATION);

        info.getValue(KEY_INT_1);
        assertSingleEvent
            (Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': got key '"+KEY_INT_1+
             "' with value '"+VALUE_INT_1+"'.",LOG_GET_LOCATION);

        info.getValue(KEY_NULL_VALUE);
        assertSingleEvent
            (Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': got key '"+KEY_NULL_VALUE+
             "' with value 'null'.",LOG_GET_LOCATION);

        info.getValueIfSet(KEY_INT_1);
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_INT_1+
             "' is present.",LOG_GET_LOCATION);
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': got key '"+KEY_INT_1+
             "' with value '"+VALUE_INT_1+"'.",LOG_GET_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();

        info.getValueIfNonNull(KEY_INT_1);
        events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_INT_1+
             "' is present.",LOG_GET_LOCATION);
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': got key '"+KEY_INT_1+
             "' with value '"+VALUE_INT_1+"'.",LOG_GET_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();

        info.getValueIfInstanceOf(KEY_INT_1,Integer.class);
        events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_INT_1+
             "' is present.",LOG_GET_LOCATION);
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': got key '"+KEY_INT_1+
             "' with value '"+VALUE_INT_1+"'.",LOG_GET_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();

        info.getValueIfNonNullInstanceOf(KEY_INT_1,Integer.class);
        events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_INT_1+
             "' is present.",LOG_GET_LOCATION);
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': got key '"+KEY_INT_1+
             "' with value '"+VALUE_INT_1+"'.",LOG_GET_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();

        setLevel(category,Level.OFF);
    }

    /**
     * Tests read-write access for the given store.
     *
     * @param info The store.
     * @param name The expected store name.
     * @param path The expected store path.
     */

    protected void readWrite
        (ReadWriteInfo info,
         String name,
         String path)
        throws Exception
    {
        // Turn off logging by default and clear log: no events should
        // be recorded during accesses below until logging tests
        // start.

        String category=info.getClass().getName();
        setLevel(category,Level.OFF);
        getAppender().clear();

        // setValue().
        
        info.setValue(KEY_INT_1,VALUE_INT_1);
        assertSame(VALUE_INT_1,info.getValue(KEY_INT_1));
        try {
            info.setValue(null,VALUE_INT_1);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        info.setValue(KEY_NULL_VALUE,null);
        assertNull(info.getValue(KEY_NULL_VALUE));

        // Read-only tests.

        read(info,name,path);

        // setValueIfUnset().

        info.setValueIfUnset(KEY_INT_2,VALUE_INT_2);
        assertSame(VALUE_INT_2,info.getValue(KEY_INT_2));
        try {
            info.setValueIfUnset(null,VALUE_INT_1);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        try {
            info.setValueIfUnset(KEY_INT_1,VALUE_INT_2);
            fail();
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_VALUE_EXISTS,
                         ex.getI18NBoundMessage());
        }
        info.removeValue(KEY_NULL_VALUE);
        info.setValueIfUnset(KEY_NULL_VALUE,null);
        assertNull(info.getValue(KEY_NULL_VALUE));
       
        // removeValue();

        info.setValue(KEY_INT_2,VALUE_INT_2);
        info.removeValue(KEY_INT_2);
        assertFalse(info.contains(KEY_INT_2));
        info.removeValue(KEY_UNSET_VALUE);
        try {
            info.removeValue(null);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }

        // removeValueIfSet();

        info.setValue(KEY_INT_2,VALUE_INT_2);
        info.removeValueIfSet(KEY_INT_2);
        assertFalse(info.contains(KEY_INT_2));
        try {
            info.removeValueIfSet(null);
            fail();
        } catch (InfoRuntimeException ex) {
            assertEquals(ex.getDetail(),MSG_NULL_KEY,
                         ex.getI18NBoundMessage());
        }
        try {
            info.removeValueIfSet(KEY_UNSET_VALUE);
        } catch (InfoException ex) {
            assertEquals(ex.getDetail(),MSG_MISSING_VALUE,
                         ex.getI18NBoundMessage());
        }

        // Logging.

        assertNoEvents();
        setLevel(category,Level.DEBUG);

        info.setValue(KEY_INT_1,VALUE_INT_1);
        assertSingleEvent
            (Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': set key '"+KEY_INT_1+
             "' to value '"+VALUE_INT_1+"'.",LOG_SET_LOCATION);

        info.removeValue(KEY_INT_1);
        assertSingleEvent
            (Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': removed key '"+KEY_INT_1+
             "'.",LOG_SET_LOCATION);

        info.setValueIfUnset(KEY_INT_2,VALUE_INT_2);
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_INT_2+
             "' is absent.",LOG_GET_LOCATION);
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': set key '"+KEY_INT_2+
             "' to value '"+VALUE_INT_2+"'.",LOG_SET_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();
 
        info.removeValueIfSet(KEY_INT_2);
        events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': key '"+KEY_INT_2+
             "' is present.",LOG_GET_LOCATION);
        assertEvent
            (events.next(),Level.DEBUG,category,
             "Store '"+info.getPath()+
             "': removed key '"+KEY_INT_2+
             "'.",LOG_SET_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();
    }
}
