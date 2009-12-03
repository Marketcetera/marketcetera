/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import org.marketcetera.module.ModuleURN;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployed Strategy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getEngine <em>Engine</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getState <em>State</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getUrn <em>Urn</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getDeployedStrategy()
 * @model
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface DeployedStrategy extends Strategy {
    /**
     * Returns the value of the '<em><b>Engine</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDeployedStrategies <em>Deployed Strategies</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Engine</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Engine</em>' reference.
     * @see #setEngine(StrategyEngine)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getDeployedStrategy_Engine()
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDeployedStrategies
     * @model opposite="deployedStrategies" transient="true"
     * @generated
     */
    StrategyEngine getEngine();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getEngine <em>Engine</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Engine</em>' reference.
     * @see #getEngine()
     * @generated
     */
    void setEngine(StrategyEngine value);

    /**
     * Returns the value of the '<em><b>State</b></em>' attribute.
     * The default value is <code>"Stopped"</code>.
     * The literals are from the enumeration {@link org.marketcetera.photon.strategy.engine.model.core.StrategyState}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>State</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>State</em>' attribute.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyState
     * @see #setState(StrategyState)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getDeployedStrategy_State()
     * @model default="Stopped" required="true"
     * @generated
     */
    StrategyState getState();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getState <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>State</em>' attribute.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyState
     * @see #getState()
     * @generated
     */
    void setState(StrategyState value);

    /**
     * Returns the value of the '<em><b>Urn</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Urn</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Urn</em>' attribute.
     * @see #setUrn(ModuleURN)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getDeployedStrategy_Urn()
     * @model dataType="org.marketcetera.photon.strategy.engine.model.core.ModuleURN" transient="true"
     * @generated
     */
    ModuleURN getUrn();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getUrn <em>Urn</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Urn</em>' attribute.
     * @see #getUrn()
     * @generated
     */
    void setUrn(ModuleURN value);

} // DeployedStrategy
