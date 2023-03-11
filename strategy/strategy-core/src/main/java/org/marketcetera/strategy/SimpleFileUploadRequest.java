package org.marketcetera.strategy;

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
    public SimpleFileUploadRequest(String inFilePath,
                                   String inNonce)
    {
        filePath = inFilePath;
        nonce = inNonce;
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
    /**
     * Sets the filePath value.
     *
     * @param inFilePath a <code>String</code> value
     */
    public void setFilePath(String inFilePath)
    {
        filePath = inFilePath;
    }
    /**
     * Sets the nonce value.
     *
     * @param inNonce a <code>String</code> value
     */
    public void setNonce(String inNonce)
    {
        nonce = inNonce;
    }
    private String filePath;
    private String nonce;
}
