package org.marketcetera.ors;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.After;
import static org.junit.Assert.*;
import org.apache.log4j.Level;

import java.util.Collection;
import java.io.File;

/* $License$ */
/**
 * Tests {@link OptionRootUnderlyingMap}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionRootUnderlyingMapTest extends TestCaseBase {
    @BeforeClass
    public static void logSetup() {
        setLevel(OptionRootUnderlyingMap.class.getName(), Level.INFO);
    }

    @After
    public void clearAppender() {
        getLogAssist().resetAppender();
    }

    @Test
    public void basic() throws Exception {
        OptionRootUnderlyingMap map = createMap(MAPPING_FILE);
        //verify initial state
        verifyMappingEmpty(map);
        //now load the properties.
        map.afterPropertiesSet();
        //Verify the number of roots and underlyings
        //entries at the bottom of the file are ignored.
        assertEquals(55, map.getNumOptionRoots());
        assertEquals(51, map.getNumUnderlyings());
        //verify gets for nulls
        assertNull(map.getUnderlying(null));
        assertEquals(null, map.getOptionRoots(null));
        //Verify gets that return non-null values.
        assertArrayEquals(new String[]{"K"},
                map.getOptionRoots("K").toArray(new String[1]));
        assertEquals("K", map.getUnderlying("K"));
        //verify gets that return null/empty values.
        assertEquals(null, map.getUnderlying("Zee"));
        assertEquals(null, map.getOptionRoots("Bee"));
    }

    @Test
    public void unmodifiableOptionRootCollection() throws Exception {
        OptionRootUnderlyingMap map = createMap(MAPPING_FILE);
        map.afterPropertiesSet();
        final Collection<String> roots = map.getOptionRoots("JNPR");
        assertArrayEquals(new String[]{"JUL", "JUP", "JUQ", "JUS", "JUX"},
                roots.toArray(new String[roots.size()]));
        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run() throws Exception {
                roots.clear();
            }
        };
    }

    @Test
    public void nonExistentFile() throws Exception {
        String filename = "notexist.txt";
        OptionRootUnderlyingMap map = createMap(filename);
        //load properties
        map.afterPropertiesSet();
        //verify the logged error
        assertLastEvent(Level.ERROR, null,
                Messages.ORUM_LOG_ERROR_LOADING_FILE.getText(filename), null);
        //verify that the mapping is empty.
        verifyMappingEmpty(map);
    }

    @Test
    public void nullFile() throws Exception {
        OptionRootUnderlyingMap map = createMap(null);
        //load properties
        map.afterPropertiesSet();
        //verify the logged message
        assertLastEvent(Level.INFO, null,
                Messages.ORUM_LOG_SKIP_LOAD_FILE.getText(), null);
        //verify that the mapping is empty.
        verifyMappingEmpty(map);
    }

    @Test
    public void includeFiltering() throws Exception {
        final OptionRootUnderlyingMap map = createMap(MAPPING_FILE);
        //Test the default value.
        map.afterPropertiesSet();
        //verify that both EU and EL types are included.
        //EU
        assertArrayEquals(new String[]{"JUL", "JUP", "JUQ", "JUS", "JUX"},
                map.getOptionRoots("JNPR").toArray(new String[2]));
        assertEquals("JNPR", map.getUnderlying("JUP"));
        //EL
        assertArrayEquals(new String[]{"KBI"},
                map.getOptionRoots("NCR").toArray(new String[1]));
        assertEquals("NCR", map.getUnderlying("KBI"));
        //now reset the include types and reload
        map.setIncludeTypes(new String[]{"EU"});
        map.afterPropertiesSet();
        //verify that EU is still there
        assertArrayEquals(new String[]{"JUL", "JUP", "JUQ", "JUS", "JUX"},
                map.getOptionRoots("JNPR").toArray(new String[5]));
        assertEquals("JNPR", map.getUnderlying("JUP"));
        //Verify that EL is not there
        assertEquals(null, map.getOptionRoots("NCR"));
        assertEquals(null, map.getUnderlying("KBI"));
        //Try out a different type
        map.setIncludeTypes(new String[]{"SF"});
        map.afterPropertiesSet();
        assertEquals(6, map.getNumUnderlyings());
        assertEquals(7, map.getNumOptionRoots());
        assertEquals("KB", map.getUnderlying("KB1C"));
        assertArrayEquals(new String[]{"KB1C","KB2C"},
                map.getOptionRoots("KB").toArray(new String[2]));
        //Now reset the include types to not include any type
        map.setIncludeTypes(new String[0]);
        map.afterPropertiesSet();
        verifyMappingEmpty(map);
        //Verify that a null value cannot be supplied to include types.
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run() throws Exception {
                map.setIncludeTypes(null);
            }
        };
    }

    /**
     * Creates the map instance and verifies that the singleton instance
     * points to it.
     *
     * @param inFilename the file to load the mappings from.
     *
     * @return the created map instance.
     */
    private static OptionRootUnderlyingMap createMap(String inFilename) {
        OptionRootUnderlyingMap map = new OptionRootUnderlyingMap();
        map.setFilename(inFilename);
        assertSame(map, OptionRootUnderlyingMap.getInstance());
        return map;
    }

    /**
     * Verifies that the map is empty. ie. has no mappings.
     *
     * @param inMap the map instance to verify.
     */
    private static void verifyMappingEmpty(OptionRootUnderlyingMap inMap) {
        assertEquals(0, inMap.getNumOptionRoots());
        assertEquals(0, inMap.getNumUnderlyings());
        assertEquals(null, inMap.getOptionRoots("K"));
        assertEquals(null, inMap.getUnderlying("K"));
    }

    private static final String MAPPING_FILE = DIR_ROOT + File.separator + "optionRootMapping.txt";
}
