package org.marketcetera.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A file type. Files on NTFS are limited to the following types:
 * {@link #NONEXISTENT}, {@link #FILE}, {@link #DIR} (folder), and
 * {@link #UNKNOWN} (the file may or may not exist, and, if it does,
 * its type cannot be determined).
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
    LINK_UNKNOWN,
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

            // Obtain parent safely.

            File absFile=file.getAbsoluteFile();
            if (absFile==null) {
                return UNKNOWN;
            }
            File parent=absFile.getParentFile();

            // Nonexistent files (per Java).

            if (!file.exists()) {

                // Java reports unresolvable (dangling or recursive)
                // links as not existing. However, we can distinguish
                // such a bad link from a truly nonexistent file by
                // checking whether its parent lists it as a child.

                if (parent!=null) {
                    String[] children=parent.list();
                    if ((children!=null) &&
                        (Arrays.asList(children).contains(file.getName()))) {
                        return LINK_UNKNOWN;
                    }
                }
                return NONEXISTENT;
            }

            // EXTREME TEST 1: comment out the next line.
            // if (true) throw new IOException();

            // Ensure parent's path within file's path does not
            // contain symlinks or other aliases.

            // For example, if d is a directory, l -> d is a link and
            // d/f is a file, then l/f is a file. Its absolute path is
            // 'xxx/l/f' and its canonical is 'xxx/d/f'. They are
            // distinct, hence we first need to canonicalize the path
            // of the parent l before comparing the absolute and
            // canonical paths of f itself.

            // Relative names for f result in additional complexity. f
            // may have no parent in this case (meaning that it may
            // have a parent directory, but getParentFile() may return
            // null), yet its abolute and canonical paths may be
            // distinct: under Windows XP, the canonical path will
            // choose full names for ancestral directories, while the
            // absolute path will choose DOS short names. As a result,
            // we still need to canonicalize the path of f's parent
            // before comparing f's absolute and canonical names. We
            // do that by obtaining the parent not of f, but of the
            // absolute f (we don't want the parent of the canonical f
            // because, if f is a link f -> d/f2, the parent of the
            // canonical f could be f2's parent, which is a wholly
            // different directory, the subdirectory d in this
            // example).

            if (parent!=null) {
                File pCanFile=parent.getCanonicalFile();
                if (pCanFile==null) {
                    return UNKNOWN;
                }
                file=new File(pCanFile,file.getName());
            }

            // Compare file's absolute name against canonical name.

            absFile=file.getAbsoluteFile();
            File canFile=file.getCanonicalFile();
            if ((canFile==null) || (absFile==null)) {
                return UNKNOWN;
            }
            if (canFile.equals(absFile)) {
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
                (this==LINK_DIR) ||
                (this==LINK_UNKNOWN));
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
