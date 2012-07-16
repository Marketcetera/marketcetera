/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.marketcetera.module.ModuleURN;

import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deployed Strategy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl#getEngine <em>Engine</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl#getState <em>State</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl#getUrn <em>Urn</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class DeployedStrategyImpl
        extends StrategyImpl
        implements DeployedStrategy
{
    /**
     * The cached value of the '{@link #getEngine() <em>Engine</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEngine()
     * @generated
     * @ordered
     */
    protected volatile StrategyEngine engine;

    /**
     * The default value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected static final StrategyState STATE_EDEFAULT = StrategyState.STOPPED;

    /**
     * The cached value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected volatile StrategyState state = STATE_EDEFAULT;

    /**
     * The default value of the '{@link #getUrn() <em>Urn</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUrn()
     * @generated
     * @ordered
     */
    protected static final ModuleURN URN_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUrn() <em>Urn</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUrn()
     * @generated
     * @ordered
     */
    protected volatile ModuleURN urn = URN_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeployedStrategyImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass()
    {
        return StrategyEngineCorePackage.Literals.DEPLOYED_STRATEGY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public synchronized StrategyEngine getEngine()
    {
        if (engine != null && engine.eIsProxy()) {
            InternalEObject oldEngine = (InternalEObject) engine;
            engine = (StrategyEngine) eResolveProxy(oldEngine);
            if (engine != oldEngine) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this,
                                                  Notification.RESOLVE,
                                                  StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE,
                                                  oldEngine,
                                                  engine));
            }
        }
        return engine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngine basicGetEngine()
    {
        return engine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEngine(StrategyEngine newEngine,
                                            NotificationChain msgs)
    {
        StrategyEngine oldEngine = engine;
        engine = newEngine;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this,
                                                                   Notification.SET,
                                                                   StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE,
                                                                   oldEngine,
                                                                   newEngine);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEngine(StrategyEngine newEngine)
    {
        if (newEngine != engine) {
            NotificationChain msgs = null;
            if (engine != null)
                msgs = ((InternalEObject) engine)
                        .eInverseRemove(this,
                                        StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES,
                                        StrategyEngine.class,
                                        msgs);
            if (newEngine != null)
                msgs = ((InternalEObject) newEngine)
                        .eInverseAdd(this,
                                     StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES,
                                     StrategyEngine.class,
                                     msgs);
            msgs = basicSetEngine(newEngine,
                                  msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE,
                                          newEngine,
                                          newEngine));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyState getState()
    {
        return state;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setState(StrategyState newState)
    {
        StrategyState oldState = state;
        state = newState == null ? STATE_EDEFAULT : newState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.DEPLOYED_STRATEGY__STATE,
                                          oldState,
                                          state));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModuleURN getUrn()
    {
        return urn;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUrn(ModuleURN newUrn)
    {
        ModuleURN oldUrn = urn;
        urn = newUrn;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.DEPLOYED_STRATEGY__URN,
                                          oldUrn,
                                          urn));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd,
                                         int featureID,
                                         NotificationChain msgs)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE:
                if (engine != null)
                    msgs = ((InternalEObject) engine)
                            .eInverseRemove(this,
                                            StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES,
                                            StrategyEngine.class,
                                            msgs);
                return basicSetEngine((StrategyEngine) otherEnd,
                                      msgs);
        }
        return super.eInverseAdd(otherEnd,
                                 featureID,
                                 msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd,
                                            int featureID,
                                            NotificationChain msgs)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE:
                return basicSetEngine(null,
                                      msgs);
        }
        return super.eInverseRemove(otherEnd,
                                    featureID,
                                    msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID,
                       boolean resolve,
                       boolean coreType)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE:
                if (resolve)
                    return getEngine();
                return basicGetEngine();
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__STATE:
                return getState();
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__URN:
                return getUrn();
        }
        return super.eGet(featureID,
                          resolve,
                          coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID,
                     Object newValue)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE:
                setEngine((StrategyEngine) newValue);
                return;
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__STATE:
                setState((StrategyState) newValue);
                return;
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__URN:
                setUrn((ModuleURN) newValue);
                return;
        }
        super.eSet(featureID,
                   newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE:
                setEngine((StrategyEngine) null);
                return;
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__STATE:
                setState(STATE_EDEFAULT);
                return;
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__URN:
                setUrn(URN_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE:
                return engine != null;
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__STATE:
                return state != STATE_EDEFAULT;
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY__URN:
                return URN_EDEFAULT == null ? urn != null : !URN_EDEFAULT.equals(urn);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString()
    {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (state: ");
        result.append(state);
        result.append(", urn: ");
        result.append(urn);
        result.append(')');
        return result.toString();
    }

} //DeployedStrategyImpl
