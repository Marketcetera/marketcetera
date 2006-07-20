package org.marketcetera.quickfix;

import quickfix.MessageFactory;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public class QuickFIXDescriptor {
    private final String mQuickFIXPackage;
    private final String mFIXBeginString;
    private final String mCombinedString;
    private final int mHashCode;
    private String mFixVers;

    public QuickFIXDescriptor(String quickFIXPackage, String fixVers, String FIXBeginString) {
        mQuickFIXPackage = quickFIXPackage;
        mFIXBeginString = FIXBeginString;
        mCombinedString = "" + mQuickFIXPackage.length() + mQuickFIXPackage
                          + mFIXBeginString.length() + mFIXBeginString;
        mHashCode = mCombinedString.hashCode();
        mFixVers = fixVers;
    }

    public String getFixVers() {
        return mFixVers;
    }

    public final String getQuickFIXPackage() {
        return mQuickFIXPackage;
    }

    public final String getFIXBeginString() {
        return mFIXBeginString;
    }

    public final MessageFactory getMessageFactory()
            throws ClassNotFoundException {
        String className = "quickfix." + mQuickFIXPackage + ".MessageFactory";
        try {
            Class messageFactoryClass = Class.forName(className);
            return (MessageFactory) messageFactoryClass.newInstance();
        } catch (Exception ex) {
            throw new ClassNotFoundException("Could not find class: "
                                             + className);
        }
    }

    public boolean equals(Object obj) {
        return mCombinedString.equals(((QuickFIXDescriptor) obj).mCombinedString);
    }

    public int hashCode() {
        return mHashCode;
    }

}
