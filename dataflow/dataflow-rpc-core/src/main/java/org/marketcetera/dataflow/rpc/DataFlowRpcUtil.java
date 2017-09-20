package org.marketcetera.dataflow.rpc;

import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Provides data flow RPC utility methods.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class DataFlowRpcUtil
{
    /**
     * Get the RPC module URN from the given module URN.
     *
     * @param inModuleURN a <code>ModuleURN</code> value
     * @return a <code>DataFLowClientRpc.ModuleURN</code> value
     */
    public static DataFlowRpc.ModuleURN getRpcModuleUrn(ModuleURN inModuleURN)
    {
        DataFlowRpc.ModuleURN.Builder builder = DataFlowRpc.ModuleURN.newBuilder();
        builder.setValue(inModuleURN.getValue());
        return builder.build();
    }
    /**
     * Get the module URN from the given RPC value.
     *
     * @param inInstance a <code>DataFlowClientRpc.ModuleURN</code> value
     * @return a <code>ModuleURN</code> value
     */
    public static ModuleURN getModuleUrn(DataFlowRpc.ModuleURN inInstance)
    {
        return new ModuleURN(inInstance.getValue());
    }
    /**
     * Get the RPC module info from the given value.
     *
     * @param inInfo a <code>ModuleInfo</code> value
     * @return a <code>DataFlowRpc.ModuleInfo</code> value
     */
    public static DataFlowRpc.ModuleInfo getRpcModuleInfo(ModuleInfo inInfo)
    {
        DataFlowRpc.ModuleInfo.Builder builder = DataFlowRpc.ModuleInfo.newBuilder();
        try {
            builder.setPayload(marshall(inInfo));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }
    /**
     * Get the module info from the given RPC value.
     *
     * @param inInfo a <code>DataFlowRpc.ModuleInfo</code> value
     * @return a <code>ModuleInfo</code> value
     */
    public static ModuleInfo getModuleInfo(DataFlowRpc.ModuleInfo inInfo)
    {
        try {
            return unmarshall(inInfo.getPayload());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the RPC value of the given parameter.
     *
     * @param inParam an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public static String getRpcParameter(Object inParam)
    {
        try {
            return marshall(inParam);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private static String marshall(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(contextLock) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /**
     * Unmarshals an object from the given XML stream.
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    @SuppressWarnings("unchecked")
    private static <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * guards access to JAXB context objects
     */
    private static final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private static JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private static Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private static Unmarshaller unmarshaller;
    /**
     * Initialize static members
     */
    static {
        try {
            synchronized(contextLock) {
                context = JAXBContext.newInstance(new DataFlowContextClassProvider().getContextClasses());
                marshaller = context.createMarshaller();
                unmarshaller = context.createUnmarshaller();
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
