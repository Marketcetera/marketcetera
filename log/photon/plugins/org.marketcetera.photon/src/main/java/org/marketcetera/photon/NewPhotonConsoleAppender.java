package org.marketcetera.photon;

import java.io.IOException;
import java.io.OutputStream;

import org.marketcetera.log.CustomAppender;
import org.marketcetera.photon.ui.PhotonConsole;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NewPhotonConsoleAppender
{
    public NewPhotonConsoleAppender(PhotonConsole inPhotonConsole)
    {
        photonConsole = inPhotonConsole;
    }
    private final PhotonConsole photonConsole;
    /**
     *
     *
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
        protected abstract OutputStream getOutputStream();
    }
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
}
//@Plugin(name = "Stub", category = "Core", elementType = "appender", printObject = true)
//public final class StubAppender extends OutputStreamAppender {
// 
//    private StubAppender(String name, Layout layout, Filter filter, StubManager manager,
//                         boolean ignoreExceptions) {
//    }
// 
//    @PluginFactory
//    public static StubAppender createAppender(@PluginAttribute("name") String name,
//                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
//                                              @PluginElement("Layout") Layout layout,
//                                              @PluginElement("Filters") Filter filter) {
// 
//        if (name == null) {
//            LOGGER.error("No name provided for StubAppender");
//            return null;
//        }
// 
//        StubManager manager = StubManager.getStubManager(name);
//        if (manager == null) {
//            return null;
//        }
//        if (layout == null) {
//            layout = PatternLayout.createDefaultLayout();
//        }
//        return new StubAppender(name, layout, filter, manager, ignoreExceptions);
//    }
//}