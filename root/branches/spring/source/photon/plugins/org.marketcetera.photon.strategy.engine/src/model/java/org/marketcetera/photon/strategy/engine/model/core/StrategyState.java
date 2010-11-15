/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Strategy State</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyState()
 * @model
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum StrategyState implements Enumerator {
    /**
     * The '<em><b>Stopped</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #STOPPED_VALUE
     * @generated
     * @ordered
     */
    STOPPED(0, "Stopped", "Stopped"),

    /**
     * The '<em><b>Running</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RUNNING_VALUE
     * @generated
     * @ordered
     */
    RUNNING(1, "Running", "Running");

    /**
     * The '<em><b>Stopped</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Stopped</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #STOPPED
     * @model name="Stopped"
     * @generated
     * @ordered
     */
    public static final int STOPPED_VALUE = 0;

    /**
     * The '<em><b>Running</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Running</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #RUNNING
     * @model name="Running"
     * @generated
     * @ordered
     */
    public static final int RUNNING_VALUE = 1;

    /**
     * An array of all the '<em><b>Strategy State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final StrategyState[] VALUES_ARRAY = new StrategyState[] {
            STOPPED, RUNNING, };

    /**
     * A public read-only list of all the '<em><b>Strategy State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<StrategyState> VALUES = Collections
            .unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Strategy State</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static StrategyState get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            StrategyState result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Strategy State</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static StrategyState getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            StrategyState result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Strategy State</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static StrategyState get(int value) {
        switch (value) {
        case STOPPED_VALUE:
            return STOPPED;
        case RUNNING_VALUE:
            return RUNNING;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private StrategyState(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }

} //StrategyState
