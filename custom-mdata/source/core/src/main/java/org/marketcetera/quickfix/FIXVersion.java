package org.marketcetera.quickfix;

import java.util.HashMap;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.messagefactory.*;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.BeginString;

/**
 * An enum for all the supported FIX versions, with the default URL for the data dictionary file.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public enum FIXVersion {
    FIX40(FIXDataDictionary.FIX_4_0_BEGIN_STRING, 4.0, "FIX40.xml", //$NON-NLS-1$
          new FIXMessageFactory(FIXDataDictionary.FIX_4_0_BEGIN_STRING, new quickfix.fix40.MessageFactory(), new FIXMessageAugmentor_40())),
    FIX41(FIXDataDictionary.FIX_4_1_BEGIN_STRING, 4.1, "FIX41.xml", //$NON-NLS-1$
            new FIXMessageFactory(FIXDataDictionary.FIX_4_1_BEGIN_STRING, new quickfix.fix41.MessageFactory(), new FIXMessageAugmentor_41())),
    FIX42(FIXDataDictionary.FIX_4_2_BEGIN_STRING, 4.2, "FIX42.xml", //$NON-NLS-1$
            new FIXMessageFactory(FIXDataDictionary.FIX_4_2_BEGIN_STRING, new quickfix.fix42.MessageFactory(), new FIXMessageAugmentor_42())),
    FIX43(FIXDataDictionary.FIX_4_3_BEGIN_STRING, 4.3, "FIX43.xml", //$NON-NLS-1$
            new FIXMessageFactory(FIXDataDictionary.FIX_4_3_BEGIN_STRING, new quickfix.fix43.MessageFactory(), new FIXMessageAugmentor_43())),
    FIX44(FIXDataDictionary.FIX_4_4_BEGIN_STRING, 4.4, "FIX44-marketcetera.xml", //$NON-NLS-1$
            new FIXMessageFactory(FIXDataDictionary.FIX_4_4_BEGIN_STRING, new quickfix.fix44.MessageFactory(), new FIXMessageAugmentor_44())),
    FIX_SYSTEM(FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING, 0.0, "FIX00-system.xml", //$NON-NLS-1$
          new SystemFIXMessageFactory());

    private static HashMap<String, FIXVersion> versionMap;

    static {
        versionMap = new HashMap<String, FIXVersion>();
        versionMap.put(FIX_SYSTEM.toString(), FIX_SYSTEM);
        versionMap.put(FIX40.toString(), FIX40);
        versionMap.put(FIX41.toString(), FIX41);
        versionMap.put(FIX42.toString(), FIX42);
        versionMap.put(FIX43.toString(), FIX43);
        versionMap.put(FIX44.toString(), FIX44);
    }

    public static FIXVersion getFIXVersion(String version) {
        FIXVersion fixVersion = versionMap.get(version);
        if(fixVersion == null) {
            throw new IllegalArgumentException(Messages.FIX_VERSION_UNSUPPORTED.getText(version));
        }
        return fixVersion;
    }

    public static FIXVersion getFIXVersion(Message inMsg) throws FieldNotFound {
        return versionMap.get(inMsg.getHeader().getField(new BeginString()).getValue());
    }


    private FIXVersion (String inVersion, double versionAsDouble, String inDDURL, FIXMessageFactory inFactory) {
        version = inVersion;
        this.versionAsDouble = versionAsDouble; 
        msgFactory = inFactory;
        dataDictionaryURL = inDDURL;
    }


    private FIXMessageFactory msgFactory;
    private final String version;
    private final double versionAsDouble;
    private final String dataDictionaryURL;

    public String toString() {
        return version;
    }

    public double getVersionAsDouble() {
		return versionAsDouble;
	}

	public FIXMessageFactory getMessageFactory() {
        return msgFactory;
    }

    /** Returns the path to the file representing the DataDictionary */
    public String getDataDictionaryURL() {
        return dataDictionaryURL;
    }
}
