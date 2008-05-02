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

    @Test
    public void directory()
        throws Exception
    {
        String name=setupTemplate();
        File root=new File(name);
        Deleter.apply(root);
        assertFalse(root.exists());

        setupTemplate();
        Deleter.apply(name);
        assertFalse(root.exists());
    }

    @Test
    public void file()
        throws Exception
    {
        String rootName=setupTemplate();
        File root=new File(rootName);
        String name=rootName+File.separator+TEST_PLAIN_FILE;
        File file=new File(name);
        Deleter.apply(file);
        assertTrue(root.exists());
        assertFalse(file.exists());

        setupTemplate();
        Deleter.apply(name);
        assertTrue(root.exists());
        assertFalse(file.exists());
    }

    @Test
    public void nonexistent()
        throws Exception
    {
        Deleter.apply(new File(TEST_NONEXISTENT_FILE));
        Deleter.apply(TEST_NONEXISTENT_FILE);
    }
 
    @Test
    public void fileLink()
        throws Exception
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());

        String rootName=setupTemplate();
        String name=rootName+File.separator+TEST_FILE_LINK;
        File root=new File(rootName);
        File file=new File(name);
        File resolvedFile=new File(rootName+File.separator+TEST_PLAIN_FILE);

        Deleter.apply(file);
        assertTrue(root.exists());
        assertTrue(resolvedFile.exists());
        assertFalse(file.exists());

        setupTemplate();
        Deleter.apply(name);
        assertTrue(root.exists());
        assertTrue(resolvedFile.exists());
        assertFalse(file.exists());
    }

    @Test
    public void dirLink()
        throws Exception
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());

        String rootName=setupTemplate();
        String name=rootName+File.separator+TEST_DIR_LINK;
        File root=new File(rootName);
        File file=new File(name);
        File resolvedFile=new File(rootName+File.separator+TEST_PLAIN_DIR);

        Deleter.apply(file);
        assertTrue(root.exists());
        assertTrue(resolvedFile.exists());
        assertFalse(file.exists());

        setupTemplate();
        Deleter.apply(name);
        assertTrue(root.exists());
        assertTrue(resolvedFile.exists());
        assertFalse(file.exists());
    }

    @Test
    public void danglingLink()
        throws Exception
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());

        String rootName=setupTemplate();
        String name=rootName+File.separator+TEST_DANGLING_LINK;
        File root=new File(rootName);
        File file=new File(name);

        Deleter.apply(file);
        assertTrue(root.exists());
        assertFalse(file.exists());

        setupTemplate();
        Deleter.apply(name);
        assertTrue(root.exists());
        assertFalse(file.exists());
    }

    @Test
    public void recursiveLink()
        throws Exception
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());

        String rootName=setupTemplate();
        String name=rootName+File.separator+TEST_RECURSIVE_LINK;
        File root=new File(rootName);
        File file=new File(name);

        Deleter.apply(file);
        assertTrue(root.exists());
        assertFalse(file.exists());

        setupTemplate();
        Deleter.apply(name);
        assertTrue(root.exists());
        assertFalse(file.exists());
    }
}
