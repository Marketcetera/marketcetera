package org.marketcetera.rpc.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides common behaviors for {@link BaseRpc} services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseRpcUtil
{
    /**
     * Get the timestamp value from the given date.
     *
     * @param inTimestamp a <code>Date</code> value
     * @return an <code>Optional&lt;com.google.protobuf.Timestamp&gt;</code> value
     */
    public static Optional<com.google.protobuf.Timestamp> getTimestampValue(Date inTimestamp)
    {
        if(inTimestamp == null) {
            return Optional.empty();
        }
        return Optional.of(Timestamps.fromMillis(inTimestamp.getTime()));
    }
    /**
     * Get the date from from the given timestamp.
     *
     * @param inTimestamp a <code>com.google.protobuf.Timestamp</code> value
     * @return an <code>Optional&lt;Date&gt;</code> value
     */
    public static Optional<Date> getDateValue(com.google.protobuf.Timestamp inTimestamp)
    {
        if(inTimestamp == null) {
            return Optional.empty();
        }
        return Optional.of(new Date(Timestamps.toMillis(inTimestamp)));
    }
    /**
     * Get the string value from the given string.
     *
     * @param inValue a <code>String</code> value
     * @return an <code>Optional&lt;String&gt;</code> value
     */
    public static Optional<String> getStringValue(String inValue)
    {
        inValue = StringUtils.trimToNull(inValue);
        if(inValue == null) {
            return Optional.empty();
        }
        return Optional.of(inValue);
    }
    /**
     * Get the value represented by the given qty object.
     *
     * @param inQty a <code>BaseRpc.Qty</code> value
     * @return an <code>Optional&lt;BigDecimal&gt;</code> value
     */
    public static Optional<BigDecimal> getScaledQuantity(BaseRpc.Qty inQty)
    {
        if(inQty == null) {
            return Optional.empty();
        }
        BigDecimal base = new BigDecimal(inQty.getQty());
        int scale = inQty.getScale();
        base = base.setScale(scale,
                             RoundingMode.HALF_UP);
        base = base.movePointLeft(scale);
        return Optional.of(base);
    }
    /**
     * Get the arbitrary object contained in the given RPC value.
     *
     * @param inRpcObject a <code>BaseRpc.Object</code> value
     * @return a <code>Clazz</code> value
     */
    public static <Clazz extends Serializable> Optional<Clazz> getObject(BaseRpc.Object inRpcObject)
    {
        if(inRpcObject == null) {
            return Optional.empty();
        }
        return Optional.of(SerializationUtils.deserialize(inRpcObject.getData().toByteArray()));
    }
    /**
     * Get the RPC value of an arbitrary serializable object.
     *
     * @param inObject a <code>Serializable</code> value
     * @return an <code>Optional&lt;BaseRpc.Object&gt;</code> value
     */
    public static Optional<BaseRpc.Object> getRpcObject(Serializable inObject)
    {
        if(inObject == null) {
            return Optional.empty();
        }
        BaseRpc.Object.Builder builder = BaseRpc.Object.newBuilder();
        builder.setData(ByteString.copyFrom(SerializationUtils.serialize(inObject)));
        return Optional.of(builder.build());
    }
    /**
     * Get a qty value from the given input.
     *
     * @param inValue a <code>BigDecimal</code>value
     * @return an <code>Optional&lt;BaseRpc.Qty&gt;</code> value
     */
    public static Optional<BaseRpc.Qty> getRpcQty(BigDecimal inValue)
    {
        if(inValue == null) {
            return Optional.empty();
        }
        BigDecimal quantity = inValue.setScale(6,
                                               RoundingMode.HALF_UP);
        quantity = quantity.movePointRight(6);
        BaseRpc.Qty.Builder qtyBuilder = BaseRpc.Qty.newBuilder();
        qtyBuilder.setQty(quantity.longValue());
        qtyBuilder.setScale(6);
        return Optional.of(qtyBuilder.build());
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
