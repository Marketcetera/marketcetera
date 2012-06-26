package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ModuleInfo;

import java.util.*;

/* $License$ */
/**
 * Mock remote web service implementations.
 * <p>
 * For each web service invocation, the class stores away the supplied
 * parameters as a property of this class, {@link #failOrSleep() checks} if the
 * method invocation needs to fail and throws the exception if it should or if
 * method invocation need to sleep for a while and sleeps for a preset amount of time,
 * otherwise returns the return value from the appropriately typed property
 * of this class.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
class MockSAServiceImpl extends ServiceBaseImpl<Object> implements SAService {

    @Override
    public List<ModuleURN> getProviders(ClientContext inCtx)
            throws RemoteException {
        return new RemoteCaller<Object, List<ModuleURN>>(
                getSessionManager()){
            @Override
            protected List<ModuleURN> call(
                    ClientContext context,
                    SessionHolder<Object> sessionHolder)
                    throws Exception {
                failOrSleep();
                return getURNList();
            }
        }.execute(inCtx);
    }


    @Override
    public List<ModuleURN> getInstances(ClientContext inCtx,
                                        final ModuleURN inProviderURN)
            throws RemoteException {
        return new RemoteCaller<Object, List<ModuleURN>>(
                getSessionManager()){
            @Override
            protected List<ModuleURN> call(
                    ClientContext context,
                    SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inProviderURN);
                failOrSleep();
                return getURNList();
            }
        }.execute(inCtx);
    }

    @Override
    public ModuleInfo getModuleInfo(ClientContext inCtx,
                                    final ModuleURN inURN)
            throws RemoteException {
        return new RemoteCaller<Object, ModuleInfo>(
                getSessionManager()){
            @Override
            protected ModuleInfo call(
                    ClientContext context,
                    SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                failOrSleep();
                return getModuleInfo();
            }
        }.execute(inCtx);
    }

    @Override
    public void start(ClientContext inCtx,
                      final ModuleURN inURN)
            throws RemoteException {
        new RemoteCaller<Object, Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                failOrSleep();
                return null;
            }
        }.execute(inCtx);
    }

    @Override
    public void stop(ClientContext inCtx,
                     final ModuleURN inURN)
            throws RemoteException {
        new RemoteCaller<Object, Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                failOrSleep();
                return null;
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#sendData(org.marketcetera.util.ws.stateful.ClientContext, java.lang.Object)
     */
    @Override
    public void sendData(ClientContext inServiceContext,
                         final Object inData)
            throws RemoteException
    {
        new RemoteCaller<Object,Void>(getSessionManager()) {
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<Object> sessionHolder)
                    throws Exception
            {
                setData(inData);
                failOrSleep();
                return null;
            }
        }.execute(inServiceContext);
    }
    @Override
    public void delete(ClientContext inCtx, final ModuleURN inURN)
            throws RemoteException {
        new RemoteCaller<Object, Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                failOrSleep();
                return null;
            }
        }.execute(inCtx);
    }

    @Override
    public MapWrapper<String, java.lang.Object> getProperties(
            ClientContext inCtx, final ModuleURN inURN)
            throws RemoteException {
        return new RemoteCaller<Object, MapWrapper<String, java.lang.Object>>(
                getSessionManager()){
            @Override
            protected MapWrapper<String, java.lang.Object> call(
                    ClientContext context,
                    SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                failOrSleep();
                return getPropertiesOut();
            }
        }.execute(inCtx);
    }

    @Override
    public MapWrapper<String, java.lang.Object> setProperties(
            ClientContext inCtx, final ModuleURN inURN,
            final MapWrapper<String, java.lang.Object> inProperties)
            throws RemoteException {
        return new RemoteCaller<Object, MapWrapper<String, java.lang.Object>>(
                getSessionManager()){
            @Override
            protected MapWrapper<String, java.lang.Object> call(
                    ClientContext context,
                    SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                setPropertiesIn(inProperties);
                failOrSleep();
                return getPropertiesOut();
            }
        }.execute(inCtx);
    }

    @Override
    public ModuleURN createStrategy(ClientContext inCtx,
                                    final CreateStrategyParameters inParameters)
            throws RemoteException {
        return new RemoteCaller<Object, ModuleURN>(getSessionManager()){
            @Override
            protected ModuleURN call(ClientContext context,
                                      SessionHolder<Object> sessionHolder)
                    throws Exception {
                setCreateStrategyParameters(inParameters);
                failOrSleep();
                return getURN();
            }
        }.execute(inCtx);
    }

    @Override
    public CreateStrategyParameters getStrategyCreateParms(ClientContext inCtx,
                                                           final ModuleURN inURN)
            throws RemoteException {
        return new RemoteCaller<Object, CreateStrategyParameters>(getSessionManager()){
            @Override
            protected CreateStrategyParameters call(ClientContext context,
                                      SessionHolder<Object> sessionHolder)
                    throws Exception {
                setURN(inURN);
                failOrSleep();
                return getCreateStrategyParameters();
            }
        }.execute(inCtx);
    }
    /**
     * Creates an instance.
     *
     * @param inSessionManager the session manager.
     */
    MockSAServiceImpl(SessionManager<Object> inSessionManager) {
        super(inSessionManager);
    }

    /**
     * Returns the create strategy parameters.
     *
     * @return the create startegy parameters.
     */
    CreateStrategyParameters getCreateStrategyParameters() {
        return mCreateStrategyParameters;
    }

    /**
     * The module URN.
     *
     * @return the module URN.
     */
    ModuleURN getURN() {
        return mURN;
    }
    /**
     * Gets the data value.
     *
     * @return an <code>Object</code> value
     */
    Object getData()
    {
        return data;
    }
    /**
     * The urn list.
     * @param inURNs the urn list.
     */
    void setURNList(List<ModuleURN> inURNs) {
        mURNList = inURNs;
    }

    /**
     * The module info.
     *
     * @param inInfo the module info.
     */
    void setModuleInfo(ModuleInfo inInfo) {
        mModuleInfo = inInfo;
    }

    /**
     * The module URN.
     *
     * @param inURN the module URN.
     */
    void setURN(ModuleURN inURN) {
        mURN = inURN;
    }
    /**
     * Sets the data value.
     *
     * @param inData an <code>Object</code> value
     */
    void setData(Object inData)
    {
        data = inData;
    }
    /**
     * Sets the create strategy parameters.
     *
     * @param inParams the create strategy parameters.
     */
    void setCreateStrategyParameters(CreateStrategyParameters inParams) {
        mCreateStrategyParameters = inParams;
    }

    /**
     * Sets the properties returned by the services.
     *
     * @param inPropertiesOut the properties that should be returned by the services.
     */
    void setPropertiesOut(MapWrapper<String, java.lang.Object> inPropertiesOut) {
        mPropertiesOut = inPropertiesOut;
    }

    /**
     * The failure that should thrown by the services.
     *
     * @param inFailure the failure to throw.
     */
    void setFailure(Exception inFailure) {
        mFailure = inFailure;
    }

    /**
     * If the services should sleep when invoked, allowing their
     * interruption to be tested.
     *
     * @param inSleep if the services should sleep;
     */
    void setSleep(boolean inSleep) {
        mSleep = inSleep;
    }

    /**
     * Resets all parameters to null.
     */
    void reset() {
        setURNList(null);
        setURN(null);
        setModuleInfo(null);
        setCreateStrategyParameters(null);
        setPropertiesIn(null);
        setPropertiesOut(null);
        setFailure(null);
        setSleep(false);
        setData(null);
    }

    /**
     * Returns the properties received by the services.
     *
     * @return the received properties.
     */
    MapWrapper<String, java.lang.Object> getPropertiesIn() {
        return mPropertiesIn;
    }

    private void setPropertiesIn(MapWrapper<String, java.lang.Object> inProperties) {
        mPropertiesIn = inProperties;
    }

    private MapWrapper<String, java.lang.Object> getPropertiesOut() {
        return mPropertiesOut;
    }

    private List<ModuleURN> getURNList() {
        return mURNList;
    }

    private ModuleInfo getModuleInfo() {
        return mModuleInfo;
    }
    
    private void failOrSleep() throws Exception {
        if(mFailure != null) {
            throw mFailure;
        }
        if(mSleep) {
            Thread.sleep(1000 * 2);
        }
    }

    private volatile List<ModuleURN> mURNList;
    private volatile ModuleInfo mModuleInfo;
    private volatile ModuleURN mURN;
    private volatile Object data;
    private volatile CreateStrategyParameters mCreateStrategyParameters;
    private volatile MapWrapper<String, java.lang.Object> mPropertiesIn;
    private volatile MapWrapper<String, java.lang.Object> mPropertiesOut;
    private volatile Exception mFailure;
    private volatile boolean mSleep;
}