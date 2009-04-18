package org.marketcetera.photon.model.marketdata.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.marketcetera.photon.model.marketdata.*;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.model.marketdata.MDPackage
 * @generated
 */
@ClassVersion("$Id$")
public class MDSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MDPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDSwitch() {
		if (modelPackage == null) {
			modelPackage = MDPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		} else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0),
					theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case MDPackage.MD_ITEM: {
			MDItem mdItem = (MDItem) theEObject;
			T result = caseMDItem(mdItem);
			if (result == null) result = defaultCase(theEObject);
			return result;
		}
		case MDPackage.MD_LATEST_TICK: {
			MDLatestTick mdLatestTick = (MDLatestTick) theEObject;
			T result = caseMDLatestTick(mdLatestTick);
			if (result == null) result = caseMDItem(mdLatestTick);
			if (result == null) result = defaultCase(theEObject);
			return result;
		}
		case MDPackage.MD_TOP_OF_BOOK: {
			MDTopOfBook mdTopOfBook = (MDTopOfBook) theEObject;
			T result = caseMDTopOfBook(mdTopOfBook);
			if (result == null) result = caseMDItem(mdTopOfBook);
			if (result == null) result = defaultCase(theEObject);
			return result;
		}
		case MDPackage.MD_MARKETSTAT: {
			MDMarketstat mdMarketstat = (MDMarketstat) theEObject;
			T result = caseMDMarketstat(mdMarketstat);
			if (result == null) result = caseMDItem(mdMarketstat);
			if (result == null) result = defaultCase(theEObject);
			return result;
		}
		case MDPackage.MD_DEPTH_OF_BOOK: {
			MDDepthOfBook mdDepthOfBook = (MDDepthOfBook) theEObject;
			T result = caseMDDepthOfBook(mdDepthOfBook);
			if (result == null) result = caseMDItem(mdDepthOfBook);
			if (result == null) result = defaultCase(theEObject);
			return result;
		}
		case MDPackage.MD_QUOTE: {
			MDQuote mdQuote = (MDQuote) theEObject;
			T result = caseMDQuote(mdQuote);
			if (result == null) result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Item</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMDItem(MDItem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Latest Tick</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Latest Tick</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMDLatestTick(MDLatestTick object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Top Of Book</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Top Of Book</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMDTopOfBook(MDTopOfBook object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Marketstat</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Marketstat</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMDMarketstat(MDMarketstat object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Depth Of Book</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Depth Of Book</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMDDepthOfBook(MDDepthOfBook object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Quote</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Quote</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMDQuote(MDQuote object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //MDSwitch
