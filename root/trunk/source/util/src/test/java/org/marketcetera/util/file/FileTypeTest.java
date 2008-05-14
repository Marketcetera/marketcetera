package org.marketcetera.util.file;

import java.io.File;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.marketcetera.util.file.FileType.*;

public class FileTypeTest
	extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        FileType.class.getName();
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


    @Before
    public void setup()
    {
        Messages.PROVIDER.setLocale(Locale.US);
        setLevel(TEST_CATEGORY,Level.WARN);
    }


    @Test
    public void all()
    {
        singleTest(NONEXISTENT,false,false,false);
        singleTest(LINK_DIR,   true, true, false);
        singleTest(DIR,        false,true, false);
        singleTest(LINK_FILE,  true, false,true);
        singleTest(FILE,       false,false,true);
        singleTest(UNKNOWN,    false,false,false);
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
        assertEquals(NONEXISTENT,get(TEST_DANGLING_LINK));
        assertEquals(NONEXISTENT,get(TEST_RECURSIVE_LINK));

        assertEquals(UNKNOWN,get(TEST_UNKNOWN_FILE));
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,
             "Cannot determine type of file '"+TEST_UNKNOWN_FILE+"'");
    }
}
