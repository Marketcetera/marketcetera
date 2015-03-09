package org.marketcetera.photon;

import java.io.IOException;
import java.io.OutputStream;

import org.marketcetera.log.CustomAppender;
import org.marketcetera.photon.ui.PhotonConsole;

/* $License$ */

/**
 * Connects the logging system to the Photon Console View.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PhotonConsoleAppender
{
    /**
     * Create a new PhotonConsoleAppender instance.
     *
     * @param inPhotonConsole a <code>PhotonConsole</code> value
     */
    public PhotonConsoleAppender(PhotonConsole inPhotonConsole)
    {
        photonConsole = inPhotonConsole;
    }
    /**
     * Starts the object.
     */
    public void start()
    {
        // register an output stream to receive events with the CustomAppender
        CustomAppender.CustomAppenderManager.registerStream("PhotonConsoleDebug",
                                                            new PhotonConsoleDebugStream());
        CustomAppender.CustomAppenderManager.registerStream("PhotonConsoleInfo",
                                                            new PhotonConsoleInfoStream());
        CustomAppender.CustomAppenderManager.registerStream("PhotonConsoleWarn",
                                                            new PhotonConsoleWarnStream());
        CustomAppender.CustomAppenderManager.registerStream("PhotonConsoleError",
                                                            new PhotonConsoleErrorStream());
    }
    /**
     * Provides common Photon Console output stream behavior.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private abstract class AbstractPhotonConsoleOutputStream
            extends OutputStream
    {
        /* (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int inB)
                throws IOException
        {
            getOutputStream().write(inB);
        }
        /**
         * Gets the OutputStream value.
         *
         * @return an <code>OutputStream</code> value
         */
        protected abstract OutputStream getOutputStream();
    }
    /**
     * Connects the Photon Console Debug Stream to the Photon Console.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class PhotonConsoleDebugStream
            extends AbstractPhotonConsoleOutputStream
    {
        /* (non-Javadoc)
         * @see org.marketcetera.photon.NewPhotonConsoleAppender.AbstractPhotonConsoleOutputStream#getOutputStream()
         */
        @Override
        protected OutputStream getOutputStream()
        {
            return photonConsole.getDebugMessageStream();
        }
    }
    /**
     * Connects the Photon Console Info Stream to the Photon Console.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class PhotonConsoleInfoStream
            extends AbstractPhotonConsoleOutputStream
    {
        /* (non-Javadoc)
         * @see org.marketcetera.photon.NewPhotonConsoleAppender.AbstractPhotonConsoleOutputStream#getOutputStream()
         */
        @Override
        protected OutputStream getOutputStream()
        {
            return photonConsole.getInfoMessageStream();
        }
    }
    /**
     * Connects the Photon Console Warn Stream to the Photon Console.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class PhotonConsoleWarnStream
            extends AbstractPhotonConsoleOutputStream
    {
        /* (non-Javadoc)
         * @see org.marketcetera.photon.NewPhotonConsoleAppender.AbstractPhotonConsoleOutputStream#getOutputStream()
         */
        @Override
        protected OutputStream getOutputStream()
        {
            return photonConsole.getWarnMessageStream();
        }
    }
    /**
     * Connects the Photon Console Error Stream to the Photon Console.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class PhotonConsoleErrorStream
            extends AbstractPhotonConsoleOutputStream
    {
        /* (non-Javadoc)
         * @see org.marketcetera.photon.NewPhotonConsoleAppender.AbstractPhotonConsoleOutputStream#getOutputStream()
         */
        @Override
        protected OutputStream getOutputStream()
        {
            return photonConsole.getErrorMessageStream();
        }
    }
    /**
     * provides Photon console services
     */
    private final PhotonConsole photonConsole;
}
