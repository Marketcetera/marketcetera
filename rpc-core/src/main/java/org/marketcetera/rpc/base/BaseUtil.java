package org.marketcetera.rpc.base;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Maps;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides common behaviors for {@link BaseRpc} services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseUtil
{
    /**
     * Get the value represented by the given qty object.
     *
     * @param inQty a <code>BaseRpc.Qty</code> value
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal getScaledQuantity(BaseRpc.Qty inQty)
    {
        BigDecimal base = new BigDecimal(inQty.getQty());
        int scale = inQty.getScale();
        base = base.setScale(scale,
                             RoundingMode.HALF_UP);
        base = base.movePointLeft(scale);
        return base;
    }
    /**
     * Get a qty value from the given input.
     *
     * @param inValue a <code>BigDecimal</code>value
     * @return a <code>BaseRpc.Qty</code> value
     */
    public static BaseRpc.Qty getQtyValueFrom(BigDecimal inValue)
    {
        BigDecimal quantity = inValue.setScale(6,
                                               RoundingMode.HALF_UP);
        quantity = quantity.movePointRight(6);
        BaseRpc.Qty.Builder qtyBuilder = BaseRpc.Qty.newBuilder();
        qtyBuilder.setQty(quantity.longValue());
        qtyBuilder.setScale(6);
        return qtyBuilder.build();
    }
    /**
     * Get an RPC map from the given map.
     *
     * @param inMap a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>BaseRpc.Map</code> value
     */
    public static BaseRpc.Map getRpcMap(Map<String,String> inMap)
    {
        BaseRpc.Map.Builder mapBuilder = BaseRpc.Map.newBuilder();
        if(inMap != null) {
            BaseRpc.KeyValuePair.Builder keyValuePairBuilder = BaseRpc.KeyValuePair.newBuilder();
            for(Map.Entry<String,String> entry : inMap.entrySet()) {
                keyValuePairBuilder.setKey(entry.getKey());
                keyValuePairBuilder.setValue(entry.getValue());
                mapBuilder.addKeyValuePairs(keyValuePairBuilder.build());
                keyValuePairBuilder.clear();
            }
        }
        return mapBuilder.build();
    }
    /**
     * Get a map value from the given RPC map.
     *
     * @param inMap a <code>BaseRpc.Map</code> value
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public static Map<String,String> getMap(BaseRpc.Map inMap)
    {
        Map<String,String> map = Maps.newHashMap();
        for(BaseRpc.KeyValuePair rpcKeyValuePair : inMap.getKeyValuePairsList()) {
            map.put(rpcKeyValuePair.getKey(),
                    rpcKeyValuePair.getValue());
        }
        return map;
    }
    /**
     * Provides common behavior for message listener proxies.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static abstract class AbstractClientListenerProxy<ListenerResponseClazz,MessageClazz,MessageListenerClazz>
            implements StreamObserver<ListenerResponseClazz>
    {
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
         */
        @Override
        public void onNext(ListenerResponseClazz inResponse)
        {
            SLF4JLoggerProxy.trace(this,
                                   "{} received {}",
                                   getId(),
                                   inResponse);
            MessageClazz message;
            try {
                message = translateMessage(inResponse);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Error translating message",
                                                 e);
                return;
            }
            if(message == null) {
                return;
            }
            try {
                sendMessage(messageListener,
                            message);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Error sending message",
                                                 e);
            }
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inT)
        {
            SLF4JLoggerProxy.warn(this,
                                  inT,
                                  "{}",
                                  getId());
            PlatformServices.handleException(this,
                                             "Remote Error",
                                             inT);
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onCompleted()
         */
        @Override
        public void onCompleted()
        {
            SLF4JLoggerProxy.trace(this,
                                   "{} completed",
                                   getId());
        }
        /**
         * Get the id value.
         *
         * @return a <code>String</code> value
         */
        public String getId()
        {
            return id;
        }
        /**
         * Translate the message contained in the given response.
         *
         * @param inResponse a <code>ListenerResponseClazz</code> value
         * @return a <code>MessageClazz</code> value
         */
        protected abstract MessageClazz translateMessage(ListenerResponseClazz inResponse);
        /**
         * Send the given message to the given message listener.
         *
         * @param inMessageListener a <code>MessageListenerClazz</code> value
         * @param inMessage a <code>MessageClazz</code> value
         */
        protected abstract void sendMessage(MessageListenerClazz inMessageListener,
                                            MessageClazz inMessage);
        /**
         * Get the messageListener value.
         *
         * @return a <code>MessageListenerClazz</code> value
         */
        protected MessageListenerClazz getMessageListener()
        {
            return messageListener;
        }
        /**
         * Create a new AbstractListenerProxy instance.
         *
         * @param inMessageListener a <code>MessageListenerClazz</code> value
         */
        protected AbstractClientListenerProxy(MessageListenerClazz inMessageListener)
        {
            messageListener = inMessageListener;
        }
        /**
         * message listener to receive the messages
         */
        private final MessageListenerClazz messageListener;
        /**
         * unique id value
         */
        private final String id = UUID.randomUUID().toString();
    }
    /**
     * Provides common behaviors for listener proxies.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class AbstractServerListenerProxy<ResponseClazz>
    {
        /**
         * Get the id value.
         *
         * @return a <code>String</code> value
         */
        public String getId()
        {
            return id;
        }
        /**
         * Closes the connection with the RPC client call.
         */
        public void close()
        {
            observer.onCompleted();
        }
        /**
         * Get the observer value.
         *
         * @return a <code>StreamObserver&lt;ResponseClazz&gt;</code> value
         */
        protected StreamObserver<ResponseClazz> getObserver()
        {
            return observer;
        }
        /**
         * Create a new AbstractListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;ResponseClazz&gt;</code> value
         */
        protected AbstractServerListenerProxy(String inId,
                                              StreamObserver<ResponseClazz> inObserver)
        {
            id = inId;
            observer = inObserver;
        }
        /**
         * listener id uniquely identifies this listener
         */
        private final String id;
        /**
         * provides the connection to the RPC client call
         */
        private final StreamObserver<ResponseClazz> observer;
    }
}
