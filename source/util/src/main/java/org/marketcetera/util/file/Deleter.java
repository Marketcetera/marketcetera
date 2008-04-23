package org.marketcetera.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.marketcetera.util.except.I18NException;

/**
 */

public class Deleter
{

    // CLASS DATA.

    /**
     * Constructor mirroring superclass constructor.
     */

    private static final class RecursiveDeleter
        extends NoLinksDirectoryWalker
    {

        // INSTANCE METHODS.

        @Override
        protected void handleDirectoryEnd
            (File directory,
             int depth,
             Collection results)
            throws IOException
        {
            deleteWrap(directory);
        }

        @Override
        protected void handleFile
            (File file,
             int depth,
             Collection results)
            throws IOException
        {
            deleteWrap(file);
        }

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

    private static void deleteWrap
        (File file)
        throws IOException
    {
        try {
            delete(file);
        } catch (I18NException ex) {
            throw new IOException(null,ex);
        }
    }

    private static void delete
        (File file)
        throws I18NException
    {
        if (!file.delete()) {
            throw new I18NException
                (Messages.PROVIDER,Messages.CANNOT_DELETE,
                 file.getAbsolutePath());
        }
    }

    public static void apply
        (File file)
        throws I18NException
    {
        (new RecursiveDeleter()).applyUnwrap(file);
    }

    public static void apply
        (String file)
        throws I18NException
    {
        apply(new File(file));
    }
}
