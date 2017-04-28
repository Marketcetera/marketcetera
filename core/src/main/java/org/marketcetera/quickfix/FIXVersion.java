package org.marketcetera.quickfix;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_40;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_41;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_42;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_43;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_44;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_50;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_50SP1;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_50SP2;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;

/**
 * An enum for all the supported FIX versions, with the default URL for the data dictionary file.
 * 
 * @author toli
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
public enum FIXVersion
{
    FIX40(FIXDataDictionary.FIX_4_0_BEGIN_STRING,
          quickfix.field.ApplVerID.FIX40,
          "FIX40.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_0_BEGIN_STRING,
                                new quickfix.fix40.MessageFactory(),
                                new FIXMessageAugmentor_40())),
    FIX41(FIXDataDictionary.FIX_4_1_BEGIN_STRING,
          quickfix.field.ApplVerID.FIX41,
          "FIX41.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_1_BEGIN_STRING,
                                new quickfix.fix41.MessageFactory(),
                                new FIXMessageAugmentor_41())),
    FIX42(FIXDataDictionary.FIX_4_2_BEGIN_STRING,
          quickfix.field.ApplVerID.FIX42,
          "FIX42.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                                new quickfix.fix42.MessageFactory(),
                                new FIXMessageAugmentor_42())),
    FIX43(FIXDataDictionary.FIX_4_3_BEGIN_STRING,
          quickfix.field.ApplVerID.FIX43,
          "FIX43.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_3_BEGIN_STRING,
                                new quickfix.fix43.MessageFactory(),
                                new FIXMessageAugmentor_43())),
    FIX44(FIXDataDictionary.FIX_4_4_BEGIN_STRING,
          quickfix.field.ApplVerID.FIX44,
          "FIX44-marketcetera.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_4_BEGIN_STRING,
                                new quickfix.fix44.MessageFactory(),
                                new FIXMessageAugmentor_44())),
    FIX50(FIXDataDictionary.FIX_5_0_BEGIN_STRING,
          quickfix.field.ApplVerID.FIX50,
          "FIX50.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_5_0_BEGIN_STRING,
                                new quickfix.fix50.MessageFactory(),
                                new FIXMessageAugmentor_50())),
    FIX50SP1(FIXDataDictionary.FIX_5_0_SP1_BEGIN_STRING,
             quickfix.field.ApplVerID.FIX50SP1,
             "FIX50SP1.xml", //$NON-NLS-1$
             new FIXMessageFactory(FIXDataDictionary.FIX_5_0_SP1_BEGIN_STRING,
                                   new quickfix.fix50sp1.MessageFactory(),
                                   new FIXMessageAugmentor_50SP1())),
    FIX50SP2(FIXDataDictionary.FIX_5_0_SP2_BEGIN_STRING,
             quickfix.field.ApplVerID.FIX50SP2,
             "FIX50SP2.xml", //$NON-NLS-1$
             new FIXMessageFactory(FIXDataDictionary.FIX_5_0_SP2_BEGIN_STRING,
                                   new quickfix.fix50sp2.MessageFactory(),
                                   new FIXMessageAugmentor_50SP2())),
    FIX_SYSTEM(FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING,
               null,
               "FIX00-system.xml", //$NON-NLS-1$
               new SystemFIXMessageFactory());
    // nb: FIXT11 deliberately excluded from this list. this is complicated, but it's not an application
    //  version and therefore has no corresponding (complete) data dictionary. the proper approach for FIXT
    //  is to use one of the getFIXVersion calls below to find the correct application FIX version.
    /**
     * Get the FIX version value associated with the given string representation.
     *
     * @param inVersion a <code>String</code> value
     * @return a <code>FIXVersion</code> value
     * @throws IllegalArgumentException if the FIX version of the given message is unsupported
     */
    public static FIXVersion getFIXVersion(String inVersion)
    {
        FIXVersion fixVersion = versionMap.get(inVersion);
        if(fixVersion == null) {
            throw new IllegalArgumentException(Messages.FIX_VERSION_UNSUPPORTED.getText(inVersion));
        }
        return fixVersion;
    }
    /**
     * Get the <code>FIXVersion</code> of the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>FIXVersion</code> value
     * @throws FieldNotFound if the given message is malformed and the version cannot be identified
     * @throws IllegalArgumentException if the FIX version of the given message is unsupported
     */
    public static FIXVersion getFIXVersion(Message inMessage)
            throws FieldNotFound
    {
        SessionID sessionId = MessageUtils.getSessionID(inMessage);
        if(sessionId.isFIXT()) {
            if(inMessage.getHeader().isSetField(quickfix.field.ApplVerID.FIELD)) {
                return doFixApplicationVersionLookup(inMessage.getHeader().getString(quickfix.field.ApplVerID.FIELD));
            }
            // this is a FIXT message _and_ ApplVerID is _not_ set, we need to know something about the session
            Session session = Session.lookupSession(sessionId);
            if(session == null) {
                throw new UnsupportedOperationException(Messages.APPL_VERID_REQUIRED.getText());
            }
            quickfix.field.ApplVerID defaultAppVerId = session.getTargetDefaultApplicationVersionID();
            return getFIXVersion(defaultAppVerId);
        } else {
            return doFixVersionLookup(inMessage.getHeader().getString(quickfix.field.BeginString.FIELD));
        }
    }
    /**
     * Get the FIX version from the given session ID.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>FIXVersion</code> value
     */
    public static FIXVersion getFIXVersion(SessionID inSessionId)
    {
        if(inSessionId.isFIXT()) {
            // this is a FIXT message _and_ ApplVerID is _not_ set, we need to know something about the session
            Session session = Session.lookupSession(inSessionId);
            if(session == null) {
                throw new UnsupportedOperationException(Messages.APPL_VERID_REQUIRED.getText());
            }
            quickfix.field.ApplVerID defaultAppVerId = session.getSenderDefaultApplicationVersionID();
            if(defaultAppVerId == null) {
                throw new UnsupportedOperationException(Messages.APPL_VERID_REQUIRED.getText());
            }
            return getFIXVersion(defaultAppVerId);
        } else {
            return getFIXVersion(inSessionId.getBeginString());
        }
    }
    /**
     * Get the FIX version associated with the given <code>ApplVerID</code> value.
     *
     * @param inApplVerId a <code>quickfix.field.ApplVerID</code> value
     * @return a <code>FIXVersion</code> value
     */
    public static FIXVersion getFIXVersion(quickfix.field.ApplVerID inApplVerId)
    {
        switch(inApplVerId.getValue()) {
            case quickfix.field.ApplVerID.FIX40:
                return FIX40;
            case quickfix.field.ApplVerID.FIX41:
                return FIX41;
            case quickfix.field.ApplVerID.FIX42:
                return FIX42;
            case quickfix.field.ApplVerID.FIX43:
                return FIX43;
            case quickfix.field.ApplVerID.FIX44:
                return FIX44;
            case quickfix.field.ApplVerID.FIX50:
                return FIX50;
            case quickfix.field.ApplVerID.FIX50SP1:
                return FIX50SP1;
            case quickfix.field.ApplVerID.FIX50SP2:
                return FIX50SP2;
            case quickfix.field.ApplVerID.FIX27:
            case quickfix.field.ApplVerID.FIX30:
            default:
                throw new IllegalArgumentException(Messages.FIX_VERSION_UNSUPPORTED.getText(inApplVerId));
        }
    }
    /**
     * Indicate if the FIX version uses FIXT1.1 or greater.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isFixT()
    {
        return fixtVersions.contains(this);
    }
    /**
     * Return a string representation of the value.
     * 
     * @return a <code>String</code> value
     */
    @Override
    public String toString()
    {
        return version;
    }
    /**
     * Get the message factory for this version.
     *
     * @return a <code>FIXMessageFactory</code> value
     */
    public FIXMessageFactory getMessageFactory()
    {
        return msgFactory;
    }
    /**
     * Return the data dictionary file name.
     * 
     * <p>A file by this name is available as an in-jar resource.
     *
     * @return a <code>String</code> value
     */
    public String getDataDictionaryName()
    {
        return dataDictionary;
    }
    /**
     * Get the applicationVersion value.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getApplicationVersion()
    {
        return applicationVersion;
    }
    /**
     * Get the version value.
     *
     * @return a <code>String</code> value
     */
    public String getVersion()
    {
        return version;
    }
    /**
     * Create a new FIXVersion instance.
     *
     * @param inVersion a <code>String</code> value
     * @param inApplicationVersion a <code>String</code> value
     * @param inDataDictionaryName a <code>String</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     */
    private FIXVersion(String inVersion,
                       String inApplicationVersion,
                       String inDataDictionaryName,
                       FIXMessageFactory inFactory)
    {
        version = inVersion;
        applicationVersion = inApplicationVersion;
        msgFactory = inFactory;
        dataDictionary = inDataDictionaryName;
    }
    /**
     * Create a new FIXVersion instance.
     *
     * @param inVersion a <code>String</code> value
     * @param inDataDictionaryName a <code>String</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     */
    private FIXVersion(String inVersion,
                       String inDataDictionaryName,
                       FIXMessageFactory inFactory)
    {
        this(inVersion,
             null,
             inDataDictionaryName,
             inFactory);
    }
    /**
     * Perform a FIX version lookup using the given version string.
     *
     * @param inVersionString a <code>String</code> value
     * @return a <code>FIXVersion</code> value
     * @throws FieldNotFound if the version could not be determined
     * @throws IllegalArgumentException if the version cannot be found
     */
    private static FIXVersion doFixVersionLookup(String inVersionString)
            throws FieldNotFound
    {
        FIXVersion fixVersion = versionMap.get(inVersionString);
        if(fixVersion == null) {
            throw new IllegalArgumentException(Messages.FIX_VERSION_UNSUPPORTED.getText(inVersionString));
        }
        return fixVersion;
    }
    /**
     * Perform a FIX version lookup using the given application version string.
     *
     * @param inApplicationVersionString a <code>String</code> value
     * @return a <code>FIXVersion</code> value
     * @throws IllegalArgumentException if the version cannot be found
     */
    private static FIXVersion doFixApplicationVersionLookup(String inApplicationVersionString)
    {
        FIXVersion fixVersion = applicationVersionMap.get(inApplicationVersionString);
        if(fixVersion == null) {
            throw new IllegalArgumentException(Messages.FIX_VERSION_UNSUPPORTED.getText(inApplicationVersionString));
        }
        return fixVersion;
    }
    /**
     * FIX application version, if applicable
     */
    private final String applicationVersion;
    /**
     * FIX message factory value for this version
     */
    private final FIXMessageFactory msgFactory;
    /**
     * string representation of this version
     */
    private final String version;
    /**
     * data dictionary name of this version
     */
    private final String dataDictionary;
    /**
     * stores FIXVersion values by version identifier
     */
    private static final HashMap<String,FIXVersion> versionMap;
    /**
     * stores FIXVersion values by application version identifier
     */
    private static final HashMap<String,FIXVersion> applicationVersionMap;
    /**
     * collection of FIX versions that use FIXT
     */
    private static final Set<FIXVersion> fixtVersions = EnumSet.of(FIX50,FIX50SP1,FIX50SP2);
    /**
     * Performs static initialization
     */
    static
    {
        versionMap = new HashMap<String,FIXVersion>();
        applicationVersionMap = new HashMap<String,FIXVersion>();
        for(FIXVersion fixVersion : FIXVersion.values()) {
            versionMap.put(fixVersion.getVersion(),
                           fixVersion);
            if(fixVersion.getApplicationVersion() != null) {
                applicationVersionMap.put(fixVersion.getApplicationVersion(),
                                          fixVersion);
            }
        }
    }
}
