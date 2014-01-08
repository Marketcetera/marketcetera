package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NMessage1P;

/* $License$ */
/**
 * Utilities to parse URNs used by the modules.
 *
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class URNUtils {

    /**
     * The special keyword <code>this</code> that can be used within URNs
     * by a {@link DataFlowRequester module} when creating data flows.
     * The element of the URN with the <code>this</code>
     *  keyword is replaced with the respective element from the requesting
     * module's URN. 
     */
    public static final String THIS = "this";  //$NON-NLS-1$

    /**
     * Validates the supplied providerURN.
     *
     * @param inURN the provider URN
     *
     * @throws InvalidURNException if the supplied URN is invalid
     */
    public static void validateProviderURN(ModuleURN inURN)
            throws InvalidURNException {
        validateProviderURN(inURN, Messages.INCOMPLETE_PROVIDER_URN, false);
    }
    /**
     * Validates the supplied provider URN.
     *
     * @param inURN the provider URN
     * @param inIncompleteURNMessage the message code to use when throwing
     * errors that indicate incomplete URNs
     * @param inIsPrefix true, if the supplied URN should be validated
     * as a prefix of an instance URN, false if it should be validated
     * as an absolute provider URN.
     *
     * @throws InvalidURNException if the URN is invalid.
     */
    private static void validateProviderURN(
            ModuleURN inURN,
            I18NMessage1P inIncompleteURNMessage,
            boolean inIsPrefix)
            throws InvalidURNException {
        //verify that its not null
        if(inURN == null) {
            throw new InvalidURNException(new I18NBoundMessage1P(
                    Messages.EMPTY_URN, "")); //$NON-NLS-1$
        }
        //verify the scheme
        verifyScheme(inURN);
        //verify the provider type
        String provType = inURN.providerType();
        if(provType == null) {
            throw new InvalidURNException(new I18NBoundMessage1P(
                    inIncompleteURNMessage, inURN.toString()));
        }
        //verify that provider type is a java identifier
        if(!isValidIdentifier(provType)) {
            throw new InvalidURNException(new I18NBoundMessage2P(
                    Messages.INVALID_PROVIDER_TYPE, inURN.toString(),provType));
        }
        //now verify the provider name
        String provName = inURN.providerName();
        if(provName == null) {
            throw new InvalidURNException(new I18NBoundMessage1P(
                    inIncompleteURNMessage, inURN.toString()));
        }
        //verify that provider name is a java identifier
        if(!isValidIdentifier(provName)) {
            throw new InvalidURNException(new I18NBoundMessage2P(
                    Messages.INVALID_PROVIDER_NAME, inURN.toString(),provName));
        }
        if(!inIsPrefix && inURN.instanceURN()) {
            throw new InvalidURNException(new I18NBoundMessage1P(
                    Messages.PROVIDER_URN_HAS_INSTANCE, inURN.toString()));
        }
    }

    /**
     * Verifies the URN scheme.
     *
     * @param inURN the URN to verify
     *
     * @throws InvalidURNException if the URN has an invalid scheme.
     */
    private static void verifyScheme(ModuleURN inURN)
            throws InvalidURNException {
        if(!ModuleURN.SCHEME.equals(inURN.scheme())) {
            throw new InvalidURNException(new I18NBoundMessage3P(
                    Messages.INVALID_URN_SCHEME, inURN.scheme(),
                    inURN.toString(), ModuleURN.SCHEME));
        }
    }

    /**
     * Validates the supplied instance URN.
     *
     * @param inURN the module instance URN
     *
     * @throws InvalidURNException If the module URN is invalid.
     */
    public static void validateInstanceURN(ModuleURN inURN)
            throws InvalidURNException {
        validateInstanceURN(inURN, null);
    }
    /**
     * Validates the module instance URN.
     *
     * @param inURN the module instance URN.
     * @param inProviderURN the module provider URN. If not null,
     * the module URN is verified to have this URN as its prefix.
     *
     * @throws InvalidURNException if the module URN is invalid.
     */
    public static void validateInstanceURN(ModuleURN inURN,
                                           ModuleURN inProviderURN)
            throws InvalidURNException {
        validateProviderURN(inURN, Messages.INCOMPLETE_INSTANCE_URN, true);
        if(!inURN.instanceURN()) {
            throw new InvalidURNException(new I18NBoundMessage1P(
                    Messages.INCOMPLETE_INSTANCE_URN, inURN.toString()));
        }
        String instanceName = inURN.instanceName();
        if(!isValidIdentifier(instanceName)) {
            throw new InvalidURNException(new I18NBoundMessage2P(
                    Messages.INVALID_INSTANCE_URN, inURN.toString(),
                    instanceName));
        }
        if (inProviderURN != null) {
            validateInstanceOf(inURN, inProviderURN);
        }
    }

    /**
     * Validates that the module instance with the specified URN is
     * from the provider with the specified provider URN.
     *
     * @param inInstanceURN the instance URN
     * @param inProviderURN the provider URN
     *
     * @throws InvalidURNException if the module instance is not
     * from the specified provider URN
     */
    private static void validateInstanceOf(ModuleURN inInstanceURN,
                                          ModuleURN inProviderURN)
            throws InvalidURNException {
        if(!inProviderURN.parentOf(inInstanceURN)) {
            throw new InvalidURNException(new I18NBoundMessage2P(
                    Messages.INSTANCE_PROVIDER_URN_MISMATCH,
                    inInstanceURN.toString(), inProviderURN.toString()));
        }
    }


    /**
     * Expands any 'this' keywords in the requested URN,
     * with corresponding elements from the requester URN,
     * if a requester is supplied.
     *
     * @param inRequester the URN of the module requesting the data flow
     * @param inURN the URN that needs to be processed
     *
     * @return the URI that should be used to lookup the module
     *
     * @throws InvalidURNException if the supplied URN is invalid
     */
    static ModuleURN processURN(ModuleURN inRequester, ModuleURN inURN)
            throws InvalidURNException {
        verifyScheme(inURN);
        //If no requester, nothing to expand in the URN, return it as is.
        if(inRequester == null) {
            return inURN;
        }
        String providerType = THIS.equals(inURN.providerType())
                ? inRequester.providerType()
                : inURN.providerType();
        String providerName = THIS.equals(inURN.providerName())
                ? inRequester.providerName()
                : inURN.providerName();
        String instanceName = THIS.equals(inURN.instanceName())
                ? inRequester.instanceName()
                : inURN.instanceName();
        if(providerType == null &&
                providerName == null &&
                instanceName == null) {
            throw new InvalidURNException(new I18NBoundMessage1P(
                    Messages.INCOMPLETE_INSTANCE_URN,inURN.toString()));
        }
        inURN = new ModuleURN(providerType, providerName, instanceName);
        return inURN;
    }

    /**
     * Returns true if the supplied identifier is a valid identifier.
     * Do note <code>this</code> is a reserved name and is not
     * a valid identifier.
     *
     * @param inIdentifier the identifier that needs to be validated
     *
     * @return true if the supplied identifier is a java identifier,
     * false otherwise.
     */
    private static boolean isValidIdentifier(String inIdentifier) {
        final char[] chars = inIdentifier.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            if(i == 0) {
                if(!Character.isJavaIdentifierStart(chars[i])) {
                    return false;
                }
            } else {
                if(!Character.isJavaIdentifierPart(chars[i])) {
                    return false;
                }
            }
        }
        return !THIS.equals(inIdentifier);
    }


    /**
     * This is a utility class, no instances can be created
     */
    private URNUtils() {
    }
}
