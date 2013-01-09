package org.marketcetera.util.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A thread that consumes an input stream.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
class InputThread
    extends Thread
{

    // INSTANCE DATA.

    private String mCommand;
    private InputStream mIn;
    private OutputStream mOut;
    private boolean mCloseOut;


    // CONSTRUCTORS.

    /**
     * Creates a new thread that consumes the given input stream of a
     * process (which is fed by the standard output stream of the
     * process). The input stream is copied to the given output stream
     * in its entirety. Upon completion (successful or not), the input
     * stream is closed; the output stream may also be closed
     * depending on the given setting. I/O errors are logged, and the
     * log messages include the given command that initiated the
     * process.
     *
     * @param command The process command.
     * @param in The input stream (standard output stream of the
     * process).
     * @param out The output stream.
     * @param closeOut True if the output stream should be closed.
     */

    InputThread
        (String command,
         InputStream in,
         OutputStream out,
         boolean closeOut)
    {
        super(command);
        setDaemon(true);
        mCommand=command;
        mIn=in;
        mOut=out;
        mCloseOut=closeOut;
    }


    // Thread.

    @Override
    public void run()
    {
        CloseableRegistry r=new CloseableRegistry();
        r.register(mIn);
        if (mCloseOut) {
            r.register(mOut);
        }
        try {
            IOUtils.copyLarge(mIn,mOut);
        } catch (IOException ex) {
            Messages.CANNOT_COPY_OUTPUT.error(this,ex,mCommand);
        } finally {
            r.close();
        }
    }
}
