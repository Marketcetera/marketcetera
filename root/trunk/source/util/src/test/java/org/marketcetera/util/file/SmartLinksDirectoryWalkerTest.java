package org.marketcetera.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.marketcetera.util.test.CollectionAssert.*;

public class SmartLinksDirectoryWalkerTest
	extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"directory_walker"+File.separator;
    protected static final String TEST_NONEXISTENT_FILE=
        TEST_ROOT+"nonexistent";
    private static final String TEST_FILE=
        "a.txt";
    private static final String TEST_LINK_NAME=
        "e";
    private static final String TEST_LINK_PATH=
        "a"+File.separator+TEST_LINK_NAME;
    private static final String TEST_LINK_CONTENTS=
        "e.txt";
    private static final String[] TEST_FILE_LIST=new String[] {
        "a.txt", "b.txt","c.txt","d.txt",TEST_LINK_CONTENTS};
    private static final String[] TEST_DIR_LIST=new String[] {
        "a","b","c","d","directory_walker"};


    public static final class ListWalker
        extends SmartLinksDirectoryWalker
    {
        private Vector<String> mFiles=new Vector<String>();
        private Vector<String> mDirectories=new Vector<String>();
        private int mMaxDepth=-1;

        public ListWalker
            (boolean followLinks)
        {
            super(followLinks);
        }

        public String[] getFiles()
        {
            return mFiles.toArray(new String[0]);
        }

        public String[] getDirectories()
        {
            return mDirectories.toArray(new String[]{});
        }

        public int getMaxDepth()
        {
            return mMaxDepth;
        }

        @Override
        protected boolean handleDirectory
            (File directory,
             int depth,
             Collection results)
            throws IOException
        {
            return (super.handleDirectory(directory,depth,results) &&
                    !".svn".equals(directory.getName()));
        }

        @Override
        protected void handleDirectoryStart
            (File directory,
             int depth,
             Collection results)
        {
            mDirectories.add(directory.getName());
            if (results!=null) {
                results.add(directory.getName());
            }
            if (depth>mMaxDepth) {
                mMaxDepth=depth;
            }
        }

        @Override
        protected void handleFile
            (File file,
             int depth,
             Collection results)
        {
            mFiles.add(file.getName());
            if (results!=null) {
                results.add(file.getName());
            }
            if (depth>mMaxDepth) {
                mMaxDepth=depth;
            }
        }
    }

    @Test
    public void singleFile()
        throws Exception
    {
        ListWalker walker=new ListWalker(false);
        walker.apply(TEST_ROOT+TEST_FILE);
        assertArrayPermutation
            (new String[] {TEST_FILE},walker.getFiles());
        assertArrayPermutation
            (new String[0],walker.getDirectories());
        assertEquals(0,walker.getMaxDepth());

        Vector<String> results=new Vector<String>();
        walker=new ListWalker(false);
        walker.apply(TEST_ROOT+TEST_FILE,results);
        assertArrayPermutation
            (new String[] {TEST_FILE},walker.getFiles());
        assertArrayPermutation
            (new String[0],walker.getDirectories());
        assertArrayPermutation
            (new String[] {TEST_FILE},results.toArray(new String[0]));
        assertEquals(0,walker.getMaxDepth());

        walker=new ListWalker(true);
        walker.apply(TEST_ROOT+TEST_FILE);
        assertArrayPermutation
            (new String[] {TEST_FILE},walker.getFiles());
        assertArrayPermutation
            (new String[0],walker.getDirectories());
        assertEquals(0,walker.getMaxDepth());
        
        results=new Vector<String>();
        walker=new ListWalker(true);
        walker.apply(TEST_ROOT+TEST_FILE,results);
        assertArrayPermutation
            (new String[] {TEST_FILE},walker.getFiles());
        assertArrayPermutation
            (new String[0],walker.getDirectories());
        assertArrayPermutation
            (new String[] {TEST_FILE},results.toArray(new String[0]));
        assertEquals(0,walker.getMaxDepth());
    }

    @Test
    public void singleLink()
        throws Exception
    {
        assumeTrue(OperatingSystem.LOCAL.isUnix());

        ListWalker walker=new ListWalker(false);
        walker.apply(TEST_ROOT+TEST_LINK_PATH);
        assertArrayPermutation
            (new String[] {TEST_LINK_NAME},walker.getFiles());
        assertArrayPermutation
            (new String[0],walker.getDirectories());
        assertEquals(0,walker.getMaxDepth());

        Vector<String> results=new Vector<String>();
        walker=new ListWalker(false);
        walker.apply(TEST_ROOT+TEST_LINK_PATH,results);
        assertArrayPermutation
            (new String[] {TEST_LINK_NAME},walker.getFiles());
        assertArrayPermutation
            (new String[0],walker.getDirectories());
        assertArrayPermutation
            (new String[] {TEST_LINK_NAME},
             results.toArray(new String[0]));
        assertEquals(0,walker.getMaxDepth());

        walker=new ListWalker(true);
        walker.apply(TEST_ROOT+TEST_LINK_PATH);
        assertArrayPermutation
            (new String[] {TEST_LINK_CONTENTS},walker.getFiles());
        assertArrayPermutation
            (new String[] {TEST_LINK_NAME},walker.getDirectories());
        assertEquals(1,walker.getMaxDepth());

        results=new Vector<String>();
        walker=new ListWalker(true);
        walker.apply(TEST_ROOT+TEST_LINK_PATH,results);
        assertArrayPermutation
            (new String[] {TEST_LINK_CONTENTS},walker.getFiles());
        assertArrayPermutation
            (new String[] {TEST_LINK_NAME},walker.getDirectories());
        assertArrayPermutation
            (new String[] {TEST_LINK_NAME,TEST_LINK_CONTENTS},
             results.toArray(new String[0]));
        assertEquals(1,walker.getMaxDepth());
    }

    @Test
    public void nonexistentFiles()
        throws Exception
    {
        ListWalker walker=new ListWalker(false);
        walker.apply(TEST_NONEXISTENT_FILE);
        assertArrayPermutation(new String[0],walker.getFiles());
        assertArrayPermutation(new String[0],walker.getDirectories());
        assertEquals(-1,walker.getMaxDepth());

        Vector<String> results=new Vector<String>();
        walker=new ListWalker(true);
        walker.apply(TEST_NONEXISTENT_FILE,results);
        assertArrayPermutation(new String[0],walker.getFiles());
        assertArrayPermutation(new String[0],walker.getDirectories());
        assertArrayPermutation(new String[0],results.toArray(new String[0]));
        assertEquals(-1,walker.getMaxDepth());
    }

    @Test
    public void walk()
        throws Exception
    {
        String[] files=TEST_FILE_LIST;
        String[] dirs=TEST_DIR_LIST;
        if (OperatingSystem.LOCAL.isWin32()) {
            files=(String[])ArrayUtils.add(files,TEST_LINK_NAME+".lnk");
        } else if (OperatingSystem.LOCAL.isUnix()) {
            files=(String[])ArrayUtils.add(files,TEST_LINK_NAME);
        } 

        ListWalker walker=new ListWalker(false);
        walker.apply(TEST_ROOT);
        assertArrayPermutation(files,walker.getFiles());
        assertArrayPermutation(dirs,walker.getDirectories());
        assertEquals(3,walker.getMaxDepth());

        Vector<String> results=new Vector<String>();
        walker=new ListWalker(false);
        walker.apply(TEST_ROOT,results);
        assertArrayPermutation(files,walker.getFiles());
        assertArrayPermutation(dirs,walker.getDirectories());
        assertArrayPermutation
            (ArrayUtils.addAll(files,dirs),
             results.toArray(new String[0]));
        assertEquals(3,walker.getMaxDepth());

        files=TEST_FILE_LIST;
        dirs=TEST_DIR_LIST;
        if (OperatingSystem.LOCAL.isWin32()) {
            files=(String[])ArrayUtils.add(files,TEST_LINK_NAME+".lnk");
        } else if (OperatingSystem.LOCAL.isUnix()) {
            files=(String[])ArrayUtils.add(files,TEST_LINK_CONTENTS);
            dirs=(String[])ArrayUtils.add(dirs,TEST_LINK_NAME);
        } 

        walker=new ListWalker(true);
        walker.apply(TEST_ROOT);
        assertArrayPermutation(files,walker.getFiles());
        assertArrayPermutation(dirs,walker.getDirectories());
        assertEquals(3,walker.getMaxDepth());

        results=new Vector<String>();
        walker=new ListWalker(true);
        walker.apply(TEST_ROOT,results);
        assertArrayPermutation(files,walker.getFiles());
        assertArrayPermutation(dirs,walker.getDirectories());
        assertArrayPermutation
            (ArrayUtils.addAll(files,dirs),
             results.toArray(new String[0]));
        assertEquals(3,walker.getMaxDepth());
    }
}
