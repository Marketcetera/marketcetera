package org.marketcetera.util.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A {@link DirectoryWalker} which provides convenience methods to
 * initiate traversal, and (optionally) does not follow symbolic
 * links.
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class SmartLinksDirectoryWalker
    extends DirectoryWalker
{

    // INSTANCE DATA.

    private boolean mFollowLinks;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @param followLinks True if links should be followed.
     *
     * @see DirectoryWalker#DirectoryWalker()
     */

    protected SmartLinksDirectoryWalker
        (boolean followLinks)
    {
        mFollowLinks=followLinks;
    }

    /**
     * Constructor mirroring superclass constructor.
     *
     * @param followLinks True if links should be followed.
     *
     * @see DirectoryWalker#DirectoryWalker(FileFilter,int)
     */

    protected SmartLinksDirectoryWalker
        (boolean followLinks,
         FileFilter filter,
         int depthLimit)
    {
        super(filter,depthLimit);
        mFollowLinks=followLinks;
    }

    /**
     * Constructor mirroring superclass constructor.
     *
     * @param followLinks True if links should be followed.
     *
     * @see DirectoryWalker#DirectoryWalker(IOFileFilter,IOFileFilter,int)
     */

    protected SmartLinksDirectoryWalker
        (boolean followLinks,
         IOFileFilter directoryFilter,
         IOFileFilter fileFilter,
         int depthLimit) 
    {
        super(directoryFilter,fileFilter,depthLimit);
        mFollowLinks=followLinks;
    }


    // INSTANCE METHODS.

    /**
     * Returns false if the given directory is a symbolic link to a
     * directory, and links are not to be followed, thereby blocking
     * following the link during traversal. In this case, it also
     * invokes {@link DirectoryWalker#handleFile(File,int,Collection)}
     * on the link.
     *
     * @see DirectoryWalker#handleDirectory(File,int,Collection)
     */

    @SuppressWarnings("unchecked")
    @Override
    protected boolean handleDirectory
        (File directory,
         int depth,
         Collection results)
        throws IOException
    {
        if ((FileType.get(directory)!=FileType.LINK_DIR) ||
            getFollowLinks()) {
            return true;
        }
        handleFile(directory,depth,results);
        return false;
    }

    /**
     * Returns true if symbolic links to directories should be
     * followed during traversal.
     *
     * @return True if so.
     */

    public boolean getFollowLinks()
    {
        return mFollowLinks;
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

    @SuppressWarnings("unchecked")
    public void apply
        (File root,
         Collection results)
        throws IOException
    {
        FileType type=FileType.get(root);
        if (type==FileType.NONEXISTENT) {
            return;
        }
        if (type.isFile() ||
            (!getFollowLinks() && type.isSymbolicLink())) {
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

    @SuppressWarnings("unchecked")
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
