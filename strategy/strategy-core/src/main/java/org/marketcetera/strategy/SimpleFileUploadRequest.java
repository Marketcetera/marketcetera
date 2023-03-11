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
public class SimpleFileUploadRequest
        implements FileUploadRequest
{
    /**
     * Create a new SimpleFileUploadRequest instance.
     *
     * @param inName
     * @param inNonce
     * @param inFilePath
     * @param inOwner
     */
    public SimpleFileUploadRequest(String inName,
                                   String inNonce,
                                   String inFilePath,
                                   User inOwner)
    {
        filePath = inFilePath;
        name = inName;
        owner = inOwner;
        nonce = inNonce;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.FileUploadRequest#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.FileUploadRequest#getOwner()
     */
    @Override
    public User getOwner()
    {
        return owner;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.FileUploadRequest#getFilePath()
     */
    @Override
    public String getFilePath()
    {
        return filePath;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.FileUploadRequest#getNonce()
     */
    @Override
    public String getNonce()
    {
        return nonce;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleFileUploadRequest [name=").append(name).append(", nonce=").append(nonce)
                .append(", filePath=").append(filePath).append(", owner=").append(owner).append("]");
        return builder.toString();
    }
    private final String nonce;
    private final String filePath;
    private final String name;
    private final User owner;
}
