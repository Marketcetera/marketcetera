package org.marketcetera.core.file;

import java.io.File;

/* $License$ */

/**
 * Receives directory watcher updates.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DirectoryWatcherSubscriber.java 83882 2014-08-01 22:31:54Z colin $
 * @since $Release$
 */
public interface DirectoryWatcherSubscriber
{
    /**
     * Receives a new or updated <code>File</code>.
     *
     * @param inFile a <code>File</code> value
     * @param inOriginalFileName a <code>String</code> value
     */
    public void received(File inFile,
                         String inOriginalFileName);
}
