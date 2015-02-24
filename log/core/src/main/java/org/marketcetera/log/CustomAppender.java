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
 *
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
     * Create a new CustomAppender instance.
     *
     * @param inName
     * @param inLayout
     * @param inFilter
     * @param inIgnoreExceptions
     * @param inImmediateFlush
     * @param inManager
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
    @PluginFactory
    public static CustomAppender createAppender(@PluginAttribute("name")String name,
                                                @PluginElement("Layout")Layout<?> layout,
                                                @PluginElement("Filters")Filter filter)
    {
        if(layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        CustomAppenderManager manager = CustomAppenderManager.getInstance(name,
                                                                          layout);
        return new CustomAppender(name,
                                  layout,
                                  filter,
                                  manager);
    }
    public static class CustomAppenderManager
            extends OutputStreamManager
    {
        /**
         * Create a new CustomAppenderManager instance.
         *
         * @param inOutputStream
         * @param inStreamName
         * @param inLayout
         */
        private CustomAppenderManager(OutputStream inOutputStream,
                                      String inStreamName,
                                      Layout<?> inLayout)
        {
            super(inOutputStream,
                  inStreamName,
                  inLayout);
        }
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
        private static final Map<String,CustomAppenderManager> instances = new HashMap<>();
        private static final Map<String,RedirectingOutputStream> registeredStreams = new HashMap<>();
    }
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
        private void setDestination(OutputStream inOutputStream)
        {
            destination = inOutputStream;
        }
        private OutputStream destination;
    }
    private static final long serialVersionUID = -1612799097293634437L;
}
