package org.marketcetera.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.marketcetera.log.CustomAppender.CustomAppenderManager;

/* $License$ */

/**
 * Provides a custom logging appender that can be used to redirect logging input to a specified output stream.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@Plugin(name="CustomAppender",category="Core",elementType ="appender",printObject=true)
public class CustomAppender
        extends AbstractOutputStreamAppender<CustomAppenderManager>
{
    /**
     * Creates a <code>CustomAppender</code> value.
     *
     * @param inName a <code>String</code> value
     * @param inLayout a <code>Layout&lt;?&gt;</code> value
     * @param inFilter a <code>Filter</code> value
     * @return a <code>CustomAppender</code> value
     */
    @PluginFactory
    public static CustomAppender createAppender(@PluginAttribute("name")String inName,
                                                @PluginElement("Layout")Layout<?> inLayout,
                                                @PluginElement("Filters")Filter inFilter)
    {
        if(inLayout == null) {
            inLayout = PatternLayout.createDefaultLayout();
        }
        CustomAppenderManager manager = CustomAppenderManager.getInstance(inName,
                                                                          inLayout);
        return new CustomAppender(inName,
                                  inLayout,
                                  inFilter,
                                  manager);
    }
    /**
     * Create a new CustomAppender instance.
     *
     * @param inName a <code>String</code> value
     * @param inLayout a <code>Layout&lt;? extends Serializable&gt;</code>value
     * @param inFilter a <code>Filter</code> value
     * @param inManager a <code>CustomAppenderManager</code> value
     */
    protected CustomAppender(String inName,
                             Layout<? extends Serializable> inLayout,
                             Filter inFilter,
                             CustomAppenderManager inManager)
    {
        super(inName,
              inLayout,
              inFilter,
              false,
              false,
              inManager);
    }
    /**
     * Manages the <code>CustomAppender</code> resources.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class CustomAppenderManager
            extends OutputStreamManager
    {
        /**
         * Registers the given <code>OutputStream</code> for the given stream name.
         *
         * @param inStreamName a <code>String</code> value
         * @param inStream an <code>OutputStream</code> value
         */
        public static void registerStream(String inStreamName,
                                          OutputStream inStream)
        {
            RedirectingOutputStream registeredStream = registeredStreams.get(inStreamName);
            if(registeredStream == null) {
                registeredStream = new RedirectingOutputStream();
                registeredStreams.put(inStreamName,
                                      registeredStream);
            }
            registeredStream.setDestination(inStream);
        }
        /**
         * Create a new CustomAppenderManager instance.
         *
         * @param inOutputStream an <code>OutputStream</code> value
         * @param inStreamName a <code>String</code> value
         * @param inLayout a <code>Layout&lt;?&gt;</code>value
         */
        private CustomAppenderManager(OutputStream inOutputStream,
                                      String inStreamName,
                                      Layout<?> inLayout)
        {
            super(inOutputStream,
                  inStreamName,
                  inLayout);
        }
        /**
         * Gets the <code>CustomAppenderManager</code> instance for the given stream name and layout.
         *
         * @param inStreamName a <code>String</code> value
         * @param inLayout a <code>Layout&lt;?&gt;</code>value
         * @return a <code>CustomAppenderManager</code> value
         */
        private static CustomAppenderManager getInstance(String inStreamName,
                                                         Layout<?> inLayout)
        {
            CustomAppenderManager instance = instances.get(inStreamName);
            if(instance != null) {
                return instance;
            }
            RedirectingOutputStream registeredStream = registeredStreams.get(inStreamName);
            if(registeredStream == null) {
                registeredStream = new RedirectingOutputStream();
                registeredStreams.put(inStreamName,
                                      registeredStream);
            }
            instance = new CustomAppenderManager(registeredStream,
                                                 inStreamName,
                                                 inLayout);
            instances.put(inStreamName,
                          instance);
            return instance;
        }
        /**
         * tracks manager instances by stream name
         */
        private static final Map<String,CustomAppenderManager> instances = new HashMap<>();
        /**
         * tracks stream redirectors by stream name
         */
        private static final Map<String,RedirectingOutputStream> registeredStreams = new HashMap<>();
    }
    /**
     * Accepts stream output, redirecting it to another stream if applicable.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class RedirectingOutputStream
            extends OutputStream
    {
        /* (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int inB)
                throws IOException
        {
            if(destination != null) {
                destination.write(inB);
            }
        }
        /**
         * Sets the destination output stream.
         * 
         * @param inOutputStream an <code>OutputStream</code> value
         */
        private void setDestination(OutputStream inOutputStream)
        {
            destination = inOutputStream;
        }
        /**
         * destination stream, may be <code>null</code>
         */
        private OutputStream destination;
    }
    private static final long serialVersionUID = -1612799097293634437L;
}
