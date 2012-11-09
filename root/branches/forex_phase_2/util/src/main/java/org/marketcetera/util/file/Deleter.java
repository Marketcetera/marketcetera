package org.marketcetera.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Deletes a file or directory recursively. It does not follow
 * symbolic links in the process.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Deleter
{

    // CLASS DATA.

    /**
     * A {@link SmartLinksDirectoryWalker} that does not follow
     * symbolic links, and deletes all files it traverses.
     */

    @ClassVersion("$Id$")
    private static final class RecursiveDeleter
        extends SmartLinksDirectoryWalker
    {

        // CONSTRUCTORS.

        /**
         * Creates a new recursive deleter.
         */

        public RecursiveDeleter()
        {
            super(false);
        }


        // INSTANCE METHODS.

        /**
         * Deletes the given directory.
         *
         * @see DirectoryWalker#handleDirectoryEnd(File,int,Collection)
         */

        @SuppressWarnings("unchecked")
        @Override
        protected void handleDirectoryEnd
            (File directory,
             int depth,
             Collection results)
            throws IOException
        {
            deleteWrap(directory);
        }

        /**
         * Deletes the given file.
         *
         * @see DirectoryWalker#handleFile(File,int,Collection)
         */

        @SuppressWarnings("unchecked")
        @Override
        protected void handleFile
            (File file,
             int depth,
             Collection results)
            throws IOException
        {
            deleteWrap(file);
        }

        /**
         * Deletes the given file.
         *
         * @param file The file.
         *
         * @throws I18NException Thrown if an I/O error occurs.
         */

        public void applyUnwrap
            (File file)
            throws I18NException
        {
            try {
                apply(file);
            } catch (IOException ex) {
                throw (I18NException)ex.getCause();
            }
        }
    }


    // CLASS METHODS.

    /**
     * Deletes the given file. If the file represents a directory, it
     * must be empty.
     *
     * @param file The file.
     *
     * @throws IOException Thrown if an I/O error occurs. It has no
     * message and wraps an {@link I18NException}.
     */

    private static void deleteWrap
        (File file)
        throws IOException
    {
        // EXTREME TEST 1: comment out if-clause and closing brace.
        if (!file.delete()) {
            throw new IOException
                (null,new I18NException
                 (new I18NBoundMessage1P
                  (Messages.CANNOT_DELETE,file.getAbsolutePath())));
        }
    }

    /**
     * Deletes the file tree rooted at the given root. It does not
     * follow symbolic links in the process. The root may be
     * nonexistent (or no-op).
     *
     * @param root The root.
     *
     * @throws I18NException Thrown if an I/O error occurs.
     */

    public static void apply
        (File root)
        throws I18NException
    {
        (new RecursiveDeleter()).applyUnwrap(root);
    }

    /**
     * Deletes the file tree rooted at the file with the given
     * name. It does not follow symbolic links in the process. The
     * root may be nonexistent (no-op).
     *
     * @param name The file name.
     *
     * @throws I18NException Thrown if an I/O error occurs.
     */

    public static void apply
        (String name)
        throws I18NException
    {
        apply(new File(name));
    }
}
