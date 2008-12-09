package org.marketcetera.modules.cep.system;

import org.marketcetera.core.Pair;
import org.marketcetera.module.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple straight-through implementation of the CEP module that
 * filters received data and only emits data that match the type specified in the query.
 * Only allows for "select * from xyz" type of queries.
 * The XYZ types can be any alias listed in @{@link CEPDataTypes} or any valid Java class name
 *
 *
 * The maps in the data structure are as follows:
 * <ul>
 * <li>{@link #mTypeLookupMap} is a mapping of all expected types to underlying classes (ie string --> class)
 * for the purposes of doing the 'select * from <em>alias</em>' query</li>
 * <li>{@link #mRequestMap} - map of {@link RequestID} --> pair of {class, {@link DataEmitterSupport}}. Given a requestID,
 * we can get the class and corresponding emitter registered to listen on that type. For cancels,
 * we pull out all the classes, and remove the emitter subscribed to listen on that event type</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CEPSystemProcessor extends Module
        implements DataReceiver, DataEmitter {

    private static final String QUERY_DELIM = "[ \t]+";
    private static final String QUERY_PREFIX = "select * from ";        //$NON-NLS-1$
    private static final String[] QUERY_SPLIT = QUERY_PREFIX.split(QUERY_DELIM);

    private final HashMap<RequestID, Pair<Class<?>, DataEmitterSupport>> mRequestMap;

    private final static Map<String, Class<?>> mTypeLookupMap = new HashMap<String, Class<?>>(20);

    static {
        for (Pair<String, Class<?>> pair : CEPDataTypes.REQUEST_PRECANNED_TYPES) {
            mTypeLookupMap.put(pair.getFirstMember(),  pair.getSecondMember());
        }

    }

    protected CEPSystemProcessor(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        mRequestMap = new HashMap<RequestID, Pair<Class<?>, DataEmitterSupport>>();
    }

    @Override
    protected void preStart() throws ModuleException {
        // no-op
    }

    @Override
    protected void preStop() throws ModuleException {
        mRequestMap.clear();
    }

    /** Map the incoming data to some type, find the list of all {@link DataEmitterSupport} objects
     * and send the data on its way there
     * Ignore the flowID
     * This is a very inefficient implementation - just iterate over all known requests
     * and if we find a match send the data that way
     */
    @Override
    public void receiveData(DataFlowID inFlowID, Object inData) throws ReceiveDataException {
        if(inData != null) {
            for (Pair<Class<?>, DataEmitterSupport> classEmitterPair : mRequestMap.values()) {
                if(classEmitterPair.getFirstMember().isAssignableFrom(inData.getClass())) {
                    classEmitterPair.getSecondMember().send(inData);
                }
            }
        }
        //ignore null data
    }

    @Override
    public void requestData(DataRequest inRequest, DataEmitterSupport inSupport) throws RequestDataException {
        if(inRequest == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        String query;
        if(obj instanceof String) {
            query = (String)obj;
        } else {
            throw new UnsupportedRequestParameterType(getURN(), obj);
        }

        String[] querySplit = query.split(QUERY_DELIM);
        if (querySplit.length != 4 || !QUERY_SPLIT[0].equals(querySplit[0])
                ||!QUERY_SPLIT[1].equals(querySplit[1]) ||!QUERY_SPLIT[2].equals(querySplit[2])) {
            throw new RequestDataException(new I18NBoundMessage1P(Messages.INVALID_QUERY, query));
        }

        // find the type they are requesting (ie select * from <type>)
        String type = query.substring(QUERY_PREFIX.length());
        Class theClass = getClassForRequest(type);
        if (theClass == null) {
            throw new RequestDataException(new I18NBoundMessage1P(Messages.UNSUPPORTED_TYPE, type));
        }
        Pair<Class<?>, DataEmitterSupport> request = new Pair<Class<?>, DataEmitterSupport>(theClass, inSupport);
        mRequestMap.put(inSupport.getRequestID(), request);
    }

    /** Find the request, and go through all its types and remove all the {@link DataEmitterSupport}
     * object associated with it */
    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        mRequestMap.remove(inRequestID);
    }

    /** Checks to see if we are looking at an alias or a fully-qualified class name.
     * Any known alias or valid FQCN is allowed
     */
    protected Class getClassForRequest(String className) {
        // first check to see if it's a known pre-canned type
        Class theClass = mTypeLookupMap.get(className);
        if(theClass != null) return theClass;

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
