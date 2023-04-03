package org.marketcetera.strategy;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Holds the information that tracks a strategy file upload process.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FileUploadRequest
{
    /**
     * Get the upload file path.
     *
     * @return a <code>String</code> value
     */
    String getFilePath();
    /**
     * Get the human-readable strategy name.
     *
     * @return a <code>String</code> value
     */
    String getName();
    /**
     * Get the one-time nonce for this strategy upload.
     *
     * @return a <code>String</code> value
     */
    String getNonce();
    /**
     * Get the owner of this strategy.
     *
     * @return a <code>User</code> value
     */
    User getOwner();
    /**
     * Report percent complete in a range of [0..1].
     *
     * @param inPercentComplete in a <code>double</code> value
     */
    default void onProgress(double inPercentComplete) {}
    /**
     * Report changes in upload status.
     *
     * @param inStatus a <code>FileUploadStatus</code> value
     */
    default void onStatus(FileUploadStatus inStatus) {}
    /**
     * Report errors that occur, if any, during the upload process.
     *
     * @param inThrowable a <code>Throwable</code> value
     */
    default void onError(Throwable inThrowable) {}
}
