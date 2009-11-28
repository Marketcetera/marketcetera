package org.marketcetera.util.file;

import java.io.File;
//import java.util.Locale;
//import org.apache.log4j.Level;
import org.junit.Test;
//import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.marketcetera.util.file.FileType.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class FileTypeTest
    extends TestCaseBase
{
//  private static final String TEST_CATEGORY=
//      FileType.class.getName();
//  private static final String TEST_LOCATION=
//      TEST_CATEGORY;
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"file_type"+File.separator;

    private static final String TEST_PLAIN_FILE=
        TEST_ROOT+"file.txt";
    private static final String TEST_PLAIN_DIR=
        TEST_ROOT+"dir";
    private static final String TEST_NONEXISTENT_FILE=
        TEST_ROOT+"nonexistent";

    private static final String TEST_FILE_LINK=
        TEST_ROOT+"file_link";
    private static final String TEST_DIR_LINK=
        TEST_ROOT+"dir_link";
    private static final String TEST_DANGLING_LINK=
        TEST_ROOT+"dangling_link";
    private static final String TEST_RECURSIVE_LINK=
        TEST_ROOT+"recursive_link";
    private static final String TEST_UNKNOWN_FILE=
        "/dev/null";

    private static final String TEST_ALIASED_FILE=
        TEST_ROOT+"dir_link"+File.separator+"file.txt";
    private static final String TEST_ALIASED_DIR=
        TEST_ROOT+"dir_link"+File.separator+"dir";
    private static final String TEST_ALIASED_NONEXISTENT_FILE=
        TEST_ROOT+"dir_link"+File.separator+"nonexistent";

    private static final String TEST_ALIASED_FILE_LINK=
        TEST_ROOT+"dir_link"+File.separator+"file_link";
    private static final String TEST_ALIASED_DIR_LINK=
        TEST_ROOT+"dir_link"+File.separator+"dir_link";
    private static final String TEST_ALIASED_DANGLING_LINK=
        TEST_ROOT+"dir_link"+File.separator+"dangling_link";
    private static final String TEST_ALIASED_RECURSIVE_LINK=
        TEST_ROOT+"dir_link"+File.separator+"recursive_link";

    private static final String TEST_DP_DIRECT_FILE_LINK=
        TEST_ROOT+"dp_direct_file_link";
    private static final String TEST_DP_DIRECT_DIR_LINK=
        TEST_ROOT+"dp_direct_dir_link";
    private static final String TEST_DP_DIRECT_DANGLING_LINK=
        TEST_ROOT+"dp_direct_dangling_link";

    private static final String TEST_DP_INDIRECT_FILE_LINK=
        TEST_ROOT+"dp_indirect_file_link";
    private static final String TEST_DP_INDIRECT_DIR_LINK=
        TEST_ROOT+"dp_indirect_dir_link";
    private static final String TEST_DP_INDIRECT_DANGLING_LINK=
        TEST_ROOT+"dp_indirect_dangling_link";

    private static final String TEST_DP_RECURSIVE_LINK=
        TEST_ROOT+"dp_recursive_link";


    private static void singleTest
        (FileType type,
         boolean isSymbolicLink,
         boolean isDirectory,
         boolean isFile)
    {
        assertEquals(isSymbolicLink,type.isSymbolicLink());
        assertEquals(isDirectory,type.isDirectory());
        assertEquals(isFile,type.isFile());
    }


    @Test
    public void all()
    {
        singleTest(NONEXISTENT,  false,false,false);
        singleTest(LINK_DIR,     true, true, false);
        singleTest(DIR,          false,true, false);
        singleTest(LINK_FILE,    true, false,true);
        singleTest(FILE,         false,false,true);
        singleTest(LINK_UNKNOWN, true, false,false);
        singleTest(UNKNOWN,      false,false,false);
    }


    @Test
    public void commonTypes()
    {
        assertEquals(FILE,get(TEST_PLAIN_FILE));
        assertEquals(DIR,get(TEST_PLAIN_DIR));
        assertEquals(NONEXISTENT,get(TEST_NONEXISTENT_FILE));
        assertEquals(UNKNOWN,get((String)null));
        assertEquals(UNKNOWN,get((File)null));
    }

    @Test
    public void unixTypes()
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());

        assertEquals(LINK_FILE,get(TEST_FILE_LINK));
        assertEquals(LINK_DIR,get(TEST_DIR_LINK));
        assertEquals(LINK_UNKNOWN,get(TEST_DANGLING_LINK));
        assertEquals(LINK_UNKNOWN,get(TEST_RECURSIVE_LINK));
        assertEquals(UNKNOWN,get(TEST_UNKNOWN_FILE));

        assertEquals(FILE,get(TEST_ALIASED_FILE));
        assertEquals(DIR,get(TEST_ALIASED_DIR));
        assertEquals(NONEXISTENT,get(TEST_ALIASED_NONEXISTENT_FILE));

        assertEquals(LINK_FILE,get(TEST_ALIASED_FILE_LINK));
	assertEquals(LINK_DIR,get(TEST_ALIASED_DIR_LINK));
        assertEquals(LINK_UNKNOWN,get(TEST_ALIASED_DANGLING_LINK));
        assertEquals(LINK_UNKNOWN,get(TEST_ALIASED_RECURSIVE_LINK));

	assertEquals(LINK_FILE,get(TEST_DP_DIRECT_FILE_LINK));
	assertEquals(LINK_DIR,get(TEST_DP_DIRECT_DIR_LINK));
	assertEquals(LINK_UNKNOWN,get(TEST_DP_DIRECT_DANGLING_LINK));

	assertEquals(LINK_FILE,get(TEST_DP_INDIRECT_FILE_LINK));
	assertEquals(LINK_DIR,get(TEST_DP_INDIRECT_DIR_LINK));
	assertEquals(LINK_UNKNOWN,get(TEST_DP_INDIRECT_DANGLING_LINK));

	assertEquals(LINK_UNKNOWN,get(TEST_DP_RECURSIVE_LINK));
    }

    /*
     * EXTREME TEST 1: run alone (no other tests in the same file,
     * and no other units test) after uncommenting sections in main
     * class.
    @Test
    public void exceptionThrown()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_CATEGORY,Level.WARN);
        assertEquals(UNKNOWN,get(TEST_PLAIN_FILE));
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,
             "Cannot determine type of file '"+
             (new File(TEST_PLAIN_FILE)).getAbsolutePath()+"'",TEST_LOCATION);
    }
    */
}
