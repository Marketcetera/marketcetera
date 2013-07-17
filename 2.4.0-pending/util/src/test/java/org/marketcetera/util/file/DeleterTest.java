package org.marketcetera.util.file;

import java.io.File;
import org.junit.After;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.exec.Disposition;
import org.marketcetera.util.exec.Exec;
//import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class DeleterTest
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"deleter"+File.separator;
    private static final String TEST_TEMPLATES=
        TEST_ROOT+"templates";
    private static final String TEST_TEMPLATE_WIN32=
        "win32";
    private static final String TEST_TEMPLATE_UNIX=
        "unix";
    private static final String TEST_PLAIN_FILE=
        "file.txt";
    private static final String TEST_PLAIN_DIR=
        "dir";
    private static final String TEST_PLAIN_DIR_CONTENTS=
        TEST_PLAIN_DIR+File.separator+"b.txt";
    private static final String TEST_NONEXISTENT_FILE=
        TEST_ROOT+"nonexistent";
    private static final String TEST_FILE_LINK=
        "file_link";
    private static final String TEST_DIR_LINK=
        "dir_link";
    private static final String TEST_DANGLING_LINK=
        "dangling_link";
    private static final String TEST_RECURSIVE_LINK=
        "recursive_link";


    private static void cleanCopy()
        throws I18NException
    {
        if (OperatingSystem.LOCAL.isUnix()) {
            Exec.run(TEST_TEMPLATES,Disposition.STDERR,
                     "rm","-r","-f",
                     ".."+File.separator+TEST_TEMPLATE_UNIX);
            return;
        }
        if (OperatingSystem.LOCAL.isWin32()) {
            Exec.run(TEST_TEMPLATES,Disposition.STDERR,
                     "cmd.exe","/c","rd","/S","/Q",
                     ".."+File.separator+TEST_TEMPLATE_WIN32);
            return;
        }
        throw new AssertionError("Unknown platform");
    }

    private static String createCopy()
        throws I18NException
    {
        if (OperatingSystem.LOCAL.isUnix()) {
            Exec.run(TEST_TEMPLATES,Disposition.STDERR,
                     "cp","-r",TEST_TEMPLATE_UNIX,
                     ".."+File.separator+TEST_TEMPLATE_UNIX);
            return TEST_ROOT+TEST_TEMPLATE_UNIX;
        }
        Exec.run(TEST_TEMPLATES,Disposition.STDERR,
                 "xcopy.exe","/E","/I",TEST_TEMPLATE_WIN32,
                 ".."+File.separator+TEST_TEMPLATE_WIN32);
        return TEST_ROOT+TEST_TEMPLATE_WIN32;
    }

    private static void single
        (String name,
         String resolvedName)
        throws I18NException
    {
        cleanCopy();
        String rootName=createCopy();
        String fileName=rootName+File.separator+name;
        File root=new File(rootName);
        File file=new File(fileName);
        File resolvedFile=null;
        if (resolvedName!=null) {
            resolvedFile=new File(rootName+File.separator+resolvedName);
        }

        Deleter.apply(file);
        assertTrue(root.exists());
        assertFalse(file.exists());
        if (resolvedFile!=null) {
            assertTrue(resolvedFile.exists());
        }

        cleanCopy();
        createCopy();
        Deleter.apply(fileName);
        assertTrue(root.exists());
        assertFalse(file.exists());
        if (resolvedFile!=null) {
            assertTrue(resolvedFile.exists());
        }
    }


    @After
    public void tearDownDeleterTest()
        throws Exception
    {
        cleanCopy();
    }


    @Test
    public void existing()
        throws Exception
    {
        single(TEST_PLAIN_DIR,null);
        single(TEST_PLAIN_FILE,null);
    }

    @Test
    public void nonexistent()
        throws Exception
    {
        Deleter.apply(new File(TEST_NONEXISTENT_FILE));
        Deleter.apply(TEST_NONEXISTENT_FILE);
    }
 
    @Test
    public void unixDeleter()
        throws Exception
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());
        single(TEST_FILE_LINK,TEST_PLAIN_FILE);
        single(TEST_DIR_LINK,TEST_PLAIN_DIR_CONTENTS);
        single(TEST_DANGLING_LINK,null);
        single(TEST_RECURSIVE_LINK,null);
    }

    /*
     * EXTREME TEST 1: run alone (no other tests in the same file,
     * and no other units test) after uncommenting sections in main
     * class.
    @Test
    public void exception()
        throws Exception
    {
        cleanCopy();
        String rootName=createCopy();
        String name=rootName+File.separator+TEST_PLAIN_FILE;
        try {
            Deleter.apply(name);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_DELETE,
                                        (new File(name)).getAbsolutePath()),
                 ex.getI18NBoundMessage());
        }
    }
    */
}
