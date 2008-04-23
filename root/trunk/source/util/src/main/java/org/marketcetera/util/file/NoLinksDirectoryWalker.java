package org.marketcetera.util.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * A {@link DirectoryWalker} which does not follow symbolic links, and
 * provides convenience methods to initiate traversal.
 */

public abstract class NoLinksDirectoryWalker
    extends DirectoryWalker
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see DirectoryWalker#DirectoryWalker()
     */

    protected NoLinksDirectoryWalker() {}

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see DirectoryWalker#DirectoryWalker(FileFilter,int)
     */

    protected NoLinksDirectoryWalker
        (FileFilter filter,
         int depthLimit)
    {
        super(filter,depthLimit);
    }

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see DirectoryWalker#DirectoryWalker(IOFileFilter,IOFileFilter,int)
     */

    protected NoLinksDirectoryWalker
        (IOFileFilter directoryFilter,
         IOFileFilter fileFilter,
         int depthLimit) 
    {
        super(directoryFilter,fileFilter,depthLimit);
    }


    // INSTANCE METHODS.

    /**
     * Returns false if the given directory is a symbolic link to a
     * directory, thereby blocking following the link during
     * traversal.
     *
     * @see DirectoryWalker#handleDirectory(File,int,Collection)
     */

    @Override
    protected boolean handleDirectory
        (File directory,
         int depth,
         Collection results)
        throws IOException
    {
        return (FileType.get(directory)!=FileType.LINK_DIR);
    }
 
    /**
     * Traverses the file tree rooted at the given root. The root may
     * be nonexistent (no-op) or a plain file (which becomes the only
     * file visited).
     *
     * @param root The root.
     * @param results An object passed intact into {@link
     * DirectoryWalker} callbacks.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public void apply
        (File root,
         Collection results)
        throws IOException
    {
        FileType type=FileType.get(root);
        if (type==FileType.NONEXISTENT) {
            return;
        }
        if (type.isFile()) {
            handleFile(root,0,results);
            return;
        }
        walk(root,results);
    }

    /**
     * Traverses the file tree rooted at the given root. The root may
     * be nonexistent (no-op) or a plain file (which becomes the only
     * file visited). The <code>results</code> argument in all {@link
     * DirectoryWalker} callbacks will be null.
     *
     * @param root The root.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public void apply
        (File root)
        throws IOException
    {
        apply(root,null);
    }

    /**
     * Traverses the file tree rooted at the file with the given
     * name. The root may be nonexistent (no-op) or a plain file
     * (which becomes the only file visited).
     *
     * @param name The file name.
     * @param results An object passed intact into {@link
     * DirectoryWalker} callbacks.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public void apply
        (String name,
         Collection results)
        throws IOException
    {
        apply(new File(name),results);
    }

    /**
     * Traverses the file tree rooted at the file with the given
     * name. The root may be nonexistent (no-op) or a plain file
     * (which becomes the only file visited). The <code>results</code>
     * argument in all {@link DirectoryWalker} callbacks will be null.
     *
     * @param name The file name.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public void apply
        (String name)
        throws IOException
    {
        apply(name,null);
    }
}
