package org.marketcetera.eventbus;

/* $License$ */

/**
 * Provides event bus services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EventBusService
{
    /**
     * Register the given subscriber to appropriate events on the default topic.
     *
     * @param inSubscriber an <code>Object</code> value
     */
    void register(Object inSubscriber);
    /**
     * Register the given subscriber to appropriate events on the given topic.
     *
     * @param inSubscriber an <code>Object</code> value
     * @param inTopic a <code>String</code> value
     */
    void register(Object inSubscriber,
                  String inTopic);
    /**
     * Unregister the given subscriber from the default topic.
     *
     * @param inSubscriber an <code>Object</code> value
     */
    void unregister(Object inSubscriber);
    /**
     * Unregister the given subscriber from the given topic.
     *
     * @param inSubscriber an <code>Object</code> value
     * @param inTopic a <code>String</code> value
     */
    void unregister(Object inSubscriber,
                     String inTopic);
    /**
     * Post the given event to the default topic.
     *
     * @param inEvent an <code>Object</code> value
     */
    void post(Object inEvent);
    /**
     * Post the given event to the given topics.
     *
     * @param inEvent an <code>Object</code> value
     * @param inTopics a <code>String[]</code> value
     */
    void post(Object inEvent,
              String...inTopics);
    /**
     * default topic
     */
    public static final String defaultTopic = "metc.default.topic";
}
