/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Strategy Engine Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl#getEngine <em>Engine</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class StrategyEngineConnectionImpl extends EObjectImpl
        implements StrategyEngineConnection {
    /**
     * The cached value of the '{@link #getEngine() <em>Engine</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEngine()
     * @generated
     * @ordered
     */
    protected StrategyEngine engine;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StrategyEngineConnectionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return StrategyEngineCorePackage.Literals.STRATEGY_ENGINE_CONNECTION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngine getEngine() {
        if (engine != null && engine.eIsProxy()) {
            InternalEObject oldEngine = (InternalEObject) engine;
            engine = (StrategyEngine) eResolveProxy(oldEngine);
            if (engine != oldEngine) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(
                            this,
                            Notification.RESOLVE,
                            StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE,
                            oldEngine, engine));
            }
        }
        return engine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngine basicGetEngine() {
        return engine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEngine(StrategyEngine newEngine,
            NotificationChain msgs) {
        StrategyEngine oldEngine = engine;
        engine = newEngine;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(
                    this,
                    Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE,
                    oldEngine, newEngine);
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
    public void setEngine(StrategyEngine newEngine) {
        if (newEngine != engine) {
            NotificationChain msgs = null;
            if (engine != null)
                msgs = ((InternalEObject) engine).eInverseRemove(this,
                        StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION,
                        StrategyEngine.class, msgs);
            if (newEngine != null)
                msgs = ((InternalEObject) newEngine).eInverseAdd(this,
                        StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION,
                        StrategyEngine.class, msgs);
            msgs = basicSetEngine(newEngine, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE,
                    newEngine, newEngine));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DeployedStrategy deploy(Strategy strategy) throws Exception {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void undeploy(DeployedStrategy strategy) throws Exception {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void start(DeployedStrategy strategy) throws Exception {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void stop(DeployedStrategy strategy) throws Exception {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void update(DeployedStrategy strategy, Strategy newConfiguration)
            throws Exception {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd,
            int featureID, NotificationChain msgs) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE:
            if (engine != null)
                msgs = ((InternalEObject) engine).eInverseRemove(this,
                        StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION,
                        StrategyEngine.class, msgs);
            return basicSetEngine((StrategyEngine) otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd,
            int featureID, NotificationChain msgs) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE:
            return basicSetEngine(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE:
            if (resolve)
                return getEngine();
            return basicGetEngine();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE:
            setEngine((StrategyEngine) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE:
            setEngine((StrategyEngine) null);
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
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE:
            return engine != null;
        }
        return super.eIsSet(featureID);
    }

} //StrategyEngineConnectionImpl
