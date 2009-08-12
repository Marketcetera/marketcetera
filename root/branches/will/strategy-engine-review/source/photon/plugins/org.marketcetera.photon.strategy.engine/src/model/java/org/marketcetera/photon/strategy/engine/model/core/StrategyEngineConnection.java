/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Strategy Engine Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection#getEngine <em>Engine</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngineConnection()
 * @model abstract="true"
 * @generated
 */
public interface StrategyEngineConnection extends EObject {
    /**
     * Returns the value of the '<em><b>Engine</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnection <em>Connection</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Engine</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Engine</em>' reference.
     * @see #setEngine(StrategyEngine)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngineConnection_Engine()
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnection
     * @model opposite="connection" required="true" transient="true"
     * @generated
     */
    StrategyEngine getEngine();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection#getEngine <em>Engine</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Engine</em>' reference.
     * @see #getEngine()
     * @generated
     */
    void setEngine(StrategyEngine value) throws Exception;

    /**
     * <!-- begin-user-doc -->
     * Deploys a new strategy to the associated engine.
     * @param strategy the configuration for the new strategy
     * @throw Exception if the operation fails
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception" strategyRequired="true"
     * @generated
     */
    DeployedStrategy deploy(Strategy strategy) throws Exception;

    /**
     * <!-- begin-user-doc -->
     * Undeploys a deployed strategy from the associated engine.
     * @param strategy the strategy to undeploy
     * @throw Exception if the operation fails
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception" strategyRequired="true"
     * @generated
     */
    void undeploy(DeployedStrategy strategy) throws Exception;

    /**
     * <!-- begin-user-doc -->
     * Starts the given deployed strategy.
     * @param strategy the deployed strategy to start
     * @throw Exception if the operation fails
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception" strategyRequired="true"
     * @generated
     */
    void start(DeployedStrategy strategy) throws Exception;

    /**
     * <!-- begin-user-doc -->
     * Stops the given deployed strategy.
     * @param strategy the deployed strategy to start
     * @throw Exception if the operation fails
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception" strategyRequired="true"
     * @generated
     */
    void stop(DeployedStrategy strategy) throws Exception;

    /**
     * <!-- begin-user-doc -->
     * Updates the configuration of the given deployed strategy.
     * @param strategy the deployed strategy to update
     * @param newConfiguration the new configuration
     * @throw Exception if the operation fails
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception" strategyRequired="true"
     * @generated
     */
    void update(DeployedStrategy strategy, Strategy newConfiguration)
            throws Exception;

} // StrategyEngineConnection
