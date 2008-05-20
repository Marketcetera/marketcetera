package org.marketcetera.util.misc;

import org.marketcetera.core.ClassVersion;

/**
 * An enumeration of operating systems. The expected value of the
 * <code>os.name</code> system property for each operating system is
 * associated with its corresponding enumerated value.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum OperatingSystem
{
    WINDOWS_2000("Windows 2000"),
    WINDOWS_2003("Windows 2003"),
    WINDOWS_CE("Windows CE"),
    WINDOWS_XP("Windows XP"),
    WINDOWS_VISTA("Windows Vista"),
    MAC_OS_X("Mac OS X"),
    DARWIN("Darwin"),
    LINUX("Linux"),
    SOLARIS("SunOS"),
    AIX("AIX"),
    HPUX("HP-UX"),
    UNKNOWN(null);


    // CLASS DATA.

    /**
     * The label attached to Windows variants by {@link #toString()}.
     */

    public static final String LABEL_WIN32="win32";

    /**
     * The label attached to UNIX variants by {@link #toString()}.
     */

    public static final String LABEL_UNIX="unix";

    /**
     * The operating system on which this JVM is running.
     */

    public static final OperatingSystem LOCAL=
        get(SystemProperties.OS_NAME);


    // INSTANCE DATA.

    private String mJavaName;


    // CONSTRUCTORS.

    /**
     * Creates a new enumerated constant, associated with the given
     * expected value for the <code>os.name</code> system property.
     *
     * @param javaName The expected property value.
     */

    OperatingSystem(String javaName)
    {
        mJavaName=javaName;
    }


    // CLASS METHODS.

    /**
     * Returns the enumerated constant associated with the given
     * expected value for the <code>os.name</code> system
     * property. Returns {@link #UNKNOWN} if there is no match;
     * returns the first (in order or declaration) match if there is
     * more than one match.
     *
     * @param javaName The expected property value.
     *
     * @return The enumerated constant.
     */

    public static OperatingSystem get
        (String javaName)
    {
        for (OperatingSystem os:OperatingSystem.values()) {
            String enumName=os.getJavaName();
            if ((enumName!=null) && enumName.equals(javaName)) {
                return os;
            }
        }
        return UNKNOWN;
    }


    // Enum.

    @Override
    public String toString()
    {
        StringBuilder builder=new StringBuilder();
        builder.append(super.toString());
        if (getJavaName()!=null) {
            builder.append(',');
            builder.append(getJavaName());
        }
        if (isWin32()) {
            builder.append(',');
            builder.append(LABEL_WIN32);
        }
        if (isUnix()) {
            builder.append(',');
            builder.append(LABEL_UNIX);
        }
        return builder.toString();
    }


    // INSTANCE METHODS.

    /**
     * Returns the expected value of the <KBD>os.name</KBD> property
     * for the receiver.
     *
     * @return The value. It may be null for {@link #UNKNOWN}.
     */

    public String getJavaName()
    {
        return mJavaName;
    }

    /**
     * Returns true if the receiver is a Windows variant.
     *
     * @return True if so.
     */

    public boolean isWin32()
    {
        return ((this==WINDOWS_2000) ||
                (this==WINDOWS_2003) ||
                (this==WINDOWS_CE) ||
                (this==WINDOWS_XP) ||
                (this==WINDOWS_VISTA));
    }

    /**
     * Returns true if the receiver is a Unix variant.
     *
     * @return True if so.
     */

    public boolean isUnix()
    {
        return ((this==DARWIN) ||
                (this==MAC_OS_X) ||
                (this==LINUX) ||
                (this==SOLARIS) ||
                (this==AIX) ||
                (this==HPUX));
    }
}
