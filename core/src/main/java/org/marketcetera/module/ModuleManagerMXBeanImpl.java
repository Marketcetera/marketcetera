package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/* $License$ */
/**
 * The implementation for the ModuleManagerMXBean interface.
 * This implementation delegates to the module manager. Its primary
 * function is to convert all the simple java parameter types that it
 * accepts for its operations to the java types that are used by
 * the Module Manager.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class ModuleManagerMXBeanImpl implements ModuleManagerMXBean {
    @Override
    public List<String> getProviders() {
        return toString(mManager.getProviders());
    }

    @Override
    public List<String> getInstances() {
        return getModuleInstances(null);
    }

    @Override
    public ProviderInfo getProviderInfo(String providerURN){
        try {
            return mManager.getProviderInfo(toModuleURN(providerURN));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public List<String> getModuleInstances(String providerURN){
        try {
            return toString(mManager.getModuleInstances(
                    toModuleURN(providerURN)));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public String createModule(String providerURN, String parameters) {
        try {
            ModuleURN urn = mManager.createModuleJMX(
                    new ModuleURN(providerURN), parameters);
            return urn.getValue();
        } catch (ModuleException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public void deleteModule(String inModuleURN){
        try {
            mManager.deleteModule(toModuleURN(inModuleURN));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public ModuleInfo getModuleInfo(String inModuleURN) {
        try {
            return mManager.getModuleInfo(toModuleURN(inModuleURN));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public void start(String inModuleURN) {
        try {
            mManager.start(toModuleURN(inModuleURN));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public void stop(String inModuleURN) {
        try {
            mManager.stop(toModuleURN(inModuleURN));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public DataFlowID createDataFlow(String inRequests) {
        try {
            return mManager.createDataFlow(parseDataRequests(inRequests));
        } catch (ModuleException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public DataFlowID createDataFlow(String inRequests,
                                     boolean inAppendSink) {
        try {
            return mManager.createDataFlow(parseDataRequests(inRequests),
                    inAppendSink);
        } catch (ModuleException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public void cancel(String inFlowID) {
        try {
            mManager.cancel(toFlowID(inFlowID));
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public List<DataFlowID> getDataFlows(boolean inIncludeModuleCreated) {
        return mManager.getDataFlows(inIncludeModuleCreated);
    }

    @Override
    public DataFlowInfo getDataFlowInfo(String inFlowID) {
        try {
            return mManager.getDataFlowInfo(toFlowID(inFlowID));
        } catch (DataFlowNotFoundException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public void refresh() {
        try {
            mManager.refresh();
        } catch (I18NException e) {
            throw transformFailure(e);
        }
    }

    @Override
    public List<DataFlowInfo> getDataFlowHistory() {
        return mManager.getDataFlowHistory();
    }

    @Override
    public int getMaxFlowHistory() {
        return mManager.getMaxFlowHistory();
    }
    
    @Override
    public void setMaxFlowHistory(int inMaxFlowHistory) {
        mManager.setMaxFlowHistory(inMaxFlowHistory);
    }

    /**
     * Creates an instance.
     *
     * @param inManager the module manager instance.
     */
    ModuleManagerMXBeanImpl(ModuleManager inManager) {
        assert inManager != null;
        mManager = inManager;
    }

    /**
     * Parses the requests as follows. Each data request is
     * delimited by '^' character. If a data request needs to
     * include the '^' character they can avoid having it
     * interpreted as an escape character by including it twice,
     * like so '^^'.
     *
     * Within each data request string, individual entries are
     * delimited by ';' character. The first entry is always
     * interpreted as the ModuleURN. The second entry is interpreted
     * as the coupling type, if it matches any of the known coupling types
     * otherwise its interpreted as a string request parameter and
     * the coupling type is defaulted to {@link DataCoupling#SYNC}.
     *
     * @param inRequests the data requests in string syntax.
     *
     * @return the parsed data request objects.
     */
    static DataRequest[] parseDataRequests(String inRequests) {
        if(inRequests == null || inRequests.trim().isEmpty()) {
            throw new RuntimeException(
                    Messages.EMPTY_STRING_DATA_REQUEST.getText());
        }
        String[] requests = inRequests.split(REQUESTS_SEPARATOR_REGEX);
        LinkedList<String> list = new LinkedList<String>();
        boolean append = false;
        //Coalesce consecutive '^' chars, which is implied by an empty string.
        for (String request : requests) {
            if (append) {
                append = false;
                list.add(list.removeLast() + REQUESTS_SEPARATOR + request);
            } else if (request.isEmpty()) {
                // the next element should be appended to the last one in
                // the list with the '^' character
                append = true;
            } else {
                list.add(request);
            }
        }
        //Parse a data request out of each element of the list.
        DataRequest[] dataRequests = new DataRequest[list.size()];
        int i = 0;
        for(String request: list) {
            dataRequests[i++] = parseDataRequest(request);
        }
        return dataRequests;
    }

    /**
     * Parses the supplied string as a data request. Individual
     * elements are delimited by ';' character. If the supplied
     * string has a ';' character, the text before its first
     * occurrence is interpreted as the module URN. Otherwise,
     * the entire string is interpreted as a module URN.
     * <p>
     * If the string has another ';' character, if the text between
     * the two ';' characters matches a supported
     * {@link DataCoupling} value, a data request is created using
     * that data coupling value and the string after the second ';'
     * character as the request parameter, otherwise, a data request is
     * created with the entire
     * string after the first ';' character as the request parameter
     * and data coupling defaulted to {@link DataCoupling#SYNC}
     * <p>
     * If the string does not have another ';' character, if the text
     * after ';' character matches a supported {@link DataCoupling} value,
     * a data request is created with that data coupling value and a null
     * request parameter, otherwise, a data request is created with the
     * text after ';' as the data request parameter and data coupling
     * type defaulted to {@link DataCoupling#SYNC}. 
     *  
     * @param inRequest the string representation of the request
     *
     * @return the data request object.
     */
    private static DataRequest parseDataRequest(String inRequest) {
        int idx = inRequest.indexOf(DATA_SEPARATOR);
        ModuleURN urn;
        DataCoupling coupling = DataCoupling.SYNC;
        if(idx > 0) {
            //Has at least one separator, parse module urn out.
            urn = new ModuleURN(inRequest.substring(0, idx));
            if (++idx < inRequest.length()) {
                inRequest = inRequest.substring(idx);
                idx = inRequest.indexOf(DATA_SEPARATOR);
                if (idx >= 0) {
                    //has another separator, parse this element out
                    String s = inRequest.substring(0, idx);
                    DataCoupling c = toCoupling(s);
                    if(c == null) {
                        // not a valid coupling value, use the entire string
                        // as request parameter
                        coupling = DataCoupling.SYNC;
                    } else {
                        //record the coupling value and use the rest of the
                        //string as request parameter, if any is left.
                        coupling = c;
                        if(++idx < inRequest.length()) {
                            inRequest = inRequest.substring(idx);
                        } else {
                            inRequest = null;
                        }
                    }
                } else {
                    //no more separator, test this string for coupling
                    //value, if no matches, use the value as request parameter
                    DataCoupling c = toCoupling(inRequest);
                    if(c == null) {
                        coupling = DataCoupling.SYNC;
                    } else {
                        coupling = c;
                        inRequest = null;
                    }
                }
            } else {
                //nothing found after the separator, use defaults
                coupling = DataCoupling.SYNC;
                inRequest = null;
            }
        } else {
            //No separators, interpret the entire string as
            //module URN
            urn = new ModuleURN(inRequest);
            inRequest = null;
        }
        return new DataRequest(urn, coupling, inRequest);
    }

    /**
     * Converts the string to a data coupling.
     *
     * @param inString the string that needs to be converted to coupling.
     *
     * @return the data coupling, if the string matches an available
     * coupling, null otherwise.
     */
    private static DataCoupling toCoupling(String inString) {
        try {
            return DataCoupling.valueOf(inString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Converts a string value to Data Flow ID. If the supplied string
     * is null or empty, the returned value is null.
     *
     * @param inFlowID the flow ID represented as a string.
     *
     * @return the data flow ID.
     */
    private DataFlowID toFlowID(String inFlowID) {
        return inFlowID == null ||
                inFlowID.trim().isEmpty()
                ? null
                : new DataFlowID(inFlowID);
    }

    /**
     * Converts a string value to Module URN value. If the supplied
     * string value is null or empty, the returned value is null.
     *
     * @param inURN the module URN represented as a string.
     *
     * @return the module URN value.
     */
    private ModuleURN toModuleURN(String inURN) {
        return inURN == null || inURN.trim().isEmpty()
                ? null
                : new ModuleURN(inURN);
    }

    /**
     * Converts an array of module URN values to an array of strings.
     * If the supplied value is null, the returned value is null.
     *
     * @param inURNs the array of module URNs.
     *
     * @return the array of string values of the module URN.
     */
    private List<String> toString(List<ModuleURN> inURNs) {
        if(inURNs == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>(inURNs.size());
        for(ModuleURN urn: inURNs) {
            list.add(urn.getValue());
        }
        return list;
    }

    /**
     * Transforms the failure into a runtime exception and copies over the stack
     * trace. The original exception should not be added to the RuntimeException
     * as the JMX client may not have the classes available to deserialize
     * them.
     *
     * @param inException the exception that needs to be transformed.
     *
     * @return the wrapped runtime exception.
     */
    private static RuntimeException transformFailure(I18NException inException) {
        RuntimeException runtimeException = new RuntimeException(
                inException.getLocalizedDetail());
        runtimeException.setStackTrace(inException.getStackTrace());
        return runtimeException;
    }

    private final ModuleManager mManager;
    private static final String REQUESTS_SEPARATOR = "^";  //$NON-NLS-1$
    private static final String REQUESTS_SEPARATOR_REGEX = "\\^";  //$NON-NLS-1$
    private static final String DATA_SEPARATOR = ";";  //$NON-NLS-1$
}
