package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.*;

/* $License$ */
/**
 * This class is used to track data flows, the modules
 * that initiate them and the modules that participate
 * in them.
 * <p>
 * This class is thread-safe. All of its methods employ locking
 * to ensure that the concurrent modifications to the instance's state
 * do not corrupt it. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class DataFlowTracker {
    /**
     * Adds a data flow.
     *
     * @param inFlow the data flow to be added.
     */
    synchronized void addFlow(DataFlow inFlow) {
        ModuleURN requester = inFlow.getRequesterURN();
        mFlows.put(inFlow.getFlowID(), inFlow);
        if(requester != null) {
            add(mInitiated, requester, inFlow.getFlowID());
        }
        for(ModuleURN p: inFlow.getParticipants()) {
            add(mParticipating, p, inFlow.getFlowID());
        }
    }

    /**
     * Fetches the data flow, given the flow ID.
     *
     * @param inFlowID the data flow ID
     *
     * @return the data flow for the specified flow ID, null if not found.
     */
    synchronized DataFlow get(DataFlowID inFlowID) {
        return mFlows.get(inFlowID);
    }

    /**
     * Removes the data flow, given the flow ID.
     *
     * @param inFlowID the data flow ID
     *
     * @return the data flow for the specified flowID, null if not found.
     */
    synchronized DataFlow remove(DataFlowID inFlowID) {
        DataFlow dataFlow = mFlows.remove(inFlowID);
        if (dataFlow != null) {
            ModuleURN requester = dataFlow.getRequesterURN();
            if(requester != null) {
                remove(mInitiated, requester, inFlowID);
            }
            for(ModuleURN p: dataFlow.getParticipants()) {
                remove(mParticipating, p, inFlowID);
            }
        }
        return dataFlow;
    }

    /**
     * Gets the data flows initiated by the specified module.
     *
     * @param inModuleURN the module URN.
     *
     * @return the set of data flow IDs that the module initiated, null
     * if this module has not initiated any data flows or any of its
     * initiated flows are not active.
     */
    synchronized Set<DataFlowID> getInitiatedFlows(ModuleURN inModuleURN) {
        Set<DataFlowID> idSet = mInitiated.get(inModuleURN);
        return idSet == null
                ? null
                : new HashSet<DataFlowID>(idSet);
    }

    /**
     * Gets the data flows that the specified module is participating in.
     *
     * @param inModuleURN the module URN
     *
     * @return the set of data flow IDs that the module is
     * participating in. null, if this module is currently not
     * participating in data flows.
     */
    synchronized Set<DataFlowID> getFlowsParticipating(ModuleURN inModuleURN) {
        Set<DataFlowID> idSet = mParticipating.get(
                inModuleURN);
        return idSet == null
                ? null
                : new HashSet<DataFlowID>(idSet);
    }

    /**
     * Returns the data flows that the specified module is participating in
     * that it did not initiate.
     *
     * @param inModuleURN the module URN
     * 
     * @return the set of data flowIDs that the module is participating in that
     * it did not initiate.
     */
    synchronized Set<DataFlowID> getFlowsParticipatingNotInitiated(
            ModuleURN inModuleURN) {
        Set<DataFlowID> idSet = mParticipating.get(
                inModuleURN);
        Set<DataFlowID> initSet = mInitiated.get(inModuleURN);
        if(idSet != null) {
            idSet = new HashSet<DataFlowID>(idSet);
            if(initSet != null) {
                idSet.removeAll(initSet);
            }
        }
        return idSet;
    }

    /**
     * Fetch all the IDs of all the active data flows.
     *
     * @param inIncludeModuleCreated if data flows created by modules
     * should be included.
     *
     * @return the IDs of all the active data flows.
     */
    synchronized List<DataFlowID> getDataFlows(boolean inIncludeModuleCreated) {
        List<DataFlowID> ids;
        if(inIncludeModuleCreated) {
            ids = new ArrayList<DataFlowID>(mFlows.keySet());
        } else {
            ids = new ArrayList<DataFlowID>();
            for(DataFlow flow: mFlows.values()) {
                if(!flow.isModuleCreated()) {
                    ids.add(flow.getFlowID());
                }
            }
        }
        return ids;

    }

    /**
     * Adds the data flow ID to the set of flows in the specified table for
     * the specified module.
     *
     * @param inTable the table of data flows for module instances.
     * @param inURN the URN of the module whose data flows set needs
     * to be updated.
     * @param inFlowID the data flow ID.
     */
    private static void add(Map<ModuleURN,Set<DataFlowID>> inTable,
                            ModuleURN inURN,
                            DataFlowID inFlowID) {
        Set<DataFlowID> value = inTable.get(inURN);
        if(value == null) {
            value = new HashSet<DataFlowID>();
            inTable.put(inURN,value);
        }
        value.add(inFlowID);
    }

    /**
     * Removes the data flow ID from the set of flows in the specified table
     * for the specified module.
     *
     * @param inTable the table of data flows for module instances.
     * @param inURN the URN of the module whose data flows set needs
     * to be updated.
     * @param inFlowID the data flow ID.
     */
    private static void remove(Map<ModuleURN, Set<DataFlowID>> inTable,
                               ModuleURN inURN,
                               DataFlowID inFlowID) {
        Set<DataFlowID> value = inTable.get(inURN);
        if(value != null) {
            value.remove(inFlowID);
            if(value.isEmpty()) {
                inTable.remove(inURN);
            }
        }
    }
    private final Map<DataFlowID, DataFlow> mFlows =
            new HashMap<DataFlowID, DataFlow>();
    private final Map<ModuleURN, Set<DataFlowID>> mInitiated =
            new HashMap<ModuleURN, Set<DataFlowID>>();
    private final Map<ModuleURN, Set<DataFlowID>> mParticipating =
            new HashMap<ModuleURN, Set<DataFlowID>>();
}
