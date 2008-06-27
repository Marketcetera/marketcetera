package org.marketcetera.util.file;

import java.io.File;
import java.io.IOException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A file type. Files on NTFS are limited to the following types:
 * {@link #NONEXISTENT}, {@link #FILE}, {@link #DIR} (folder), and
 * {@link #UNKNOWN} (the file may or may not exist, and, if it does,
 * its type cannot be determined). On Unix systems, unresolvable
 * (dangling or recursive) links are {@link #NONEXISTENT}.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum FileType
{
    NONEXISTENT,
    LINK_FILE,
    FILE,
    LINK_DIR,
    DIR,
    UNKNOWN;


    // CLASS METHODS.

    /**
     * Returns the enumerated constant representing the type of the
     * given file.
     *
     * @param file The file. It may be null, in which case
     * {@link #UNKNOWN} is returned.
     *
     * @return The enumerated constant.
     */

    public static final FileType get
        (File file)
    {
        if (file==null) {
            return UNKNOWN;
        }
        try {
            if (!file.exists()) {
                return NONEXISTENT;
            }
            // EXTREME TEST 1: comment out the next line.
            // if (true) throw new IOException();
            if (file.getCanonicalFile().equals(file.getAbsoluteFile())) {
                if (file.isDirectory()) {
                    return DIR;
                }
                if (file.isFile()) {
                    return FILE;
                }
            } else {
                if (file.isDirectory()) {
                    return LINK_DIR;
                }
                if (file.isFile()) {
                    return LINK_FILE;
                }
            }
        } catch (IOException ex) {
            Messages.CANNOT_GET_TYPE.warn
                (FileType.class,ex,file.getAbsolutePath());
        }
        return UNKNOWN;
    }

    /**
     * Returns the enumerated constant representing the type of the
     * file with the given name.
     *
     * @param name The file name. It may be null, in which case {@link
     * #UNKNOWN} is returned.
     *
     * @return The enumerated constant.
     */

    public static final FileType get
        (String name)
    {
        if (name==null) {
            return UNKNOWN;
        }
        return get(new File(name));
    }


    // INSTANCE METHODS.

    /**
     * Returns true if the receiver represents a symbolic link.
     *
     * @return True if so.
     */

    public boolean isSymbolicLink()
    {
        return ((this==LINK_FILE) ||
                (this==LINK_DIR));
    }

    /**
     * Returns true if the receiver represents a directory (possibly
     * via a symbolic link).
     *
     * @return True if so.
     */

    public boolean isDirectory()
    {
        return ((this==LINK_DIR) ||
                (this==DIR));
    }

    /**
     * Returns true if the receiver represents a plain file (possibly
     * via a symbolic link).
     *
     * @return True if so.
     */

    public boolean isFile()
    {
        return ((this==LINK_FILE) ||
                (this==FILE));
    }
}
