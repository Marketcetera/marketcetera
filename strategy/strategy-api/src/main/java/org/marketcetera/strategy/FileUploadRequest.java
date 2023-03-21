package org.marketcetera.strategy;

import org.marketcetera.admin.User;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FileUploadRequest
{
    String getFilePath();
    String getName();
    String getNonce();
    User getOwner();
    default void onProgress(double inPercentComplete)
    {
        
    }
    default void onStatus(FileUploadStatus inStatus)
    {
        
    }
    default void onError(Throwable inThrowable)
    {
        
    }
}
