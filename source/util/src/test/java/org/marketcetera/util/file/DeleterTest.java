package org.marketcetera.util.file;

import java.io.File;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.exec.Disposition;
import org.marketcetera.util.exec.Exec;
import org.marketcetera.util.exec.ExecResult;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.marketcetera.util.file.FileType.*;

public class DeleterTest
	extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"deleter"+File.separator;
    private static final String TEST_TEMPLATES=
        TEST_ROOT+"templates"+File.separator;
    private static final String TEST_TEMPLATE_WIN32=
        "win32";
    private static final String TEST_TEMPLATE_UNIX=
        "unix";
    private static final String TEST_PLAIN_FILE=
        "a.txt";
    private static final String TEST_PLAIN_DIR=
        "a";
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

    private static String setupTemplate()
        throws I18NException
    {
        String[] command;
        if (OperatingSystem.LOCAL.isUnix()) {
            command=new String[] {
                "rm","-r","-f","../"+TEST_TEMPLATE_UNIX};
        } else if (OperatingSystem.LOCAL.isWin32()) {
            command=new String[] {
                "cmd.exe","/c","rd","/S","/Q","..\\"+TEST_TEMPLATE_WIN32};
        } else {
            throw new AssertionError("Unknown platform");
        }
        Exec.run(TEST_TEMPLATES,Disposition.STDERR,command);
        if (OperatingSystem.LOCAL.isUnix()) {
            command=new String[] {
                "cp","-r",TEST_TEMPLATE_UNIX,
                "../"+TEST_TEMPLATE_UNIX};
        } else {
            command=new String[] {
                "xcopy.exe","/E","/I",TEST_TEMPLATE_WIN32,
                "..\\"+TEST_TEMPLATE_WIN32};
        }
        Exec.run(TEST_TEMPLATES,Disposition.STDERR,command);
        if (OperatingSystem.LOCAL.isUnix()) {
            return TEST_ROOT+TEST_TEMPLATE_UNIX;
        }
        return TEST_ROOT+TEST_TEMPLATE_WIN32;
    }

    private static void single
        (String name,
         String resolvedName)
        throws Exception
    {
        String rootName=setupTemplate();
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

        setupTemplate();
        Deleter.apply(fileName);
        assertTrue(root.exists());
        assertFalse(file.exists());
        if (resolvedFile!=null) {
            assertTrue(resolvedFile.exists());
        }
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
        single(TEST_DIR_LINK,TEST_PLAIN_DIR);
        single(TEST_DANGLING_LINK,null);
        single(TEST_RECURSIVE_LINK,null);
    }
}
