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
          "FIX40.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_0_BEGIN_STRING,
                                new quickfix.fix40.MessageFactory(),
                                new FIXMessageAugmentor_40())),
    FIX41(FIXDataDictionary.FIX_4_1_BEGIN_STRING,
          "FIX41.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_1_BEGIN_STRING,
                                new quickfix.fix41.MessageFactory(),
                                new FIXMessageAugmentor_41())),
    FIX42(FIXDataDictionary.FIX_4_2_BEGIN_STRING,
          "FIX42.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                                new quickfix.fix42.MessageFactory(),
                                new FIXMessageAugmentor_42())),
    FIX43(FIXDataDictionary.FIX_4_3_BEGIN_STRING,
          "FIX43.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_3_BEGIN_STRING,
                                new quickfix.fix43.MessageFactory(),
                                new FIXMessageAugmentor_43())),
    FIX44(FIXDataDictionary.FIX_4_4_BEGIN_STRING,
          "FIX44-marketcetera.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_4_BEGIN_STRING,
                                new quickfix.fix44.MessageFactory(),
                                new FIXMessageAugmentor_44())),
    FIX50(FIXDataDictionary.FIX_5_0_BEGIN_STRING,
          "FIX50.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_5_0_BEGIN_STRING,
                                new quickfix.fix50.MessageFactory(),
                                new FIXMessageAugmentor_50())),
    FIX50SP1(FIXDataDictionary.FIX_5_0_SP1_BEGIN_STRING,
             "FIX50SP1.xml", //$NON-NLS-1$
             new FIXMessageFactory(FIXDataDictionary.FIX_5_0_SP1_BEGIN_STRING,
                                   new quickfix.fix50sp1.MessageFactory(),
                                   new FIXMessageAugmentor_50SP1())),
    FIX50SP2(FIXDataDictionary.FIX_5_0_SP2_BEGIN_STRING,
             "FIX50SP2.xml", //$NON-NLS-1$
             new FIXMessageFactory(FIXDataDictionary.FIX_5_0_SP2_BEGIN_STRING,
                                   new quickfix.fix50sp2.MessageFactory(),
                                   new FIXMessageAugmentor_50SP2())),
//    FIXT11(FIXDataDictionary.FIXT_1_1_BEGIN_STRING,
//           "FIXT11.xml", //$NON-NLS-1$
//           new FIXMessageFactory(FIXDataDictionary.FIXT_1_1_BEGIN_STRING,
//                                 new quickfix.fixt11.MessageFactory(),
//                                 new FIXMessageAugmentor_T11())),
    FIX_SYSTEM(FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING,
               "FIX00-system.xml", //$NON-NLS-1$
               new SystemFIXMessageFactory());
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
        String beginString = inMessage.getHeader().getString(quickfix.field.BeginString.FIELD);
        FIXVersion fixVersion = versionMap.get(beginString);
        if(fixVersion == null) {
            throw new IllegalArgumentException(Messages.FIX_VERSION_UNSUPPORTED.getText(beginString));
        }
        return fixVersion;
    }
    /**
     * Indicate if the FIX version
     *
     *
     * @return a <code>boolean</code> value
     */
    public boolean isFix5OrGreater()
    {
        return version5OrGreater.contains(this);
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
        version = inVersion;
        msgFactory = inFactory;
        dataDictionary = inDataDictionaryName;
    }
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
     * 
     */
    private static final Set<FIXVersion> version5OrGreater = EnumSet.of(FIX50,FIX50SP1,FIX50SP2);
    /**
     * Performs static initialization
     */
    static
    {
        versionMap = new HashMap<String,FIXVersion>();
        for(FIXVersion fixVersion : FIXVersion.values()) {
            versionMap.put(fixVersion.toString(),
                           fixVersion);
        }
    }
}
