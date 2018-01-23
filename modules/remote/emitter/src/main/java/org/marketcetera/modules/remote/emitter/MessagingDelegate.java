package org.marketcetera.modules.remote.emitter;

import java.io.IOException;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.activemq.transport.TransportListener;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.util.ErrorHandler;

/* $License$ */
/**
 * Provides means to communicate with JMS.
 * This class is not meant to be used by the clients of this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MessagingDelegate
        implements ExceptionListener,TransportListener,ErrorHandler
{
    /**
     * Receives an object
     *
     * @param inObject The received object.
     */

    public void handleMessage(Object inObject) {
        mDataEmitter.receive(inObject);
    }

    @Override
    public void onException(JMSException inException) {
        mDataEmitter.onException(inException);
    }

    @Override
    public void onException(IOException inException) {
        mDataEmitter.onException(inException);
    }
    /* (non-Javadoc)
     * @see org.springframework.util.ErrorHandler#handleError(java.lang.Throwable)
     */
    @Override
    public void handleError(Throwable inT)
    {
        if(inT instanceof Exception) {
            mDataEmitter.onException((Exception)inT);
        }
    }
    @Override
    public void onCommand(Object inObject) {
        //do nothing.
    }

    @Override
    public void transportInterupted() {
        //do nothing
    }

    @Override
    public void transportResumed() {
        //do nothing
    }

    /**
     * Sets the module instance that should be receiving the objects and
     * errors recived by this delegate via {@link #handleMessage(Object)},
     * {@link #onException(JMSException)} & {@link #onException(IOException)}. 
     *
     * @param inEmitterModule the module instance.
     */
    void setDataEmitter(RemoteDataEmitter inEmitterModule) {
        mDataEmitter = inEmitterModule;
    }

    private volatile RemoteDataEmitter mDataEmitter;
}