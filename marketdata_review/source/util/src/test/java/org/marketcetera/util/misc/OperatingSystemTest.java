package org.marketcetera.util.misc;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.misc.OperatingSystem.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class OperatingSystemTest
    extends TestCaseBase
{
    private static void single
        (OperatingSystem os,
         boolean isWin32,
         boolean isUnix,
         String string)
    {
        assertEquals(os,get(os.getJavaName()));
        assertEquals(isWin32,os.isWin32());
        assertEquals(isUnix,os.isUnix());
        assertEquals(string,os.toString());
    }

    @Test
    public void all()
    {
        single(WINDOWS_2000, true,false, "WINDOWS_2000,Windows 2000,win32");
        single(WINDOWS_2003, true,false, "WINDOWS_2003,Windows 2003,win32");
        single(WINDOWS_XP,   true,false, "WINDOWS_XP,Windows XP,win32");
        single(WINDOWS_CE,   true,false, "WINDOWS_CE,Windows CE,win32");
        single(WINDOWS_VISTA,true,false, "WINDOWS_VISTA,Windows Vista,win32");

        single(DARWIN,       false,true, "DARWIN,Darwin,unix");
        single(MAC_OS_X,     false,true, "MAC_OS_X,Mac OS X,unix");
        single(LINUX,        false,true, "LINUX,Linux,unix");
        single(SOLARIS,      false,true, "SOLARIS,SunOS,unix");
        single(AIX,          false,true, "AIX,AIX,unix");
        single(HPUX,         false,true, "HPUX,HP-UX,unix");

        single(UNKNOWN,      false,false,"UNKNOWN");

        assertEquals(UNKNOWN,get("nonexistent"));
        assertEquals(UNKNOWN,get(null));
    }

    @Test
    public void runningOnKnownOpearatingSystem()
    {
        assertNotSame(UNKNOWN,LOCAL);
    }
}
